package com.example.deen_dar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    TextView login_reg_title, slogan;
    ImageView logo;
    TextInputLayout fullname, username, email, password;
    private Button login_btn, go;
    private static final String TAG = "Registration";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);
        logo = findViewById(R.id.logo_image);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_reg_title = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        go = (Button) findViewById(R.id.button4);

        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            String s_fullname = fullname.getEditText().getText().toString();
            String s_username = username.getEditText().getText().toString();
            String s_email = email.getEditText().getText().toString();
            String s_password = password.getEditText().getText().toString();

            if(TextUtils.isEmpty(s_fullname)){
                Toast.makeText(Registration.this, "Please enter your Full Name!", Toast.LENGTH_LONG).show();
                fullname.setError("Full Name is a required field!");
                fullname.requestFocus();
            }
            else if(TextUtils.isEmpty(s_username)) {
                Toast.makeText(Registration.this, "Please enter your User Name!", Toast.LENGTH_LONG).show();
                username.setError("User Name is a required field!");
                username.requestFocus();
            }
            else if(s_username.length() > 15) {
                Toast.makeText(Registration.this, "User Name Length Exceeded!", Toast.LENGTH_LONG).show();
                username.setError("User Name must be within 15 characters!");
                username.requestFocus();
            }
            else if(TextUtils.isEmpty(s_email)) {
                Toast.makeText(Registration.this, "Please enter your Email!", Toast.LENGTH_LONG).show();
                email.setError("Email is a required field!");
                email.requestFocus();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(s_email).matches()) {
                Toast.makeText(Registration.this, "Please enter a valid Email!", Toast.LENGTH_LONG).show();
                email.setError("Email should be a valid one!");
                email.requestFocus();
            }
            else if(TextUtils.isEmpty(s_password)) {
                Toast.makeText(Registration.this, "Please enter your Password!", Toast.LENGTH_LONG).show();
                password.setError("Password field cannot be empty!");
                password.requestFocus();
            }
            else if(s_password.length() < 8) {
                Toast.makeText(Registration.this, "Please enter a valid Password!", Toast.LENGTH_LONG).show();
                password.setError("Password length must be greater than or equal to 8!");
                password.requestFocus();
            }
            else{
                registerUser(s_fullname, s_username, s_email, s_password);
            }

            }

        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Registration.this, Login.class);
                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(logo, "logo_tr");
                pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");
                pairs[2] = new Pair<View, String>(slogan, "slogan_tr");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Registration.this, pairs);
                startActivity(i, options.toBundle());
                finish();
            }
        });
    }

    private void registerUser(String s_fullname, String s_username, String s_email, String s_password){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(s_email, s_password).addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Registration.this, "User created successfully!", Toast.LENGTH_LONG).show();
                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    firebaseUser.sendEmailVerification();
                    Intent i = new Intent(Registration.this, VerificationCompletion.class);
                    Pair[] pairs = new Pair[3];
                    pairs[0] = new Pair<View, String>(logo, "logo_tr");
                    pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");

                    i.putExtra("username", s_username);
                    i.putExtra("fullname", s_fullname);

                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Registration.this, pairs);
                    startActivity(i, options.toBundle());
                    finish();
                }
                else {
                    try{
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        password.setError("Your password was too weak! Please use a mixture of alphabets, numbers and caracters");
                        password.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        email.setError("Your email is invalid or is already in use. Kindly re-enter your email.");
                        email.requestFocus();
                    }
                    catch (FirebaseAuthUserCollisionException e){
                        email.setError("User is already registered with this email! Please use another one.");
                        email.requestFocus();
                    }
                    catch (Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Registration.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    @Override
    public void onBackPressed(){
        login_reg_title = findViewById(R.id.logo_name);
        logo = findViewById(R.id.logo_image);

        Intent i = new Intent(Registration.this, Login_and_Reg.class);
        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(logo, "logo_tr");
        pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Registration.this, pairs);
        startActivity(i, options.toBundle());
        finish();
    }
}