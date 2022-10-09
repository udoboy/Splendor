package com.example.splendor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.splendor.DetailsActivity;
import com.example.splendor.Models.Users;
import com.example.splendor.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConnectWithPeopleAdapter extends RecyclerView.Adapter<ConnectWithPeopleAdapter.ViewHolder> {
    List<Users> usersList;
    Context context;

    public ConnectWithPeopleAdapter(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_people_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users user = usersList.get(position);
        Glide.with(context).asBitmap().load(user.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(holder.profileImage);
        holder.username.setText(user.getUsername());
        holder.bio.setText(user.getBio());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("publisherId", user.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        TextView username, bio;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.fpProfileImage);
            username = itemView.findViewById(R.id.fpUsername);
            bio = itemView.findViewById(R.id.fpBio);
        }
    }
}
