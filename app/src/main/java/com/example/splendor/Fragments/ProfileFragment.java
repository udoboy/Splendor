package com.example.splendor.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splendor.Adapter.ProfilePostAdapter;
import com.example.splendor.EditProfileActivity;
import com.example.splendor.FollowActivity;
import com.example.splendor.GrandpageActivity;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentProfileBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collector;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

FragmentProfileBinding b;
String profileId;
FirebaseAuth mAuth;
FirebaseDatabase firebaseDatabase;
List<Posts> mPosts;
ProfilePostAdapter profilePostAdapter;
Menu nav_menu;
View header;
CircleImageView navProfileImage;
TextView navUsername, navBio;
List<Posts> textPosts;
ProfilePostAdapter textPostAdapter;



DatabaseReference followersReference;

OnCompleteListener onCompleteListener;
ValueEventListener isFollowedEventListener;
DatabaseReference isFollowedRef;

DatabaseReference userInfoRef;
ValueEventListener userInfoValueEventListener;

DatabaseReference followingReference;
DatabaseReference sFollowersReference;
ValueEventListener followingEventListener;
ValueEventListener sFollowersEventListener;

DatabaseReference getPostCountReference;
ValueEventListener getPostCountEventListener;

DatabaseReference getPostReference;
ValueEventListener getPostValueEventListener;

