package com.example.deen_dar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Match extends AppCompatActivity {
    private ImageView matching_nav, matches_nav, profile_nav;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<String> matchList;
    private ArrayList<User> likedUsers;
    private TextView name, location, phone, interests, gender;
    private ImageView profile_img;
    boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        matching_nav = findViewById(R.id.imageView2);
        matches_nav = findViewById(R.id.matching_img);
        profile_nav = findViewById(R.id.profile_img);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        matchList = new ArrayList<>();
        likedUsers = new ArrayList<>();
        check = false;

        profile_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle profile_nav click
                Intent intent = new Intent(Match.this, Profile.class);
                startActivity(intent);
            }
        });

        matching_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Match.this, Cards.class);
                startActivity(intent);
            }
        });

        matches_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Match.this, "You are already seeing the list of Matches!", Toast.LENGTH_SHORT).show();
            }
        });

        retrieveMatchList();
    }

    private void retrieveMatchList() {
        String Uid = user.getUid();
        db.collection("Users")
                .document(Uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot userDocument = task.getResult();
                            if (userDocument.exists()) {
                                ArrayList<String> fetchedMatchList = (ArrayList<String>) userDocument.get("matchList");
                                if (fetchedMatchList != null && !fetchedMatchList.isEmpty()) {
                                    matchList.clear(); // Clear the existing list before adding new elements
                                    matchList.addAll(fetchedMatchList); // Add all elements from fetchedMatchList
                                    searchDocuments();
                                } else {
                                    Toast.makeText(Match.this, "Could not retrieve matchList!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Match.this, "Could not find user!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Exception exception = task.getException();
                            Toast.makeText(Match.this, "Error: " + exception, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void searchDocuments() {
        if (!matchList.isEmpty()) {
            CollectionReference usersCollectionRef = db.collection("Users");

            // Create a query to search for documents where the "name" field is in the matchList
            Query query = usersCollectionRef.whereIn("fullname", matchList);

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();

                        if (querySnapshot.isEmpty()) {
                            return;
                        }

                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
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
                            likedUsers.add(user);
                        }
                        showcaseData();
                    } else {
                        Exception exception = task.getException();
                        Toast.makeText(Match.this, "Error: " + exception, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No matches!" + matchList, Toast.LENGTH_SHORT).show();
        }
    }

    private void showcaseData() {
        LinearLayout matchItemsContainer = findViewById(R.id.match_items_container);

        if (likedUsers.isEmpty()) {
            // Handle the case when there are no liked users
            Toast.makeText(Match.this, "Failed to fetch Liked Users!", Toast.LENGTH_SHORT).show();
            return;
        }

        for (User user : likedUsers) {
            // Inflate the layout for each user item
            View userItemView = LayoutInflater.from(this).inflate(R.layout.match_item_layout, null);

            // Get the views within the user item layout
            name = userItemView.findViewById(R.id.name);
            location = userItemView.findViewById(R.id.loc_data);
            interests = userItemView.findViewById(R.id.interest_data);
            phone = userItemView.findViewById(R.id.no_data);
            gender = userItemView.findViewById(R.id.gen_data);
            profile_img = userItemView.findViewById(R.id.profile_image);

            if (user.imageUri != null) {
                Glide.with(Match.this)
                        .load(user.imageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_img);
            }

            String start_msg = user.name + ", " + user.age;


            // Set the data to the views
            name.setText(start_msg);
            location.setText(user.location);
            phone.setText(user.phone);
            gender.setText(user.gender.toString());

            if (user.interests != null && !user.interests.isEmpty()) {
                String interestsText = TextUtils.join(", ", user.interests);
                Match.this.interests.setText(interestsText);
            } else {
                Match.this.interests.setText("No interests given.");
            }

            // Add the user item view to the match_items_container
            matchItemsContainer.addView(userItemView);
        }
    }



}
