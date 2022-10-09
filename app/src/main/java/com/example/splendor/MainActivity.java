package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import com.example.splendor.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.ListenerRegistration;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding b;
    FirebaseAuth mAuth;
    FirebaseUser firebaseuser;
    OnCompleteListener onReloadComplete;
    TranslateAnimation animation;
    AlertDialog.Builder builder;

    CountDownTimer countDownTimer;
    @Override
    protected void onDestroy() {
        onReloadComplete = null;
        animation.setAnimationListener(null);
        builder = null;
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        System.out.println("on result permmison was called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        firebaseuser= mAuth.getCurrentUser();

        b.loadingProgress.setVisibility(View.VISIBLE);

        animation = new TranslateAnimation(0,0,0,0);
        animation.setDuration(1000);
        animation.setAnimationListener(new myAnimation());
        b.imgSplendorText.setAnimation(animation);
        animation.start();

    }

    public class myAnimation implements Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (firebaseuser != null){
                if (!firebaseuser.isEmailVerified()){
                    onReloadComplete  = new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                if (firebaseuser.isEmailVerified()){
                                    signInUser();
                                    b.loadingProgress.setVisibility(View.GONE);
                                }
                                else{
                                    b.loadingProgress.setVisibility(View.GONE);
                                    builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("The Splendor account registered on this device has been signed out, this might be due to an unverified email address, please verify your email and try logging in ");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            startActivity(new Intent(getApplicationContext(), ReDirectActivity.class));
                                        }
                                    });

                                    builder.show();
                                }
                            }


                        }
                    };
                    firebaseuser.reload().addOnCompleteListener(onReloadComplete);


                }
                else{
                    signInUser();
                    b.loadingProgress.setVisibility(View.GONE);
                }
            }
            else{
                startActivity(new Intent(getApplicationContext(), ReDirectActivity.class));
                b.loadingProgress.setVisibility(View.GONE);
            }

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    public void signInUser(){
        Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void runtimePermission(){
        Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                System.out.println("on permission checked was called");

            }


            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }
}