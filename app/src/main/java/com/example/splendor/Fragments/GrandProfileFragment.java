package com.example.splendor.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentGrandProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class GrandProfileFragment extends Fragment {

FragmentGrandProfileBinding b;
String profileId;
FirebaseAuth mAuth;
int state;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentGrandProfileBinding.inflate(inflater, container, false);
       // return inflater.inflate(R.layout.fragment_grand_profile, container, false);
        mAuth = FirebaseAuth.getInstance();
        state = 0;
       b.lExpanded.setVisibility(View.GONE);


        String data = getContext().getSharedPreferences("grandprofile", Context.MODE_PRIVATE).getString("profileId","none");
        if (data.equals("none")){
            profileId = mAuth.getCurrentUser().getUid();
        }
        else if (data.equals(mAuth.getCurrentUser().getUid())){
            profileId = mAuth.getCurrentUser().getUid();
        }
        else{
            profileId = data;
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(profileId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(b.profileImage);
                Glide.with(getContext()).asBitmap().load(users.getProfileImage()).into(b.coverPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.fltShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (state !=0){
                   state = 0;
                   setState(0);
               }
               else{
                   state = 1;
                   setState(1);
               }
            }
        });
        return b.getRoot();
    }
    public void setState(int mState){
        if (mState == 0){
         b.lExpanded.setVisibility(View.GONE);
        }
        else{
            b.lExpanded.setVisibility(View.VISIBLE);
        }

    }
}