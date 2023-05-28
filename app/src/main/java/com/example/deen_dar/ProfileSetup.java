package com.example.deen_dar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileSetup extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;

    private TextInputLayout location, age, height, phone, occupation;
    private RadioGroup gender_btn;
    private ImageView profileImage;
    private Button uploadImage_btn, go_btn;
    private CheckBox chkRead, chkCook, chkSport, chkWrite, chkTravel, chkQuran;

    private Uri selectedImageUri;
    String username, fullname, gender;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private List<String> interests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);


        Intent intent = getIntent();
        interests = new ArrayList<>();
        username = intent.getStringExtra("username");
        fullname = intent.getStringExtra("fullname");

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        profileImage = findViewById(R.id.profileImage);
        location = findViewById(R.id.fullname);
        age = findViewById(R.id.fullname1);
        height = findViewById(R.id.fullname2);
        phone = findViewById(R.id.fullname3);
        occupation = findViewById(R.id.fullname4);

        uploadImage_btn = findViewById(R.id.uploadImage_btn);
        go_btn = findViewById(R.id.button4);
        gender_btn = findViewById(R.id.radioGroup);


        chkRead = findViewById(R.id.chkRead);
        chkCook = findViewById(R.id.chkCook);
        chkSport = findViewById(R.id.chkSport);
        chkWrite = findViewById(R.id.chkWrite);
        chkTravel = findViewById(R.id.chkTravel);
        chkQuran = findViewById(R.id.chkQuran);

        uploadImage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(ProfileSetup.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openImagePicker();
                } else {
                    ActivityCompat.requestPermissions(ProfileSetup.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_REQUEST_CODE);
                }
            }
        });

        go_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileInfo();
            }
        });

        chkRead.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interests.add(chkRead.getText().toString());
                } else {
                    interests.remove(chkRead.getText().toString());
                }
            }
        });

        chkCook.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interests.add(chkCook.getText().toString());
                } else {
                    interests.remove(chkCook.getText().toString());
                }
            }
        });

        chkSport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interests.add(chkSport.getText().toString());
                } else {
                    interests.remove(chkSport.getText().toString());
                }
            }
        });

        chkWrite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interests.add(chkWrite.getText().toString());
                } else {
                    interests.remove(chkWrite.getText().toString());
                }
            }
        });

        chkTravel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interests.add(chkTravel.getText().toString());
                } else {
                    interests.remove(chkTravel.getText().toString());
                }
            }
        });

        chkQuran.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    interests.add(chkQuran.getText().toString());
                } else {
                    interests.remove(chkQuran.getText().toString());
                }
            }
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Storage Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void saveProfileInfo() {
        String s_location = this.location.getEditText().getText().toString().trim();
        String s_age = this.age.getEditText().getText().toString().trim();
        String s_height = this.height.getEditText().getText().toString().trim();
        String s_phone = this.phone.getEditText().getText().toString().trim();
        String s_occupation = this.occupation.getEditText().getText().toString().trim();

        int selectedRadioButtonId = gender_btn.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            gender = selectedRadioButton.getText().toString();
        }

        if (s_location.isEmpty() || s_age.isEmpty() || s_height.isEmpty() || s_phone.isEmpty() || s_occupation.isEmpty() || selectedImageUri == null || gender.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("profile_images/" + System.currentTimeMillis() + ".jpg");

        storageRef.putFile(selectedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                        downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUrl) {
                                String imageUrl = downloadUrl.toString();


                                String userId = auth.getCurrentUser().getUid();
                                Map<String, Object> profileData = new HashMap<>();
                                profileData.put("location", s_location);
                                profileData.put("age", s_age);
                                profileData.put("height", s_height);
                                profileData.put("phone", s_phone);
                                profileData.put("occupation", s_occupation);
                                profileData.put("username", username);
                                profileData.put("fullname", fullname);
                                profileData.put("gender", gender);
                                profileData.put("image_url", imageUrl);
                                profileData.put("interests", interests);

                                firestore.collection("Users").document(userId)
                                        .set(profileData)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(ProfileSetup.this, "Profile information saved successfully", Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(ProfileSetup.this, Profile.class);
                                                    startActivity(i);
                                                    finish();
                                                } else {
                                                    Toast.makeText(ProfileSetup.this, "Failed to save profile information", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the image upload failure
                        Toast.makeText(ProfileSetup.this, "Failed to upload image!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}