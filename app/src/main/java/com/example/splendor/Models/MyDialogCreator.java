package com.example.splendor.Models;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.splendor.R;

public class MyDialogCreator extends Dialog {
    public MyDialogCreator(@NonNull Context context, int layout, boolean cancelable, OnCancelListener onCancelListener, String title) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setAttributes(params);
        if (title != null){
            setTitle(title);
        }
        setCancelable(cancelable);
        View view = LayoutInflater.from(context).inflate(layout, null);
        setContentView(view);
    }
}
