package com.example.pawgersapp.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pawgersapp.Fragments.FriendsFragment;
import com.example.pawgersapp.Fragments.MessagesFragment;
import com.example.pawgersapp.Fragments.NotificationsFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter {
    public TabsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;
            case 1:
                MessagesFragment messagesFragment = new MessagesFragment();
                return messagesFragment;
            case 2:
                FriendsFragment friendsFragment = new FriendsFragment();
                return friendsFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "NOTIFICATIONS";
            case 1:
                return "MESSAGES";
            case 2:
                return "FRIENDS";
            default:
                return null;
        }
    }
}
