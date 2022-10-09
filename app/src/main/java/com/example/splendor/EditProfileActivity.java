package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.helper.widget.Layer;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.splendor.Models.CustomProgressDialogue;
import com.example.splendor.Models.MyDialogCreator;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityEditProfileBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    ActivityEditProfileBinding b;

    Uri imageUri;
    
    ProgressDialog pd;
    String newProfileUrl;
    CustomProgressDialogue mypd;
    MyDialogCreator myDialogCreator;
    ChooseDialog chooseDialog;

    ProgressBar uploadProgressBar;
    TextView uploadPercentage, uploadingStatus;
    ImageView uploadSourceImage;
    ImageView cancelLoad;


    //firebase variables
    OnProgressListener onUploadProgress;
    FirebaseAuth mAuth;
    OnCompleteListener onUploadComplete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
       
        pd = new ProgressDialog(this);
        mypd = new CustomProgressDialogue(this);
         chooseDialog = new ChooseDialog(this);
        



        b.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        b.txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            mAuth.signOut();
            startActivity(new Intent(EditProfileActivity.this, MainActivity.class));
            }
        });

        b.btnChangeProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(EditProfileActivity.this);
            }
        });
        b.close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                b.edtUserName.setText(user.getUsername());
                b.edtBio.setText(user.getBio());
                Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.my_user_placeholder).into(b.profileImage);
                b.edtFullName.setText(user.getFullname());
                if (user.getGrandpage().equals("false")){
                    b.txtEnableGrandpage.setText("Enable Grandpage");

                }
                else{
                    b.txtEnableGrandpage.setText("Disable Grandpage");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        b.txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload();


            }
        });

        b.txtEnableGrandpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (b.txtEnableGrandpage.getText().toString().equals("Enable Grandpage")){
//                    Intent intent = new Intent(EditProfileActivity.this, EnableGrandpageActivity.class);
//                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "This feature is not available yet", Toast.LENGTH_SHORT).show();
                    
                }
                else if (b.txtEnableGrandpage.getText().toString().equals("Disable Grandpage")){
                    FirebaseDatabase.getInstance().getReference().child("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("grandpage").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(EditProfileActivity.this, "Grandpage disabled", Toast.LENGTH_SHORT).show();
                                //todo: show the user a dialog to make sure of his decision before disabling the grandpage
                                //todo: find a way to remove the whole grandpage from the server
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });
    }

    public void updateData(){
        uploadingStatus.setText("Saving..");
        HashMap<String, Object> map = new HashMap<>();
        String username = b.edtUserName.getText().toString();
        String fullname = b.edtFullName.getText().toString();
        String bio = b.edtBio.getText().toString();
        map.put("username", username);
        map.put("bio", bio);
        map.put("fullname", fullname);
        if (newProfileUrl!= null){
            map.put("profileImage", newProfileUrl);
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Users users = snapshot.getValue(Users.class);
                    map.put("profileImage", users.getProfileImage());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Profile updated", Toast.LENGTH_SHORT).show();
                    myDialogCreator.dismiss();
                    imageUri = null;

                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    myDialogCreator.dismiss();

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            b.profileImage.setImageURI(imageUri);


        }
    }

    public void upload(){
        myDialogCreator = new MyDialogCreator(this, R.layout.loading_layout, false, null, null);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        uploadProgressBar = myDialogCreator.findViewById(R.id.llLoadingProgress);
        uploadPercentage = myDialogCreator.findViewById(R.id.lltxtShowProgress);
        uploadSourceImage = myDialogCreator.findViewById(R.id.imgLoading);
        cancelLoad = myDialogCreator.findViewById(R.id.cancelLoading);
        uploadingStatus = myDialogCreator.findViewById(R.id.uploadingStatus);
        myDialogCreator.show();
        onUploadProgress = new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                uploadProgressBar.setProgress((int) progress);
                uploadPercentage.setText((int) progress + "%");
            }
        };

        onUploadComplete = new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Uri uri = task.getResult();
                newProfileUrl = uri.toString();
                updateData();
            }
        };
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImage").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        if (imageUri!= null){
            StorageTask uploadTask = ref.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    else{
                        return ref.getDownloadUrl();
                    }
                }
            }).addOnCompleteListener(onUploadComplete);

            uploadTask.addOnProgressListener(onUploadProgress);


        }
        else{
            //Toast.makeText(getApplicationContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            updateData();
        }


    }

    private class ChooseDialog extends Dialog {

        public ChooseDialog(@NonNull Context context) {
            super(context);

            WindowManager.LayoutParams params = getWindow().getAttributes();
            getWindow().setAttributes(params);

            setTitle(null);
            setCancelable(true);
            View view = LayoutInflater.from(context).inflate(R.layout.enable_grandpage_dialog, null);
            setContentView(view);
        }
    }

}