package com.example.splendor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.View;

import com.example.splendor.Fragments.HomeFragment;
import com.example.splendor.Fragments.NotificationFragment;
import com.example.splendor.Fragments.ProfileFragment;
import com.example.splendor.Fragments.SearchFragment;
import com.example.splendor.Models.Users;
import com.example.splendor.databinding.ActivityDetailsActivityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    ActivityDetailsActivityBinding b;
    Fragment selectedFragment;
    boolean navigate;


    @Override
    public void onBackPressed() {
        if (b.bottomNavBar.getSelectedItemId() == R.id.home1){
            super.onBackPressed();
        }
        else{
           b.bottomNavBar.setSelectedItemId(R.id.home1);
        }

    }


    @Override
    protected void onPostResume() {
        System.out.println("details activity on post resume was called");
        super.onPostResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("details activty on create was called");
        super.onCreate(savedInstanceState);
        b= ActivityDetailsActivityBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());
        //getSupportActionBar().hide();

        navigate = true;
        b.bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.home1:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.profile:
                            getSharedPreferences("profile", MODE_PRIVATE).edit().putString("profileId", "none").apply();
                            selectedFragment = new ProfileFragment();
                            break;
                        case R.id.notifications:
                            selectedFragment = new NotificationFragment();
                            break;
                        case R.id.post:
                            startActivity(new Intent(getApplicationContext(), PostActivity.class));
                            //inflateFragment("post", selectedFragment);
                            break;
                        case R.id.search:
                            selectedFragment = new SearchFragment();
                            break;
                        default: break;
                    }

                    if (selectedFragment != null){
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

                    }
                    else{

                    }
                return true;
            }
        });


        Bundle intent = getIntent().getExtras();
        if(intent != null){
               if((Boolean)intent.getBoolean("resolveToHome") != null && intent.getBoolean("resolveToHome") ){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
                b.bottomNavBar.setSelectedItemId(R.id.home1);
            }
               else{
                   String profileId = intent.getString("publisherId");
                   getSharedPreferences("profile", MODE_PRIVATE).edit().putString("profileId", profileId).apply();
                   getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ProfileFragment()).commit();
               }
        }
        else{
            getSharedPreferences("profile", MODE_PRIVATE).edit().putString("profileId", "none").apply();
            System.out.println("i apply");
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new HomeFragment()).commit();
            b.bottomNavBar.setSelectedItemId(R.id.home1);
        }
    }




}