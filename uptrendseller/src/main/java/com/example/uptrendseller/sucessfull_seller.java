package com.example.uptrendseller;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class sucessfull_seller extends AppCompatActivity {

    ImageView imgSuccessIcon;
    TextView txtSuccessTitle, txtSuccessMessage, txtRedirect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucessfull_seller);


        // Set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        imgSuccessIcon = findViewById(R.id.imgSuccessIcon);
        txtSuccessTitle = findViewById(R.id.txtSuccessTitle);
        txtSuccessMessage = findViewById(R.id.txtSuccessMessage);
        txtRedirect = findViewById(R.id.txtRedirect);

        startSuccessAnimation();
        startDotsAnimation();

        // Auto redirect after 3.5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            startActivity(new Intent(sucessfull_seller.this, dashboard_admin.class));
            finish();
        }, 3500);
    }

    private void startSuccessAnimation() {
        // 1. Icon pop-in with bounce
        imgSuccessIcon.animate()
                .scaleX(1f).scaleY(1f)
                .alpha(1f)
                .setDuration(800)
                .setStartDelay(300)
                .withEndAction(() -> {
                    // Bounce effect
                    imgSuccessIcon.animate()
                            .scaleX(1.15f).scaleY(1.15f)
                            .setDuration(200)
                            .withEndAction(() -> imgSuccessIcon.animate()
                                    .scaleX(1f).scaleY(1f)
                                    .setDuration(200))
                            .start();
                })
                .start();

        // 2. Title fade & slide up
        txtSuccessTitle.animate()
                .alpha(1f)
                .translationY(0)
                .setDuration(600)
                .setStartDelay(800)
                .start();

        // 3. Message fade in
        txtSuccessMessage.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(1100)
                .start();

        // 4. Redirect text fade in
        txtRedirect.animate()
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(1500)
                .start();
    }
    private void startDotsAnimation() {
        final String[] dots = {"", ".", "..", "..."};
        final int[] index = {0};

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                txtRedirect.setText("Redirecting to dashboard" + dots[index[0]]);
                index[0] = (index[0] + 1) % dots.length;
                txtRedirect.postDelayed(this, 500); // Repeat every 500ms
            }
        }, 1500); // Start after title/message animation
    }
}