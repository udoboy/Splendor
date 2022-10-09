package com.example.splendor;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.splendor.Adapter.ColorAdapter;
import com.example.splendor.Adapter.ColorClickedListener;
import com.example.splendor.Models.ChoosePostDialog;
import com.example.splendor.Models.CustomProgressDialogue;
import com.example.splendor.Models.MyDialogCreator;
import com.example.splendor.databinding.ActivityPostBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PostActivity extends AppCompatActivity implements ColorClickedListener {
    ActivityPostBinding b;
    Uri imageUri;
    boolean isPhoto;
    ProgressDialog pd;
    String imageUrl;
    Uri videoUri;
    MediaController mediaController;
    String videoUrl;
    boolean istext;
    List<String> idList;
    ChoosePostDialog choosePostDialog;
    RelativeLayout pImage, pVideo, pText, cancel;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    String timedetail;
    OnProgressListener onProgressListener;
    MyDialogCreator myDialogCreator;
    ProgressBar uploadProgressBar;
    TextView uploadPercentage;
    ImageView uploadSourceImage;
    ImageView cancelLoad;
    StorageTask uploadTask;
    OnCompleteListener onUploadComplete;
    DatabaseReference ref;
    OnFailureListener onFailureListener ;
    ConnectivityManager cm;
    NetworkInfo ni;
    CountDownTimer countDownTimer;
    ColorAdapter colorAdapter;
    String currentBackground;
    List<String> colorList;
    String currentColor;

    DatabaseReference followersRef;
    ValueEventListener followersEventListener;

    @Override
    protected void onDestroy() {
        pd = null;

        if(followersRef != null){
            followersRef.removeEventListener(followersEventListener);
        }
        choosePostDialog = null;
        if(uploadTask != null){
            uploadTask.removeOnProgressListener(onProgressListener);
            uploadTask.removeOnCompleteListener(onUploadComplete);
        }
        if(ref != null){

        }
        mediaController.setAnchorView(null);
        mediaController= null;
        if (countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        navigateToHome();
       // super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPostBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        pd = new ProgressDialog(this);
        mediaController = new MediaController(this);
        istext = false;
        idList = new ArrayList<>();
        choosePostDialog = new ChoosePostDialog(this);

        cancel = choosePostDialog.findViewById(R.id.cancel);
        pImage= choosePostDialog.findViewById(R.id.pImage);
        pText = choosePostDialog.findViewById(R.id.pText);
        pVideo = choosePostDialog.findViewById(R.id.pVideo);


        //setup colorlist
        colorList  = new ArrayList<>();
        colorAdapter = new ColorAdapter(colorList, null, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        b.colorRec.setLayoutManager(gridLayoutManager);
        b.colorRec.setAdapter(colorAdapter);

        choosePostDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                navigateToHome();
            }
        });
        choosePostDialog.show();

        pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.imgAdded.setVisibility(View.VISIBLE);
                CropImage.activity().start(PostActivity.this);
                b.relPhotoPost.setVisibility(View.VISIBLE);
                isPhoto = true;
                choosePostDialog.dismiss();
                b.lChooseBackground.setVisibility(View.GONE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();

            }
        });

        b.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
            }
        });

        b.txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPhoto == true){
                    if (isOnline()){
                        uploadImage(imageUri);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please connect your device to the internet and try again", Toast.LENGTH_SHORT).show();
                    }

                }
                else if (istext == true){
                    String text = b.edtPostText.getText().toString();
                    if (text.isEmpty()){
                        Toast.makeText(getApplicationContext(), "Please enter a text", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if (isOnline()){
                            uploadText(text);
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Please connect your device to the internet and try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
                else{
                    if (isOnline()){
                        uploadVideo(videoUri);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Please connect your device to the internet and try again", Toast.LENGTH_SHORT).show();
                    }


                }
            }
        });

        pText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                istext = true;
                b.videpAdded.setVisibility(View.GONE);
                b.imgAdded.setVisibility(View.GONE);
                b.edtDescription.setVisibility(View.GONE);
                b.edtPostText.setVisibility(View.VISIBLE);
                b.edtDescription.setVisibility(View.VISIBLE);
                b.relPhotoPost.setVisibility(View.VISIBLE);
                choosePostDialog.dismiss();
                b.lChooseBackground.setVisibility(View.VISIBLE);
                initBackgrounds();
            }
        });

        pVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPhoto = false;
                b.videpAdded.setVisibility(View.VISIBLE);
                b.relPhotoPost.setVisibility(View.VISIBLE);
                Intent openVideo = new Intent();
                openVideo.setType("video/*");
                openVideo.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(openVideo, 1);
                choosePostDialog.dismiss();
                b.lChooseBackground.setVisibility(View.GONE);
            }
        });

    }
    public void initBackgrounds(){
        b.colorRec.setVisibility(View.GONE);
        populateColorList();
        currentBackground = "default";
        currentColor = "#F6266C";
        b.lDefaultBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               b.rDefaultSelected.setVisibility(View.VISIBLE);
               b.rSolidSelected.setVisibility(View.GONE);
               currentBackground = "default";
               b.colorRec.setVisibility(View.GONE);
            }
        });

        b.lSolid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.rDefaultSelected.setVisibility(View.GONE);
                b.rSolidSelected.setVisibility(View.VISIBLE);
                onColorClicked(currentColor);
                b.colorRec.setVisibility(View.VISIBLE);
            }
        });
    }

    public void populateColorList(){
        colorList.add("#FF3300");
        colorList.add("#FF8400");
        colorList.add("#FFD500");
        colorList.add("#88FF00");
        colorList.add("#00C4FF");
        colorList.add("#001AFF");
        colorList.add("#A600FF");
        colorList.add("#FF0015");
        colorList.add("#422C3F");
        colorList.add("#D32F2F");
        colorList.add("#388E3C");
        colorList.add("#00796B");
        colorList.add("#00C853");
        colorList.add("#F6266C");
        colorAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
           // b.imgAdded.setImageURI(imageUri);
            Glide.with(getApplicationContext()).load(imageUri).override(320,210).into(b.imgAdded);

        }
        else if(requestCode == 1){
            if (data!= null ){
                b.videpAdded.setMediaController(mediaController);
                mediaController.setAnchorView(b.videpAdded);
                videoUri = data.getData();
                b.videpAdded.setVideoURI(videoUri);
                b.videpAdded.start();
                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
            }
            else{
             navigateToHome();
            }
            

        }
        else{
            navigateToHome();
        }
    }

    public void uploadImage(Uri uri){
        myDialogCreator = new MyDialogCreator(PostActivity.this, R.layout.loading_layout, true, null, null);
        uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
        uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);

        onProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                uploadProgressBar.setProgress((int) progress);
                uploadPercentage.setText((int) progress + "%");
            }
        };
        myDialogCreator.show();
        String description = b.edtDescription.getText().toString();

        if (imageUri != null){
            uploadSourceImage.setImageURI(imageUri);
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("Posts").child(System.currentTimeMillis() +"."+ getExtention(imageUri));
             uploadTask = ref.putFile(imageUri);
             onUploadComplete = new OnCompleteListener<Uri>() {
                 @Override
                 public void onComplete(@NonNull Task<Uri> task) {
                     if (!uploadTask.isCanceled()){
                         if(task.getResult() != null){
                             Uri downloadUri = task.getResult();
                             imageUrl = downloadUri.toString();
                             DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
                             String postId = ref.push().getKey();
                             HashMap<String, Object> map = new HashMap<>();
                             map.put("publisherId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                             map.put("imageUrl", imageUrl);
                             map.put("description", description);
                             map.put("postId", postId);
                             map.put("type", "photo");
                             map.put("time", getToday());
                             map.put("backgroundColor", currentBackground);

                             ref.child(postId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         followersRef = FirebaseDatabase.getInstance().getReference().child("Follow")
                                                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers");
                                     followersEventListener = new ValueEventListener() {
                                         @Override
                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                             for (DataSnapshot snapshot1: snapshot.getChildren()){
                                                 addNotification(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Added a new post", postId, snapshot1.getKey());
                                             }
                                         }

                                         @Override
                                         public void onCancelled(@NonNull DatabaseError error) {

                                         }
                                     };
                                     followersRef.addValueEventListener(followersEventListener);
                                     }
                                 }
                             });

                             DatabaseReference mHashtagRefs = FirebaseDatabase.getInstance().getReference().child("Hashtags");
                             List<String> hashtags = b.edtDescription.getHashtags();
                             if (!hashtags.isEmpty()){
                                 for (String tags : hashtags){
                                     map.clear();
                                     map.put("tag", tags.toLowerCase());
                                     map.put("postId", postId);

                                     mHashtagRefs.child(tags.toLowerCase()).setValue(map);


                                 }
                             }
                             Toast.makeText(getApplicationContext(), "Upload success", Toast.LENGTH_SHORT).show();
                            navigateToHome();
                         }
                         else{
                             navigateToHome();
                         }
                     }

                 }
             };
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(onUploadComplete);
            uploadTask.addOnProgressListener(onProgressListener);

            cancelLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadTask.cancel();
                    myDialogCreator.dismiss();

                }
            });

        }
        else {
            Toast.makeText(getApplicationContext(), "Please select a picture", Toast.LENGTH_SHORT).show();

        }
    }

    public Bitmap getVideoThumbNail(){
         MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
         mediaMetadataRetriever.setDataSource(this, videoUri);
         Bitmap imageBitmap = mediaMetadataRetriever.getFrameAtTime();
         return imageBitmap;

    }

    public void navigateToHome(){
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.putExtra("resolveToHome", true);
        startActivity(intent);
        finish();
    }

    public void uploadVideo(Uri videoUri){
        myDialogCreator = new MyDialogCreator(this, R.layout.loading_layout, false, null, null);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
        uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);
        String description = b.edtDescription.getText().toString();

        onProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                uploadProgressBar.setProgress((int) progress);
                uploadPercentage.setText((int) progress + "%");
            }
        };

     if(videoUri != null){
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("Posts").child(System.currentTimeMillis()+"."+getExtention(videoUri));
        uploadSourceImage.setImageBitmap(getVideoThumbNail());
        myDialogCreator.show();
        StorageTask uploadTask = ref.putFile(videoUri);
        onUploadComplete = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (!uploadTask.isCanceled()){
                    if (task.getResult() != null){
                        Uri uri = task.getResult();
                        videoUrl = uri.toString();
                        DatabaseReference ref =FirebaseDatabase.getInstance().getReference().child("Posts");
                        String postId = ref.push().getKey();
                        HashMap<String, Object> map = new HashMap();
                        map.put("publisherId", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("imageUrl", videoUrl);
                        map.put("description", description);
                        map.put("postId", postId);
                        map.put("type", "video");
                        map.put("time", getToday());
                        map.put("backgroundColor", currentBackground);

                        ref.child(postId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    followersRef = FirebaseDatabase.getInstance().getReference().child("Follow")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers");

                                    followersEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot snapshot1: snapshot.getChildren()){
                                                addNotification(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Added a new post", postId, snapshot1.getKey());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    };

                                    followersRef.addValueEventListener(followersEventListener);
                                }
                            }
                        });


                        DatabaseReference mHashtagRefs = FirebaseDatabase.getInstance().getReference().child("Hashtags");
                        List<String> hashtags = b.edtDescription.getHashtags();
                        if (!hashtags.isEmpty()){
                            for (String tags : hashtags){
                                map.clear();
                                map.put("tag", tags.toLowerCase());
                                map.put("postId", postId);

                                mHashtagRefs.child(tags.toLowerCase()).setValue(map);


                            }
                        }

                        Toast.makeText(PostActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    }

                }
            }
        };
        uploadTask.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(onUploadComplete);

        uploadTask.addOnProgressListener(onProgressListener);
        cancelLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadTask.cancel();
                myDialogCreator.dismiss();
            }
        });
    }



    }

    public void uploadText(String text){
        countDownTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long l) {
                System.out.println(TimeUnit.MILLISECONDS.toSeconds(l));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Session timeout please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                myDialogCreator.dismiss();
                ref = null;
                navigateToHome();

            }

        };
        countDownTimer.start();
        myDialogCreator = new MyDialogCreator(this, R.layout.loading_layout, false, null, null);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
        uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);
        uploadProgressBar.setIndeterminate(true);
        uploadPercentage.setVisibility(View.GONE);

        if (text != null){
           myDialogCreator.show();
            onProgressListener = new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    uploadProgressBar.setProgress((int) progress);
                }
            };

            ref = FirebaseDatabase.getInstance().getReference().child("Posts");
            String postId = ref.push().getKey();
            HashMap<String, Object> map = new HashMap();
            map.put("publisherId", FirebaseAuth.getInstance().getCurrentUser().getUid());
            map.put("imageUrl", "default");
            map.put("description", text);
            map.put("postId", postId);
            map.put("type", "text");
            map.put("time", getToday());
            map.put("backgroundColor", currentBackground);
            cancelLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialogCreator.dismiss();
                    ref = null;
                   navigateToHome();
                }
            });

            onFailureListener = new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    myDialogCreator.dismiss();
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                   navigateToHome();
                }
            };

            onUploadComplete = new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        myDialogCreator.dismiss();
                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_SHORT).show();
                        navigateToHome();


                        followersRef = FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers");

                        followersEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snapshot1: snapshot.getChildren()){
                                    addNotification(FirebaseAuth.getInstance().getCurrentUser().getUid(), "Added a new post", "null", snapshot1.getKey());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };

                        followersRef.addValueEventListener(followersEventListener);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            };

            ref.child(postId).setValue(map).addOnCompleteListener(onUploadComplete).addOnFailureListener(onFailureListener);

            DatabaseReference mHashtagRefs = FirebaseDatabase.getInstance().getReference().child("Hashtags");
            List<String> hashtags = b.edtDescription.getHashtags();
            if (!hashtags.isEmpty()){
                for (String tags : hashtags){
                    map.clear();
                    map.put("tag", tags.toLowerCase());
                    map.put("postId", postId);
                    mHashtagRefs.child(tags.toLowerCase()).setValue(map);
                }
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Error %e", Toast.LENGTH_SHORT).show();
        }




    }

    private String getExtention(Uri imageUri) {
          return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }


    public void addNotification(String publisherId, String text, String postId, String userId){
        HashMap<String, Object> map = new HashMap<>();
        map.put("publisherId", publisherId );
        map.put("description", text);
        map.put("postId", postId);
        map.put("ntype", "post");

        FirebaseDatabase.getInstance().getReference().child("Notifications").child(userId).child(publisherId+"posted"+postId).setValue(map);
    }

    public String getToday(){
        simpleDateFormat = new SimpleDateFormat("HH:mm");
       calendar = Calendar.getInstance();
       int month = calendar.get(Calendar.MONTH);
       month = month +1;
       int day = calendar.get(Calendar.DAY_OF_MONTH);
       timedetail = simpleDateFormat.format(calendar.getTime());
       return day + " " + getMonthFormat(month) + "_" +  timedetail;

    }

    private boolean isOnline()  {
        cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        ni = cm.getActiveNetworkInfo();
        return (ni != null && ni.isConnected());
    }

    public String getMonthFormat(int month){
        if(month ==1){
            return "Jan";
        }
        if (month == 2){
            return "Feb";
        }
        if (month == 3){
            return "Mar";
        }
        if (month == 4){
            return "Apr";
        }
        if (month == 5){
            return "May";
        }
        if (month == 6){
            return "Jun";
        }
        if (month == 7){
            return "July";
        }
        if (month == 8){
            return "Aug";
        }
        if (month == 9){
            return "Sep";
        }
        if (month == 10) {

            return "Oct";
        }
        if (month == 11){
            return "Nov";
        }
        if (month == 12) {
            return "Dec";
        }
        return "Jan";
    }

    @Override
    public void onColorClicked(String color) {
        b.solidDisplay.setBackgroundColor(Color.parseColor(color));
        currentBackground = color;
        currentColor = color;
    }
}