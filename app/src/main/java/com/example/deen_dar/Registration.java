package com.example.deen_dar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Registration extends AppCompatActivity {
    TextView login_reg_title, slogan;
    ImageView logo;
    private Button login_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        logo = findViewById(R.id.logo_image);
        login_btn = (Button) findViewById(R.id.login_btn);
        login_reg_title = findViewById(R.id.logo_name);
        slogan = findViewById(R.id.slogan_name);


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
    }
}