package com.example.pawgersapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawgersapp.ProfileActivity;
import com.example.pawgersapp.R;
import com.example.pawgersapp.Users;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.MyViewHolder>{
    Context context;
    List<String> userInformations;
    DatabaseReference userReference;

    public FriendsAdapter(Context context, List<String> userInformations) {
        this.context = context;
        this.userInformations = userInformations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.messages_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsAdapter.MyViewHolder holder, int position) {
        userReference = FirebaseDatabase.getInstance().getReference("Users").child(userInformations.get(position));

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                user.key = snapshot.getKey();

                String name = user.getName();
                String dogName = user.getDogs().getDogName();
                String dogBreed = user.getDogs().getDogBreed();
                String image = user.getImage();

                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.default_picture).into(holder.ivProfilePicture);
                }else{
                    holder.ivProfilePicture.setImageResource(R.drawable.default_picture);
                }

                holder.tvName.setText(name);
                holder.tvBreed.setText(dogBreed);
                holder.cvUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("userID", user.getKey());
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userInformations.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvName, tvBreed, tvTime;
        CardView cvUser;
        ImageView ivProfilePicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_nameRowMessages);
            tvTime = itemView.findViewById(R.id.tv_messageTime);
            tvBreed = itemView.findViewById(R.id.tv_lastRowMessages);
            cvUser = itemView.findViewById(R.id.cv_UsersMessages);
            ivProfilePicture = itemView.findViewById(R.id.iv_usersRowPictureMessages);
        }
    }
}
