package com.example.splendor.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.splendor.Adapter.NotificationAdapter;
import com.example.splendor.MainActivity;
import com.example.splendor.Models.Notifications;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentNotificationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificationFragment extends Fragment {
   FragmentNotificationBinding b;
   List<Notifications> notificationsList;
   NotificationAdapter notificationAdapter;

   DatabaseReference getNotificationsRef;
   ValueEventListener getNotificationsEventListener;

    @Override
    public void onDestroyView() {
        if (getNotificationsRef != null){
            getNotificationsRef.removeEventListener(getNotificationsEventListener);
        }
        b.notificationsRec.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b= FragmentNotificationBinding.inflate(inflater, container, false);
        notificationsList = new ArrayList<>();
        getNotifications();
        notificationAdapter = new NotificationAdapter(notificationsList, getContext());
        b.notificationsRec.setHasFixedSize(true);
        b.notificationsRec.setAdapter(notificationAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        b.notificationsRec.setLayoutManager(linearLayoutManager);

        return b.getRoot();

    }
    public void getNotifications(){
        getNotificationsRef= FirebaseDatabase.getInstance().getReference().child("Notifications")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getNotificationsEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                for (DataSnapshot snapshot1: snapshot.getChildren()){
                    Notifications notifications = snapshot1.getValue(Notifications.class);
                    notificationsList.add(notifications);
                }
                Collections.reverse(notificationsList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
      getNotificationsRef.addValueEventListener(getNotificationsEventListener);
    }
    
}