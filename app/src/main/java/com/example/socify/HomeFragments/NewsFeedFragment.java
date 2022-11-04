package com.example.socify.HomeFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socify.Activities.Home;
import com.example.socify.Adapters.GetNewsFeedAdapter;
import com.example.socify.Classes.PostMember;
import com.example.socify.R;
import com.example.socify.databinding.FragmentNewsFeedBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NewsFeedFragment extends Fragment {

    FragmentNewsFeedBinding binding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference personPostsRef, likeref;
    GetNewsFeedAdapter getNewsFeed;
    ArrayList<PostMember> postMemberArrayList;
    RecyclerView rec;
    public static ArrayList<String> chattingusers;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chattingusers = new ArrayList<>();
        postMemberArrayList = new ArrayList<>();
        getNewsFeed = new GetNewsFeedAdapter(getActivity(),postMemberArrayList);


    }

    public void loadData(){
        Log.i("newsfeeedfragment","accessible");
        personPostsRef = FirebaseDatabase.getInstance().getReference().child("College")
                .child(Home.getUserData.college_name)
                .child("Posts");
        for(String personid:Home.getUserData.followinglistuids){
            personPostsRef.child(personid).child("All Images").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot post:snapshot.getChildren()){
                        PostMember member = post.getValue(PostMember.class);
                        postMemberArrayList.add(member);
                    }
                    getNewsFeed.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController controller = Navigation.findNavController(view);

        getNewsFeed.notifyDataSetChanged();
        //Ordering data from bottom to top
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        rec = getView().findViewById(R.id.postsRV);
        rec.setLayoutManager(layoutManager);
        rec.setAdapter(getNewsFeed);

        binding.chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                @NonNull NavDirections action = NewsFeedFragmentDirections.actionNewsFeedFragmentToAllChatFragment();
                controller.navigate(action);
            }
        });

        binding.btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavDirections action = NewsFeedFragmentDirections.actionNewsFeedFragmentToNotificationFragment();
                controller.navigate(action);
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentNewsFeedBinding.inflate(inflater, container, false);


        return binding.getRoot();
    }


}