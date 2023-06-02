package com.example.deen_dar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import android.net.Uri;
import java.util.ArrayList;

public class Match extends AppCompatActivity {
    private ImageView matching_nav, matches_nav, profile_nav;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ArrayList<String> matchList;
    private ArrayList<User> likedUsers;
    private TextView name, location, phone, interests, gender, father_no;
    private ImageView profile_img;
    boolean check;
    private String currentUserName;

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
                                currentUserName = userDocument.get("fullname").toString();
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

                            if (document.contains("matchList")) {
                                ArrayList<String> fetchedMatchList = (ArrayList<String>) document.get("matchList");
                                // Check if the current user is present in the fetched match list
                                if (fetchedMatchList != null && fetchedMatchList.contains(currentUserName)) {
                                    User user = new User(name, username, location, gender, age, occupation, height, phone, imageUri, interests);
                                    likedUsers.add(user);
                                }
                            }
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
            Toast.makeText(Match.this, "No Liked Users!", Toast.LENGTH_SHORT).show();
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
            father_no = userItemView.findViewById(R.id.textView14);

            if (user.imageUri != null) {
                Glide.with(Match.this)
                        .load(user.imageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profile_img);
            }

            String start_msg = user.name + ", " + user.age;
            String father_msg = "Father's no:";

            // Set the data to the views
            name.setText(start_msg);
            location.setText(user.location);
            if(user.gender == User.Gender.Female){
                father_no.setText(father_msg);
            }
            phone.setText(user.phone);
            gender.setText(user.gender.toString());

            if (user.interests != null && !user.interests.isEmpty()) {
                String interestsText = TextUtils.join(", ", user.interests);
                Match.this.interests.setText(interestsText);
            } else {
                Match.this.interests.setText("No interests given.");
            }

            final String phoneNumber = user.phone;

            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Open the dialer with the phone number
                    Intent dialerIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                    startActivity(dialerIntent);
                }
            });

            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String locationText = location.getText().toString();

                    // Check if Google Maps app is installed
                    boolean isGoogleMapsInstalled = isAppInstalled("com.google.android.apps.maps");

                    if (isGoogleMapsInstalled) {
                        // Open location in Google Maps
                        Uri locationUri = Uri.parse("geo:0,0?q=" + locationText);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, locationUri);
                        mapIntent.setPackage("com.google.android.apps.maps");

                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        } else {
                            // Fallback: Open location in web browser
                            openLocationInBrowser(locationText);
                        }
                    } else {
                        // Fallback: Open location in web browser
                        openLocationInBrowser(locationText);
                    }
                }
            });



            // Add the user item view to the match_items_container
            matchItemsContainer.addView(userItemView);
        }
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void openLocationInBrowser(String location) {
        Uri locationUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=" + location);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, locationUri);
        startActivity(browserIntent);
    }

}
