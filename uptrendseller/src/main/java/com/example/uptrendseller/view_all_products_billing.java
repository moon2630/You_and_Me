package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class view_all_products_billing extends AppCompatActivity {

    private RecyclerView recyclerViewProducts;
    private ProductBillingAdapter adapter;
    private List<ProductBillingItem> productList;

    private TextView txtReportDate, txtTotalProducts, txtTotalEarnings, txtTotalCommission,back30;
    private TextView txtEmptyState;
    private ProgressBar progressBar;

    private DatabaseReference productsRef;
    private FirebaseUser currentUser;
    private loadingDialog2 loading;

    private static final String TAG = "AllProductsBilling"; // For logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all_products_billing);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        // Initialize views
        initViews();

        // Set current date
        setReportDate();

        back30.setOnClickListener(v -> onBackPressed());


        // Initialize Firebase
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            productsRef = FirebaseDatabase.getInstance().getReference("Product");
            loadProducts();
        } else {
            txtEmptyState.setText("Please login to view products");
            txtEmptyState.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
        }

        // Initialize loading dialog
        loading = new loadingDialog2(this);
    }

    private void initViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        txtReportDate = findViewById(R.id.txtReportDate);
        txtTotalProducts = findViewById(R.id.txtTotalProducts);
        txtTotalEarnings = findViewById(R.id.txtTotalEarnings);
        txtTotalCommission = findViewById(R.id.txtTotalCommission);
        txtEmptyState = findViewById(R.id.txtEmptyState);
        progressBar = findViewById(R.id.progressBar);
        back30 = findViewById(R.id.back30);

        // Setup RecyclerView
        productList = new ArrayList<>();
        adapter = new ProductBillingAdapter(productList);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(adapter);
    }

    private void setReportDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        txtReportDate.setText("Report Date: " + currentDate);
    }

    private void loadProducts() {
        Log.d(TAG, "Starting to load products...");
        progressBar.setVisibility(View.VISIBLE);
        txtEmptyState.setVisibility(View.GONE);

        productsRef.orderByChild("adminId").equalTo(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d(TAG, "DataSnapshot count: " + snapshot.getChildrenCount());

                        List<ProductBillingItem> newProductList = new ArrayList<>();

                        double totalEarnings = 0;
                        double totalCommission = 0;
                        int activeProducts = 0;
                        int draftProducts = 0;
                        int skippedProducts = 0;

                        for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                            try {
                                String productId = productSnapshot.getKey();
                                Log.d(TAG, "Processing product: " + productId);

                                String productName = productSnapshot.child("productName").getValue(String.class);
                                String productCategory = productSnapshot.child("productCategory").getValue(String.class);
                                String productSubCategory = productSnapshot.child("productSubCategory").getValue(String.class);
                                String productDisplayId = productSnapshot.child("productDisplayId").getValue(String.class);
                                String sellingPriceStr = productSnapshot.child("sellingPrice").getValue(String.class);
                                String originalPriceStr = productSnapshot.child("originalPrice").getValue(String.class);
                                String productStatus = productSnapshot.child("productStatus").getValue(String.class);

                                // Log category for debugging
                                Log.d(TAG, "Product: " + productName + " | Category: " + productCategory + " | SubCategory: " + productSubCategory);

                                if ("draft".equals(productStatus)) {
                                    draftProducts++;
                                    Log.d(TAG, "Draft product found: " + productName);
                                    // DON'T continue - include it in the list
                                }
                                // Check if required fields exist
                                if (productName == null || productCategory == null) {
                                    skippedProducts++;
                                    Log.w(TAG, "Skipping product due to missing required fields: " + productId);
                                    continue;
                                }

                                double originalPrice = 0;
                                double sellingPrice = 0;

                                // Parse original price
                                if (originalPriceStr != null && !originalPriceStr.isEmpty()) {
                                    try {
                                        originalPrice = Double.parseDouble(originalPriceStr);
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, "Invalid original price for product: " + productId);
                                        originalPrice = 0;
                                    }
                                }

                                // Parse selling price
                                if (sellingPriceStr != null && !sellingPriceStr.isEmpty()) {
                                    try {
                                        sellingPrice = Double.parseDouble(sellingPriceStr);
                                    } catch (NumberFormatException e) {
                                        Log.e(TAG, "Invalid selling price for product: " + productId);
                                        sellingPrice = 0;
                                    }
                                }

                                // Calculate commission and earnings
                                double commission = CommissionCalculator.calculateCommission(sellingPrice, productCategory, productSubCategory);
                                double platformFee = 30.0;
                                double earnings = sellingPrice - commission - platformFee;

                                // Fix: Check if displayId is valid or just Firebase key
                                String displayIdToShow;
                                if (productDisplayId == null ||
                                        productDisplayId.isEmpty() ||
                                        productDisplayId.equals(productId) ||
                                        !productDisplayId.contains("-")) { // Check if it's formatted like "BE-123456"
                                    displayIdToShow = "ID Not Set";
                                    Log.d(TAG, "Invalid displayId for product: " + productName + " | DisplayId: " + productDisplayId);
                                } else {
                                    displayIdToShow = productDisplayId;
                                }

                                // Add to totals
                                if (sellingPrice > 0) {
                                    totalEarnings += earnings;
                                    totalCommission += commission;
                                    activeProducts++;
                                }

                                // Create billing item
                                ProductBillingItem item = new ProductBillingItem(
                                        productId,
                                        displayIdToShow,
                                        productName,
                                        productCategory,
                                        productSubCategory != null ? productSubCategory : "",
                                        originalPrice,
                                        sellingPrice,
                                        commission,
                                        earnings,
                                        productStatus != null ? productStatus : "" // ADD productStatus
                                );
                                newProductList.add(item);
                                Log.d(TAG, "Added product: " + productName + " | Category: " + productCategory);

                            } catch (Exception e) {
                                Log.e(TAG, "Error processing product: " + e.getMessage());
                                e.printStackTrace();
                            }
                        }

                        // Log summary
                        Log.d(TAG, "Loaded " + newProductList.size() + " products");
                        Log.d(TAG, "Draft products: " + draftProducts);
                        Log.d(TAG, "Skipped products: " + skippedProducts);

                        // Update adapter safely
                        adapter.updateData(newProductList);

                        // Update summary
                        updateSummary(activeProducts, totalEarnings, totalCommission);

                        // Update UI
                        progressBar.setVisibility(View.GONE);

                        if (newProductList.isEmpty()) {
                            String message = "No active products found.";
                            if (draftProducts > 0) {
                                message += " You have " + draftProducts + " draft products.";
                            }
                            txtEmptyState.setText(message);
                            txtEmptyState.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to load products: " + error.getMessage());
                        progressBar.setVisibility(View.GONE);
                        txtEmptyState.setText("Failed to load products. Please try again.");
                        txtEmptyState.setVisibility(View.VISIBLE);
                        Toast.makeText(view_all_products_billing.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("DefaultLocale")
    private void updateSummary(int totalProducts, double totalEarnings, double totalCommission) {
        txtTotalProducts.setText(String.valueOf(totalProducts));
        txtTotalEarnings.setText(String.format("₹%.0f", totalEarnings));
        txtTotalCommission.setText(String.format("₹%.0f", totalCommission));
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, dashboard_admin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    // ProductBillingItem class
    // Update ProductBillingItem class:
    private static class ProductBillingItem {
        String productId;
        String displayId;
        String productName;
        String category;
        String subCategory;
        double originalPrice;
        double sellingPrice;
        double commission;
        double earnings;
        String productStatus; // ADD THIS FIELD

        public ProductBillingItem(String productId, String displayId, String productName,
                                  String category, String subCategory,
                                  double originalPrice, double sellingPrice,
                                  double commission, double earnings, String productStatus) { // UPDATE CONSTRUCTOR
            this.productId = productId;
            this.displayId = displayId;
            this.productName = productName;
            this.category = category;
            this.subCategory = subCategory;
            this.originalPrice = originalPrice;
            this.sellingPrice = sellingPrice;
            this.commission = commission;
            this.earnings = earnings;
            this.productStatus = productStatus; // ADD THIS
        }
    }
    // Adapter class
    private class ProductBillingAdapter extends RecyclerView.Adapter<ProductBillingAdapter.ViewHolder> {

        private List<ProductBillingItem> items;
        private List<Boolean> expandedStates;

        public ProductBillingAdapter(List<ProductBillingItem> items) {
            this.items = items;
            this.expandedStates = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                expandedStates.add(false);
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_product_billing, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint({"SetTextI18n", "ResourceAsColor"})
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (position < 0 || position >= items.size()) {
                return;
            }

            ProductBillingItem item = items.get(position);

            if (expandedStates.size() <= position) {
                expandedStates.add(false);
            }

            boolean isExpanded = expandedStates.get(position);

            // Product name - Check if draft and add [DRAFT] tag
            String productName = item.productName != null && !item.productName.isEmpty()
                    ? item.productName : "Unnamed Product";

            // FIX: Check if product is draft (need to pass productStatus to ProductBillingItem)
            // Since we don't have productStatus in ProductBillingItem, we'll check if earnings are 0
            // OR better: Add productStatus field to ProductBillingItem class
            if (item.earnings <= 0 && item.sellingPrice <= 0) {
                // This might be a draft product
                holder.txtProductName.setText(productName + " [DRAFT]");
                holder.txtProductName.setTextColor(getResources().getColor(android.R.color.darker_gray));
            } else {
                holder.txtProductName.setText(productName);
                holder.txtProductName.setTextColor(getResources().getColor(android.R.color.black));
            }

            // Product ID - FIXED: Show proper text
            if (item.displayId == null || item.displayId.isEmpty() || item.displayId.equals("ID Not Set")) {
                holder.txtProductId.setText("ID: Not Set");
                holder.txtProductId.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            } else {
                holder.txtProductId.setText("ID: " + item.displayId);
                holder.txtProductId.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            }

            // Category
            String categoryText = item.category != null ? item.category : "Unknown";
            if (item.subCategory != null && !item.subCategory.isEmpty()) {
                categoryText += " - " + item.subCategory;
            }
            holder.txtCategory.setText(categoryText);

            // Earnings - Different color for draft/active
            if (item.earnings > 0) {
                holder.txtEarnings.setText(String.format("₹%.0f", item.earnings));
                holder.txtEarnings.setTextColor(getResources().getColor(R.color.green));
            } else {
                holder.txtEarnings.setText("₹0");
                holder.txtEarnings.setTextColor(getResources().getColor(android.R.color.darker_gray));
            }

            // Price details
            if (item.sellingPrice > 0) {
                holder.txtOriginalPrice.setText(String.format("Original: ₹%.0f", item.originalPrice));
                holder.txtSellingPrice.setText(String.format("Selling: ₹%.0f", item.sellingPrice));
                holder.txtCommission.setText(String.format("Comm: ₹%.0f", item.commission));
            } else {
                holder.txtOriginalPrice.setText("Original: N/A");
                holder.txtSellingPrice.setText("Selling: N/A");
                holder.txtCommission.setText("Comm: N/A");
            }

            // Expand/collapse
            holder.detailsLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.btnExpand.setText(isExpanded ? "Hide Details" : "Show Details");

            holder.btnExpand.setOnClickListener(v -> {
                expandedStates.set(position, !isExpanded);
                notifyItemChanged(position);
            });

            // Click on item
            holder.itemView.setOnClickListener(v -> {
                // Optional: Add detailed view
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public void updateData(List<ProductBillingItem> newItems) {
            items.clear();
            items.addAll(newItems);
            expandedStates.clear();
            for (int i = 0; i < items.size(); i++) {
                expandedStates.add(false);
            }
            notifyDataSetChanged();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView txtProductName, txtProductId, txtCategory, txtEarnings;
            TextView txtOriginalPrice, txtSellingPrice, txtCommission;
            TextView btnExpand;
            LinearLayout detailsLayout;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                txtProductName = itemView.findViewById(R.id.txtProductName);
                txtProductId = itemView.findViewById(R.id.txtProductId);
                txtCategory = itemView.findViewById(R.id.txtCategory);
                txtEarnings = itemView.findViewById(R.id.txtEarnings);
                txtOriginalPrice = itemView.findViewById(R.id.txtOriginalPrice);
                txtSellingPrice = itemView.findViewById(R.id.txtSellingPrice);
                txtCommission = itemView.findViewById(R.id.txtCommission);
                btnExpand = itemView.findViewById(R.id.btnExpand);
                detailsLayout = itemView.findViewById(R.id.detailsLayout);
            }
        }
    }
}