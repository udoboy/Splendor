package com.example.splendor.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.splendor.Adapter.ChatUserAdapter;
import com.example.splendor.Interfaces.ChatUserInterface;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentInboxBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class InboxFragment extends Fragment implements ChatUserInterface {
   FragmentInboxBinding b;
    ArrayList<String> followingList;
    List<Users> usersList;
    ChatUserAdapter chatUserAdapter;

    DatabaseReference followingRef;
    ValueEventListener followingEventListener;

    DatabaseReference usersRef;
    ValueEventListener usersEventListener;


    @Override
    public void onDestroyView() {
        if (followingRef != null){
            followingRef.removeEventListener(followingEventListener);
        }
        if (usersRef != null){
            usersRef.removeEventListener(usersEventListener);
        }

        b.mUsersRec.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentInboxBinding.inflate(inflater, container, false);

        followingList = new ArrayList<>();
        usersList = new ArrayList<>();
        chatUserAdapter = new ChatUserAdapter(usersList, getContext(), this);
        b.mUsersRec.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        b.mUsersRec.setLayoutManager(linearLayoutManager);
        b.mUsersRec.setAdapter(chatUserAdapter);
        b.mUsersRec.setHasFixedSize(true);


        followingRef = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");

        followingEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    followingList.add(snapshot1.getKey());
                }
                getUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        followingRef.addValueEventListener(followingEventListener);
        return b.getRoot();
    }


    //functions
    public void getUser(){
        usersList.clear();
        for (String id : followingList){
            usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            usersEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        Users users = snapshot1.getValue(Users.class);
                        if (users.getId().equals(id)){
                            usersList.add(users);
                        }
                    }
                    chatUserAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            usersRef.addValueEventListener(usersEventListener);
        }

    }

    @Override
    public void onUserClicked(String uerId) {
        Bundle bundle = new Bundle();
        bundle.putString("userId", uerId);
        ChatRoomFragment chatRoomFragment = new ChatRoomFragment();
        chatRoomFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.chatSpaceContainer, chatRoomFragment).addToBackStack(null).commit();
    }
}