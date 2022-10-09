package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityWelcomePageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WelcomePageActivity extends AppCompatActivity {
    ActivityWelcomePageBinding b;
    FirebaseAuth mAuth;
    String name, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityWelcomePageBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        mAuth = FirebaseAuth.getInstance();
        b.txtOpenGmail.setVisibility(View.GONE);
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    if (!mAuth.getCurrentUser().isEmailVerified()){
                        b.txtOpenGmail.setVisibility(View.VISIBLE);
                        b.txtHello.setText("Hello " + name + " your splendor account has been created successfully, please ensure to verify your email address by clicking in the link we sent to " + email + " ,an unverified email address will not be permitted to use Splendor We hope you enjoy your time with us ");

                    }
                    else{
                        b.txtHello.setText("Hello " + name + " your Splendor account has been created successfully, proceed to start using splendor");
                        b.txtOpenGmail.setVisibility(View.GONE);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "There was a problem reloading your account", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        email = intent.getStringExtra("email");





        FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
                b.txtHello.setText("Hello " + users.getUsername() + " to prevent fake user account registration, we have sent an email to verify your email address, click the link on your email address and press continue to proceed ");
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        


        b.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
                finish();
                startActivity(intent);
            }
        });

        b.txtOpenGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                startActivity(intent);
            }
        });



    }
}