package com.example.uptrend;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import DataModel.Cart;

public class CartNotificationHelper {
    private TextView cartNotificationText;
    private DatabaseReference cartRef;
    private ValueEventListener cartValueEventListener;
    private String activityName;

    public CartNotificationHelper(TextView cartNotificationText, String activityName) {
        this.cartNotificationText = cartNotificationText;
        this.activityName = activityName;
        setupCartNotification();
    }

    private void setupCartNotification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            cartNotificationText.setVisibility(View.GONE);
            return;
        }

        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        cartValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;

                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cart = cartSnapshot.getValue(Cart.class);
                    if (cart != null && cart.getUserId() != null &&
                            cart.getUserId().equals(currentUser.getUid())) {
                        cartCount++;
                    }
                }

                if (cartCount > 0) {
                    cartNotificationText.setText(String.valueOf(cartCount));
                    cartNotificationText.setVisibility(View.VISIBLE);
                } else {
                    cartNotificationText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(activityName, "Error loading cart count: " + error.getMessage());
            }
        };

        cartRef.addValueEventListener(cartValueEventListener);
    }

    public void cleanup() {
        if (cartRef != null && cartValueEventListener != null) {
            cartRef.removeEventListener(cartValueEventListener);
        }
    }
}