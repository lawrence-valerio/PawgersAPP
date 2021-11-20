package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {
    AwesomeValidation awesomeValidation;
    EditText etEmail, etPassword, etFullName, etDogName;
    Button btnRegister;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    AutoCompleteTextView atvBreeds;
    List<String> breeds = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        new ParseURL().execute();

        etEmail = findViewById(R.id.et_Email_Register);
        etPassword = findViewById(R.id.et_Password_Register);
        etFullName = findViewById(R.id.et_fullName);
        etDogName = findViewById(R.id.et_dogNameEmail);
        btnRegister = findViewById(R.id.btn_Register);
        firebaseAuth = FirebaseAuth.getInstance();
        atvBreeds = findViewById(R.id.atv_BreedsEmail);

        //Form validation
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.et_fullName, RegexTemplate.NOT_EMPTY, R.string.invalidFullName);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.et_dogNameEmail, RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.et_Password_Register, RegexTemplate.NOT_EMPTY, R.string.invalid_password);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.et_Email_Register, RegexTemplate.NOT_EMPTY, R.string.empty_email);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.atv_BreedsEmail, RegexTemplate.NOT_EMPTY, R.string.invalid_breed);
        awesomeValidation.addValidation(RegisterActivity.this, R.id.et_Email_Register, Patterns.EMAIL_ADDRESS, R.string.invalid_email);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String fullName = etFullName.getText().toString();
                String dogName = etDogName.getText().toString();
                String dogBreed = atvBreeds.getText().toString();

                if(awesomeValidation.validate()){
                    createUser(email, password, fullName, dogName, dogBreed);
                }
            }
        });
    }

    private void createUser(String email, String password, String fullName, String dogName, String dogBreed) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    String uid = FirebaseAuth.getInstance().getUid();

                                    //Storing user data to database
                                    databaseReference = FirebaseDatabase
                                            .getInstance()
                                            .getReference("Users")
                                            .child(uid);

                                    HashMap<String, Object> userData = new HashMap<>();
                                    HashMap<String, String> dogData = new HashMap<>();

                                    dogData.put(dogName, dogBreed);

                                    userData.put("name", fullName);
                                    userData.put("status", "I am a new user!");
                                    userData.put("image", "default");
                                    userData.put("dogs", dogData);

                                    databaseReference.setValue(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        }else{
                            Toast.makeText(RegisterActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private class ParseURL extends AsyncTask<Void, Void, JSONArray> {

        protected JSONArray doInBackground(Void... params) {
            String str="https://api-dog-breeds.herokuapp.com/api/dogs";
            URLConnection urlConn = null;
            BufferedReader bufferedReader = null;
            try
            {
                URL url = new URL(str);
                urlConn = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null)
                {
                    stringBuffer.append(line);
                }

                return new JSONArray(stringBuffer.toString());
            }
            catch(Exception ex)
            {
                Log.e("App", "yourDataTask", ex);
                return null;
            }
            finally
            {
                if(bufferedReader != null)
                {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(JSONArray response) {
            if(response != null)
            {
                try {
                    breeds.add("Choose a Breed");
                    for(int i = 0 ; i < response.length() ; i++){
                        String breed = response.getJSONObject(i).getString("breedName");
                        breeds.add(breed);

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                                RegisterActivity.this,
                                android.R.layout.select_dialog_item,
                                breeds);
                        atvBreeds.setThreshold(1);
                        atvBreeds.setAdapter(adapter);
                    }
                } catch (JSONException ex) {
                    Log.e("App", "Failure", ex);
                }
            }
        }
    }
}