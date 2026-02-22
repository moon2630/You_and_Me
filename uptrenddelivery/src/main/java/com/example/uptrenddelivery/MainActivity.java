package com.example.uptrenddelivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Your splash layout

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("DeliveryPrefs", MODE_PRIVATE);

        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();

            if (currentUser != null) {
                // User is logged in → Go directly to dashboard
                startActivity(new Intent(MainActivity.this, dashboard_delivery.class));
            } else {
                // Not logged in → Go to login
                startActivity(new Intent(MainActivity.this, delivery_log_in.class));
            }
            finish();
        }, 2000); // 2 second splash
    }
}