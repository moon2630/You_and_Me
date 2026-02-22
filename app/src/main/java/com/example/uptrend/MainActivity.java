package com.example.uptrend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
        // 1. Simple appear
        AlphaAnimation appear = new AlphaAnimation(0, 1);
        appear.setDuration(500);

        // 2. Simple pulse (grow and shrink)
        ScaleAnimation pulse = new ScaleAnimation(
                1f, 1.2f, 1f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        pulse.setDuration(600);
        pulse.setRepeatCount(2);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setStartOffset(500);

        AnimationSet birdAnim = new AnimationSet(false);
        birdAnim.addAnimation(appear);
        birdAnim.addAnimation(pulse);

        // 3. Simple text fade with delay
        AlphaAnimation textFade = new AlphaAnimation(0, 1);
        textFade.setDuration(800);
        textFade.setStartOffset(1000);

        birdLogo.startAnimation(birdAnim);
        appNameText.startAnimation(textFade);
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

        Intent intent;
        if (user != null) {
            intent = new Intent(MainActivity.this, select_language.class);
        } else {
            intent = new Intent(MainActivity.this, select_language.class);
        }

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