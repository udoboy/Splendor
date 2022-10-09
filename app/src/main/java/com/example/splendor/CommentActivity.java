package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.splendor.Adapter.CommentAdapter;
import com.example.splendor.Adapter.CommentReplyAdapter;
import com.example.splendor.Models.CommentReplies;
import com.example.splendor.Models.Comments;
import com.example.splendor.Models.CustomProgressDialogue;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    ActivityCommentBinding b;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    List<Comments> commentsList;
    CommentAdapter commentAdapter;
    List<CommentReplies> commentRepliesList;
    CommentReplyAdapter commentReplyAdapter;
    List<CommentReplies> repliesList1;
    CustomProgressDialogue mypd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityCommentBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        commentsList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        mypd = new CustomProgressDialogue(this);
        String postId = getIntent().getStringExtra("postId");
        String publisherId = getIntent().getStringExtra("publisherId");
        getComments(postId);
        commentRepliesList = new ArrayList<>();
        //getCommentReplies(postId, );
        commentAdapter = new CommentAdapter(commentsList, this, postId, repliesList1);


        b.recyclerViewComments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        b.recyclerViewComments.setLayoutManager(linearLayoutManager);
        b.recyclerViewComments.setAdapter(commentAdapter);






        b.backarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(b.imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        b.postComment.setTag("null");
        b.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b.postComment.getTag().equals("null")){
                    mypd.show();

                    String comment = b.commentedt.getText().toString();

                    if (!comment.isEmpty()){
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("comment", comment);
                        map.put("publisherId", mAuth.getCurrentUser().getUid());


                        DatabaseReference ref = firebaseDatabase.getReference("Comments");
                        String commentId = ref.push().getKey();
                        map.put("commentId", commentId);


                        firebaseDatabase.getReference().child("Comments").child(postId).child(commentId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mypd.dismiss();
                                    Toast.makeText(CommentActivity.this, "Comment added successfully", Toast.LENGTH_SHORT).show();
                                    b.commentedt.setText("");

                                    addNotification(postId, "replied to your post",FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId  );
                                }
                                else {
                                    mypd.dismiss();
                                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });




                    }
                    else{
                        Toast.makeText(CommentActivity.this, "Please enter a comment", Toast.LENGTH_SHORT).show();
                    }

                }



            }
        });




    }
    public void getComments(String postId){

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Comments comments = snapshot1.getValue(Comments.class);
                    commentsList.add(comments);


                }
                commentAdapter.notifyDataSetChanged();
                Collections.reverse(commentsList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    public void addNotification(String postId, String text, String publisherId, String userId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("publisherId", publisherId );
        map.put("description", text);
        map.put("postId", postId);
        map.put("ntype", "like");

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).push().setValue(map);

    }





    }


