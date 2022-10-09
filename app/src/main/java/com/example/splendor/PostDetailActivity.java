package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.splendor.Models.MyDialogCreator;
import com.example.splendor.Models.Posts;
import com.example.splendor.Models.Users;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

   ImageView imgLike, arrowDown, arrowUp ,postImage, imgComment, imgDownloadFromCloud;
   RelativeLayout rel1, foldedRel, expandedRel, parent, pdvParent, pdbBottom;
   TextView txtUsername, numberOfLikes, txtNumberOfComments, foldedText, expandedText, pdvTextPost, pdbImageTime;
   CircleImageView profileImage1;
   VideoView postVideo;
   LinearLayout pdbTop;
   View pdbView;
   int state;
   NestedScrollView textScroll;
   String publisherId, postId;
   SeekBar pdbSeekBar;
   ProgressBar loadingPdvProgress;
   File pathAsFile;

   Handler uiHandler;
   Runnable uiRunnable;

   DatabaseReference likesRef;
   ValueEventListener likesEventListener;

   DatabaseReference commentsRef;
   ValueEventListener commentEventListener;

   DatabaseReference usersRef;
   ValueEventListener userEventListener;

   DatabaseReference isLikedRef;
   ValueEventListener isLikedEventListener;

   DatabaseReference postsRef;
   ValueEventListener postEventListener;

   DatabaseReference checkNotificationRef;
   ValueEventListener checkNotificationEventListener;

    MyDialogCreator myDialogCreator;
    ProgressBar uploadProgressBar;
    TextView uploadPercentage;
    ImageView uploadSourceImage;
    ImageView cancelLoad;
    OnProgressListener onProgressListener;
    TextView uploadingStatus;


    @Override
    protected void onDestroy() {
        if(likesRef != null){
            likesRef.removeEventListener(likesEventListener);
        }
        if (commentsRef != null){
            commentsRef.removeEventListener(commentEventListener);
        }
        if (usersRef != null){
            usersRef.removeEventListener(userEventListener);
        }
        if (isLikedRef != null){
            isLikedRef.removeEventListener(isLikedEventListener);
        }

        if (postsRef != null){
            postsRef.removeEventListener(postEventListener);
        }
        if(checkNotificationRef != null){
            checkNotificationRef.removeEventListener(checkNotificationEventListener);
        }
        if(uiHandler != null && uiRunnable != null){
            uiHandler.removeCallbacksAndMessages(null);
            uiHandler.removeCallbacks(uiRunnable);
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_detail);
        getSupportActionBar().hide();

        imgLike = findViewById(R.id.pdbImgLike);
        rel1 = findViewById(R.id.pdbrel1);
        txtUsername = findViewById(R.id.pdbtxtUsername);
        arrowDown = findViewById(R.id.pdbarrowDown);
        arrowUp = findViewById(R.id.pdbarrowup);
        numberOfLikes = findViewById(R.id.pdbnumberOfLikes);
        txtNumberOfComments = findViewById(R.id.pdbtxtNumberOfComments);
        postImage = findViewById(R.id.pdbpostImage);
        postVideo = findViewById(R.id.pdbpostVideo);
        foldedText = findViewById(R.id.pdbfoldedText);
        foldedRel = findViewById(R.id.pdbfoldedRel);
        expandedRel = findViewById(R.id.pdbexpandedRel);
        profileImage1 = findViewById(R.id.pdbprofileImage1);
        expandedText = findViewById(R.id.pdbexpandedText);
        parent = findViewById(R.id.parent);
        pdbView = findViewById(R.id.pdbView);
        pdvParent = findViewById(R.id.pdvParent);
        pdvTextPost = findViewById(R.id.pdvtextPost);
        textScroll = findViewById(R.id.textScroll);
        pdbTop = findViewById(R.id.pdbtop);
        imgComment = findViewById(R.id.pdbcomments);
        pdbBottom = findViewById(R.id.pdbBottom);
        pdbImageTime = findViewById(R.id.pdbImageTime);
        pdbSeekBar = findViewById(R.id.pdbSeekBar);
        loadingPdvProgress = findViewById(R.id.loadingPdvProgress);
        imgDownloadFromCloud = findViewById(R.id.pdbDownloadFromCloud);



        setPdvState(2);
        state = 2;
        parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state ==2){
                    setPdvState(1);
                    state = 1;
                }
                else if(state ==1){
                    setPdvState(2);
                    state = 2;
                }

            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (state ==2){
                    setPdvState(1);
                    state = 1;
                }
                else if(state ==1){
                    setPdvState(2);
                    state = 2;
                }

            }
        });


        Intent mintent = getIntent();
         publisherId = mintent.getStringExtra("publisherId");
         postId = mintent.getStringExtra("postId");
        String imageUrl = mintent.getStringExtra("imageUrl");
        String type = mintent.getStringExtra("type");
        String description = mintent.getStringExtra("description");
        String datePosted = mintent.getStringExtra("dataPosted");



        pdbImageTime.setText(datePosted);
          isLiked(imgLike, postId);
       if (description.equals("")){
           rel1.setVisibility(View.GONE);
       }
       setstate(1, description);

       txtUsername.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
               intent.putExtra("publisherId", publisherId);
               startActivity(intent);
           }
       });


       arrowDown.setOnClickListener(view -> setstate(2, description));
       arrowUp.setOnClickListener(view -> setstate(1, description));


       likesRef = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
       likesEventListener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.getChildrenCount()==0 || snapshot.getChildrenCount()>1){
                   numberOfLikes.setText(snapshot.getChildrenCount()+" Likes");
               }
               else{
                   numberOfLikes.setText(snapshot.getChildrenCount()+" Like");

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };
       likesRef.addValueEventListener(likesEventListener);


       commentsRef = FirebaseDatabase.getInstance().getReference().child("Comments").child(postId);
       commentEventListener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (snapshot.getChildrenCount()==0 || snapshot.getChildrenCount()>1){
                   txtNumberOfComments.setText(snapshot.getChildrenCount()+" Comments");
               }
               else{
                   txtNumberOfComments.setText(snapshot.getChildrenCount()+" Comment");
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };
       commentsRef.addValueEventListener(commentEventListener);


       usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(publisherId);
       userEventListener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               Users users = snapshot.getValue(Users.class);
               txtUsername.setText(users.getUsername());
               Picasso.get().load(users.getProfileImage()).placeholder(R.drawable.ic_splendor_placeholder).into(profileImage1);
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };
       usersRef.addValueEventListener(userEventListener);

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like(imgLike, postId);
            }
        });

        if (type.equals("photo")){
            postImage.setVisibility(View.VISIBLE);
            postVideo.setVisibility(View.GONE);
            pdvTextPost.setVisibility(View.GONE);
            textScroll.setVisibility(View.GONE);
            pdbSeekBar.setVisibility(View.GONE);
            Glide.with(this).asBitmap().load(imageUrl).placeholder(R.drawable.ic_splendor_placeholder).into(postImage);

            imgDownloadFromCloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runtimePermission(imageUrl, "image");
                }
            });
        }
        else if(type.equals("video")){
            loadingPdvProgress.setVisibility(View.VISIBLE);
            loadingPdvProgress.bringToFront();
            pdbSeekBar.setProgress(0);
            uiHandler = new Handler();
            uiRunnable = new Runnable() {
                @Override
                public void run() {
                    if (postVideo != null){
                        pdbSeekBar.setProgress(postVideo.getCurrentPosition());
                    }
                    uiHandler.postDelayed(this,100);
                }
            };
            PostDetailActivity.this.runOnUiThread(uiRunnable);
            postImage.setVisibility(View.GONE);
            postVideo.setVisibility(View.VISIBLE);
            pdvTextPost.setVisibility(View.GONE);
            textScroll.setVisibility(View.GONE);
            pdbSeekBar.setVisibility(View.VISIBLE);
            rel1.bringToFront();
            rel1.setBottom(400);
            Uri uri = Uri.parse(imageUrl);
            postVideo.setVideoURI(uri);
            postVideo.start();
            postVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    loadingPdvProgress.setVisibility(View.GONE);
                    System.out.println("on prepared listener was called");
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                    pdbSeekBar.setMax(postVideo.getDuration());
                    pdbSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if(postVideo != null && b){
                                postVideo.seekTo(i);
                            }
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });
                }
            });

            imgDownloadFromCloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runtimePermission(imageUrl, "video");
                }
            });

        }
        else{
            parent.setBackgroundColor(Color.parseColor("#FFFFFF"));
            postImage.setVisibility(View.GONE);
            postVideo.setVisibility(View.GONE);
            pdvTextPost.setVisibility(View.VISIBLE);
            pdvTextPost.setText(description);
            textScroll.setVisibility(View.VISIBLE);
            rel1.setVisibility(View.GONE);
            pdbTop.setBackgroundColor(Color.parseColor("#000000"));
            txtUsername.setTextColor(Color.parseColor("#FFFFFF"));
            pdbBottom.setBackgroundColor(Color.parseColor("#000000"));
            pdbSeekBar.setVisibility(View.GONE);
            imgDownloadFromCloud.setVisibility(View.GONE);
        }

    }



    public void setstate(int state, String description){
        if (state ==1){
            foldedText.setText(description);
            foldedRel.setVisibility(View.VISIBLE);
            expandedRel.setVisibility(View.GONE);
        }
        else if(state ==2){
            expandedText.setText(description);
            foldedRel.setVisibility(View.GONE);
            expandedRel.setVisibility(View.VISIBLE);
        }

    }

    private void runtimePermission(String downloadUrl, String contentType){
        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                downloadFile(downloadUrl, contentType);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    private void downloadFile(String downloadUrl, String contentType) {

        boolean success;
        String path = Environment.getExternalStorageDirectory() + File.separator +"Splendor";
        pathAsFile = new File(path);
        if (!pathAsFile.exists()){
            success = pathAsFile.mkdir();
            System.out.println(success);
        }

        File localFile = null;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(downloadUrl);

        if (contentType != null){
            if (contentType.equals("video")){
                try {
                    localFile = File.createTempFile("Sp-VID-", ".mp4", pathAsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else if (contentType.equals("image")){
                try {
                    localFile = File.createTempFile("Sp-IMG-", ".jpg", pathAsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            myDialogCreator = new MyDialogCreator(this, R.layout.loading_layout, false, null, null);
            uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
            uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
            uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
            cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);
            uploadingStatus = myDialogCreator.findViewById(R.id.uploadingStatus);


            cancelLoad.setVisibility(View.GONE);
            myDialogCreator.show();
            Glide.with(this).asBitmap().load(downloadUrl).placeholder(R.drawable.ic_splendor_placeholder).into(uploadSourceImage);

            uploadingStatus.setText("Downloading");
            onProgressListener = new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    uploadProgressBar.setProgress((int) progress);
                    uploadPercentage.setText((int) progress + "%");
                }
            };

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    myDialogCreator.dismiss();
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    myDialogCreator.dismiss();
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(onProgressListener);
        }

    }

    public void isLiked(ImageView imageView, String postId){
        isLikedRef = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        isLikedEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()){

                    imageView.setTag("liked");
                    imgLike.setImageResource(R.drawable.ic_liked_red);
                }
                else{

                    imageView.setTag("like");
                    imgLike.setImageResource(R.drawable.ic_white_like);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
       isLikedRef.addValueEventListener(isLikedEventListener);

    }

    public void Like(ImageView imageView, String postId){
        if (imageView.getTag().equals("like")){
            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postId);
                        postEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Posts posts = snapshot.getValue(Posts.class);
                                if (posts.getType().equals("text")){
                                    checkNotification(postId, FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Likes what you said");
                                }
                                else if(posts.getType().equals("video")){
                                    checkNotification(postId, FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your video");
                                }
                                else if (posts.getType().equals("photo")){
                                    checkNotification(postId, FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your photo");
                                }
                                else{
                                    checkNotification(postId, FirebaseAuth.getInstance().getCurrentUser().getUid(), publisherId, "Liked your post");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                     postsRef.addValueEventListener(postEventListener);

                    }
                }
            });

        }
        else if(imageView.getTag().equals("liked")){
            FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
        }

    }

    public void setPdvState(int state){
        if (state == 1){
            pdbView.animate().alpha(0f);
        }
        else if(state ==2){
            pdbView.animate().alpha(1f);
        }

    }

    public boolean checkNotification(String postId, String publisherId, String userId, String text){
        checkNotificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(postId+publisherId);
        checkNotificationEventListener = new ValueEventListener() {
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
        };

      checkNotificationRef.addValueEventListener(checkNotificationEventListener);
      return true;
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
}