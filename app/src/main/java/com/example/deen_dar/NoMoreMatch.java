package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NoMoreMatch extends AppCompatActivity {
    private ImageView matchesImg, matchingImg, profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_more_match);
        matchingImg = findViewById(R.id.imageView2);
        matchesImg = findViewById(R.id.imageView9);
        profileImg = findViewById(R.id.imageView10);

        matchingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NoMoreMatch.this, Cards.class);
                startActivity(i);
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NoMoreMatch.this, Profile.class);
                startActivity(i);
            }
        });

        matchesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoMoreMatch.this, Match.class);
                startActivity(intent);
            }
        });
    }
}