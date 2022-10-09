package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.splendor.databinding.ActivityRegister3Binding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity3 extends AppCompatActivity {
    ActivityRegister3Binding b;
    Uri imageUri;
    FirebaseAuth mAuth;
    ProgressDialog pd;
    String  profilePicDownloadUrl;
    FirebaseDatabase firebaseDatabase;
    StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegister3Binding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        b.txtBtnNext.setVisibility(View.GONE);
        mAuth= FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        String fullname = intent.getStringExtra("fullname");
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        String email = intent.getStringExtra("email");
        String dateofbirth = intent.getStringExtra("dateofbirth");
        int yearofbirth = intent.getIntExtra("yearofbirth",0);
        String phonenumber = intent.getStringExtra("phonenumber");

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);
        builder.setMessage("A profile picture will help friends  identify you");
        builder.setPositiveButton("Skip Anyway", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("fullname", fullname);
                map.put("username", username);
                map.put("password", password);
                map.put("email", email);
                map.put("profileImage", "default");
                map.put("bio", "");
                map.put("grandpage", "false");
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("dateOfBirth", dateofbirth);
                map.put("yearOfBirth", yearofbirth);
                map.put("phoneNumber", phonenumber);
                addUser(map, username, email);


            }
        });



        b.txtBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 builder.show();


            }
        });

        b.imgBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity().start(RegisterActivity3.this);
            }
        });
        b.txtBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("fullname", fullname);
                map.put("username", username);
                map.put("password", password);
                map.put("email", email);
                map.put("profileImage", profilePicDownloadUrl);
                map.put("bio","");
                map.put("grandpage","false");
                map.put("id", mAuth.getCurrentUser().getUid());
                map.put("dateOfBirth", dateofbirth);
                map.put("yearOfBirth", yearofbirth);
                map.put("phoneNumber", phonenumber);
                addUser(map, username, email);

                
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode ==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null){
                b.txtBtnNext.setVisibility(View.VISIBLE);
                imageUri = result.getUri();
                b.profileImage.setImageURI(imageUri);
                upload();
            }
            else{
                Toast.makeText(RegisterActivity3.this, "you have not selected any images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void upload() {
        pd.setMessage("Uploading..");
        pd.show();

        if (imageUri != null){
            StorageReference ref = FirebaseStorage.getInstance().getReference().child("profileImage").child(FirebaseAuth.getInstance().getUid());
         uploadTask =   ref.putFile(imageUri);
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
         }).addOnCompleteListener(new OnCompleteListener<Uri>() {
             @Override
             public void onComplete(@NonNull Task<Uri> task) {
                 Uri uri = task.getResult();
                 profilePicDownloadUrl = uri.toString();

              pd.dismiss();
                 Toast.makeText(RegisterActivity3.this, "Success!!, you can now proceed with your registration", Toast.LENGTH_SHORT).show();

             }
         });

        }
        else{
            //This would happen if the image uri was null
        }
    }

    private void addUser(Map map, String name, String email){
        firebaseDatabase.getReference().child("Users").child(mAuth.getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    sendVerificationEmail(name, email);
                }
                else{
                    Toast.makeText(RegisterActivity3.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendVerificationEmail(String name, String email){
        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity3.this);
                builder.setMessage("Hello "+ name + " To prevent fake user account registration we sent an email to " + email+ " Click the link in the email to verify your email address, if you cant find our mail then try checking your spam folder");
                builder.setPositiveButton("OK I WILL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(RegisterActivity3.this, WelcomePageActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("email", email);
                        startActivity(intent);
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
    }


}