package com.example.splendor.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.splendor.CommentActivity;
import com.example.splendor.DetailsActivity;
import com.example.splendor.Models.Notifications;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;
import com.example.splendor.PostDetailActivity;
import com.example.splendor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firestore.v1.FirestoreGrpc;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Comment;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MPostAdapter extends RecyclerView.Adapter {
    Context context;
    List<Posts> mPosts;
    List<Users> usersList;
    //Posts posts;

    int VIDEO_VIEW_TYPE =1;
    int PHOTO_VIEW_TYPE =2;
    int TEXT_VIEW_TYPE =3;

    public MPostAdapter(Context context, List<Posts> mPosts) {
        this.context = context;
        this.mPosts = mPosts;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == PHOTO_VIEW_TYPE){
            View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
            return new PhotoViewHolder(view);

        }

        else if(viewType == VIDEO_VIEW_TYPE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);
            return new VideoViewHolder(view);

        }

        else{
            View view1= LayoutInflater.from(parent.getContext()).inflate(R.layout.text_post_item, parent, false);
            return new TextViewHolder(view1);
        }



    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (holder.getClass() == PhotoViewHolder.class){
          Posts posts = mPosts.get(position);
            isLiked(posts.getPostId(), ((PhotoViewHolder)holder).imgLike, posts.getPublisherId());
            getNumberOfComments(posts.getPostId(),((PhotoViewHolder)holder).numberOfComments);
            getLikesCount(posts.getPostId(),((PhotoViewHolder)holder).numberOfLikes);
            Glide.with(context).asBitmap().load(posts.getImageUrl()).placeholder(R.drawable.ic_splendor_placeholder).into(((PhotoViewHolder)holder).imagePosted);
            ((PhotoViewHolder)holder).pImageTime.setText(posts.getTime());
            FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getPublisherId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    ((PhotoViewHolder)holder).username.setText(user.getUsername());
                    Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.ic_splendor_placeholder).into(((PhotoViewHolder)holder).profileImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            ((PhotoViewHolder)holder).comments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Posts posts1 = mPosts.get(position);
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", posts1.getPostId());
                    intent.putExtra("publisherId", posts1.getPublisherId());
                    context.startActivity(intent);

                }
            });
            ((PhotoViewHolder)holder).username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("publisherId", posts.getPublisherId());
                    context.startActivity(intent);
                }
            });

            if (posts.getDescription().equals("")){
                ((PhotoViewHolder)holder).rel1.setVisibility(View.GONE);
            }
            else{
                ((PhotoViewHolder)holder).rel1.setVisibility(View.VISIBLE);
                ((PhotoViewHolder)holder).expandedText.setText(posts.getDescription());
                ((PhotoViewHolder)holder).foldedText.setText(posts.getDescription());
            }

            ((PhotoViewHolder)holder).arrowdown.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PhotoViewHolder)holder).setState(2);
                }
            });

            ((PhotoViewHolder)holder).arrowup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((PhotoViewHolder)holder).setState(1);
                }
            });


            ((PhotoViewHolder)holder).imgLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Posts posts1 = mPosts.get(position);
                    like(((PhotoViewHolder)holder).imgLike, posts1.getPostId(), posts.getPublisherId(), "photo");
                }
            });

            ((PhotoViewHolder)holder).imagePosted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context,((PhotoViewHolder)holder).imagePosted, "t1");
                    intent.putExtra("postId", posts.getPostId());
                    intent.putExtra("publisherId", posts.getPublisherId());
                    intent.putExtra("description", posts.getDescription());
                    intent.putExtra("type", posts.getType());
                    intent.putExtra("imageUrl", posts.getImageUrl());
                    intent.putExtra("dataPosted",posts.getTime());
                    context.startActivity(intent);

                }
            });

        }
        else if(holder.getClass() == VideoViewHolder.class){
           Posts posts = mPosts.get(position);
           // Glide.with(context).asBitmap().load(posts.getImageUrl()).placeholder(R.drawable.placeholder).into(((VideoViewHolder)holder).videoPosted);
            isLiked(posts.getPostId(), ((VideoViewHolder)holder).imgLiked1, posts.getPublisherId());
            getNumberOfComments(posts.getPostId(),((VideoViewHolder)holder).numberOfComments1);
            getLikesCount(posts.getPostId(), ((VideoViewHolder)holder).numberOfLikes1);

            FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getPublisherId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    ((VideoViewHolder)holder).username1.setText(users.getUsername());
                    Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.ic_splendor_placeholder).into(((VideoViewHolder)holder).profileImage1);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            ((VideoViewHolder)holder).imgLiked1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Posts posts1 = mPosts.get(position);
                    like(((VideoViewHolder)holder).imgLiked1, posts1.getPostId(), posts1.getPublisherId(), "video");

                }
            });


            ((VideoViewHolder)holder).comments1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Posts posts1 = mPosts.get(position);
                    Intent intent = new Intent(context, CommentActivity.class);
                    intent.putExtra("postId", posts1.getPostId());
                    intent.putExtra("publisherId", posts1.getPublisherId());
                    context.startActivity(intent);
                }
            });

            Glide.with(context).asBitmap().load(posts.getImageUrl()).placeholder(R.drawable.ic_splendor_placeholder).into(((VideoViewHolder)holder).videoPosted);
            ((VideoViewHolder)holder).pVideoTime.setText(posts.getTime());

            FirebaseDatabase.getInstance().getReference().child("Users").child(posts.getPublisherId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users user = snapshot.getValue(Users.class);
                    ((VideoViewHolder)holder).username1.setText(user.getUsername());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (posts.getDescription().equals("")){
                ((VideoViewHolder)holder).rel2.setVisibility(View.GONE);
            }
            else{
                ((VideoViewHolder)holder).rel2.setVisibility(View.VISIBLE);
                ((VideoViewHolder)holder).expandedText1.setText(posts.getDescription());
                ((VideoViewHolder)holder).foldedText1.setText(posts.getDescription());
            }

            ((VideoViewHolder)holder).arrowdown1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((VideoViewHolder)holder).setState(2);
                }
            });

            ((VideoViewHolder)holder).arrowup1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((VideoViewHolder)holder).setState(1);
                }
            });

            ((VideoViewHolder)holder).buffering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra("postId", posts.getPostId());
                    intent.putExtra("publisherId", posts.getPublisherId());
                    intent.putExtra("description", posts.getDescription());
                    intent.putExtra("type", posts.getType());
                    intent.putExtra("imageUrl", posts.getImageUrl());
                    intent.putExtra("dataPosted",posts.getTime());
                    context.startActivity(intent);
                }
            });



        }
        else if(holder.getClass() == TextViewHolder.class){
            Posts tpposts = mPosts.get(position);
            ((TextViewHolder)holder).textPost.setText(tpposts.getDescription());
            ((TextViewHolder)holder).pTextTime.setText(tpposts.getTime());
            ((TextViewHolder)holder).textPost.setSelected(true);
            System.out.println(tpposts.getBackgroundColor());
            if (tpposts.getBackgroundColor() != null){
                if (!tpposts.getBackgroundColor().equals("default") && !tpposts.getBackgroundColor().equals("null")){

                    ((TextViewHolder)holder).relTextPost.setBackgroundColor(Color.parseColor(tpposts.getBackgroundColor()));
                    ((TextViewHolder)holder).backPlaceHolderImage.setVisibility(View.INVISIBLE);
                    ((TextViewHolder)holder).textPost.setTextColor(Color.parseColor("#FFFFFF"));
                }
                else{

                }
            }
            FirebaseDatabase.getInstance().getReference().child("Users").child(tpposts.getPublisherId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    ((TextViewHolder)holder).username2.setText(users.getUsername());
                    Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.ic_splendor_placeholder).into(((TextViewHolder)holder).profileImage2);
                    isLiked(tpposts.getPostId(), ((TextViewHolder)holder).imgLike, tpposts.getPublisherId());
                    getLikesCount(tpposts.getPostId(), ((TextViewHolder)holder).tpNumberOfLikes);
                    getNumberOfComments(tpposts.getPostId(), ((TextViewHolder)holder).tpNumberOfComments);

                    ((TextViewHolder)holder).imgLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            like(((TextViewHolder)holder).imgLike, tpposts.getPostId(), tpposts.getPublisherId(),"text");
                        }
                    });

                    ((TextViewHolder)holder).imgComment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Posts posts1 = mPosts.get(position);
                            Intent intent = new Intent(context, CommentActivity.class);
                            intent.putExtra("postId", posts1.getPostId());
                            intent.putExtra("publisherId", posts1.getPublisherId());
                            context.startActivity(intent);

                        }
                    });

                    ((TextViewHolder)holder).textPost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(context, PostDetailActivity.class);
                            intent.putExtra("postId", tpposts.getPostId());
                            intent.putExtra("publisherId", tpposts.getPublisherId());
                            intent.putExtra("description", tpposts.getDescription());
                            intent.putExtra("type", tpposts.getType());
                            intent.putExtra("imageUrl","default");
                            intent.putExtra("dataPosted",tpposts.getTime());
                            context.startActivity(intent);

                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }




    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public int getItemViewType(int position) {

        Posts posts = mPosts.get(position);
        if (posts.getType().equals("photo")){
            return PHOTO_VIEW_TYPE;
        }
        else if (posts.getType().equals("video")){
            return VIDEO_VIEW_TYPE;
        }
        else{
            return TEXT_VIEW_TYPE;
        }
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage;
        ImageView imagePosted, arrowdown, arrowup, imgLike, comments ;
        TextView username, numberOfLikes, numberOfComments, expandedText, foldedText, pImageTime;
        RelativeLayout expandedRel, foldedRel, rel1;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            imagePosted = itemView.findViewById(R.id.postImage);
            username = itemView.findViewById(R.id.txtUsername);
            numberOfLikes = itemView.findViewById(R.id.numberOfLikes);
            numberOfComments = itemView.findViewById(R.id.txtNumberOfComments);
            profileImage = itemView.findViewById(R.id.profileImage1);
            arrowup = itemView.findViewById(R.id.arrowup);
            arrowdown = itemView.findViewById(R.id.arrowDown);
            expandedRel = itemView.findViewById(R.id.expandedRel);
            foldedRel = itemView.findViewById(R.id.foldedRel);
            expandedText = itemView.findViewById(R.id.expandedText);
            foldedText = itemView.findViewById(R.id.foldedText);
            rel1 = itemView.findViewById(R.id.rel1);
            imgLike = itemView.findViewById(R.id.imgLike);
            comments = itemView.findViewById(R.id.comments);
            pImageTime = itemView.findViewById(R.id.pImageTime);


        }
        public void setState(int state){
        if (state==1){
            expandedRel.setVisibility(View.GONE);
            foldedRel.setVisibility(View.VISIBLE);
        }
        else if (state==2){
            expandedRel.setVisibility(View.VISIBLE);
            foldedRel.setVisibility(View.GONE);
        }
        }
    }


    public class VideoViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImage1;
        ImageView  arrowdown1, arrowup1, buffering, imgLiked1, comments1;
        ImageView videoPosted;
        TextView username1, numberOfLikes1, numberOfComments1, expandedText1, foldedText1, pVideoTime;
        RelativeLayout expandedRel1, foldedRel1, rel2;
        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage1 = itemView.findViewById(R.id.profileImage2);
            videoPosted = itemView.findViewById(R.id.postVideo);
            arrowdown1 = itemView.findViewById(R.id.arrowDown1);
            arrowup1 = itemView.findViewById(R.id.arrowup1);
            username1 = itemView.findViewById(R.id.txtUsername1);
            numberOfLikes1 = itemView.findViewById(R.id.numberOfLikes1);
            numberOfComments1 = itemView.findViewById(R.id.txtNumberOfComments1);
            expandedText1 = itemView.findViewById(R.id.expandedText1);
            expandedRel1 = itemView.findViewById(R.id.expandedRel1);
            foldedText1 = itemView.findViewById(R.id.foldedText1);
            foldedRel1 = itemView.findViewById(R.id.foldedRel1);
            rel2  = itemView.findViewById(R.id.rel2);
            buffering = itemView.findViewById(R.id.bufferimg);
            imgLiked1 = itemView.findViewById(R.id.imgLike1);
            comments1 = itemView.findViewById(R.id.comments1);
            pVideoTime = itemView.findViewById(R.id.pVideoTime);

        }



        public void setState(int state){
            if (state==1){
                expandedRel1.setVisibility(View.GONE);
                foldedRel1.setVisibility(View.VISIBLE);
            }
            else if (state==2){
                expandedRel1.setVisibility(View.VISIBLE);
                foldedRel1.setVisibility(View.GONE);
            }
        }


    }

    public class TextViewHolder extends RecyclerView.ViewHolder{
        TextView textPost, username2, tpNumberOfLikes, tpNumberOfComments, pTextTime;
        CircleImageView profileImage2;
        ImageView imgLike, imgComment, backPlaceHolderImage;
        RelativeLayout relTextPost;
        public TextViewHolder(@NonNull View itemView) {
            super(itemView);
            textPost = itemView.findViewById(R.id.textPost);
            username2 = itemView.findViewById(R.id.txtUsername2);
            profileImage2 = itemView.findViewById(R.id.profileImage3);
            imgLike = itemView.findViewById(R.id.tpImgLike);
            imgComment = itemView.findViewById(R.id.tpImgComment);
            tpNumberOfLikes= itemView.findViewById(R.id.tpnumberOfLikes1);
            tpNumberOfComments = itemView.findViewById(R.id.tptxtNumberOfComments1);
            pTextTime = itemView.findViewById(R.id.pTextTime);
            relTextPost = itemView.findViewById(R.id.relTextPost);
            backPlaceHolderImage = itemView.findViewById(R.id.backgroundPlaceholderImage);

        }
    }

    public void like(ImageView imageView, String postId, String publisherId, String type){
        if(imageView.getTag().equals("like")){
            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).child(FirebaseAuth.getInstance().getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        switch (type){
                            case "photo":
                                checkNotification(postId,FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your photo" );
                                break;
                            case "video":
                                checkNotification(postId,FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your video" );
                                break;
                            case "text":
                                checkNotification(postId, FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Likes what you said");break;
                            default:
                                checkNotification(postId,FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your post" );
                                break;

                        }

                    }
                }
            });

        }
        else {

            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).child(FirebaseAuth.getInstance().getUid()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        checkNotification(postId,FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your post" );
                        removeNotificaition(publisherId,postId,FirebaseAuth.getInstance().getCurrentUser().getUid());
                    }
                }
            });
            
        }


    }
    public void isLiked(String postId, ImageView imageView , String publisherId){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(FirebaseAuth.getInstance().getUid()).exists()){
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

    public void getLikesCount(String postId, TextView numberOfLikes){
      FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot) {
              if (snapshot.getChildrenCount()==0 || snapshot.getChildrenCount()>1){
                  numberOfLikes.setText(""+snapshot.getChildrenCount()+ " Likes");
              }
              else{
                  numberOfLikes.setText(""+snapshot.getChildrenCount()+ " Like");
              }

          }

          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });
    }
    public void getNumberOfComments(String postId, TextView numText){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount()==0 || snapshot.getChildrenCount()>1){
                    numText.setText(""+snapshot.getChildrenCount()+ " Comments");
                }
                else{
                    numText.setText(""+snapshot.getChildrenCount() + " Comment");
                }

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(postId+publisherId);
        ref.setValue(map);
    }
    public boolean checkNotification(String postId, String publisherId, String userId, String text){

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(postId+publisherId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                }
                else{

                    addNotification(postId,text, publisherId, userId );

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return true;
       
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        if (this.context != null){
            this.context = null;
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }

    public void removeNotificaition(String userId, String postId, String publisherId){
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(postId+publisherId).removeValue();
    }



}
