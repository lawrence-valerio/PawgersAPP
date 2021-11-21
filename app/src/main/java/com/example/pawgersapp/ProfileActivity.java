package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    EditText otherName, otherDogName, otherBreed;
    ImageView ivProfileImage;
    Button btnAddFriend, btnDeclineRequest;
    DatabaseReference databaseReference, friendRequestReference, friendsReference, usersReference;
    FirebaseAuth firebaseAuth;
    String userId, friendState, currentUid;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getStringExtra("userID");

        btnAddFriend = findViewById(R.id.btnAddFriend);
        ivProfileImage = findViewById(R.id.ivProfilePicture);
        otherName = findViewById(R.id.et_OtherName);
        otherBreed = findViewById(R.id.et_otherBreed);
        otherDogName = findViewById(R.id.et_otherDogName);
        btnDeclineRequest = findViewById(R.id.btn_declineRequest);

        firebaseAuth = FirebaseAuth.getInstance();
        usersReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId);
        friendRequestReference = FirebaseDatabase.getInstance()
                .getReference("Friend_Requests");
        friendsReference = FirebaseDatabase.getInstance()
                .getReference("Friends");
        currentUid = firebaseAuth.getCurrentUser().getUid();

        friendState = "not_friends";

        setData();

        toolbar = findViewById(R.id.accountNavbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnAddFriend.setEnabled(false);
                requestStateCheck();
            }
        });

        btnDeclineRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRequests("not_friends", "add as friend");
            }
        });
    }

    private void requestStateCheck(){
        if(friendState.equals("not_friends")){
            friendRequestReference
                    .child(currentUid)
                    .child(userId)
                    .child("request_type")
                    .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        friendRequestReference
                                .child(userId)
                                .child(currentUid)
                                .child("request_type")
                                .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                friendState = "request_sent";
                                btnAddFriend.setText("Cancel Friend Request");

                                Toast.makeText(ProfileActivity.this, "Friend Request has been sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Toast.makeText(ProfileActivity.this, "Failed sending request", Toast.LENGTH_SHORT).show();
                    }

                    btnAddFriend.setEnabled(true);
                }
            });
        }

        if(friendState.equals("request_sent")){
            removeRequests("not_friends", "add as friend");
        }

        if(friendState.equals("request_received")){
            friendsReference.child(currentUid).child(userId).setValue("true")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            friendsReference.child(userId).child(currentUid).setValue("true")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            usersReference.child(userId).child("friends").child(currentUid).setValue("true");
                                            usersReference.child(currentUid).child("friends").child(userId).setValue("true");
                                            removeRequests("friends", "unfriend user");
                                        }
                                    });
                        }
                    });
        }

        if(friendState.equals("friends")){
            removeFriend();
        }
    }

    //removes users from friend requests collection
    private void removeRequests(String state, String buttonMsg){
        friendRequestReference.child(currentUid).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                friendRequestReference.child(userId).child(currentUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        btnAddFriend.setEnabled(true);
                        friendState = state;
                        btnAddFriend.setText(buttonMsg);
                        btnDeclineRequest.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    //Removes user from friends collection
    private void removeFriend(){
        friendsReference.child(currentUid).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                friendsReference.child(userId).child(currentUid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        usersReference.child(userId).child("friends").child(currentUid).removeValue();
                        usersReference.child(currentUid).child("friends").child(userId).removeValue();

                        btnAddFriend.setEnabled(true);
                        friendState = "not_friends";
                        btnAddFriend.setText("Add as friend");
                    }
                });
            }
        });
    }

    private void setData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String dogName = snapshot.child("dogs").child("dogName").getValue().toString();
                String dogBreed = snapshot.child("dogs").child("dogBreed").getValue().toString();
                String image = snapshot.child("image").getValue().toString();

                getSupportActionBar().setTitle(name + "'s profile");
                otherName.setText(name);
                otherDogName.setText(dogName);
                otherBreed.setText(dogBreed);

                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.default_picture).into(ivProfileImage);
                }else{
                    ivProfileImage.setImageResource(R.drawable.default_picture);
                }

                friendRequestReference.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(userId)){
                            String request = snapshot.child(userId).child("request_type").getValue().toString();

                            if(request.equals("received")){
                                friendState = "request_received";
                                btnAddFriend.setText("Accept Friend Request");
                                btnDeclineRequest.setVisibility(View.VISIBLE);
                            }else if(request.equals("sent")){
                                friendState = "request_sent";
                                btnAddFriend.setText("Cancel Friend Request");
                            }
                        }else{
                            friendsReference.child(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.hasChild(userId)){
                                        friendState = "friends";
                                        btnAddFriend.setText("Unfriend user");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}