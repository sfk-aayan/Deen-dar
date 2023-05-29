package com.example.deen_dar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    TextInputLayout email, password;
    TextView login_reg_title, slogan;
    ImageView logo;
    private Button reg_btn;
    private Button forgot_pwd_btn;
    private Button login_btn;
    private FirebaseAuth auth;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        logo = findViewById(R.id.logo_image);
        reg_btn = findViewById(R.id.reg_btn);
        login_reg_title = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);
        forgot_pwd_btn = findViewById(R.id.button3);
        login_btn = findViewById(R.id.button4);

        auth = FirebaseAuth.getInstance();

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login.this, Registration.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(logo, "logo_tr");
                pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");
                pairs[2] = new Pair<View, String>(slogan, "slogan_tr");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(i, options.toBundle());
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_email = email.getEditText().getText().toString();
                String s_password = password.getEditText().getText().toString();

                if (s_email.isEmpty()) {
                    email.setError("Please enter your email");
                    return;
                }

                if (s_password.isEmpty()) {
                    password.setError("Please enter your password");
                    return;
                }

                email.setError(null);
                password.setError(null);

                auth.signInWithEmailAndPassword(s_email, s_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = auth.getCurrentUser();
                                    if (user != null && user.isEmailVerified()) {
                                        Intent i = new Intent(Login.this, Profile.class);
                                        Pair[] pairs = new Pair[1];
                                        pairs[0] = new Pair<View, String>(logo, "logo_tr");
                                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i, options.toBundle());
                                    } else {
                                        Toast.makeText(Login.this, "Please verify your email address!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(Login.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        forgot_pwd_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_email = email.getEditText().getText().toString().trim();

                if (s_email.isEmpty()) {
                    email.setError("Please enter your email");
                    return;
                }

                email.setError(null);

                auth.sendPasswordResetEmail(s_email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Login.this, "Password Reset Email sent", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(Login.this, "Failed to send Password Reset Email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onBackPressed(){
        login_reg_title = findViewById(R.id.logo_name);

        Intent i = new Intent(Login.this, ProfileSetup.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(logo, "logo_tr");
        pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
        startActivity(i, options.toBundle());
    }
}