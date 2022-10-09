package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.splendor.Models.CustomProgressDialogue;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityEnableGrandpageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EnableGrandpageActivity extends AppCompatActivity {
    ActivityEnableGrandpageBinding b;
    CustomProgressDialogue pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityEnableGrandpageBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        pd = new CustomProgressDialogue(this);

        b.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        b.btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("grandpage").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            pd.dismiss();
                            Intent intent = new Intent(EnableGrandpageActivity.this, GrandpageActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(EnableGrandpageActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                        }
                    }

                });
            }
        });
    }
}