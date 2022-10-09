package com.example.splendor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.splendor.Adapter.ConnectWithPeopleAdapter;
import com.example.splendor.Adapter.MPostAdapter;
import com.example.splendor.ChatActivity;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.FragmentHomeBinding;
import com.google.apphosting.datastore.testing.DatastoreTestTrace;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

FragmentHomeBinding b;
List<Posts> mPosts;
//PostAdapter postAdapter;
MPostAdapter mPostAdapter;
FirebaseDatabase firebaseDatabase;
FirebaseAuth mAuth;
List<String> followingList;
List<Users> usersList;
ConnectivityManager cm;
NetworkInfo ni;
ConnectWithPeopleAdapter connectWithPeopleAdapter;

DatabaseReference databaseReference;
ValueEventListener valueEventListener;

DatabaseReference followingUsersReference;
ValueEventListener followingUsersListener;

DatabaseReference readPostReference;
ValueEventListener readPostListener;

    @Override
    public void onDestroyView() {
        if (databaseReference != null){
            databaseReference.removeEventListener(valueEventListener);
        }
        if (followingUsersReference != null){
            followingUsersReference.removeEventListener(followingUsersListener);
        }
        if (readPostReference != null){
            readPostReference.removeEventListener(readPostListener);
        }

        b.recConnect.setAdapter(null);
        b.recyclerViewAllPosts.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentHomeBinding.inflate(inflater, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        followingList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        b.recyclerViewAllPosts.setLayoutManager(linearLayoutManager);
        b.recyclerViewAllPosts.setHasFixedSize(true);
        mPosts = new ArrayList<>();
        usersList = new ArrayList<>();


        b.pFetchingViews.setVisibility(View.VISIBLE);


        //init connect adapter
        connectWithPeopleAdapter = new ConnectWithPeopleAdapter(usersList, getContext());
        LinearLayoutManager connectLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        b.recConnect.setAdapter(connectWithPeopleAdapter);
        b.recConnect.setLayoutManager(connectLayoutManager);

        //init post adapter
        mPostAdapter = new MPostAdapter(getContext(), mPosts);
        b.recyclerViewAllPosts.setAdapter(mPostAdapter);


        populateConnectList();
       checkFollowingUsers();

       b.imgInbox.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getActivity().getApplicationContext(), ChatActivity.class);
               startActivity(intent);
           }
       });


        return b.getRoot();
    }


    private boolean isOnline()  {
        cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isConnected());
    }

    public void populateConnectList(){
        databaseReference = firebaseDatabase.getReference().child("Users");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Users users = snapshot1.getValue(Users.class);
                    if (!users.getId().equals(mAuth.getCurrentUser().getUid())){
                        usersList.add(users);
                    }

                }
                connectWithPeopleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
       databaseReference.addValueEventListener(valueEventListener);
    }
    private void checkFollowingUsers(){
        followingUsersReference = firebaseDatabase.getReference().child("Follow").child(mAuth.getCurrentUser().getUid()).child("following");
        followingUsersListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    followingList.add(snapshot1.getKey());
                }
                followingList.add(mAuth.getCurrentUser().getUid());
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
       followingUsersReference.addValueEventListener(followingUsersListener);
    }
    public void readPosts(){
        readPostReference = firebaseDatabase.getReference().child("Posts");

        readPostListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mPosts.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Posts posts = snapshot1.getValue(Posts.class);
                    for (String id: followingList){
                        if (id.equals(posts.getPublisherId())){
                            mPosts.add(posts);
                        }
                    }
                }
                mPostAdapter.notifyDataSetChanged();
                b.pFetchingViews.setVisibility(View.GONE);
                b.txtConnect.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        readPostReference.addValueEventListener(readPostListener);
    }
}