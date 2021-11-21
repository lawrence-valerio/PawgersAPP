package com.example.pawgersapp.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawgersapp.POJO_Classes.Messages;
import com.example.pawgersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder>{
    List<Messages> messages;
    DatabaseReference userReference;
    FirebaseAuth firebaseAuth;
    String image;

    public MessagesAdapter(List<Messages> messages, String image) {
        this.image = image;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_row, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        String currentUid = firebaseAuth.getCurrentUser().getUid();
        String from = messages.get(position).getFrom();
        String message = messages.get(position).getMessage();

        if(from.equals(currentUid)){
            holder.ivProfilePicture.setVisibility(View.GONE);
            holder.tvMessageOther.setVisibility(View.GONE);
            holder.tvMessage.setText(message);
        }else{
            holder.tvMessage.setVisibility(View.GONE);
            if(!image.equals("default")){
                Picasso.get().load(image).placeholder(R.drawable.default_picture).into(holder.ivProfilePicture);
            }else{
                holder.ivProfilePicture.setImageResource(R.drawable.default_picture);
            }

            holder.tvMessageOther.setText(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
    TextView tvMessage, tvMessageOther;
    ImageView ivProfilePicture;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        tvMessage = itemView.findViewById(R.id.tv_messageRow);
        tvMessageOther = itemView.findViewById(R.id.tv_messageOther);
        ivProfilePicture = itemView.findViewById(R.id.iv_messageRowPicture);
    }
}
}
