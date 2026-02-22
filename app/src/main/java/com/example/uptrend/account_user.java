package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrend.Adapter.Onclick;
import com.example.uptrend.Adapter.RecentlyViewProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import DataModel.Cart;
import DataModel.RecentlyViewProduct;
import io.github.muddz.styleabletoast.StyleableToast;

public class account_user extends AppCompatActivity implements Onclick {


    AppCompatButton wishlist_btn, profile_btn,order_btn,rating_btn,notification_btn,btnLogout;
    private FirebaseUser user;
    private CartNotificationHelper cartNotificationHelper;

    private DatabaseReference cartRef;
    private ValueEventListener cartValueEventListener;

    private TextView cartNotificationText3;
    private LinearLayout layoutRecentlyViewProduct;
    private RecyclerView recyclerViewRecentlyViewProduct;
    private DatabaseReference recentlyViewProductRef;
    private ArrayList<RecentlyViewProduct> recentlyViewProductArrayList;
    private TextView txtUserName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_user);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        layoutRecentlyViewProduct = findViewById(R.id.layoutRecentlyProductuser);
        recyclerViewRecentlyViewProduct = findViewById(R.id.recyclerViewRecentlyProductuser);
        txtUserName = findViewById(R.id.txtUserName);

        wishlist_btn = findViewById(R.id.wishlist_btn);
        profile_btn = findViewById(R.id.profile_btn);
        order_btn = findViewById(R.id.orders_btn);
        rating_btn = findViewById(R.id.rating_btn);
        btnLogout=findViewById(R.id.btnLogout);
        notification_btn = findViewById(R.id.notification_btn);
        cartNotificationText3 = findViewById(R.id.add_to_cart_notification3);


        setupCartNotification();


        // In onCreate(), after finding views:
        cartNotificationHelper = new CartNotificationHelper(
                findViewById(R.id.add_to_cart_notification3),
                "CategoryActivity"
        );




        TextView close_btn_account = findViewById(R.id.close_btn_account);
        close_btn_account.setOnClickListener(v -> {
            Intent intent = new Intent(account_user.this, home.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });



        TextView home3 = findViewById(R.id.home3);
        home3.setOnClickListener(v -> {
            Intent intent = new Intent(account_user.this, home.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView categories3 = findViewById(R.id.categories3);
        categories3.setOnClickListener(v -> {
            Intent intent = new Intent(account_user.this, category_product.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView bag3 = findViewById(R.id.bag3);
        bag3.setOnClickListener(v -> {
            Intent intent = new Intent(account_user.this, add_to_cart_product.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });



        user = FirebaseAuth.getInstance().getCurrentUser();
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), complete_order.class));
            }
        });
        wishlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), wishlist_poduct.class));
            }
        });

        notification_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), notification.class));
            }
        });
        rating_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), rating_products.class));
            }
        });

        profile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), user_profile.class));
            }
        });



        displayRecentlyViewProduct();
        displayUserName();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearLayout dialogLayout = new LinearLayout(account_user.this);
                dialogLayout.setOrientation(LinearLayout.VERTICAL);
                dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
                dialogLayout.setPadding(50, 50, 50, 35);

                TextView title = new TextView(account_user.this);
                title.setText("Logout");
                title.setTypeface(ResourcesCompat.getFont(account_user.this, R.font.caudex), Typeface.BOLD);
                title.setPadding(0, 0, 10, 20);
                title.setTextSize(22);
                title.setTextColor(getResources().getColor(android.R.color.black));

                TextView message = new TextView(account_user.this);
                message.setText("Do you sure you want to log out your account?");
                message.setTypeface(ResourcesCompat.getFont(account_user.this, R.font.caudex));
                message.setTextSize(16);
                message.setPadding(0, 10, 0, 0);
                message.setTextColor(getResources().getColor(android.R.color.black));

                dialogLayout.addView(title);
                dialogLayout.addView(message);

                AlertDialog dialog = new AlertDialog.Builder(account_user.this)
                        .setView(dialogLayout)
                        .setPositiveButton("OK", (d, which) -> {
                            FirebaseAuth.getInstance().signOut();
                            StyleableToast.makeText(account_user.this, "Logout Successfully", R.style.UptrendToast).show();
                            Intent intent = new Intent(account_user.this, signUp_and_logIn_page.class);
                            intent.putExtra("status", "SignIn");
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .create();

                dialog.show();

                if (dialog.getWindow() != null) {
                    dialog.getWindow().setLayout(
                            (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
                    Typeface customFont = ResourcesCompat.getFont(account_user.this, R.font.caudex);
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(customFont, Typeface.BOLD);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont, Typeface.BOLD);
                }
            }
        });

    }

    private void setupCartNotification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            cartNotificationText3.setVisibility(View.GONE);
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
                    cartNotificationText3.setText(String.valueOf(cartCount));
                    cartNotificationText3.setVisibility(View.VISIBLE);
                } else {
                    cartNotificationText3.setVisibility(View.GONE);
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
    public void displayUserName() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        Query userQuery = userRef.orderByChild("userId").equalTo(user.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                String userName = userSnapshot.child("userName").getValue(String.class);
                txtUserName.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void displayRecentlyViewProduct() {
        recentlyViewProductArrayList = new ArrayList<>();
        recentlyViewProductRef = FirebaseDatabase.getInstance().getReference("RecentlyViewProduct");
        Query query = recentlyViewProductRef.orderByChild("userId").equalTo(user.getUid()).limitToLast(10);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentlyViewProductArrayList.clear();
                for (DataSnapshot productSnapShot : snapshot.getChildren()) {
                    RecentlyViewProduct recentlyViewProduct = productSnapShot.getValue(RecentlyViewProduct.class);
                    long difference = System.currentTimeMillis() - Long.parseLong(recentlyViewProduct.getTimeStamp());
                    if (difference > 0) {
                        recentlyViewProductArrayList.add(recentlyViewProduct);

                    }
                }
                Collections.sort(recentlyViewProductArrayList, new Comparator<RecentlyViewProduct>() {
                    @Override
                    public int compare(RecentlyViewProduct product1, RecentlyViewProduct product2) {
                        // Compare timestamps in descending order
                        return Long.compare(Long.parseLong(product2.getTimeStamp()), Long.parseLong(product1.getTimeStamp()));
                    }
                });
                if (recentlyViewProductArrayList.size() != 0) {
                    layoutRecentlyViewProduct.setVisibility(View.VISIBLE);
                    RecentlyViewProductAdapter recentlyViewProductAdapter = new RecentlyViewProductAdapter(account_user.this, recentlyViewProductArrayList, account_user.this, "accountUser");
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(account_user.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewRecentlyViewProduct.setLayoutManager(linearLayoutManager);
                    recyclerViewRecentlyViewProduct.setAdapter(recentlyViewProductAdapter);
                } else {
                    layoutRecentlyViewProduct.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void ItemOnClickListener(String productId) {
        Intent i = new Intent(account_user.this, open_product.class);
        i.putExtra("productId", productId);
        startActivity(i);
        finish();

    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), home.class);
        startActivity(intent);
        finish();
    }
}