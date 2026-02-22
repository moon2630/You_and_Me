package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class skip_signin extends AppCompatActivity {

    AppCompatButton btnSignIn, btnCreateAccount;
    Animation top;
    RelativeLayout rl_anime;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skip_signin);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        // Initialization
        btnSignIn = findViewById(R.id.btnSignIn);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        rl_anime = findViewById(R.id.relative_anime);

        // Top animation
        top = AnimationUtils.loadAnimation(this, R.anim.top);
        rl_anime.setAnimation(top);

        btnSignIn.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), signUp_and_logIn_page.class);
            i.putExtra("status", "SignIn");
            startActivity(i);
            finish();
        });

        btnCreateAccount.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), signUp_and_logIn_page.class);
            i.putExtra("status", "CreateAccount");
            startActivity(i);
            finish();
        });
    }
}