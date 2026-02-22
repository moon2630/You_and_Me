package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import io.github.muddz.styleabletoast.StyleableToast;

public class edit_product_billing extends AppCompatActivity {

    private TextView txtProductId, txtDateTime, txtCategory, txtSubCategory;
    private TextView txtOriginalPrice, txtSellingPrice, txtSavedAmount;
    private TextView txtCommission, txtPlatformFee, txtSellerEarnings;
    private TextView btnBack, btnSaveAndExit;
    private ProgressBar progressBar;

    private String productKey;
    private String category, subCategory;
    private double originalPrice = 0, sellingPrice = 0;
    private DatabaseReference productRef;
    private loadingDialog2 loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_billing); // Use same XML layout

        // Set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        // Initialize views
        initViews();

        // Get product key from intent
        productKey = getIntent().getStringExtra("productId");
        if (productKey == null || productKey.isEmpty()) {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize database reference
        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productKey);

        // Initialize loading dialog
        loading = new loadingDialog2(this);

        // Set current date and time
        setCurrentDateTime();

        // Load product data
        loadProductData();

        // Setup button listeners
        setupButtonListeners();
    }

    private void initViews() {
        txtProductId = findViewById(R.id.txtProductId);
        txtDateTime = findViewById(R.id.txtDateTime);
        txtCategory = findViewById(R.id.txtCategory);
        txtSubCategory = findViewById(R.id.txtSubCategory);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        txtSellingPrice = findViewById(R.id.txtSellingPrice);
        txtSavedAmount = findViewById(R.id.txtSavedAmount);
        txtCommission = findViewById(R.id.txtCommission);
        txtPlatformFee = findViewById(R.id.txtPlatformFee);
        txtSellerEarnings = findViewById(R.id.txtSellerEarnings);
        btnBack = findViewById(R.id.btnBack);
        btnSaveAndExit = findViewById(R.id.btnSubmit); // Reuse submit button
        btnSaveAndExit.setText("Save & Exit"); // Change text
        progressBar = findViewById(R.id.progressBar);
    }

    private void setCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        String currentDateTime = sdf.format(new Date());
        txtDateTime.setText("" + currentDateTime);
    }

    private void loadProductData() {
        loading.show();
        progressBar.setVisibility(View.VISIBLE);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load product display ID
                    String displayId = snapshot.child("productDisplayId").getValue(String.class);
                    if (displayId != null) {
                        txtProductId.setText("" + displayId);
                    } else {
                        txtProductId.setText("" + productKey.substring(0, Math.min(8, productKey.length())));
                    }

                    // Load category and subcategory
                    category = snapshot.child("productCategory").getValue(String.class);
                    subCategory = snapshot.child("productSubCategory").getValue(String.class);

                    if (category != null) {
                        txtCategory.setText("" + category);
                    }

                    if (subCategory != null) {
                        txtSubCategory.setText("" + subCategory);
                    }

                    // Load prices
                    String originalPriceStr = snapshot.child("originalPrice").getValue(String.class);
                    String sellingPriceStr = snapshot.child("sellingPrice").getValue(String.class);
                    String savedAmountStr = snapshot.child("savedAmount").getValue(String.class);
                    String discountPercentStr = snapshot.child("discountPercent").getValue(String.class);

                    if (originalPriceStr != null && !originalPriceStr.isEmpty()) {
                        try {
                            originalPrice = Double.parseDouble(originalPriceStr);
                            txtOriginalPrice.setText("₹" + originalPriceStr);
                        } catch (NumberFormatException e) {
                            originalPrice = 0;
                        }
                    }

                    if (sellingPriceStr != null && !sellingPriceStr.isEmpty()) {
                        try {
                            sellingPrice = Double.parseDouble(sellingPriceStr);
                            txtSellingPrice.setText("₹" + sellingPriceStr);
                        } catch (NumberFormatException e) {
                            sellingPrice = 0;
                        }
                    }

                    // Show saved amount if available
                    if (savedAmountStr != null && discountPercentStr != null) {
                        txtSavedAmount.setText("₹" + savedAmountStr + " (" + discountPercentStr + ")");
                    } else if (originalPrice > 0 && sellingPrice > 0) {
                        double saved = originalPrice - sellingPrice;
                        double discountPercent = (saved / originalPrice) * 100;
                        txtSavedAmount.setText("₹" + String.format("%.0f", saved) +
                                " (" + String.format("%.0f", discountPercent) + "%)");
                    }

                    // Calculate and display commission and payout
                    calculateAndDisplayCommission();

                } else {
                    Toast.makeText(edit_product_billing.this, "Product data not found", Toast.LENGTH_SHORT).show();
                }
                loading.cancel();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.cancel();
                progressBar.setVisibility(View.GONE);
                Toast.makeText(edit_product_billing.this, "Failed to load product data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAndDisplayCommission() {
        if (sellingPrice <= 0) {
            return;
        }

        // Get commission percentage
        int commissionPercent = CommissionCalculator.getCommissionPercentage(category, subCategory);

        // Calculate commission amount
        double commissionAmount = CommissionCalculator.calculateCommission(sellingPrice, category, subCategory);

        // Fixed platform fee
        double platformFee = 30.0;

        // Calculate seller earnings
        double sellerEarnings = CommissionCalculator.calculatePayout(sellingPrice, category, subCategory);

        // Update UI with calculations
        txtCommission.setText("(" + commissionPercent + "%) ₹" + String.format("%.0f", commissionAmount));
        txtPlatformFee.setText("₹" + String.format("%.0f", platformFee));
        txtSellerEarnings.setText("₹" + String.format("%.0f", sellerEarnings));
    }

    private void setupButtonListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnSaveAndExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAndExitToInventory();
            }
        });
    }

    private void saveAndExitToInventory() {
        loading.show();

        // Update last updated date
        HashMap<String, Object> updates = new HashMap<>();
        updates.put("productLastUpdatedDate", DateHelper.getCurrentDate());

        // Save commission data if not already saved
        if (sellingPrice > 0) {
            double commissionAmount = CommissionCalculator.calculateCommission(sellingPrice, category, subCategory);
            double sellerEarnings = CommissionCalculator.calculatePayout(sellingPrice, category, subCategory);

            updates.put("commissionPercentage", CommissionCalculator.getCommissionPercentage(category, subCategory));
            updates.put("commissionAmount", String.format("%.0f", commissionAmount));
            updates.put("platformFee", "30");
            updates.put("sellerEarnings", String.format("%.0f", sellerEarnings));
        }

        productRef.updateChildren(updates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loading.cancel();

                        if (task.isSuccessful()) {
                            StyleableToast.makeText(edit_product_billing.this,
                                    "Product updated successfully!",
                                    R.style.UptrendToast).show();

                            // Navigate back to inventory
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(edit_product_billing.this, inventory_product.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }, 1500);
                        } else {
                            StyleableToast.makeText(edit_product_billing.this,
                                    "Failed to update. Please try again.",
                                    R.style.UptrendToast).show();
                        }
                    }
                });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Navigate back to edit_product with product ID
        Intent intent = new Intent(this, edit_product.class);
        intent.putExtra("productId", productKey);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when activity resumes (in case user went back and changed prices)
        if (productKey != null) {
            loadProductData();
        }
    }
}