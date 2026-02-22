package com.example.uptrenddelivery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class dashboard_delivery extends AppCompatActivity {

    TextView tvNewOrdersCount, btnNewOrders, btnActiveOrders, btnEarnings, btnProfile;
    DatabaseReference ordersRef;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_delivery);

        // Initialize views
        tvNewOrdersCount = findViewById(R.id.tvNewOrdersCount);
        btnNewOrders = findViewById(R.id.btnNewOrders);
        btnActiveOrders = findViewById(R.id.btnActiveOrders);
        btnEarnings = findViewById(R.id.btnEarnings);
        btnProfile = findViewById(R.id.btnProfile);

        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

        // Load new orders count in real-time
        loadNewOrdersCount();

        // Click listeners
        btnNewOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, new_orders.class));
        });

        btnActiveOrders.setOnClickListener(v -> {
            startActivity(new Intent(this, active_orders.class));
        });

        btnEarnings.setOnClickListener(v -> {
            startActivity(new Intent(this, earnings_wallet.class));
        });

        btnProfile.setOnClickListener(v -> {
            startActivity(new Intent(this, profile_delivery.class));
        });
    }

    private void loadNewOrdersCount() {
        ordersRef.orderByChild("orderStatus").equalTo("confirmed")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        long count = snapshot.getChildrenCount();
                        tvNewOrdersCount.setText(String.valueOf(count));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvNewOrdersCount.setText("0");
                    }
                });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        finishAffinity(); // Exit app
    }
}