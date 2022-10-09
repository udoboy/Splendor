package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.splendor.Adapter.CommentReplyAdapter;
import com.example.splendor.Models.CommentReplies;
import com.example.splendor.Models.Comments;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityCommentRepliesBinding;
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
import java.util.HashMap;
import java.util.List;

public class CommentRepliesActivity extends AppCompatActivity {
    ActivityCommentRepliesBinding b;
    List<CommentReplies> commentRepliesList;
    CommentReplyAdapter commentReplyAdapter;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b= ActivityCommentRepliesBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        b.commentRepliesRec.setVisibility(View.VISIBLE);
        b.commentRepliesRec.bringToFront();

        getSupportActionBar().hide();
        Intent cIntent = getIntent();
        String username = cIntent.getStringExtra("username");
        String comment = cIntent.getStringExtra("comment");
        String commentId = cIntent.getStringExtra("commentId");
        String postId = cIntent.getStringExtra("postId");
        String profileImage = cIntent.getStringExtra("profileImage");
        String replying = cIntent.getStringExtra("replying");
        getUserProfile(b.imageProfile);

        if(replying.equals("true")){
            getEdtReady(b.commentedt);
        }

        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Comments comments = snapshot.getValue(Comments.class);
                userId = comments.getPublisherId();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.txtReplying.setSelected(true);
        b.txtReplying.setText("Replying to: " + username+ ">>  " + comment);

        b.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        b.txtcommentUsername.setText(username);
        b.txtComment.setText(comment);
        Picasso.get().load(profileImage).placeholder(R.drawable.my_user_placeholder).into(b.commentImageProfile);

        commentRepliesList = new ArrayList<>();
        getCommentReplies(commentId);
         commentReplyAdapter = new CommentReplyAdapter(commentRepliesList, this);
        b.commentRepliesRec.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setStackFromEnd(true);
        b.commentRepliesRec.setLayoutManager(linearLayoutManager);
        b.commentRepliesRec.setAdapter(commentReplyAdapter);



        b.txtReplycomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEdtReady(b.commentedt);
            }
        });

        b.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                putCommentReply(b.commentedt.getText().toString(), postId, commentId, comment);
            }
        });

    }

    public void getCommentReplies(String commentId){
        FirebaseDatabase.getInstance().getReference().child("CommentReplies").child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentRepliesList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    CommentReplies commentReplies = snapshot1.getValue(CommentReplies.class);
                    commentRepliesList.add(commentReplies);
                }
                commentReplyAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void putCommentReply(String text, String postId, String parentCommentId, String txtComment) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("CommentReplies");
        String commentId = ref.push().getKey();



        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", text);
        map.put("commentReplyPublisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("parentCommentId", parentCommentId);
        map.put("postId", postId);
        map.put("commentId", commentId);
        ref.child(parentCommentId).child(commentId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    b.commentedt.setText("");
                    checkNotification(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Replied to your comment: "+ txtComment, commentId, userId );

                }
                else{
                    Toast.makeText(CommentRepliesActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // FirebaseDatabase.getInstance().getReference().child("CommentReplies").child(parentCommentId).child(commentId).setValue(map);


    }

    public void getUserProfile(ImageView profileImage){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(profileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getEdtReady(EditText editText){
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) CommentRepliesActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        //putCommentReply(b.commentedt.getText().toString(),postId, commentId );


    }

    public void addNotification(String publisherId, String text, String commentId, String userId ){
        HashMap<String, Object> map = new HashMap<>();
        map.put("publisherId", publisherId );
        map.put("description", text);
        map.put("postId", "null");
        map.put("ntype", "comment");

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(publisherId+"replied"+commentId).setValue(map);

    }

    public void checkNotification(String publisherId, String text, String commentId, String userId){
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(publisherId+"replied"+commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Toast.makeText(context, "You have already notified the user", Toast.LENGTH_SHORT).show();
                }
                else{
                    addNotification(publisherId, text, commentId, userId);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}