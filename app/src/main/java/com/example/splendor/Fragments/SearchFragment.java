package com.example.splendor.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.splendor.Adapter.UserAdapter;
import com.example.splendor.Models.Users;
import com.example.splendor.R;
import com.example.splendor.databinding.FragmentSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class SearchFragment extends Fragment {
    FragmentSearchBinding b;
    List<Users> usersList;
    UserAdapter adapter;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;

    DatabaseReference queryRef;
    ValueEventListener queryListener;


    @Override
    public void onDestroyView() {
        if (queryRef != null){
            queryRef.removeEventListener(queryListener);
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        b = FragmentSearchBinding.inflate(inflater, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        b.recPeople.setHasFixedSize(true);
        b.recPeople.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth = FirebaseAuth.getInstance();
        usersList = new ArrayList<>();
        adapter = new UserAdapter(getContext(), usersList);
        b.recPeople.setAdapter(adapter);

       b.searchbar.addTextChangedListener(new TextWatcher() {
           @Override
           public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

           }

           @Override
           public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
               findUser(charSequence.toString());
           }

           @Override
           public void afterTextChanged(Editable editable) {

           }
       });
        return b.getRoot();
    }



    public void findUser(String keyword) {
        queryRef = FirebaseDatabase.getInstance().getReference().child("Users");
        queryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    Users users = snapshot1.getValue(Users.class);

                    if (users.getUsername().toLowerCase().contains(keyword.toLowerCase())) {
                        usersList.add(users);
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        queryRef.addValueEventListener(queryListener);
    }

}