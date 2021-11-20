package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.pawgersapp.Fragments.AccountFragment;
import com.example.pawgersapp.Fragments.FindUsersFragment;
import com.example.pawgersapp.Fragments.HomeFragment;
import com.example.pawgersapp.Fragments.MessagesFragment;
import com.example.pawgersapp.Fragments.NotificationsFragment;
import com.example.pawgersapp.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    BottomNavigationView bottomNavigationView;
    Fragment currentFragment;
//    Toolbar topNavbar;
//    ViewPager viewPager;
//    TabsPagerAdapter pagerAdapter;
//    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

//        //Top navbar
//        topNavbar = findViewById(R.id.topNavBar);
//        setSupportActionBar(topNavbar);
//        getSupportActionBar().setTitle("Pawgers");

//        //Pager
//        viewPager = findViewById(R.id.tab_pager);
//        tabLayout = findViewById(R.id.main_tabs);
//
//        pagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());
//        viewPager.setAdapter(pagerAdapter);
//        tabLayout.setupWithViewPager(viewPager);

        //Bottom Nav
        currentFragment = new HomeFragment();
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
                    case R.id.nav_account:
                        currentFragment = new AccountFragment();
                        break;
                    case R.id.nav_findOwners:
                        currentFragment = new FindUsersFragment();
                        break;
                    case R.id.nav_messages:
                        currentFragment = new MessagesFragment();
                        break;
                    case R.id.nav_settings:
                        currentFragment = new SettingsFragment();
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