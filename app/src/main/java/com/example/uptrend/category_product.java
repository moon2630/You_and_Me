package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import DataModel.Cart;

public class category_product extends AppCompatActivity {

    // Add this variable
    private CartNotificationHelper cartNotificationHelper;
    private TextView cartNotificationText2;
    private DatabaseReference cartRef;
    private ValueEventListener cartValueEventListener;

    private BottomNavigationView bottomNavigationView;

    private RelativeLayout layoutShoes, layoutMobiles, layoutHoodies, layoutJewellery, layoutShirts, layoutChocolates,
            layoutJeansPant, layoutTeddyBear, layoutBeauty, layoutWatches, layoutSports, layoutSarees, layoutGlasses, layoutDresses, layoutArts;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        //FindViewById
       layoutShoes = findViewById(R.id.layoutShoes);
        layoutMobiles = findViewById(R.id.layoutMobiles);
        layoutHoodies = findViewById(R.id.layoutHoodies);
        layoutJewellery = findViewById(R.id.layoutJewellery);
        layoutShirts = findViewById(R.id.layoutShirt);
        layoutChocolates = findViewById(R.id.layoutChocolates);
        layoutJeansPant = findViewById(R.id.layoutJeansPant);
        layoutTeddyBear = findViewById(R.id.layoutTeddyBear);
        layoutBeauty = findViewById(R.id.layoutBeauty);
        layoutWatches = findViewById(R.id.layoutWatches);
        layoutSports = findViewById(R.id.layoutSports);
        layoutSarees = findViewById(R.id.layoutSarees);
        layoutGlasses = findViewById(R.id.layoutGlasses);
        layoutDresses = findViewById(R.id.layoutDresses);
        layoutArts = findViewById(R.id.layoutArts);
        cartNotificationText2 = findViewById(R.id.add_to_cart_notification2);


        setupCartNotification();


        // In onCreate(), after finding views:
        cartNotificationHelper = new CartNotificationHelper(
                findViewById(R.id.add_to_cart_notification2),
                "CategoryActivity"
        );



        TextView close_btn_categories = findViewById(R.id.close_btn_categories);
        close_btn_categories.setOnClickListener(v -> {
            Intent intent = new Intent(category_product.this, home.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView home2 = findViewById(R.id.home2);
        home2.setOnClickListener(v -> {
            Intent intent = new Intent(category_product.this, home.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView account2 = findViewById(R.id.account2);
        account2.setOnClickListener(v -> {
            Intent intent = new Intent(category_product.this, account_user.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView bag2 = findViewById(R.id.bag2);
        bag2.setOnClickListener(v -> {
            Intent intent = new Intent(category_product.this, add_to_cart_product.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });






        //ClickEvent Of Category.
        layoutShoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "Footware");

            }
        });
        layoutMobiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Smartphones");
            }
        });
        layoutHoodies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Sweaters and Hoodies");
            }
        });
        layoutJewellery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "Jewellery");
            }
        });
        layoutShirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Shirts");
            }
        });
        layoutChocolates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "Chocolate");
            }
        });
        layoutJeansPant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Jeans");
            }
        });
        layoutTeddyBear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Teddy Bear");
            }
        });
        layoutBeauty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "Beauty");
            }
        });
        layoutWatches.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "Watches");
            }
        });
        layoutSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "Sports");
            }
        });
        layoutSarees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Saree");
            }
        });
        layoutGlasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category", "EyeWear");
            }
        });
        layoutDresses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("SubCategory", "Dresses");
            }
        });
        layoutArts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCategoryActivity("Category","Art");
            }
        });


    }

    public void openCategoryActivity(String sortBy, String value) {
        Intent i = new Intent(category_product.this, open_category_product.class);
        i.putExtra("sortBy", sortBy);
        i.putExtra("value", value);
        startActivity(i);

    }


    private void setupCartNotification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            cartNotificationText2.setVisibility(View.GONE);
            return;
        }

        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        cartValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;

                // Count all cart items for the current user
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cart = cartSnapshot.getValue(Cart.class);
                    if (cart != null && cart.getUserId() != null &&
                            cart.getUserId().equals(currentUser.getUid())) {
                        cartCount++;
                    }
                }

                // Update UI
                if (cartCount > 0) {
                    cartNotificationText2.setText(String.valueOf(cartCount));
                    cartNotificationText2.setVisibility(View.VISIBLE);
                } else {
                    cartNotificationText2.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CategoryActivity", "Error loading cart count: " + error.getMessage());
            }
        };

        // Listen for cart changes
        cartRef.addValueEventListener(cartValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartNotificationHelper != null) {
            cartNotificationHelper.cleanup();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), home.class);
        startActivity(intent);
        finish();
    }
}