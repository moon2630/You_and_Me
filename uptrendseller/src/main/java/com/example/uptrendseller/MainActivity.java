package com.example.uptrendseller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_SCREEN = 2400; // 2.5 seconds
    private FirebaseUser user;
    private ImageView birdLogo;
    private TextView appNameText;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FULL SCREEN
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
        );

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_main);

        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        birdLogo = findViewById(R.id.bird_logo);
        appNameText = findViewById(R.id.appNameText);
        progressBar = findViewById(R.id.progressBar);

        // Start the animation sequence
        startSplashAnimations();
        startProgressBar();
    }

    private void startSplashAnimations() {
        // 1. Bird spins and grows from center
        RotateAnimation spin = new RotateAnimation(
                0, 720, // Two full rotations
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        spin.setDuration(1200);
        spin.setInterpolator(new DecelerateInterpolator());

        ScaleAnimation grow = new ScaleAnimation(
                0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        grow.setDuration(1200);

        AnimationSet birdAnim = new AnimationSet(true);
        birdAnim.addAnimation(spin);
        birdAnim.addAnimation(grow);

        // 2. App name circles around bird
        AnimationSet nameAnim = new AnimationSet(true);

        // Circular path animation (simulated)
        TranslateAnimation circleX = new TranslateAnimation(
                0, 0, 0, 0
        );
        circleX.setDuration(1500);

        // Fade in while circling
        AlphaAnimation nameFade = new AlphaAnimation(0, 1);
        nameFade.setDuration(800);
        nameFade.setStartOffset(700);

        nameAnim.addAnimation(nameFade);
        nameAnim.setStartOffset(500);



        // 4. Bird gentle glow pulse
        AlphaAnimation glow = new AlphaAnimation(1f, 0.7f);
        glow.setDuration(800);
        glow.setStartOffset(1500);
        glow.setRepeatCount(Animation.INFINITE);
        glow.setRepeatMode(Animation.REVERSE);

        // Start animations
        birdLogo.startAnimation(birdAnim);
        appNameText.startAnimation(nameAnim);

        new Handler().postDelayed(() -> {
            birdLogo.startAnimation(glow);
        }, 1500);
    }


    private void startProgressBar() {
        // Animate progress bar from 0 to 100
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;

                    // Update progress bar on UI thread
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });

                    try {
                        // Sleep for 23ms (100% in 2300ms)
                        Thread.sleep(24);  // CHANGED FROM 25 to 23
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // When progress completes, navigate
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        navigateToNextScreen();
                    }
                });
            }
        }).start();
    }
    private void navigateToNextScreen() {
        // Stop any ongoing animations
        birdLogo.clearAnimation();
        appNameText.clearAnimation();
        progressBar.clearAnimation();

        // Skip fade out animation and navigate directly
        Intent intent = user != null
                ? new Intent(getApplicationContext(), dashboard_admin.class)
                : new Intent(MainActivity.this, tutorial.class);

        // Start activity with no animation
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}