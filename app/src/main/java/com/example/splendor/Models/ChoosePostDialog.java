package com.example.splendor.Models;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.splendor.R;

public class ChoosePostDialog extends Dialog {
    public ChoosePostDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setAttributes(params);
        setTitle(null);
        setCancelable(true);
        View view = LayoutInflater.from(context).inflate(R.layout.choose_post_dialog, null);
        setContentView(view);
    }


}
