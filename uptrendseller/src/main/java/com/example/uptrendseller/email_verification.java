package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.github.muddz.styleabletoast.StyleableToast;

public class email_verification extends AppCompatActivity {

    private ProgressBar progressBar2;
    private int valueProgress = 0;
    private LinearLayout linearLayout_0;
    private CardView cardView_0;
    private loadingDialog loading;
    private AppCompatButton btn_verify;
    private TextView txt_percentage_verification, txt_email, txt_verify, click_info_0;
    private FirebaseAuth auth;
    private Handler handler;
    private Runnable verificationCheckRunnable;
    private static final long VERIFICATION_CHECK_INTERVAL = 5000; // Check every 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.green_meee));
        }

        // Initialize Firebase and views
        auth = FirebaseAuth.getInstance();
        progressBar2 = findViewById(R.id.progressbar_email_verification);
        txt_percentage_verification = findViewById(R.id.text_percentage_email_verification);
        txt_email = findViewById(R.id.txt_email);
        txt_verify = findViewById(R.id.txt_verify);
        btn_verify = findViewById(R.id.btn_verification);
        click_info_0 = findViewById(R.id.click_info_0);
        linearLayout_0 = findViewById(R.id.linearLayout_0);
        cardView_0 = findViewById(R.id.cardView_0);
        loading = new loadingDialog(this);
        handler = new Handler(Looper.getMainLooper());

        // Set email from intent
        String email = getIntent().getStringExtra("email");
        if (email != null && !email.isEmpty()) {
            txt_email.setText(email);
        } else {
            txt_email.setText("No email provided");
            StyleableToast.makeText(getApplicationContext(), "No email provided in intent", R.style.UptrendToast).show();
        }

        // Button click listener for email verification
        btn_verify.setOnClickListener(v -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                if (!user.isEmailVerified()) {
                    String emailToVerify = txt_email.getText().toString().trim();
                    if (!emailToVerify.isEmpty()) {
                        verifyEmail(emailToVerify);
                    } else {
                        StyleableToast.makeText(getApplicationContext(), "Email address is empty", R.style.UptrendToast).show();
                    }
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Email already verified", R.style.UptrendToast).show();
                }
            } else {
                StyleableToast.makeText(getApplicationContext(), "No user logged in", R.style.UptrendToast).show();
            }
        });

        // Initialize instruction effect (expand/collapse CardView)
        click_info_0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (cardView_0.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(linearLayout_0, new AutoTransition());
                cardView_0.setVisibility(var);
            }
        });

        // Start periodic verification check
        startVerificationCheck();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        valueProgress = sharedPreferences.getInt("process", 0);
        if (valueProgress >= 0) {
            progressBar2.setProgress(valueProgress);
            txt_percentage_verification.setText(valueProgress + "%");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop periodic verification check when activity stops
        if (handler != null && verificationCheckRunnable != null) {
            handler.removeCallbacks(verificationCheckRunnable);
        }
    }

    private void updateProgress(int value) {
        valueProgress = value;
        progressBar2.setProgress(value);
        txt_percentage_verification.setText(value + "%");
    }

    private void verifyEmail(String email) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            Log.d("EmailVerification", "Sending verification email to: " + email);
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        StyleableToast.makeText(getApplicationContext(), "Verification email sent to " + email, R.style.UptrendToast).show();
                    } else {
                        Log.e("EmailVerification", "Failed to send verification email: " + task.getException().getMessage());
                        StyleableToast.makeText(getApplicationContext(), "Failed to send verification email: " + task.getException().getMessage(), R.style.UptrendToast).show();
                    }
                }
            });
        } else {
            Log.d("EmailVerification", "No user logged in for email verification");
            StyleableToast.makeText(getApplicationContext(), "No user logged in", R.style.UptrendToast).show();
        }
    }

    private void startVerificationCheck() {
        verificationCheckRunnable = new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    Log.d("EmailVerification", "Checking verification status for user: " + user.getEmail());
                    user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (user.isEmailVerified()) {
                                    Log.d("EmailVerification", "Email verified, navigating to seller_information");
                                    loading.show();
                                    txt_verify.setText("Your Email is Verified...");
                                    txt_verify.setTextColor(getColor(R.color.green));
                                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    valueProgress = sharedPreferences.getInt("process", 0);
                                    valueProgress += 25;
                                    editor.putInt("process", valueProgress);
                                    editor.apply();
                                    updateProgress(valueProgress);
                                    // Stop further checks
                                    handler.removeCallbacks(verificationCheckRunnable);
                                    // Navigate after delay
                                    handler.postDelayed(() -> {
                                        Log.d("EmailVerification", "Navigating to seller_information");
                                        startActivity(new Intent(getApplicationContext(), seller_information.class));
                                        finish();
                                        loading.cancel();
                                    }, 2500);
                                } else {
                                    // Continue checking if not verified
                                    handler.postDelayed(verificationCheckRunnable, VERIFICATION_CHECK_INTERVAL);
                                }
                            } else {
                                Log.e("EmailVerification", "Failed to check verification status: " + task.getException().getMessage());
                                StyleableToast.makeText(getApplicationContext(), "Failed to check verification status: " + task.getException().getMessage(), R.style.UptrendToast).show();
                                // Retry after interval
                                handler.postDelayed(verificationCheckRunnable, VERIFICATION_CHECK_INTERVAL);
                            }
                        }
                    });
                } else {
                    Log.d("EmailVerification", "No user logged in during verification check");
                    StyleableToast.makeText(getApplicationContext(), "No user logged in", R.style.UptrendToast).show();
                    // Retry after interval
                    handler.postDelayed(verificationCheckRunnable, VERIFICATION_CHECK_INTERVAL);
                }
            }
        };
        // Start initial check
        handler.postDelayed(verificationCheckRunnable, VERIFICATION_CHECK_INTERVAL);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Ensure verification check is running
        startVerificationCheck();
    }
}