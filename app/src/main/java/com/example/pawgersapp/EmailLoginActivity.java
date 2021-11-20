package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.basgeekball.awesomevalidation.utility.custom.SimpleCustomValidation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginActivity extends AppCompatActivity {
    EditText etEmail, etPassword;
    Button btnLogin;
    FirebaseAuth firebaseAuth;
    AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        firebaseAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_Email_Login);
        etPassword = findViewById(R.id.et_Password_Login);
        btnLogin = findViewById(R.id.btn_Login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();

                awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
                awesomeValidation.addValidation(EmailLoginActivity.this, R.id.et_Email_Login, Patterns.EMAIL_ADDRESS, R.string.invalid_email);
                awesomeValidation.addValidation(EmailLoginActivity.this, R.id.et_Email_Login, RegexTemplate.NOT_EMPTY, R.string.empty_email);
                awesomeValidation.addValidation(EmailLoginActivity.this, R.id.et_Password_Login, RegexTemplate.NOT_EMPTY, R.string.invalid_password);

                if(awesomeValidation.validate()){
                    loginUser(email, password);
                }
            }
        });
    }

    //Method to login using Firebase sign in with email.
    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(EmailLoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                            Intent intent = new Intent(EmailLoginActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                            return;
                                        }else{
                                            awesomeValidation.addValidation(EmailLoginActivity.this, R.id.et_Email_Login, new SimpleCustomValidation() {
                                                @Override
                                                public boolean compare(String input) {
                                                    return firebaseAuth.getCurrentUser().isEmailVerified();
                                                }
                                            }, R.string.email_verification_error);
                                            awesomeValidation.validate();
                                        }
                                    }else{
                                        awesomeValidation.addValidation(EmailLoginActivity.this, R.id.et_Email_Login, new SimpleCustomValidation() {
                                            @Override
                                            public boolean compare(String input) {
                                                return task.isSuccessful();
                                            }
                                        }, R.string.email_signin_error);
                                        awesomeValidation.validate();
                                    }
                                }
                            });
                        }else{
                            Toast.makeText(EmailLoginActivity.this, "Failed to login.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}