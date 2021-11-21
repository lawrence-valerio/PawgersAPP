package com.example.pawgersapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawgersapp.Adapters.UsersAdapter;
import com.example.pawgersapp.R;
import com.example.pawgersapp.POJO_Classes.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindUsersFragment extends Fragment {
    RecyclerView recyclerView;
    List<Users> userInformations;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find_users, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        DatabaseReference databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users");
        DatabaseReference friendsReference = FirebaseDatabase
                .getInstance()
                .getReference("Friends");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userInformations = new ArrayList<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(!dataSnapshot.getKey().equals(currentUser.getUid()) && !dataSnapshot.child("friends").hasChild(currentUser.getUid())){
                        Users user = dataSnapshot.getValue(Users.class);
                        user.key = dataSnapshot.getKey();
                        userInformations.add(user);
                    }
                }

                recyclerView = view.findViewById(R.id.rv_userListFind);
                UsersAdapter usersAdapter = new UsersAdapter(getContext(), userInformations);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(usersAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}