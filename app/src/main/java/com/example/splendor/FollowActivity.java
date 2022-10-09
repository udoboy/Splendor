package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Switch;
import android.widget.Toast;

import com.example.splendor.Adapter.UserAdapter;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityFollowBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;

public class FollowActivity extends AppCompatActivity {
    ActivityFollowBinding b;
    List<Users> usersList;
    UserAdapter userAdapter;
    List<String> idList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityFollowBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();


        String userId = getIntent().getStringExtra("userId");
        String list = getIntent().getStringExtra("list");

        usersList = new ArrayList<>();
        userAdapter = new UserAdapter(this, usersList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        b.folowRec.setHasFixedSize(true);
        b.folowRec.setLayoutManager(linearLayoutManager);
        b.folowRec.setAdapter(userAdapter);
        idList = new ArrayList<>();

        switch (list){
            case "following":
                b.txtFollow.setText("Following");
                getFollowing(userId); break;
            case "followers":
                b.txtFollow.setText("Followers");
                getFollowers(userId);break;
        }

    }

    public void getFollowers(String userId){
        FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                idList.clear();
             for (DataSnapshot snapshot1 : snapshot.getChildren()){
                 idList.add(snapshot1.getKey());
             }
             showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getFollowing(String userId){
         FirebaseDatabase.getInstance().getReference().child("Follow").child(userId).child("following").addValueEventListener(new ValueEventListener() {
             @Override
             public void onDataChange(@NonNull DataSnapshot snapshot) {
                 idList.clear();
                 for (DataSnapshot snapshot1 : snapshot.getChildren()){
                     idList.add(snapshot1.getKey());
                 }
                 showUsers();
             }

             @Override
             public void onCancelled(@NonNull DatabaseError error) {

             }
         });
    }

    public void showUsers(){
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                if (snapshot.exists()){
                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        Users user = snapshot1.getValue(Users.class);
                        if (user.getId() != null){
                            for (String id: idList){
                                if(user.getId().equals(id)){
                                    usersList.add(user);
                                }

                            }
                        }
                        else{

                        }

                        userAdapter.notifyDataSetChanged();
                    }
                }
                else{

                }
               
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}