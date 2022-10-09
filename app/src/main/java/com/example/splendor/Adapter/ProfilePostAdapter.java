package com.example.splendor.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.splendor.Models.CusDeleteDialogue;
import com.example.splendor.Models.Posts;
import com.example.splendor.PostDetailActivity;
import com.example.splendor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class ProfilePostAdapter extends RecyclerView.Adapter {
    Context context;
    List<Posts> mposts;
    String profileId;
    int PHOTO_VIEW_TYPE=1;
    int VIDEO_VIEW_TYPE= 2;
    int TEXT_VIEW_TYPE = 3;
    Posts posts;

    public ProfilePostAdapter(Context context, List<Posts> mposts, String profileId) {
        this.context = context;
        this.mposts = mposts;
        this.profileId = profileId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==PHOTO_VIEW_TYPE){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_post_item_image, parent, false);
            return new ProfilePostAdapter.PhotoViewHolder(view);
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_text_posts, parent, false);
            return new ProfilePostAdapter.TextViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder.getClass() == PhotoViewHolder.class){
            posts = mposts.get(position);
            if (posts.getType().equals("video")){

                ((PhotoViewHolder)holder).videoIndicator.setVisibility(View.VISIBLE);
                ((PhotoViewHolder)holder).imageIndicator.setVisibility(View.GONE);

            }
            else if(posts.getType().equals("photo")){
                ((PhotoViewHolder)holder).videoIndicator.setVisibility(View.GONE);
                ((PhotoViewHolder)holder).imageIndicator.setVisibility(View.VISIBLE);
            }




            if (profileId.equals(posts.getPublisherId())){
                ((PhotoViewHolder)holder).itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    Posts posts = mposts.get(position);
                    String itemPostId = posts.getPostId();
                    @Override
                    public boolean onLongClick(View view) {
                        CusDeleteDialogue deleteDialogue = new CusDeleteDialogue(context);
                        Button delete = deleteDialogue.findViewById(R.id.btnYes);
                        Button dismiss = deleteDialogue.findViewById(R.id.btnNo);
                        deleteDialogue.show();

                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference().child("Posts").child(itemPostId).removeValue();
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(posts.getImageUrl());
                                storageReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                           //write your code here
                                        }
                                        else{
                                           //write your code here
                                        }
                                    }
                                });

                                deleteDialogue.dismiss();


                            }
                        });

                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialogue.dismiss();

                            }
                        });
                        return true;
                    }
                });

            }
            if (!posts.getImageUrl().equals("default")){
                Glide.with(context).asBitmap().load(posts.getImageUrl()).placeholder(R.drawable.bg_rectangle_image).into(((PhotoViewHolder)holder).postedImage);
            }
            else{
                //Write your code here

            }



        }
        else if(holder.getClass()== TextViewHolder.class){
            Posts textPost = mposts.get(position);
            if (textPost.getType().equals("text")){
                ((TextViewHolder)holder).postText.setText(textPost.getDescription());
            }
            if (textPost.getBackgroundColor() != null){
                if (!textPost.getBackgroundColor().equals("default") && !textPost.getBackgroundColor().equals("null")){

                    ((TextViewHolder)holder).relTextPost.setBackgroundColor(Color.parseColor(textPost.getBackgroundColor()));
                    ((TextViewHolder)holder).backPlaceHolder.setVisibility(View.INVISIBLE);
                    ((TextViewHolder)holder).postText.setTextColor(Color.parseColor("#FFFFFF"));
                }
                else{

                }
            }

            if (profileId.equals(textPost.getPublisherId())){
                ((TextViewHolder)holder).postText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        CusDeleteDialogue deleteDialogue = new CusDeleteDialogue(context);
                        Button delete = deleteDialogue.findViewById(R.id.btnYes);
                        Button dismiss = deleteDialogue.findViewById(R.id.btnNo);


                        deleteDialogue.show();
                        delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                FirebaseDatabase.getInstance().getReference().child("Posts").child(textPost.getPostId()).removeValue();
                                deleteDialogue.dismiss();
                            }
                        });

                        dismiss.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                deleteDialogue.dismiss();
                            }
                        });


                        return true;
                    }
                });
            }
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Posts clickedPost = mposts.get(position);
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("postId", clickedPost.getPostId());
                intent.putExtra("publisherId", clickedPost.getPublisherId());
                intent.putExtra("description", clickedPost.getDescription());
                intent.putExtra("type", clickedPost.getType());
                intent.putExtra("imageUrl", clickedPost.getImageUrl());
                intent.putExtra("dataPosted",clickedPost.getTime());
                context.startActivity(intent);
            }
        });

    }



    @Override
    public int getItemViewType(int position) {
        posts = mposts.get(position);
        if (posts.getType().equals("photo")|| posts.getType().equals("video")){
            return PHOTO_VIEW_TYPE;
        }
        else {
            return TEXT_VIEW_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return mposts.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        ImageView postedImage, imageIndicator, videoIndicator;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            postedImage = itemView.findViewById(R.id.proflePostImage);
            imageIndicator = itemView.findViewById(R.id.imageindicator);
            videoIndicator = itemView.findViewById(R.id.videoIndicator);
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder{
        TextView postText;
        RelativeLayout relTextPost;
        ImageView backPlaceHolder;
        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            postText = itemView.findViewById(R.id.profileTextView);
            relTextPost = itemView.findViewById(R.id.relProfileTextPost);
            backPlaceHolder = itemView.findViewById(R.id.backPlaceHolder);
        }

    }
}
