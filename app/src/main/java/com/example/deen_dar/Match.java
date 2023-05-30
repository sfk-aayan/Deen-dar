package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class Match extends AppCompatActivity {
    private ImageView matching_nav, matches_nav, profile_nav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        matching_nav = findViewById(R.id.imageView2);
        matches_nav = findViewById(R.id.matching_img);
        profile_nav = findViewById(R.id.profile_img);

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
    }
}