package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class Profile extends AppCompatActivity {

    private TextView name_age, location, gender, height, phone, occupation, interests;
    private ImageView profilePic;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

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

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        String userId = auth.getCurrentUser().getUid();

        DocumentReference documentRef = db.collection("Users").document(userId);

        documentRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String age = documentSnapshot.getString("age");
                String location = documentSnapshot.getString("location");
                String gender = documentSnapshot.getString("gender");
                String height = documentSnapshot.getString("height");
                String phone = documentSnapshot.getString("phone");
                String occupation = documentSnapshot.getString("occupation");
                List<String> interests = (List<String>) documentSnapshot.get("interests");
                String imageUri = documentSnapshot.getString("image_url");


                if (imageUri != null) {
                    Glide.with(this).load(imageUri).into(profilePic);
                }
                String start_msg = name + ", " + age;

                Profile.this.name_age.setText(start_msg);
                Profile.this.location.setText(location);
                Profile.this.gender.setText(gender);
                Profile.this.height.setText(height);
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
    }
}