DatabaseReference checkNotificationRef;
ValueEventListener checkNotificationListener;



    @Override
    public void onDestroyView() {
        b.recyclerViewPosts.setAdapter(null);
        if (isFollowedRef != null){
            isFollowedRef.removeEventListener(isFollowedEventListener);
        }
        if (userInfoRef != null){
            userInfoRef.removeEventListener(userInfoValueEventListener);
        }
        if (sFollowersReference != null){
            sFollowersReference.removeEventListener(sFollowersEventListener);
        }
        if (followingReference != null){
            followingReference.removeEventListener(followingEventListener);
        }
        if (getPostCountReference != null){
            getPostCountReference.removeEventListener(getPostCountEventListener);
        }
        if (getPostReference != null){
            getPostReference.removeEventListener(getPostValueEventListener);
        }
        b.recTextPosts.setAdapter(null);
        b.recyclerViewPosts.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentProfileBinding.inflate(inflater, container, false);
        String data = getContext().getSharedPreferences("profile", Context.MODE_PRIVATE).getString("profileId", "none");

        mAuth = FirebaseAuth.getInstance();
        nav_menu = b.navView.getMenu();
        header = b.navView.getHeaderView(0);
         navProfileImage = header.findViewById(R.id.navHeadProfileImage);
         navUsername = header.findViewById(R.id.navHeadUsername);
         navBio = header.findViewById(R.id.navHeadBio);


        if(data.equals("none")){
            profileId = mAuth.getCurrentUser().getUid();
        }
        else{
            profileId = data;
        }


        firebaseDatabase = FirebaseDatabase.getInstance();
        b.recyclerViewPosts.setHasFixedSize(true);
        mPosts = new ArrayList<>();
        textPosts = new ArrayList<>();


        b.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_grandpage:
                        Intent intent = new Intent(getContext().getApplicationContext(), GrandpageActivity.class);
                        intent.putExtra("profileId", profileId);
                        startActivity(intent); break;
                    case R.id.nav_media:
                        getPosts("media");
                        b.drawerLayout.closeDrawer(GravityCompat.END);
                        break;
                    case R.id.nav_text:
                        getPosts("text");
                        b.drawerLayout.closeDrawer(GravityCompat.END);
                        break;

                }
                return true;
            }
        });


        userInfo();
        getfollowingCount();
        getPostCountCount();
        getPosts("media");

        if (profileId.equals(mAuth.getCurrentUser().getUid())){
            b.btnEditProfile.setText("Edit Profile");
        }
        else{
            isFollowed(profileId, b.btnEditProfile);

        }



        b.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        b.txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext().getApplicationContext(), FollowActivity.class);
                intent.putExtra("userId", profileId);
                intent.putExtra("list", "followers");
                startActivity(intent);
            }
        });

        b.txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext().getApplicationContext(), FollowActivity.class);
                intent.putExtra("userId", profileId);
                intent.putExtra("list", "following");
                startActivity(intent);
            }
        });



        b.btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(b.btnEditProfile.getText().equals("Follow")){
                    followersReference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(mAuth.getCurrentUser().getUid());
                    onCompleteListener= new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            checkNotification(FirebaseAuth.getInstance().getCurrentUser().getUid(), profileId, "Followed you");
                        }
                    };
                   followersReference.setValue(true).addOnCompleteListener(onCompleteListener);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(mAuth.getCurrentUser().getUid()).child("following").child(profileId).setValue(true);

                }
                else if (b.btnEditProfile.getText().equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("followers").child(mAuth.getCurrentUser().getUid()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(mAuth.getCurrentUser().getUid()).child("following").child(profileId).removeValue();

                }
                else {
                    startActivity(new Intent(getContext().getApplicationContext(), EditProfileActivity.class));
                }
            }
        });



        return b.getRoot();
    }


    public void isFollowed(String id, Button button){
        isFollowedRef = firebaseDatabase.getReference().child("Follow").child(mAuth.getCurrentUser().getUid()).child("following").child(id);
        isFollowedEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    button.setText("Following");
                }
                else{
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        isFollowedRef.addValueEventListener(isFollowedEventListener);
    }
    //todo: remove all the fonts you are not using

    public void userInfo(){
        userInfoRef = firebaseDatabase.getReference().child("Users").child(profileId);
        userInfoValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    Users user = snapshot.getValue(Users.class);
                    b.username.setText(user.getUsername());
                    b.bio.setText(user.getBio());
                    b.txtBarUsername.setText(user.getUsername());
                    Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(b.profileImage);
                    Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(navProfileImage);
                    navUsername.setText(user.getUsername());
                    navBio.setText(user.getBio());
                    if (user.getGrandpage().equals("false")){
                        nav_menu.findItem(R.id.nav_grandpage_item).setVisible(false);

                    }
                    else if(user.getGrandpage().equals("true")){
                        nav_menu.findItem(R.id.nav_grandpage_item).setVisible(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        userInfoRef.addValueEventListener(userInfoValueEventListener);
    }
    public void getfollowingCount(){
        followingReference = firebaseDatabase.getReference().child("Follow").child(profileId).child("following");
        followingEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b.numberoffollowing.setText(""+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        followingReference.addValueEventListener(followingEventListener);

        sFollowersReference = firebaseDatabase.getReference().child("Follow").child(profileId).child("followers");
        sFollowersEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b.numberOfFollowers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        sFollowersReference.addValueEventListener(sFollowersEventListener);

    }

    public void getPostCountCount(){
        getPostCountReference = firebaseDatabase.getReference().child("Posts");
        getPostCountEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;
                mPosts.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Posts posts = snapshot1.getValue(Posts.class);
                    if(posts.getPublisherId().equals(profileId)){
                        mPosts.add(posts);
                        counter++;

                    }

                    b.numberOfPosts.setText(String.valueOf(counter));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
     getPostCountReference.addValueEventListener(getPostCountEventListener);

    }
    public void getPosts(String type){
        if (type.equals("media")){
            profilePostAdapter = new ProfilePostAdapter(getContext(), mPosts, mAuth.getCurrentUser().getUid());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
            gridLayoutManager.setReverseLayout(true);
            b.recyclerViewPosts.setLayoutManager(gridLayoutManager);
            b.recyclerViewPosts.setAdapter(profilePostAdapter);
            getPostReference = firebaseDatabase.getReference().child("Posts");
            getPostValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mPosts.clear();
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        Posts posts = snapshot1.getValue(Posts.class);
                        if (posts.getType().equals("photo") || posts.getType().equals("video")){
                            if (posts.getPublisherId().equals(profileId)){
                                mPosts.add(posts);
                            }
                        }

                    }

                    Collections.reverse(mPosts);
                    profilePostAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            getPostReference.addValueEventListener(getPostValueEventListener);

        }
        else if(type.equals("text")){
            textPostAdapter = new ProfilePostAdapter(getContext(), textPosts, mAuth.getCurrentUser().getUid());
           // profilePostAdapter = new ProfilePostAdapter(getContext(), textPosts, mAuth.getCurrentUser().getUid());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            b.recyclerViewPosts.setLayoutManager(linearLayoutManager);
            b.recyclerViewPosts.setAdapter(textPostAdapter);
           getPostReference = firebaseDatabase.getReference().child("Posts");
           getPostValueEventListener = new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                   textPosts.clear();
                   for (DataSnapshot snapshot1: snapshot.getChildren()){
                       Posts posts = snapshot1.getValue(Posts.class);
                       if (posts.getType().equals("text")){

                           if (posts.getPublisherId().equals(profileId)){
                               textPosts.add(posts);
                           }

                       }
                   }

                   Collections.reverse(textPosts);
                   textPostAdapter.notifyDataSetChanged();
               }

               @Override
               public void onCancelled(@NonNull DatabaseError error) {

               }
           };
           getPostReference.addValueEventListener(getPostValueEventListener);
        }

    }

    public void addNotification( String publisherId, String text, String userId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("publisherId", publisherId );
        map.put("description", text);
        map.put("postId", "null");
        map.put("ntype", "follow");

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(publisherId+"followed"+userId).setValue(map);
       // String notificationId = ref.push().getKey();

    }

    public void checkNotification(String publisherId , String userId, String text){
        checkNotificationRef = firebaseDatabase.getReference().child("Notifications").child(userId).child(publisherId+"followed"+userId);
        checkNotificationListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                }
                else{
                    addNotification(publisherId, text, userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

    }

}