package com.example.pawgersapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pawgersapp.POJO_Classes.Users;
import com.example.pawgersapp.ProfileActivity;
import com.example.pawgersapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    Context context;
    List<Map> userKeys;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");
    DatabaseReference requestReference = FirebaseDatabase.getInstance().getReference("Friend_Requests");
    FirebaseAuth firebaseAuth;

    public NotificationsAdapter(Context context, List<Map> userKeys){
        this.userKeys = userKeys;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_row, parent, false);

        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String currentUID = FirebaseAuth.getInstance().getUid();

        Map userKey = userKeys.get(position);

        String key = userKey.get("user").toString();

        databaseReference.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                holder.tvNotification.setText(snapshot.child("name").getValue().toString() + " has sent you a friend request!");
                if(!snapshot.child("image").equals("default")){
                    Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.default_picture).into(holder.ivProfilePicture);
                }else{
                    holder.ivProfilePicture.setImageResource(R.drawable.default_picture);
                }
                holder.cvNotification.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ProfileActivity.class);
                        intent.putExtra("userID", key);
                        context.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        requestReference.child(currentUID).child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String time = snapshot.child("time").getValue().toString();
                    if(!formatTime(time).equals("0 minutes ago")){
                        holder.tvTime.setText(formatTime(time));
                    }else{
                        holder.tvTime.setText("A few seconds ago");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userKeys.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvNotification, tvTime;
        CardView cvNotification;
        ImageView ivProfilePicture;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNotification = itemView.findViewById(R.id.tv_notificationMessage);
            tvTime = itemView.findViewById(R.id.tv_notificationTime);
            cvNotification = itemView.findViewById(R.id.cv_notification);
            ivProfilePicture = itemView.findViewById(R.id.iv_notificationRowPicture);
        }
    }

    private String formatTime(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("CST"));
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String niceDateStr = (String) DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);

        return niceDateStr;
    }
}
