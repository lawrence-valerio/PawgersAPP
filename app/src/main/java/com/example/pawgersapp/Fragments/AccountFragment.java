package com.example.pawgersapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.pawgersapp.R;
import com.example.pawgersapp.StartActivity;
import com.example.pawgersapp.Users;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {
    View loadingView;
    ProgressBar loadingSpinner;
    TextView tvName;
    EditText etEmail, etDogName, etName;
    AutoCompleteTextView atvBreed;
    Button btnLogout;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;
    FirebaseUser currentUser;
    String currentUserUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_account, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();
        btnLogout = view.findViewById(R.id.btn_logout);
        etName = view.findViewById(R.id.account_fullName);
        loadingSpinner = view.findViewById(R.id.account_loader);
        loadingView = view.findViewById(R.id.account_loadingView);
        atvBreed = view.findViewById(R.id.atv_accountBreed);
        etDogName = view.findViewById(R.id.et_accountDogName);
        etEmail = view.findViewById(R.id.et_emailAccount);

        setData();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutUser();
            }
        });

        return view;
    }

    public void logoutUser(){
        firebaseAuth.signOut();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        googleSignInClient.signOut();

        Intent intent = new Intent(getActivity(), StartActivity.class);
        startActivity(intent);
    }

    public void setData(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserUID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                String name = user.getName();
                String email = currentUser.getEmail();
                String breed = user.getDogs().getDogBreed();
                etName.setText(name);
                etDogName.setText(user.getDogs().getDogName());
                etEmail.setText(email);
                atvBreed.setText(breed);

                loadingView.setVisibility(View.INVISIBLE);
                loadingSpinner.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}