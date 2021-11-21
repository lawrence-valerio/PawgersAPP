package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

public class AccountSettingsActivity extends AppCompatActivity {
    View loadingView;
    ProgressBar loadingSpinner;
    EditText etEmail, etDogName, etName;
    AutoCompleteTextView atvBreed;
    Button btnSaveChanges, btnUploadPicture, btnEditAccount;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;
    String currentUserUID;
    Boolean editingCheck;
    ImageView ivProfilePicture;
    static final int UPLOAD_CODE = 1;
    ProgressDialog progressDialog;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        currentUserUID = currentUser.getUid();
        btnSaveChanges = findViewById(R.id.btn_saveChanges);
        etName = findViewById(R.id.account_fullName);
        loadingSpinner = findViewById(R.id.account_loader);
        loadingView = findViewById(R.id.account_loadingView);
        atvBreed = findViewById(R.id.atv_accountBreed);
        etDogName = findViewById(R.id.et_accountDogName);
        etEmail = findViewById(R.id.et_emailAccount);
        btnEditAccount = findViewById(R.id.btn_editInformation);
        btnUploadPicture = findViewById(R.id.btn_UploadPicture);
        ivProfilePicture = findViewById(R.id.iv_accountPicture);
        editingCheck = false;

        //Top navbar
        toolbar = findViewById(R.id.accountNavbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(currentUser.getUid());

        setData();

        btnUploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadPictureIntent = new Intent();
                uploadPictureIntent.setType("image/*");
                uploadPictureIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(uploadPictureIntent, "Choose a picture"), UPLOAD_CODE);
            }
        });

        btnEditAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editingCheck.equals(false)){
                    editingCheck = true;

                    btnEditAccount.setText("Cancel");
                    btnSaveChanges.setVisibility(View.VISIBLE);
                    btnUploadPicture.setVisibility(View.VISIBLE);

                    etName.setEnabled(true);
                    etDogName.setEnabled(true);
                    atvBreed.setEnabled(true);
                }else{
                    editingCheck = false;

                    btnEditAccount.setText("Edit Information");
                    btnSaveChanges.setVisibility(View.GONE);
                    btnUploadPicture.setVisibility(View.GONE);

                    etName.setEnabled(false);
                    etDogName.setEnabled(false);
                    atvBreed.setEnabled(false);
                }

            }
        });
    }

    public void setData(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                String name = user.getName();
                String email = currentUser.getEmail();
                String breed = user.getDogs().getDogBreed();
                String image = user.getImage();

                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.default_picture).into(ivProfilePicture);
                }else{
                    ivProfilePicture.setImageResource(R.drawable.default_picture);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == UPLOAD_CODE && resultCode == RESULT_OK){
            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(AccountSettingsActivity.this);
                progressDialog.setTitle("Uploading Image...");
                progressDialog.setMessage("Please wait while we process the image.");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();

                StorageReference picturesRef = storageReference.child("profile_pictures");
                StorageReference uploadedPictureRef = picturesRef.child(currentUser.getUid() + ".jpg");
                uploadedPictureRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            uploadedPictureRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imageUrl = uri.toString();

                                    databaseReference.child("image").setValue(imageUrl)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });
                                }
                            });
                        }else{
                            Toast.makeText(AccountSettingsActivity.this, "Upload failed.", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}