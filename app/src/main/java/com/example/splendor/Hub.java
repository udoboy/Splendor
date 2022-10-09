package com.example.splendor;

import androidx.fragment.app.Fragment;

import com.example.splendor.Fragments.HomeFragment;
import com.example.splendor.Fragments.NotificationFragment;
import com.example.splendor.Fragments.ProfileFragment;

import java.util.List;

public class Hub {
    private static String currentFragment;
    private static String lastFragment;
    private static List<String> stackFragments;
    public static int currentIndex;

    public static List<String> getStackFragments(){
        return stackFragments;
    }

    public static void addTosStackFragments(String addFragment){
        getStackFragments().add(addFragment);
    }
    public static String getCurrentFragment(){
        return currentFragment;
    }
    public static String getLastFragment(){
        return lastFragment;
    }
    public static void setCurrentFragment(String fragment){
        currentFragment = fragment;
    }

    public static void setLastFragment(String fragment){
        lastFragment = fragment;
    }


    public static Fragment getPureFragment(String roughFragment){
        if (roughFragment.contains("HomeFragment")){
            return new HomeFragment();
        }
        else if (roughFragment.contains("NotificationFragment")){
            return new NotificationFragment();
        }
        else if (roughFragment.contains("ProfileFragment")){
            return new ProfileFragment();
        }
        else{
            return new HomeFragment();
        }
    }
}
