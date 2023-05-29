package com.example.deen_dar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class VerificationCompletion extends AppCompatActivity {

    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    ImageView logo;
    TextView verification_msg;
    Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_completion);

        auth = FirebaseAuth.getInstance();

        logo = findViewById(R.id.logo);
        verification_msg = findViewById(R.id.verification_msg);
        continue_btn = findViewById(R.id.continue_btn);

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser = auth.getCurrentUser();
                if (currentUser != null) {
                    currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                currentUser = auth.getCurrentUser();
                                if (currentUser.isEmailVerified()) {
                                    Intent i = new Intent(VerificationCompletion.this, ProfileSetup.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(VerificationCompletion.this, "Email is not verified!", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(VerificationCompletion.this, "Failed to reload user data!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}