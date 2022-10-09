package com.example.splendor.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splendor.CommentRepliesActivity;
import com.example.splendor.Models.CommentReplies;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentReplyAdapter extends RecyclerView.Adapter<CommentReplyAdapter.ViewHolder> {
    List<CommentReplies> mCommentReplies;
    Context mContext;

    public CommentReplyAdapter(List<CommentReplies> mCommentReplies, Context mContext) {
        this.mCommentReplies = mCommentReplies;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_reply_item, parent, false);
        return new CommentReplyAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        CommentReplies replies = mCommentReplies.get(position);
        holder.comment.setText(replies.getComment());
        isLiked(replies.getCommentId(), holder.Like);

        FirebaseDatabase.getInstance().getReference().child("Likes").child(replies.getCommentId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.numberOfCommentLikes.setText(snapshot.getChildrenCount() +"");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("CommentReplies").child(replies.getCommentId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() ==0){
                    holder.foldedLinear.setVisibility(View.GONE);
                }
                else{
                    holder.foldedLinear.setVisibility(View.VISIBLE);
                    holder.numberOfComments.setText(snapshot.getChildrenCount()+ "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("Users").child(replies.getCommentReplyPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(holder.profileImage);
                holder.username.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentRepliesActivity.class);
                FirebaseDatabase.getInstance().getReference().child("Users").child(replies.getCommentReplyPublisher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        intent.putExtra("username", users.getUsername());
                        intent.putExtra("profileImage", users.getProfileImage());
                        intent.putExtra("comment", replies.getComment());
                        intent.putExtra("commentId", replies.getCommentId());
                        intent.putExtra("postId", replies.getPostId());
                        intent.putExtra("replying", "false");
                        mContext.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
        holder.Like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like(replies.getCommentId(), holder.Like);
                notifyItemChanged(position);
            }
        });

        holder.replycomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CommentRepliesActivity.class);
                FirebaseDatabase.getInstance().getReference().child("Users").child(replies.getCommentReplyPublisher()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Users users = snapshot.getValue(Users.class);
                        intent.putExtra("username", users.getUsername());
                        intent.putExtra("profileImage", users.getProfileImage());
                        intent.putExtra("comment", replies.getComment());
                        intent.putExtra("commentId", replies.getCommentId());
                        intent.putExtra("postId", replies.getPostId());
                        intent.putExtra("replying", "true");
                        mContext.startActivity(intent);
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
        return mCommentReplies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView username, comment, replycomment, numberOfComments, numberOfCommentLikes;
        LinearLayout foldedLinear;
        ImageView Like;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.commentImageProfile1);
            username = itemView.findViewById(R.id.txtcommentUsername1);
            comment= itemView.findViewById(R.id.txtComment1);
            replycomment = itemView.findViewById(R.id.txtReplycomment1);
            foldedLinear = itemView.findViewById(R.id.foldedLinearcomments);
            numberOfComments = itemView.findViewById(R.id.commentNumberOfComments);
            numberOfCommentLikes = itemView.findViewById(R.id.numberOfLikescomment);
            Like = itemView.findViewById(R.id.imgLikecomment3);


        }
    }

    public void isLiked(String commentId, ImageView imageView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){

                    imageView.setImageResource(R.drawable.ic_liked_red);
                    imageView.setTag("liked");
                }
                else{

                    imageView.setImageResource(R.drawable.ic_like);
                    imageView.setTag("like");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void like(String commentId, ImageView imageView){
        if (imageView.getTag().equals("like")){

            FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
        }


        else if(imageView.getTag().equals("liked")) {
            FirebaseDatabase.getInstance().getReference().child("Likes").child(commentId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        }


    }
}
