package com.example.socify.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socify.Classes.PostMember;
import com.example.socify.HomeFragments.CommentsFragment;
import com.example.socify.HomeFragments.VisitProfile;
import com.example.socify.R;
import com.example.socify.Activities.Home;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import kr.co.prnd.readmore.ReadMoreTextView;

public class GetNewsFeedAdapter extends RecyclerView.Adapter<GetNewsFeedAdapter.NewsFeedViewHolder> {

    Context context;
    ArrayList<PostMember> postMembers;
    String type;
    PostMember member;

    public GetNewsFeedAdapter(Context context, ArrayList<PostMember> postMembers) {
        this.context = context;
        this.postMembers = postMembers;

    }

    @NonNull
    @Override
    public NewsFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.newsfeedbox, parent, false);
        return new NewsFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  NewsFeedViewHolder holder, int position) {
        member = postMembers.get(position);
            if (member.getType().equals("image")) {
                holder.playerView.setVisibility(View.INVISIBLE);
                holder.post.setVisibility(View.VISIBLE);
                Glide.with(context).load(member.getUrl()).into(holder.profilepic);
                Glide.with(context).load(member.getPostUri()).into(holder.post);
                holder.namepost.setText(member.getName());
                holder.date.setText(member.getTime());
                holder.usernamepost.setText(member.getUsername());
                holder.description.setText(member.getDesc());
            } else if (member.getType().equals("video")) {
                Glide.with(context).load(member.getUrl()).into(holder.profilepic);
                holder.namepost.setText(member.getName());
                holder.date.setText(member.getTime());
                holder.usernamepost.setText(member.getUsername());
                holder.playerView.setVisibility(View.VISIBLE);
                holder.description.setText(member.getDesc());
                holder.post.setVisibility(View.INVISIBLE);
                try {
                    holder.exoplayer = new ExoPlayer.Builder(context.getApplicationContext()).build();
                    holder.playerView.setPlayer(holder.exoplayer);
                    MediaItem mediaItem = MediaItem.fromUri(member.getPostUri());
                    holder.exoplayer.addMediaItems(Collections.singletonList(mediaItem));
                    holder.exoplayer.setPlayWhenReady(false);
                    holder.exoplayer.prepare();

                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                }

            }
            type = member.getType();
            isLiked(member.getPostid(), holder.like);
            nrlikes(holder.likescount, member.getPostid());



            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentView, new CommentsFragment(member.getPostid())).addToBackStack(null).commit();
                }
            });

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(holder.like.getTag().equals("Like")){
                        FirebaseDatabase.getInstance().getReference().child("College")
                                .child(Home.getUserData.college_name)
                                .child("Posts")
                                .child(member.getUid())
                                .child("All Images")
                                .child(member.getPostid())
                                .child("Likes")
                                .child(Home.getUserData.uid)
                                .setValue(true);
                    }
                    else{
                        FirebaseDatabase.getInstance().getReference().child("College")
                                .child(Home.getUserData.college_name)
                                .child("Posts")
                                .child(member.getUid())
                                .child("All Images")
                                .child(member.getPostid())
                                .child("Likes")
                                .child(Home.getUserData.uid)
                                .removeValue();
                    }
                }
            });


        holder.profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) context;
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.FragmentView,new VisitProfile(member.getUid())).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postMembers.size();
    }

    public static class NewsFeedViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilepic;
        ImageView post;
        MaterialTextView namepost, usernamepost, date,likescount;
        public ImageView like, comment;
        ReadMoreTextView description;
        StyledPlayerView playerView;
        ExoPlayer exoplayer;
        CircleImageView profile;

        public NewsFeedViewHolder(@NonNull View itemView) {
            super(itemView);
            profilepic = itemView.findViewById(R.id.ProfilePic);
            post = itemView.findViewById(R.id.post);
            namepost = itemView.findViewById(R.id.picadded);
            date = itemView.findViewById(R.id.time);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.commentbtn);
//        comment = itemView.findViewById(R.id.comment);
            likescount = itemView.findViewById(R.id.likecount);
            description = itemView.findViewById(R.id.caption);
            usernamepost = itemView.findViewById(R.id.usernamepost);
            playerView = itemView.findViewById(R.id.videopost);

        }

    }

    private void isLiked(String postid, final ImageView imageView) {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("College")
                .child(Home.getUserData.college_name)
                .child("Posts")
                .child(member.getUid())
                .child("All Images")
                .child(member.getPostid())
                .child("Likes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(firebaseUser.getUid()).exists()) {
                    imageView.setImageResource(R.drawable.likedpost);
                    imageView.setTag("Liked");
                }
                else{
                    imageView.setImageResource(R.drawable.likepost);
                    imageView.setTag("Like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void nrlikes(MaterialTextView likescount, String postid) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("College")
                .child(Home.getUserData.college_name)
                .child("Posts")
                .child(member.getUid())
                .child("All Images")
                .child(member.getPostid())
                .child("Likes");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likescount.setText(snapshot.getChildrenCount()+ " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


}