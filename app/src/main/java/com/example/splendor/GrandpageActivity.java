package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.splendor.Fragments.GrandHomeFragment;
import com.example.splendor.Fragments.GrandNotificationsFragment;
import com.example.splendor.Fragments.GrandProfileFragment;
import com.example.splendor.Fragments.GrandSearchFragment;
import com.example.splendor.databinding.ActivityGrandpageBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class GrandpageActivity extends AppCompatActivity {
    ActivityGrandpageBinding b;
    Fragment selected_fragment;
    public static int CURRENT_GRAND_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b= ActivityGrandpageBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        getSupportActionBar().hide();
        CURRENT_GRAND_FRAGMENT = 1;
        b.grandButtomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home1:
                        CURRENT_GRAND_FRAGMENT = 1;
                        selected_fragment = new GrandHomeFragment(); break;
                    case R.id.profile:
                        CURRENT_GRAND_FRAGMENT = 2;
                        selected_fragment = new GrandProfileFragment(); break;
                    case R.id.search:
                        CURRENT_GRAND_FRAGMENT = 3;
                        selected_fragment = new GrandSearchFragment(); break;
                    case R.id.post:
                        CURRENT_GRAND_FRAGMENT = 3;
                        startActivity(new Intent(GrandpageActivity.this, GrandPostActivity.class));break;
                    case R.id.notifications:
                        CURRENT_GRAND_FRAGMENT = 4;
                        selected_fragment = new GrandNotificationsFragment(); break;
                    default:
                        CURRENT_GRAND_FRAGMENT = 1;
                        selected_fragment = new GrandHomeFragment(); break;
                }
                if (selected_fragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.grandFragmentContainer, selected_fragment).commit();
                }
                return true;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.grandFragmentContainer, new GrandHomeFragment()).commit();

        Bundle intent = getIntent().getExtras();
        if (intent!= null){
            String profileId = intent.getString("profileId");
            getSharedPreferences("grandprofile", MODE_PRIVATE).edit().putString("profileId", profileId).apply();
        }
        else{
            getSharedPreferences("grandprofile", MODE_PRIVATE).edit().putString("profileId", "none");
        }



    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (CURRENT_GRAND_FRAGMENT == 1){
            finish();
        }
    }
}