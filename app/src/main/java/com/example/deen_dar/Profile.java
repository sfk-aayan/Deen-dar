package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Profile extends AppCompatActivity {

    private TextView name_age, location, gender, height, phone, occupation, interests, father_no;
    private ImageView profilePic, matchesImg, matchingImg, profileImg, matches_icon;
    private Button logout_btn;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        name_age = findViewById(R.id.textView);
        location = findViewById(R.id.location);
        gender = findViewById(R.id.gender);
        height = findViewById(R.id.height);
        phone = findViewById(R.id.phone);
        occupation = findViewById(R.id.occupation);
        interests = findViewById(R.id.interests);
        profilePic = findViewById(R.id.imageView4);
        father_no = findViewById(R.id.textView6);

        matches_icon = findViewById(R.id.matches_img);
        matchingImg = findViewById(R.id.imageView2);
        matchesImg = findViewById(R.id.matching_img);
        profileImg = findViewById(R.id.profile_img);
        logout_btn = findViewById(R.id.button5);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference documentRef = db.collection("Users").document(userId);
        progressBar.setVisibility(View.VISIBLE);

        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("fullname");
                String age = documentSnapshot.getString("age");
                String location = documentSnapshot.getString("location");
                String gender = documentSnapshot.getString("gender");
                String height = documentSnapshot.getString("height");
                String phone = documentSnapshot.getString("phone");
                String occupation = documentSnapshot.getString("occupation");
                List<String> interests = (List<String>) documentSnapshot.get("interests");
                String imageUri = documentSnapshot.getString("image_url");


                if (imageUri != null) {
                    Glide.with(Profile.this)
                            .load(imageUri)
                            .apply(RequestOptions.circleCropTransform())
                            .into(profilePic);
                }

                String start_msg = name + ", " + age;
                String father_msg = "Father's Number: ";

                progressBar.setVisibility(View.GONE);

                Profile.this.name_age.setText(start_msg);
                Profile.this.location.setText(location);
                Profile.this.gender.setText(gender);
                Profile.this.height.setText(height);
                if(gender.equals("Female")) {
                    Profile.this.father_no.setText(father_msg);
                }
                Profile.this.phone.setText(phone);
                Profile.this.occupation.setText(occupation);

                if (interests != null && !interests.isEmpty()) {
                    String interestsText = TextUtils.join(", ", interests);
                    Profile.this.interests.setText(interestsText);
                } else {
                    Profile.this.interests.setText("No interests given.");
                }
            }
        });

        matchingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Cards.class);
                startActivity(i);
            }
        });

        matches_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile.this, Match.class);
                startActivity(i);
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Profile.this, "You are already in Profile!", Toast.LENGTH_SHORT).show();
            }
        });

        matchesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, Match.class);
                startActivity(intent);
            }
        });

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent i = new Intent(Profile.this, Login_and_Reg.class);
                startActivity(i);
                finish();
            }
        });
    }
}
