package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity {
//    Toolbar toolbar;
//    RecyclerView recyclerView;
//    DatabaseReference databaseReference;
//    FirebaseRecyclerAdapter<Users, UsersViewHolder> firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
//
//        toolbar = findViewById(R.id.usersTopNav);
//        recyclerView = findViewById(R.id.rvUsers);
//        databaseReference = FirebaseDatabase.getInstance()
//                .getReference("Users");
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("All Users");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        fetch();
    }

//    private void fetch() {
//        Query query = FirebaseDatabase.getInstance()
//                .getReference("Users");
//
//        FirebaseRecyclerOptions<Users> options =
//                new FirebaseRecyclerOptions.Builder<Users>()
//                        .setQuery(query, new SnapshotParser<Users>() {
//                            @NonNull
//                            @Override
//                            public Users parseSnapshot(@NonNull DataSnapshot snapshot) {
//                                return new Users(snapshot.child("name").getValue().toString(),
//                                        snapshot.child("image").getValue().toString(),
//                                        snapshot.child("status").getValue().toString());
//                            }
//                        })
//                        .build();
//        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull UsersViewHolder holder, int position, @NonNull Users model) {
//                holder.setTvName(model.getName());
//                holder.setTvStatus(model.getStatus());
//                holder.setIvPicture(model.getImage());
//
//                String userId = getRef(position).getKey();
//
//                holder.view.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
//                        intent.putExtra("userID", userId);
//                        startActivity(intent);
//                    }
//                });
//            }
//
//            @NonNull
//            @Override
//            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view  = LayoutInflater.from(UsersActivity.this)
//                        .inflate(R.layout.users_row, parent, false);
//                return new UsersViewHolder(view);
//            }
//        };
//
//        recyclerView.setAdapter(firebaseRecyclerAdapter);
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseRecyclerAdapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        firebaseRecyclerAdapter.stopListening();
//    }
//
//    public static class UsersViewHolder extends RecyclerView.ViewHolder{
//        View view;
//
//        public UsersViewHolder(@NonNull View itemView) {
//            super(itemView);
//            view = itemView;
//        }
//
//        public void setTvName(String string){
//            TextView tvName = view.findViewById(R.id.tv_userRowName);
//            tvName.setText(string);
//        }
//
//        public void setIvPicture(String string){
//            ImageView ivPicture = view.findViewById(R.id.iv_userRowPicture);
//            Picasso.get().load(string).placeholder(R.drawable.default_picture).into(ivPicture);
//        }
//
//        public void setTvStatus(String string){
//            TextView tvStatus = view.findViewById(R.id.tv_userRowBreed);
//            tvStatus.setText(string);
//        }
//    }
}