package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Cards extends AppCompatActivity {
    private ArrayList<User> userList;
    private ArrayList<User> matchedUsers;
    private ArrayList<User> copyUsers;
    private ArrayList<String> matchList;
    private int currentUserIndex;
    private User currentUser;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    private ImageView profile_img, profile_nav, matching_nav, matches_nav;
    private TextView name_age;
    private TextView interests;
    Button like, dislike;
    private LinearLayout linear_gesture;
    private GestureDetector gestureDetector;
    private SharedPreferences sharedPreferences;
    private Set<String> matchedUserNames;
    private ProgressBar progressBar;
    private String Uid;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);
        profile_img = findViewById(R.id.imageView7);
        name_age = findViewById(R.id.textView8);
        interests = findViewById(R.id.interests);
        linear_gesture = findViewById(R.id.linear_gesture);
        like = findViewById(R.id.button);
        dislike = findViewById(R.id.button2);
        profile_nav = findViewById(R.id.imageView10);
        matching_nav = findViewById(R.id.imageView2);
        matches_nav = findViewById(R.id.imageView9);
        progressBar = findViewById(R.id.progressBar);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        Uid = user.getUid();
        matchList = new ArrayList<>();
        copyUsers = new ArrayList<>();

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());
        sharedPreferences = getSharedPreferences("matchedUsers", Context.MODE_PRIVATE);
        // Retrieve the value of currentUserIndex from SharedPreferences
        currentUserIndex = sharedPreferences.getInt("currentUserIndex"+Uid, 0);


        //Debug Code
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("matchedUsers"+Uid);
        editor.remove("currentUserIndex"+Uid);
        editor.apply();

        linear_gesture.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });

        getCurrentUser();
        fetchUsers();

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (matchedUsers != null && matchedUsers.size() > 0 && currentUserIndex < matchedUsers.size()) {
                    LayoutInflater inflater = getLayoutInflater();
                    View toastLayout = inflater.inflate(R.layout.toast_matched_with, findViewById(R.id.toast_linear));

                    toastLayout.setScaleX(0f);
                    toastLayout.setScaleY(0f);

                    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleX", 0f, 1f);
                    scaleXAnimator.setDuration(500);

                    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleY", 0f, 1f);
                    scaleYAnimator.setDuration(500);

                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
                    animatorSet.start();

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(toastLayout);
                    toast.show();

                    String userId = user.getUid();

                    //store data locally
                    String matchedUserName = matchedUsers.get(currentUserIndex).name;
                    matchedUserNames.add(matchedUserName);
                    sharedPreferences.edit().putStringSet("matchedUsers"+userId, matchedUserNames).apply();


                    DocumentReference userRef = db.collection("Users").document(userId);

                    userRef.get().addOnCompleteListener(getTask -> {
                        if (getTask.isSuccessful()) {
                            DocumentSnapshot document = getTask.getResult();
                            if (document.exists()) {
                                ArrayList<String> matchList = (ArrayList<String>) document.get("matchList");
                                if (matchList == null) {
                                    matchList = new ArrayList<>();
                                }

                                matchList.add(matchedUserName);

                                Map<String, Object> updateData = new HashMap<>();
                                updateData.put("matchList", matchList);

                                userRef.set(updateData, SetOptions.merge())
                                        .addOnCompleteListener(setTask -> {
                                            if (setTask.isSuccessful()) {
                                                // Update operation successful
                                                // Handle any additional logic here
                                            } else {
                                                // Update operation failed
                                                // Handle the error here
                                            }
                                        });
                            } else {
                                // Document doesn't exist
                                // Handle the error here
                            }
                        } else {
                            // Error fetching document
                            // Handle the error here
                        }
                    });

                    currentUserIndex++;
                    displayCurrentUser();
                } else {
                    Toast.makeText(Cards.this, "No more users found", Toast.LENGTH_SHORT).show();
                }
            }
        });


        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View toastLayout = inflater.inflate(R.layout.toast_thumbs_down, findViewById(R.id.toast_linear));

                toastLayout.setScaleX(0f);
                toastLayout.setScaleY(0f);

                ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleX", 0f, 1f);
                scaleXAnimator.setDuration(500);

                ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleY", 0f, 1f);
                scaleYAnimator.setDuration(500);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
                animatorSet.start();

                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(toastLayout);
                toast.show();

                //Store data locally
                String matchedUserName = matchedUsers.get(currentUserIndex).name;
                matchedUserNames.add(matchedUserName);
                sharedPreferences.edit().putStringSet("matchedUsers"+Uid, matchedUserNames).apply();
                currentUserIndex++;
                displayCurrentUser();
            }
        });

        profile_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle profile_nav click
                Intent intent = new Intent(Cards.this, Profile.class);
                startActivity(intent);
            }
        });

        matching_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Cards.this, "You are already in Matching!", Toast.LENGTH_SHORT).show();
            }
        });

        matches_nav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Cards.this, Match.class);
                startActivity(intent);
            }
        });
    }

    private void fetchUsers() {

        progressBar.setVisibility(View.VISIBLE);
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

                    progressBar.setVisibility(View.GONE);
                    findMatchesForCurrentUser();  // Find matches for the current user
                    if(!matchedUsers.isEmpty() && matchedUsers != null)
                        displayCurrentUser();  // Update the views after fetching the users
                    else {
                        Intent i = new Intent(Cards.this, NoMoreMatch.class);
                        startActivity(i);
                    }
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

            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
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

            // Apply fade-in animation to the views
            AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
            fadeInAnimation.setDuration(1000); // Set the duration of the animation in milliseconds
            profile_img.startAnimation(fadeInAnimation);
            name_age.startAnimation(fadeInAnimation);
            interests.startAnimation(fadeInAnimation);

            name_age.setText(start_msg);

            if (currentMatch.interests != null && !currentMatch.interests.isEmpty()) {
                String interestsText = TextUtils.join(", ", currentMatch.interests);
                Cards.this.interests.setText(interestsText);
            } else {
                Cards.this.interests.setText("No interests given.");
            }
        }
        else{
            Intent i = new Intent(Cards.this, NoMoreMatch.class);
            startActivity(i);
        }
    }

    private void findMatchesForCurrentUser() {
        progressBar.setVisibility(View.VISIBLE);
        if(currentUser != null){
            matchedUserNames = sharedPreferences.getStringSet("matchedUsers"+Uid, new HashSet<>());
            matchedUsers = User.findMatches(currentUser, userList);

            ArrayList<User> unmatchedUsers = new ArrayList<>();
            for(User user: matchedUsers){
                if(!matchedUserNames.contains(user.name)){
                    unmatchedUsers.add(user);
                }
            }
            matchedUsers = unmatchedUsers;

            if (currentUserIndex >= matchedUsers.size()) {
                currentUserIndex = matchedUsers.size() - 1;
            }
            progressBar.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(Cards.this, "Loading Users!", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(Cards.this, Cards.class);
            startActivity(i);
        }
    }

    private void onSwipeRight() {
        if (matchedUsers != null && matchedUsers.size() > 0 && currentUserIndex < matchedUsers.size()) {
            LayoutInflater inflater = getLayoutInflater();
            View toastLayout = inflater.inflate(R.layout.toast_matched_with, findViewById(R.id.toast_linear));

            toastLayout.setScaleX(0f);
            toastLayout.setScaleY(0f);

            ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleX", 0f, 1f);
            scaleXAnimator.setDuration(500);

            ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleY", 0f, 1f);
            scaleYAnimator.setDuration(500);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
            animatorSet.start();

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastLayout);
            toast.show();

            String userId = user.getUid();

            //store data locally
            String matchedUserName = matchedUsers.get(currentUserIndex).name;
            matchedUserNames.add(matchedUserName);
            sharedPreferences.edit().putStringSet("matchedUsers"+userId, matchedUserNames).apply();

            DocumentReference userRef = db.collection("Users").document(userId);

            userRef.get().addOnCompleteListener(getTask -> {
                if (getTask.isSuccessful()) {
                    DocumentSnapshot document = getTask.getResult();
                    if (document.exists()) {
                        ArrayList<String> matchList = (ArrayList<String>) document.get("matchList");
                        if (matchList == null) {
                            matchList = new ArrayList<>();
                        }

                        matchList.add(matchedUserName);

                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("matchList", matchList);

                        userRef.set(updateData, SetOptions.merge())
                                .addOnCompleteListener(setTask -> {
                                    if (setTask.isSuccessful()) {
                                        // Update operation successful
                                        // Handle any additional logic here
                                    } else {
                                        // Update operation failed
                                        // Handle the error here
                                    }
                                });
                    } else {
                        // Document doesn't exist
                        // Handle the error here
                    }
                } else {
                    // Error fetching document
                    // Handle the error here
                }
            });

            currentUserIndex++;
            displayCurrentUser();
        } else {
            Toast.makeText(Cards.this, "No more users found", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSwipeLeft() {
        LayoutInflater inflater = getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.toast_thumbs_down, findViewById(R.id.toast_linear));

        toastLayout.setScaleX(0f);
        toastLayout.setScaleY(0f);

        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleX", 0f, 1f);
        scaleXAnimator.setDuration(500);

        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(toastLayout, "scaleY", 0f, 1f);
        scaleYAnimator.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleXAnimator, scaleYAnimator);
        animatorSet.start();

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();

        //Store data locally
        String matchedUserName = matchedUsers.get(currentUserIndex).name;
        matchedUserNames.add(matchedUserName);
        sharedPreferences.edit().putStringSet("matchedUsers"+Uid, matchedUserNames).apply();

        currentUserIndex++;
        displayCurrentUser();
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
                result = true;
            }

            return result;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("currentUserIndex"+Uid, currentUserIndex);
        editor.apply();
    }


}