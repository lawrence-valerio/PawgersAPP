package com.example.pawgersapp.Adapters;

import android.content.Context;
import android.content.Intent;
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
import com.example.pawgersapp.POJO_Classes.Users;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    Context context;
    List<Users> userInformations;

    public UsersAdapter(Context context, List<Users> userInformations) {
        this.context = context;
        this.userInformations = userInformations;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.users_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = userInformations.get(position).getName();
        String dogName = userInformations.get(position).getDogs().getDogName();
        String dogBreed = userInformations.get(position).getDogs().getDogBreed();
        String UID = userInformations.get(position).getKey();
        String image = userInformations.get(position).getImage();

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
                intent.putExtra("userID", UID);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userInformations.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvName;
        TextView tvBreed;
        CardView cvUser;
        ImageView ivProfilePicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_nameRow);
            tvBreed = itemView.findViewById(R.id.tv_breedRow);
            cvUser = itemView.findViewById(R.id.cv_Users);
            ivProfilePicture = itemView.findViewById(R.id.iv_usersRowPicture);
        }
    }
}
