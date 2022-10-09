package com.example.splendor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.splendor.Models.ChoosePostDialog;
import com.example.splendor.databinding.ActivityGrandPostBinding;

public class GrandPostActivity extends AppCompatActivity {
    ActivityGrandPostBinding b;
    ChoosePostDialog choosePostDialog;
    RelativeLayout pImage, pVideo, pText, cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityGrandPostBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        choosePostDialog = new ChoosePostDialog(this);

        cancel = choosePostDialog.findViewById(R.id.cancel);
        pImage = choosePostDialog.findViewById(R.id.pImage);
        pVideo = choosePostDialog.findViewById(R.id.pVideo);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePostDialog.dismiss();
                startActivity(new Intent(GrandPostActivity.this, GrandpageActivity.class));
            }
        });
        choosePostDialog.show();

        choosePostDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                startActivity(new Intent(GrandPostActivity.this, GrandpageActivity.class));
            }
        });


    }
}