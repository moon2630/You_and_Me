package com.example.uptrendseller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import DataModel.Product;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.muddz.styleabletoast.StyleableToast;

public class dashboard_admin extends AppCompatActivity {

    private TextView drawerOpenTxt, toolbarTitle, editProfileTxt;
    private TextView drawerOpenIcon;
    private DrawerLayout drawerLayout;
    private FirebaseUser user;
    private CircleImageView navProfileImage;
    private DatabaseReference adminNode;

    // Add these constants at the top with your other variables
    private static final int ANIM_DURATION = 300;
    private static final float SCALE_FACTOR = 0.95f;
    private static final float PULSE_SCALE = 1.05f;

    // Navigation items
    private LinearLayout navListing, navInventory, navOrder, navBilling, navCommission,
            navReturn, navReport, navLogout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }


        // Initialize Firebase user
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Initialize views
        initializeViews();

        // Setup drawer and navigation
        setupDrawer();
        setupNavigationListeners();

        // Initialize SharedPreferences
        setupSharedPreferences();

        // Load user profile image
        loadUserProfileImage();

        // Check and request notification permission
        checkNotificationPermission();


        if (savedInstanceState == null) {
            loadHomeFragment();
        }

        // Send notifications if needed - ONLY IN onCreate
        if (user != null) {
            sendNotification(user.getUid());
        }

    }

    private void initializeViews() {
        // Main views
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerOpenIcon = findViewById(R.id.drawer_open_icon);
        toolbarTitle = findViewById(R.id.toolbar_title);

        // Navigation drawer views
        navProfileImage = findViewById(R.id.navProfileImage);
        editProfileTxt = findViewById(R.id.edit_profile_txt);

        // Navigation items
        navListing = findViewById(R.id.nav_listing);
        navInventory = findViewById(R.id.nav_inventory);
        navOrder = findViewById(R.id.nav_order);
        navBilling = findViewById(R.id.nav_billing);
        navCommission = findViewById(R.id.nav_commission);
        navReturn = findViewById(R.id.nav_return);
        navReport = findViewById(R.id.nav_report);
        navLogout = findViewById(R.id.nav_logout);
    }



    // Update the drawerOpenIcon click listener
