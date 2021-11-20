package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pawgersapp.Adapters.TabsPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    Toolbar topNavbar;
    ViewPager viewPager;
    TabsPagerAdapter pagerAdapter;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();
        topNavbar = findViewById(R.id.topNavBar);

        //Top navbar
        setSupportActionBar(topNavbar);
        getSupportActionBar().setTitle("Pawgers");

//        //Pager
//        viewPager = findViewById(R.id.tab_pager);
//        tabLayout = findViewById(R.id.main_tabs);
//
//        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(pagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.mi_logout){
            firebaseAuth.signOut();
            Intent intent = new Intent(HomeActivity.this, StartActivity.class);
            startActivity(intent);
            finish();
        }

        if(item.getItemId() == R.id.mi_settings){
            Intent intent = new Intent(HomeActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.mi_allUsers){
            Intent intent = new Intent(HomeActivity.this, UsersActivity.class);
            startActivity(intent);
        }

        return true;
    }
}