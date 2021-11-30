package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pawgersapp.Adapters.MessagesAdapter;
import com.example.pawgersapp.Adapters.UsersAdapter;
import com.example.pawgersapp.POJO_Classes.Messages;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    Toolbar toolbar;
    DatabaseReference databaseReference, chatsReference, messagesReference, singleMessageRef;
    TextView tvChatTitle;
    EditText etChatBox;
    ImageButton btnSendMessage;
    ImageView ivProfile;
    FirebaseAuth firebaseAuth;
    String currentUID, otherUid, name, image;
    RecyclerView recyclerView;
    List<Messages> messagesList;
    LinearLayoutManager linearLayoutManager;
    MessagesAdapter messagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //getting intent strings
        otherUid = getIntent().getStringExtra("userID");
        name = getIntent().getStringExtra("name");
        image = getIntent().getStringExtra("image");

        setToolbar();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUID = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        messagesReference = FirebaseDatabase.getInstance().getReference("Messages");
        singleMessageRef = messagesReference.child(currentUID).child(otherUid);
        chatsReference = FirebaseDatabase.getInstance().getReference("Chats");
        btnSendMessage = findViewById(R.id.btn_sendMessage);
        etChatBox = findViewById(R.id.et_chatBox);
        messagesList = new ArrayList<>();
        recyclerView = findViewById(R.id.rv_messageList);
        messagesAdapter = new MessagesAdapter(messagesList, image);
        tvChatTitle = findViewById(R.id.tv_chatName);
        ivProfile = findViewById(R.id.iv_chatPicture);
        linearLayoutManager = new LinearLayoutManager(ChatActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messagesAdapter);

        dataSetup();
        getMessages();

        Map messageMap = new HashMap<>();
        messageMap.put("seen", false);
        messageMap.put("timestamp", ServerValue.TIMESTAMP);

        chatsReference.child(currentUID).child(otherUid).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    chatsReference.child(otherUid).child(currentUID).setValue(messageMap);
                }
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();

                etChatBox.setText("");
            }
        });
    }

    private void getMessages() {
        DatabaseReference test = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUID).child(otherUid);

        test.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Messages message = snapshot.getValue(Messages.class);

                messagesList.add(message);
                messagesAdapter.notifyDataSetChanged();

                recyclerView.scrollToPosition(messagesList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dataSetup(){
        if(!image.equals("default")){
            Picasso.get().load(image).placeholder(R.drawable.default_picture).into(ivProfile);
        }else{
            ivProfile.setImageResource(R.drawable.default_picture);
        }

        tvChatTitle.setText(name);
    }

    private void setToolbar(){
        toolbar = findViewById(R.id.chatNavbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View navbarView = inflater.inflate(R.layout.chat_nav_layout, null);
        actionBar.setCustomView(navbarView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.friend_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.mi_friendProfile){
            Intent intent = new Intent(ChatActivity.this, ProfileActivity.class);
            intent.putExtra("userID", otherUid);
            startActivity(intent);
        }

        return true;
    }


    private void sendMessage() {
        String message = etChatBox.getText().toString();

        if(!TextUtils.isEmpty(message)){

            DatabaseReference currentUserRef = FirebaseDatabase.getInstance().getReference("Messages").child(currentUID).child(otherUid);
            DatabaseReference otherUserRef = FirebaseDatabase.getInstance().getReference("Messages").child(otherUid).child(currentUID);

            Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("time", java.time.Clock.systemUTC().instant().toString());
            messageMap.put("from", currentUID);

            currentUserRef.push().setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    otherUserRef.push().setValue(messageMap);
                }
            });
        }
    }
}