package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pawgersapp.Fragments.FindUsersFragment;
import com.example.pawgersapp.Fragments.MessagesFragment;
import com.example.pawgersapp.Fragments.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    Fragment currentFragment;
    Toolbar topNavbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        //Top navbar
        topNavbar = findViewById(R.id.accountNavbar);
        setSupportActionBar(topNavbar);
        getSupportActionBar().setTitle("Pawgers");

        //Bottom Nav
        currentFragment = new NotificationsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

        botNavSetup();
    }

    public void botNavSetup(){
        bottomNavigationView = findViewById(R.id.nav_bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_notifications:
                        currentFragment = new NotificationsFragment();
                        break;
                    case R.id.nav_findOwners:
                        currentFragment = new FindUsersFragment();
                        break;
                    case R.id.nav_messages:
                        currentFragment = new MessagesFragment();
                        break;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, currentFragment).commit();

                return true;
            }
        });
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

        if(item.getItemId() == R.id.mi_settings){
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
        }

        if(item.getItemId() == R.id.mi_account){
            Intent intent = new Intent(HomeActivity.this, AccountSettingsActivity.class);
            startActivity(intent);
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}