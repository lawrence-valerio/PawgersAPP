package com.example.pawgersapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent;
            //Check if user is logged in / authenticated
            if(currentUser != null && currentUser.isEmailVerified()) {

                intent = new Intent(MainActivity.this, HomeActivity.class);
            }else{
                intent = new Intent(MainActivity.this, StartActivity.class);
            }
            startActivity(intent);
            finish();
            return;
        }, 2000);
    }
}