// Add this method to animate navigation items sequentially
    private void animateDrawerItems() {
        // Get all drawer items
        View[] drawerItems = {
                navProfileImage, editProfileTxt,
                navListing, navInventory, navOrder,
                navBilling, navCommission, navReturn,
                navReport, navLogout
        };

        // Animate each item with delay
        for (int i = 0; i < drawerItems.length; i++) {
            final View item = drawerItems[i];
            if (item != null) {
                // Set initial position (slide from left)
                item.setAlpha(0f);
                item.setTranslationX(-30f);

                // Animate with delay
                item.animate()
                        .translationX(0f)
                        .alpha(1f)
                        .setDuration(300)
                        .setStartDelay(50 + i * 30) // Staggered delay
                        .start();
            }
        }
    }

    // Simple click animation method for all items
    private void animateItemClick(View view, Runnable action) {
        view.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(100)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(100)
                                .withEndAction(action)
                                .start();
                    }
                })
                .start();
    }

    // Update setupDrawer() method
    private void setupDrawer() {
        drawerOpenIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START);
                    // Start drawer animation when opening
                    animateDrawerItems();
                }
            }
        });

        // Profile image click
        navProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateItemClick(v, new Runnable() {
                    @Override
                    public void run() {
                        closeDrawer();
                        startActivity(new Intent(getApplicationContext(), profile_seller.class));
                    }
                });
            }
        });

        // Edit profile text click
        editProfileTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateItemClick(v, new Runnable() {
                    @Override
                    public void run() {
                        closeDrawer();
                        startActivity(new Intent(getApplicationContext(), profile_seller.class));
                    }
                });
            }
        });
    }

    // Update setupNavigationListeners() with simple animation
    private void setupNavigationListeners() {
        View.OnClickListener navClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateItemClick(v, new Runnable() {
                    @Override
                    public void run() {
                        closeDrawer();
                        handleNavigationClick(v.getId());
                    }
                });
            }
        };

        // Set same click listener for all navigation items
        navListing.setOnClickListener(navClickListener);
        navInventory.setOnClickListener(navClickListener);
        navOrder.setOnClickListener(navClickListener);
        navBilling.setOnClickListener(navClickListener);
        navCommission.setOnClickListener(navClickListener);
        navReturn.setOnClickListener(navClickListener);
        navReport.setOnClickListener(navClickListener);
        navLogout.setOnClickListener(navClickListener);
    }

    // Handle navigation clicks
    private void handleNavigationClick(int viewId) {
        if (viewId == R.id.nav_listing) {
            startActivity(new Intent(getApplicationContext(), listing_product.class));
            finish();
        } else if (viewId == R.id.nav_inventory) {
            startActivity(new Intent(getApplicationContext(), inventory_product.class));
            finish();
        } else if (viewId == R.id.nav_order) {
            startActivity(new Intent(getApplicationContext(), order_Details.class));
            finish();
        } else if (viewId == R.id.nav_billing) {
            startActivity(new Intent(getApplicationContext(), view_all_products_billing.class));
            finish();
        } else if (viewId == R.id.nav_commission) {
            startActivity(new Intent(getApplicationContext(), commission_notification.class));
            finish();
        } else if (viewId == R.id.nav_return) {
            startActivity(new Intent(getApplicationContext(), cancel_return.class));
            finish();
        } else if (viewId == R.id.nav_report) {
            startActivity(new Intent(getApplicationContext(), report_selling.class));
            finish();
        } else if (viewId == R.id.nav_logout) {
            logoutUser();
        }
    }

    // Close drawer with simple animation
    private void closeDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        long currentSessionTime = System.currentTimeMillis();
        long lastSessionTime = sharedPreferences.getLong("sessionTime", 0);

        if (currentSessionTime - lastSessionTime > 30 * 60 * 1000) { // New session if > 30 minutes
            editor.putLong("sessionTime", currentSessionTime);
            editor.putBoolean("hasNotified", false);
            editor.apply();
        }
    }

    private void loadUserProfileImage() {
        if (user == null) return;

        adminNode = FirebaseDatabase.getInstance().getReference("Admin");
        Query query = adminNode.orderByChild("adminId").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot sellerSnapshot = snapshot.getChildren().iterator().next();
                    String imageUri = sellerSnapshot.child("profileImage").getValue(String.class);
                    if (imageUri != null && !imageUri.isEmpty()) {
                        Glide.with(dashboard_admin.this).load(imageUri).into(navProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardAdmin", "Failed to load profile image: " + error.getMessage());
            }
        });
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(dashboard_admin.this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(dashboard_admin.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void loadHomeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new home())
                .commit();

        // Send notifications if needed
        if (user != null) {
            sendNotification(user.getUid());
        }
    }

    private void openDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }


    private void logoutUser() {

        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Logout");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to logout?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {

                    // Clear SharedPreferences
                    SharedPreferences sharedPreferences =
                            getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("hasNotified", false);
                    editor.apply();

                    // Firebase logout
                    FirebaseAuth.getInstance().signOut();

                    StyleableToast.makeText(
                            getApplicationContext(),
                            "Logout Successfully",
                            R.style.UptrendToast
                    ).show();

                    // Navigate to login screen
                    Intent intent = new Intent(getApplicationContext(), admin_login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    .setTextColor(getResources().getColor(R.color.blue));
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTextColor(Color.RED);

            Typeface customFont = ResourcesCompat.getFont(this, R.font.caudex);
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                    .setTypeface(customFont, Typeface.BOLD);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                    .setTypeface(customFont, Typeface.BOLD);
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                finish();
            }
        }
    }

    public void sendNotification(String adminId) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        boolean hasNotified = sharedPreferences.getBoolean("hasNotified", false);

        // Check if notifications were already sent during this app session
        if (hasNotified) {
            Log.d("DashboardAdmin", "Notifications already sent for this app session");
            return;
        }

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product");
        Query sellerQuery = productRef.orderByChild("adminId").equalTo(adminId);

        sellerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        // Check product sizes
                        if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                            for (int i = 0; i < product.getProductSizes().size(); i++) {
                                String sizeValue = product.getProductSizes().get(i);
                                if (sizeValue != null && !sizeValue.trim().isEmpty()) {
                                    try {
                                        int stockValue = Integer.parseInt(sizeValue.trim());
                                        if (stockValue <= 3 && stockValue >= 1) {
                                            NotificationHelper.lowStockNotification(getApplicationContext(), product.getProductName());
                                            break;
                                        } else if (stockValue == 0) {
                                            NotificationHelper.outOfStockNotification(getApplicationContext(), product.getProductName());
                                            break;
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("DashboardAdmin", "Invalid size value: " + sizeValue);
                                    }
                                }
                            }
                        } else {
                            // Check total stock
                            String totalStock = product.getTotalStock();
                            if (totalStock != null && !totalStock.trim().isEmpty()) {
                                try {
                                    int stockValue = Integer.parseInt(totalStock.trim());
                                    if (stockValue <= 3 && stockValue >= 1) {
                                        NotificationHelper.lowStockNotification(getApplicationContext(), product.getProductName());
                                    } else if (stockValue == 0) {
                                        NotificationHelper.outOfStockNotification(getApplicationContext(), product.getProductName());
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("DashboardAdmin", "Invalid totalStock value: " + totalStock);
                                }
                            }
                        }
                    }
                }

                // Mark notifications as sent for this app session
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("hasNotified", true);
                editor.apply();
                Log.d("DashboardAdmin", "Notifications sent for this app session");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("DashboardAdmin", "sendNotification cancelled: " + error.getMessage());
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Update toolbar title based on current fragment
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        // You can implement logic to update toolbar title based on current fragment
        // For now, it defaults to "Dashboard"
    }
}