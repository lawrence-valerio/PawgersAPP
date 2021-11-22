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
import com.google.firebase.installations.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessagesAdapter extends RecyclerView.Adapter{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    List<Messages> messages;
    DatabaseReference userReference;
    FirebaseAuth firebaseAuth;
    String image;

    public MessagesAdapter(List<Messages> messages, String image) {
        this.image = image;
        this.messages = messages;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Messages message = messages.get(position);
        firebaseAuth = FirebaseAuth.getInstance();

        if (message.getFrom().equals(firebaseAuth.getUid())) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messages_row, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.messages_row_other, parent, false);
            return new ReceivedMessageHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages message = messages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.tv_messageRow);
        }

        void bind(Messages message) {
            messageText.setText(message.getMessage());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.tv_messageOther);
            profileImage = itemView.findViewById(R.id.iv_messageRowPicture);
        }

        void bind(Messages message) {
            messageText.setText(message.getMessage());

            if(!image.equals("default")){
                Picasso.get().load(image).placeholder(R.drawable.default_picture).into(profileImage);
            }else{
                profileImage.setImageResource(R.drawable.default_picture);
            }
        }
    }
}


//public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder>{
//    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
//    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
//    List<Messages> messages;
//    DatabaseReference userReference;
//    FirebaseAuth firebaseAuth;
//    String image;
//
//    public MessagesAdapter(List<Messages> messages, String image) {
//        this.image = image;
//        this.messages = messages;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Messages message = messages.get(position);
//        firebaseAuth = FirebaseAuth.getInstance();
//
//        if (message.getFrom().equals(firebaseAuth.getUid())) {
//            // If the current user is the sender of the message
//            return VIEW_TYPE_MESSAGE_SENT;
//        } else {
//            // If some other user sent the message
//            return VIEW_TYPE_MESSAGE_RECEIVED;
//        }
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View v;
//
//        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
//            v = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.messages_row, parent, false);
//            return new MyViewHolder(v);
//        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
//            v = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.messages_row_other, parent, false);
//            return new MyViewHolder(v);
//        }
//
//        return null;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//        firebaseAuth = FirebaseAuth.getInstance();
//        String currentUid = firebaseAuth.getCurrentUser().getUid();
//        String from = messages.get(position).getFrom();
//        String message = messages.get(position).getMessage();
//
//        if(from.equals(currentUid)){
////            holder.ivProfilePicture.setVisibility(View.INVISIBLE);
////            holder.tvMessageOther.setVisibility(View.INVISIBLE);
//            holder.tvMessage.setText(message);
//        }else{
////            holder.tvMessage.setVisibility(View.INVISIBLE);
//            if(!image.equals("default")){
//                Picasso.get().load(image).placeholder(R.drawable.default_picture).into(holder.ivProfilePicture);
//            }else{
//                holder.ivProfilePicture.setImageResource(R.drawable.default_picture);
//            }
//
//            holder.tvMessageOther.setText(message);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return messages.size();
//    }
//
//    public static class MyViewHolder extends RecyclerView.ViewHolder{
//    TextView tvMessage, tvMessageOther;
//    ImageView ivProfilePicture;
//
//    public MyViewHolder(@NonNull View itemView) {
//        super(itemView);
//        tvMessage = itemView.findViewById(R.id.tv_messageRow);
//        tvMessageOther = itemView.findViewById(R.id.tv_messageOther);
//        ivProfilePicture = itemView.findViewById(R.id.iv_messageRowPicture);
//    }
//}
//}
