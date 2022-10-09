package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.splendor.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
   ActivityLoginBinding b;
   FirebaseAuth mAuth;
   ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        mAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);

        b.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setMessage("Fetching account please wait");
                pd.show();
                String email = b.edtEmail.getText().toString();
                String password = b.edtPassword.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()){

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                if (FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), DetailsActivity.class));
                                }
                                else{
                                    pd.dismiss();
                                    FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("To login to your account, you need to verify your Email address, click on the link sent to your email to proceed");
                                    builder.show();
                                }
                            }
                            else{
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

//
//                    if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
//                        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                if (task.isSuccessful()){
//
//                                }
//                                else{
//
//                                }
//                            }
//                        });
//                    }
//                    else{
//                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                        builder.setMessage("To login to your account, you need to verify your Email address, click on the link sent to your email to proceed");
//                    }
//
//                }
//                else{
//                    Toast.makeText(LoginActivity.this, "Please fill in all the feilds", Toast.LENGTH_SHORT).show();
                }

            }
        });

        b.txtDoNotHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
    }
}