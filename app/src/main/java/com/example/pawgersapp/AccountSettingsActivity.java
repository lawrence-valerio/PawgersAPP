package com.example.pawgersapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
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
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

public class AccountSettingsActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    FirebaseUser currentUser;
    TextView tvName, tvStatus;
    Button btnUpdateStatus, btnUpdatePicture;
    static final int UPLOAD_CODE = 1;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    ImageView ivProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        tvName = findViewById(R.id.tv_settingsName);
        tvStatus = findViewById(R.id.tv_settingsStatus);
        btnUpdatePicture = findViewById(R.id.btn_changeImage);
        btnUpdateStatus = findViewById(R.id.btn_changeStatus);
        ivProfilePicture = findViewById(R.id.iv_settingsProfilePicture);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();

        databaseReference = FirebaseDatabase
                .getInstance()
                .getReference("Users")
                .child(currentUser.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String image = snapshot.child("image").getValue().toString();
                String name = snapshot.child("name").getValue().toString();
                String status = snapshot.child("status").getValue().toString();

                tvName.setText(name);
                tvStatus.setText(status);
                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.default_picture).into(ivProfilePicture);
                }else{
                    ivProfilePicture.setImageResource(R.drawable.default_picture);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnUpdatePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadPictureIntent = new Intent();
                uploadPictureIntent.setType("image/*");
                uploadPictureIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(uploadPictureIntent, "Choose a picture"), UPLOAD_CODE);
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