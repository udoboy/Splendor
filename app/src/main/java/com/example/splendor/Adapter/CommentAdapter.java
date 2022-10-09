package com.example.splendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.metrics.Event;
import android.provider.Telephony;
import android.renderscript.ScriptGroup;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splendor.CommentRepliesActivity;
import com.example.splendor.Models.CommentReplies;
import com.example.splendor.Models.Comments;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
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
import java.util.concurrent.ThreadFactory;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    List<Comments> mComments;
    Context context;
    String postId;

    String postPublisher;

    List<CommentReplies> commentRepliesList;
    String username;
    String profileImage;


    public CommentAdapter(List<Comments> mComments, Context context, String postId, List<CommentReplies>commentRepliesList) {
        this.mComments = mComments;
        this.context = context;
        this.postId = postId;
        this.commentRepliesList = commentRepliesList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Comments comments = mComments.get(position);
        holder.commentTxt.setText(comments.getComment());
        isLiked(comments.getCommentId(), holder.imgLike);
        getNumberOfCommentLikes(comments.getCommentId(), holder.numberOfCommentLikes);
        FirebaseDatabase.getInstance().getReference().child("CommentReplies").child(comments.getCommentId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() ==0){
                    holder.foldedLinear.setVisibility(View.GONE);
                }
                else{
                    holder.foldedLinear.setVisibility(View.VISIBLE);
                    holder.numberOfCommentReplies.setText(snapshot.getChildrenCount() +"");
                }




            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").child(comments.getPublisherId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);

                holder.username.setText(user.getUsername());
                Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(holder.imageProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like(holder.imgLike, comments.getCommentId());
                notifyItemChanged(position);
            }
        });



//             //comments.setRepliesList(commentRepliesList);
//             CommentReplyAdapter commentReplyAdapter = new CommentReplyAdapter(comments.getRepliesList(), context);
//             //getCommentReplies(postId, comments.getCommentId(), commentReplyAdapter );
//             holder.commentReplies.setAdapter(commentReplyAdapter);
//             //holder.commentReplies.setHasFixedSize(true);
//             holder.commentReplies.setLayoutManager(new LinearLayoutManager(context));
//             holder.expandMe.setOnClickListener(new View.OnClickListener() {
//                 @Override
//                 public void onClick(View view) {
//                     holder.commentReplies.setVisibility(View.VISIBLE);
//                 }
//             });
//             commentReplyAdapter.notifyDataSetChanged();

        holder.replyTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comments comments = mComments.get(position);

                Intent intent = new Intent(context, CommentRepliesActivity.class);

                FirebaseDatabase.getInstance().getReference().child("Users").child(comments.getPublisherId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        String username = user.getUsername();
                        String profileImage = user.getProfileImage();
                        intent.putExtra("username", username);
                        intent.putExtra("profileImage", profileImage);
                        intent.putExtra("comment", comments.getComment());
                        intent.putExtra("commentId", comments.getCommentId());
                        intent.putExtra("postId", postId);
                        intent.putExtra("replying", "true");

                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



        holder.foldedLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Comments comments = mComments.get(position);

                Intent intent = new Intent(context, CommentRepliesActivity.class);

                FirebaseDatabase.getInstance().getReference().child("Users").child(comments.getPublisherId()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users user = snapshot.getValue(Users.class);
                        String username = user.getUsername();
                        String profileImage = user.getProfileImage();
                        intent.putExtra("username", username);
                        intent.putExtra("profileImage", profileImage);
                        intent.putExtra("comment", comments.getComment());
                        intent.putExtra("commentId", comments.getCommentId());
                        intent.putExtra("postId", postId);
                        intent.putExtra("replying", "false");

                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




            }
        });








    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imageProfile, expandMe, imgLike;
        TextView username, numberOfCommentLikes, numberOfCommentReplies, replyTxt, commentTxt;
        LinearLayout foldedLinear;
        RecyclerView commentReplies;


        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.txtcommentUsername);
            commentTxt = itemView.findViewById(R.id.txtComment);
            imageProfile = itemView.findViewById(R.id.commentImageProfile);
            foldedLinear = itemView.findViewById(R.id.foldedLinear1);
            imgLike = itemView.findViewById(R.id.imgLikecomment);
            numberOfCommentLikes = itemView.findViewById(R.id.numberOfLikescomment);
            numberOfCommentReplies = itemView.findViewById(R.id.commentNumberOfComments);
           // commentReplies = itemView.findViewById(R.id.commentReplies);
            replyTxt = itemView.findViewById(R.id.txtReplycomment);
            //expandMe = itemView.findViewById(R.id.imgExpandme1);


        }
    }

    public void isLiked(String commentId, ImageView imageView) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()) {
                    imageView.setImageResource(R.drawable.ic_liked_red);
                    imageView.setTag("liked");

                } else {
                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void like(ImageView imageView, String commentId) {
        if (imageView.getTag().equals("like")) {
            FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
        } else if (imageView.getTag().equals("liked")) {
            FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();

        }
    }

    public void getNumberOfCommentLikes(String commentId, TextView numText) {
        FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numText.setText("" + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void putCommentReply(String text, String postId, String parentCommentId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("CommentReplies");
        String commentId = ref.push().getKey();


        FirebaseDatabase.getInstance().getReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Posts posts = snapshot.getValue(Posts.class);
                postPublisher = posts.getPublisherId();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        HashMap<String, Object> map = new HashMap<>();
        map.put("comment", text);
        map.put("commentReplyPublisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
        map.put("parentCommentId", parentCommentId);
        map.put("postId", postId);
        map.put("commentId", commentId);
        ref.child(parentCommentId).child(commentId).setValue(map);

       // FirebaseDatabase.getInstance().getReference().child("CommentReplies").child(parentCommentId).child(commentId).setValue(map);


    }



//    public void getCommentReplies(String postId, String parentCommentId, CommentReplyAdapter commentReplyAdapter) {
//        FirebaseDatabase.getInstance().getReference().child("CommentReplies").child(parentCommentId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                commentRepliesList.clear();
//
//                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
//
//                        CommentReplies commentReplies = snapshot1.getValue(CommentReplies.class);
//                        if (commentReplies.getParentCommentId().equals(parentCommentId)){
//                            commentRepliesList.add(commentReplies);
//                        }
//                }
//                commentReplyAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }
}
