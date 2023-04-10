package com.example.deen_dar;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;


public class Login_and_Reg extends AppCompatActivity {
    TextView intro_msg, login_reg_title;
    ImageView logo;
    Animation fade_in_anim;
    private Button login, reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_reg);
        intro_msg = findViewById(R.id.intro_msg);
        login_reg_title = findViewById(R.id.login_reg_title);
        fade_in_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        logo = findViewById(R.id.imageView);

        setText("Welcome to Deen-dar, the Halal Tinder!");

        login_reg_title.startAnimation(fade_in_anim);

        login = (Button) findViewById(R.id.login_btn);
        reg = (Button) findViewById(R.id.reg_btn);

        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_and_Reg.this, Login.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logo, "logo_tr");
                pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_and_Reg.this, pairs);
                startActivity(i, options.toBundle());
            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Login_and_Reg.this, Registration.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logo, "logo_tr");
                pairs[1] = new Pair<View, String>(login_reg_title, "title_tr");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_and_Reg.this, pairs);
                startActivity(i, options.toBundle());
            }
        });
    }

    public void setText(final String s)
    {
        final int[] i = new int[1];
        i[0] = 0;
        final int length = s.length();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                char c= s.charAt(i[0]);
                intro_msg.append(String.valueOf(c));
                i[0]++;
            }
        };
        final Timer timer = new Timer();
        TimerTask taskEverySplitSecond = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
                if (i[0] == length - 1) {
                    timer.cancel();
                }
            }
        };
        timer.schedule(taskEverySplitSecond, 1, 95);
    }
}