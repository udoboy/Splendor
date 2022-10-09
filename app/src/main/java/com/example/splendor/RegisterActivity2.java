package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.splendor.databinding.ActivityRegister2Binding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity2 extends AppCompatActivity {
    ActivityRegister2Binding b;
    ProgressDialog pd;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    String fullname;
    String username;
    String dateofbirth;
    int yearofbirth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityRegister2Binding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        pd = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        fullname = intent.getStringExtra("fullname");
         username = intent.getStringExtra("username");

         dateofbirth = intent.getStringExtra("dateofbirth");
         yearofbirth = intent.getIntExtra("yearofbirth", 0);


        b.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // confirming that no field is left empty
                String password = b.edtPassword.getText().toString();
                String email = b.edtEmail.getText().toString();
                 String confirmPassword = b.edtConfirmPassword.getText().toString();
                if (!password.isEmpty() && !confirmPassword.isEmpty() && !email.isEmpty()){
                    if (password.equals(confirmPassword)){
                       b.s1.setVisibility(View.GONE);
                       b.s2.setVisibility(View.VISIBLE);
                       b.btnPhoneNext.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               if (!b.edtPhone.getText().toString().isEmpty()){
                                   String phonenumber = b.edtPhone.getText().toString();
                                   createUser(password, email, phonenumber);
                               }
                               else{
                                   Toast.makeText(RegisterActivity2.this, "Please input a phone number", Toast.LENGTH_SHORT).show();
                               }
                           }
                       });
                    }else{
                        Toast.makeText(RegisterActivity2.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(RegisterActivity2.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void createUser(String password, String email, String phonenumber){
        pd.setMessage("Creating Account");
        pd.show();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    pd.dismiss();
                    Intent intent1 = new Intent(RegisterActivity2.this, RegisterActivity3.class);
                    intent1.putExtra("fullname", fullname);
                    intent1.putExtra("username", username);
                    intent1.putExtra("password", password);
                    intent1.putExtra("email", email);
                    intent1.putExtra("dateofbirth", dateofbirth);
                    intent1.putExtra("yearofbirth", yearofbirth);
                    intent1.putExtra("phonenumber", phonenumber);
                    startActivity(intent1);

                }
                else{
                    pd.dismiss();
                    Toast.makeText(RegisterActivity2.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}