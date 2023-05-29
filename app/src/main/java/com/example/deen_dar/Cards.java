package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Cards extends AppCompatActivity {
    private ArrayList<User> userList;
    private ArrayList<User> matchedUsers;
    private int currentUserIndex = 0;
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private ImageView profile_img;
    private TextView name_age;
    private TextView interests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        profile_img = findViewById(R.id.imageView7);
        name_age = findViewById(R.id.textView8);
        interests = findViewById(R.id.interests);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        getCurrentUser();
        fetchUsers();
    }

    private void fetchUsers() {
        db = FirebaseFirestore.getInstance();
        CollectionReference usersCollectionRef = db.collection("Users");

        usersCollectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null) {
                    userList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        // Parse user data and add it to the userList
                        String name = document.getString("fullname");
                        String username = document.getString("username");
                        String location = document.getString("location");
                        User.Gender gender = User.Gender.valueOf(document.getString("gender"));
                        String age = document.getString("age");
                        String imageUri = document.getString("image_url");
                        String occupation = document.getString("occupation");
                        String height = document.getString("height");
                        String phone = document.getString("phone");
                        ArrayList<String> interests = (ArrayList<String>) document.get("interests");

                        User user = new User(name, username, location, gender, age, occupation, height, phone, imageUri, interests);
                        userList.add(user);
                    }

                    findMatchesForCurrentUser();  // Find matches for the current user
                    displayCurrentUser();  // Update the views after fetching the users
                }
            } else {
                Exception exception = task.getException();
                if (exception != null) {
                    String errorMessage = "Error fetching users: " + exception.getMessage();
                    Toast.makeText(Cards.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void getCurrentUser() {
        if (user != null) {
            String userId = user.getUid();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        // Parse current user data
                        String name = document.getString("fullname");
                        String username = document.getString("username");
                        String location = document.getString("location");
                        User.Gender gender = User.Gender.valueOf(document.getString("gender"));
                        String age = document.getString("age");
                        String imageUri = document.getString("image_url");
                        String occupation = document.getString("occupation");
                        String height = document.getString("height");
                        String phone = document.getString("phone");
                        ArrayList<String> interests = (ArrayList<String>) document.get("interests");

                        currentUser = new User(name, username, location, gender, age, imageUri, occupation, height, phone, interests);
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception != null) {
                        String errorMessage = "Error: " + exception.getMessage();
                        Toast.makeText(Cards.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void displayCurrentUser() {
        if (matchedUsers != null && matchedUsers.size() > 0 && currentUserIndex < matchedUsers.size()) {
            // Get the current user from the userList
            User currentMatch = matchedUsers.get(currentUserIndex);

            // Update the views with the current user's data
            String imageUri = currentMatch.imageUri;

            if (imageUri != null) {
                int widthInPixels = profile_img.getWidth();
                int heightInPixels = profile_img.getHeight();

                Glide.with(Cards.this)
                        .load(imageUri)
                        .override(widthInPixels, heightInPixels)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(profile_img);
            }


            String s_name = currentMatch.name;
            String s_age = currentMatch.age;
            String start_msg = s_name + ", " + s_age;

            name_age.setText(start_msg);

            if (currentMatch.interests != null && !currentMatch.interests.isEmpty()) {
                String interestsText = TextUtils.join(", ", currentMatch.interests);
                Cards.this.interests.setText(interestsText);
            } else {
                Cards.this.interests.setText("No interests given.");
            }
        }
    }

    private void findMatchesForCurrentUser() {
        matchedUsers = User.findMatches(currentUser, userList);
    }


}