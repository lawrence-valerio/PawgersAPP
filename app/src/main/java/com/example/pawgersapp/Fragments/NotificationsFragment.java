package com.example.pawgersapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pawgersapp.Adapters.NotificationsAdapter;
import com.example.pawgersapp.POJO_Classes.Users;
import com.example.pawgersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationsFragment extends Fragment {
    RecyclerView recyclerView;
    List<Map> userKeys;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        String currentUserId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference userFriendRequestRef = FirebaseDatabase.getInstance().getReference("Friend_Requests").child(currentUserId);

        userFriendRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userKeys = new ArrayList<>();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    if(dataSnapshot.child("request_type").getValue().equals("received")){
                        Map requestData = new HashMap<>();
                        requestData.put("user", dataSnapshot.getKey());
                        requestData.put("time", dataSnapshot.child("time").getValue().toString());
                        userKeys.add(requestData);
                    }
                }

                recyclerView = view.findViewById(R.id.rv_notificationList);
                NotificationsAdapter notificationsAdapter = new NotificationsAdapter(getContext(), userKeys);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                recyclerView.setAdapter(notificationsAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}