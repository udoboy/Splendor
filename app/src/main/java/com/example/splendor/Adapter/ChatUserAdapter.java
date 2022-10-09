package com.example.splendor.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.splendor.ChatDetailActivity;
import com.example.splendor.Fragments.ChatRoomFragment;
import com.example.splendor.Interfaces.ChatUserInterface;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUserAdapter extends RecyclerView.Adapter<ChatUserAdapter.ViewHolder>{
   List<Users> mUsers;
   Context context;
   ChatUserInterface chatUserInterface;

    public ChatUserAdapter(List<Users> mUsers, Context context, ChatUserInterface chatUserInterface) {
        this.mUsers = mUsers;
        this.context = context;
        this.chatUserInterface = chatUserInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_user_item, parent, false);
        return new ChatUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     Users users = mUsers.get(position);
     holder.cUsername.setText(users.getUsername());
     Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(holder.cProfileImage);
     holder.itemView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             chatUserInterface.onUserClicked(users.getId());

         }

     });

        FirebaseDatabase.getInstance().getReference().child("Chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid() + users.getId())
                .orderByChild("timeStamp").limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    for (DataSnapshot snapshot1 : snapshot.getChildren()){
                        String lastMessage = snapshot1.child("message").getValue().toString();
                        if (!lastMessage.isEmpty()){
                            holder.cLastMessage.setText(lastMessage);
                        }
                        else{
                            holder.cLastMessage.setText("Last message");
                        }
                    }
                }
                else {
                    holder.cLastMessage.setText("Last message");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cUsername, cLastMessage;
        CircleImageView cProfileImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cUsername = itemView.findViewById(R.id.cUsername);
            cLastMessage = itemView.findViewById(R.id.cLastMessage);
            cProfileImage = itemView.findViewById(R.id.cProfileImage);

        }
    }
}
