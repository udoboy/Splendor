package com.example.splendor.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.splendor.Models.Notifications;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    List<Notifications> mNotifications;
    Context context;

    public NotificationAdapter(List<Notifications> mNotifications, Context context) {
        this.mNotifications = mNotifications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifications notifications = mNotifications.get(position);
        FirebaseDatabase.getInstance().getReference().child("Users").child(notifications.getPublisherId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.ic_splendor_placeholder).into(holder.nProfileImage);
                holder.nUsername.setText(users.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.nDescription.setText(notifications.getDescription());

        if (notifications.getPostId().equals("null")){
            holder.nPostImage.setVisibility(View.GONE);
        }
        else{
            holder.nPostImage.setVisibility(View.VISIBLE);
            FirebaseDatabase.getInstance().getReference().child("Posts").child(notifications.getPostId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Posts posts = snapshot.getValue(Posts.class);
                    if (posts.getImageUrl() != null && !posts.getImageUrl().equals("default")){
                       Glide.with(context).asBitmap().load(posts.getImageUrl()).placeholder(R.drawable.ic_splendor_placeholder).into(holder.nPostImage);
                    }
                    else{
                       //this implies that either the post has been deleted or the post is a text
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView nProfileImage;
        TextView nUsername, nDescription;
        ImageView nPostImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            nProfileImage = itemView.findViewById(R.id.nProfileImage);
            nUsername = itemView.findViewById(R.id.nUsername);
            nDescription = itemView.findViewById(R.id.nDescription);
            nPostImage = itemView.findViewById(R.id.nPostImage);
        }
    }
}
