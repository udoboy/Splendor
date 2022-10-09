package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityMainBinding;
import com.example.splendor.databinding.ActivityRegisterBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    ActivityRegisterBinding b;
    DatePickerDialog datePickerDialog;
    String date_of_birth;
    int mYear;
    int mMonth;
    int mDAy;
    AlertDialog alertDialog;
    boolean exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b  = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        date_of_birth = "0";
        exists = false;

        b.edtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
              checkUser(editable.toString());
            }
        });


        b.btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //todo: delete the setUsernameActivity
                String fullname = b.edtFullName.getText().toString();
                String username  = b.edtUserName.getText().toString();
                if (!fullname.isEmpty() && !username.isEmpty() && !date_of_birth.equals("0")){
                    if (verifyAge(mYear)){
                        if (exists == false){

                            Intent intent = new Intent(getApplicationContext(), RegisterActivity2.class);
                            intent.putExtra("fullname", fullname);
                            intent.putExtra("username",username);
                            intent.putExtra("dateofbirth", date_of_birth);
                            intent.putExtra("yearofbirth", mYear);
                            startActivity(intent);
                        }
                        else{
                          // Toast.makeText(RegisterActivity.this, "The user", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else{
                        AlertDialog.Builder alertDialogue = new AlertDialog.Builder(RegisterActivity.this);
                        alertDialogue.setMessage("To be on splendor, you have to be 15 years or older");
                        alertDialogue.show();
                    }



                }
                else{
                    Toast.makeText(RegisterActivity.this, "All feilds are required", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b.btnDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDatePicker();
                openDatePicker();
            }
        });
    }


    public void checkUser(String keyword){

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()){
                    Users users = snapshot1.getValue(Users.class);
                    if (users.getUsername().equals(keyword)){
                        exists = true;
                        b.txtUsernameExists.setVisibility(View.VISIBLE);
                        break;
                    }
                    else{
                        exists = false;
                        b.txtUsernameExists.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }



    public void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1+1;
                date_of_birth = makeDateString(i, i1, i2);
                mYear = i;
                //b.btnDateOfBirth.setTextColor(Color.parseColor("#000000"));
                b.btnDateOfBirth.setText(date_of_birth);
            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style= AlertDialog.THEME_HOLO_DARK;
        datePickerDialog = new DatePickerDialog(this, style,dateSetListener,year,month,day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());


    }

    public boolean verifyAge(int personBirthYear){
        Calendar cal = Calendar.getInstance();
        int current_year = cal.get(Calendar.YEAR);
        int average = current_year - personBirthYear;
        if (average >=15){
            return true;
        }
        else{
           return false;
        }

    }


    public String makeDateString(int year, int month, int day){
        return getMothFormat(month)+ " " + String.valueOf(day) +" " + String.valueOf(year);
    }

    public String getMothFormat(int month){
        if(month ==1){
            return "January";
        }
        if (month == 2){
            return "February";
        }
        if (month == 3){
            return "March";
        }
        if (month == 4){
            return "April";
        }
        if (month == 5){
            return "May";
        }
        if (month == 6){
            return "June";
        }
        if (month == 7){
            return "July";
        }
        if (month == 8){
            return "August";
        }
        if (month == 9){
            return "September";
        }
        if (month == 10) {

            return "October";
        }
        if (month == 11){
            return "November";
        }
        if (month == 12) {
            return "December";
        }
        return "Jan";

    }


    public void openDatePicker(){
        datePickerDialog.show();
    }
}