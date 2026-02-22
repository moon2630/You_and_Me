package com.example.uptrendseller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrendseller.Adapter.LowStockAdapter;
import com.example.uptrendseller.Adapter.RecentProductAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import DataModel.Order;
import DataModel.Product;


public class home extends Fragment {

    private TextView txtTodaySales, txtTodayIncome, txtTotalProducts, txtPendingOrders;
    private TextView txtYearlySales, txtYearlyIncome, txtCancelledOrders, txtReturnedOrders, txtAvailableStock,txtTotalIncome;
    private TextView txtNoLowStock, txtNoRecentProducts;
    private LineChart chartMonthlyPerformance;
    private AppCompatButton btnAddProduct, btnViewOrders, btnViewCommission, btnViewAnalytics, btnStockManagement;
    private RecyclerView recyclerLowStock, recyclerRecentProducts;
    private FirebaseUser firebaseUser;
    private DatabaseReference orderRef, productRef, cancelRef, returnRef;
    private static final String TAG = "HomeFragment";
    private static final String DATE_FORMAT = "dd-MM-yyyy";
    private static final int LOW_STOCK_THRESHOLD = 10;
    private LowStockAdapter lowStockAdapter;
    private RecentProductAdapter recentProductAdapter;
    private List<Product> lowStockList = new ArrayList<>();
    private List<Product> recentProductList = new ArrayList<>();

    public home() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return view;
        }

        initializeViews(view);
        initializeDatabaseReferences();
        setupRecyclerViews();
        loadDashboardData();
        setupButtonListeners();

        return view;
    }

    private void initializeViews(View view) {
        txtTodaySales = view.findViewById(R.id.txtTodaySales);
        txtTodayIncome = view.findViewById(R.id.txtTodayIncome);
        txtTotalProducts = view.findViewById(R.id.txtTotalProducts);
        txtPendingOrders = view.findViewById(R.id.txtPendingOrders);
        txtYearlySales = view.findViewById(R.id.txtYearlySales);
        txtYearlyIncome = view.findViewById(R.id.txtYearlyIncome);
        txtCancelledOrders = view.findViewById(R.id.txtCancelledOrders);
        txtReturnedOrders = view.findViewById(R.id.txtReturnedOrders);
        txtAvailableStock = view.findViewById(R.id.txtAvailableStock);
        txtNoLowStock = view.findViewById(R.id.txtNoLowStock);
        txtNoRecentProducts = view.findViewById(R.id.txtNoRecentProducts);

        chartMonthlyPerformance = view.findViewById(R.id.chartMonthlyPerformance);

        recyclerLowStock = view.findViewById(R.id.recyclerLowStock);
        recyclerRecentProducts = view.findViewById(R.id.recyclerRecentProducts);

        btnAddProduct = view.findViewById(R.id.btnAddProduct);
        btnViewOrders = view.findViewById(R.id.btnViewOrders);
        btnViewCommission = view.findViewById(R.id.btnViewCommission);
        btnViewAnalytics = view.findViewById(R.id.btnViewAnalytics);
        btnStockManagement = view.findViewById(R.id.btnStockManagement);
        txtTotalIncome = view.findViewById(R.id.txtTotalIncome);


        setDefaultValues();
    }

    private void setDefaultValues() {
        txtTodaySales.setText("0");
        txtTodayIncome.setText("₹0");
        txtTotalProducts.setText("0");
        txtPendingOrders.setText("0");
        txtYearlySales.setText("0");
        txtYearlyIncome.setText("₹0");
        txtCancelledOrders.setText("0");
        txtReturnedOrders.setText("0");
        txtAvailableStock.setText("0");
    }

    private void initializeDatabaseReferences() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("Order");
        productRef = database.getReference("Product");
        cancelRef = database.getReference("Cancel");
        returnRef = database.getReference("Return");
    }

    private void setupRecyclerViews() {
        lowStockAdapter = new LowStockAdapter(lowStockList, getContext(), new LowStockAdapter.OnLowStockClickListener() {
            @Override
            public void onLowStockItemClick(Product product) {
                // Navigate to edit_product activity
                Intent intent = new Intent(getActivity(), edit_product.class);
                intent.putExtra("productId", product.getProductId());
                startActivity(intent);

                // Add animation if needed
                if (getActivity() != null) {
                }
            }
        });
        recyclerLowStock.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerLowStock.setAdapter(lowStockAdapter);

        recentProductAdapter = new RecentProductAdapter(recentProductList, getContext(), new RecentProductAdapter.OnRecentProductClickListener() {
            @Override
            public void onRecentProductClick(Product product) {
                // Navigate to edit_product activity
                Intent intent = new Intent(getActivity(), edit_product.class);
                intent.putExtra("productId", product.getProductId());
                startActivity(intent);

                // Add animation if needed
                if (getActivity() != null) {
                }
            }
        });
        recyclerRecentProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRecentProducts.setAdapter(recentProductAdapter);
    }

    private void setupButtonListeners() {
        btnAddProduct.setOnClickListener(v -> {
            Log.d(TAG, "Add Product clicked");
            Intent intent = new Intent(getActivity(), listing_product.class);
            startActivity(intent);
        });


        btnViewOrders.setOnClickListener(v -> {
            Log.d(TAG, "View Orders clicked");
            Intent intent = new Intent(getActivity(), order_Details.class);
            startActivity(intent);
        });

        btnViewCommission.setOnClickListener(v -> {
            Log.d(TAG, "Commission clicked");
            Intent intent = new Intent(getActivity(), commission_notification.class);
            startActivity(intent);
        });

        btnViewAnalytics.setOnClickListener(v -> {
            Log.d(TAG, "Analytics clicked");
            Intent intent = new Intent(getActivity(), report_selling.class);
            startActivity(intent);
        });

        btnStockManagement.setOnClickListener(v -> {
            Log.d(TAG, "Stock Management clicked");
            Intent intent = new Intent(getActivity(), inventory_product.class);
            startActivity(intent);
        });
    }

    private void loadDashboardData() {
        loadTodaySales();
        loadTodayIncome();
        loadTotalProducts();
        loadPendingOrders();
        loadYearlySales();
        loadYearlyIncome();
        loadCancelledOrders();
        loadReturnedOrders();
        loadAvailableStock();
        loadLowStockProducts();
        loadRecentProducts();
        loadMonthlyChart();
        loadTotalIncome(); // Add this line
    }

    // ==================== TOTAL INCOME (ALL TIME) ====================
    private void loadTotalIncome() {
        Query incomeQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        incomeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalNetIncome = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && shouldCountForSales(order.getOrderStatus())) {
                        try {
                            int qty = Integer.parseInt(order.getProductQty());
                            double sellingPrice = Double.parseDouble(order.getProductSellingPrice());

                            // Calculate net amount (sellingPrice - commission - platformFee)
                            double commissionPercentage = 10.0; // Default 10%
                            double commissionAmount = (sellingPrice * commissionPercentage) / 100;
                            double platformFee = 30.0;
                            double netAmount = (sellingPrice - commissionAmount - platformFee) * qty;

                            totalNetIncome += Math.round(netAmount);

                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error calculating total income for order: " + e.getMessage());
                        }
                    }
                }
                txtTotalIncome.setText("₹" + totalNetIncome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTotalIncome.setText("₹0");
            }
        });
    }
    // ==================== LOW STOCK PRODUCTS ====================
    private void loadLowStockProducts() {
        Query lowStockQuery = productRef.orderByChild("adminId").equalTo(firebaseUser.getUid());
        lowStockQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lowStockList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(dataSnapshot.getKey()); // Set the product ID

                        // Check if product has low stock (total stock <= 10)
                        if (isLowStockProduct(product)) {
                            lowStockList.add(product);
                        }
                    }
                }

                // Sort by most critical (lowest stock first)
                Collections.sort(lowStockList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        int stock1 = getProductStockValue(p1);
                        int stock2 = getProductStockValue(p2);
                        return Integer.compare(stock1, stock2);
                    }
                });

                updateLowStockUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load low stock products: " + error.getMessage());
            }
        });
    }
    private boolean isLowStockProduct(Product product) {
        if (product.getTotalStock() == null || product.getTotalStock().isEmpty()) {
            return false;
        }

        try {
            int totalStock = Integer.parseInt(product.getTotalStock().trim());

            // Check for products with sizes (clothing, shoes)
            if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                // Check individual sizes for low quantity
                for (String sizeQty : product.getProductSizes()) {
                    try {
                        int quantity = Integer.parseInt(sizeQty);
                        if (quantity <= 3 && quantity > 0) {
                            return true; // At least one size has low quantity
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid size values
                    }
                }

                // Also check total stock for sized products
                return totalStock <= 10;
            } else {
                // For products without sizes (smartphones, other categories)
                return totalStock <= 10;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private int getProductStockValue(Product product) {
        try {
            if (product.getTotalStock() != null) {
                return Integer.parseInt(product.getTotalStock().trim());
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing stock value for product: " + product.getProductName());
        }
        return 0;
    }
    private void updateLowStockUI() {
        if (lowStockList.isEmpty()) {
            txtNoLowStock.setVisibility(View.VISIBLE);
            recyclerLowStock.setVisibility(View.GONE);
        } else {
            txtNoLowStock.setVisibility(View.GONE);
            recyclerLowStock.setVisibility(View.VISIBLE);

            // Update the adapter with click listener
            lowStockAdapter = new LowStockAdapter(lowStockList, getContext(), new LowStockAdapter.OnLowStockClickListener() {
                @Override
                public void onLowStockItemClick(Product product) {
                    // Navigate to edit_product activity
                    Intent intent = new Intent(getActivity(), edit_product.class);
                    intent.putExtra("productId", product.getProductId());
                    startActivity(intent);

                    // Add animation if needed
                    if (getActivity() != null) {
                    }
                }
            });
            recyclerLowStock.setAdapter(lowStockAdapter);
            lowStockAdapter.notifyDataSetChanged();
        }
    }

    // ==================== RECENT PRODUCTS ====================
    private void loadRecentProducts() {
        Query recentQuery = productRef.orderByChild("adminId").equalTo(firebaseUser.getUid());
        recentQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG, "=== LOADING RECENT PRODUCTS ===");
                Log.d(TAG, "Current time: " + new Date());

                recentProductList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(dataSnapshot.getKey());

                        // DEBUG: Log ALL dates
                        Log.d(TAG, "Product: " + product.getProductName());
                        Log.d(TAG, "  - Created Date: " + product.getProductCreatedDate());
                        Log.d(TAG, "  - Last Updated Date: " + product.getProductLastUpdatedDate());
                        Log.d(TAG, "  - Timestamp: " + product.getTimestamp());
                        Log.d(TAG, "  - Timestamp to Date: " + (product.getTimestamp() > 0 ?
                                new Date(product.getTimestamp()).toString() : "N/A"));

                        recentProductList.add(product);
                    }
                }

                Log.d(TAG, "Total products loaded: " + recentProductList.size());

                // Sort by date/time - NEWEST FIRST
                Collections.sort(recentProductList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        try {
                            // Get latest date/time for both products
                            long time1 = getLatestTimestamp(p1);
                            long time2 = getLatestTimestamp(p2);

                            Log.d(TAG, "Comparing: " + p1.getProductName() + " (time: " + time1 + ") vs " +
                                    p2.getProductName() + " (time: " + time2 + ")");
                            Log.d(TAG, "  " + p1.getProductName() + " date: " +
                                    (time1 > 0 ? new Date(time1).toString() : "N/A"));
                            Log.d(TAG, "  " + p2.getProductName() + " date: " +
                                    (time2 > 0 ? new Date(time2).toString() : "N/A"));

                            // Compare timestamps - higher (newer) timestamp comes first
                            return Long.compare(time2, time1);

                        } catch (Exception e) {
                            Log.e(TAG, "Error comparing products: " + e.getMessage());
                            e.printStackTrace();
                            return 0;
                        }
                    }
                });

                // Log the final sorted order
                Log.d(TAG, "=== FINAL SORTED ORDER (NEWEST TO OLDEST) ===");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                for (int i = 0; i < recentProductList.size(); i++) {
                    Product p = recentProductList.get(i);
                    long latestTime = getLatestTimestamp(p);
                    String dateStr = latestTime > 0 ? sdf.format(new Date(latestTime)) : "No date";

                    Log.d(TAG, (i+1) + ". " + p.getProductName() +
                            " | Time: " + dateStr +
                            " | Updated: " + (p.getProductLastUpdatedDate() != null ? p.getProductLastUpdatedDate() : "N/A") +
                            " | Created: " + (p.getProductCreatedDate() != null ? p.getProductCreatedDate() : "N/A"));
                }

                // Keep only 5 most recent
                if (recentProductList.size() > 5) {
                    recentProductList = new ArrayList<>(recentProductList.subList(0, 5));
                    Log.d(TAG, "Keeping only 5 most recent products");
                }

                updateRecentProductsUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load recent products: " + error.getMessage());
            }
        });
    }

    // Get the LATEST timestamp for a product
    private long getLatestTimestamp(Product product) {
        // Priority 1: Use timestamp field (contains milliseconds)
        if (product.getTimestamp() > 0) {
            return product.getTimestamp();
        }

        // Priority 2: Try to parse productLastUpdatedDate (with time if available)
        if (product.getProductLastUpdatedDate() != null &&
                !product.getProductLastUpdatedDate().isEmpty()) {
            try {
                // Try with time format first
                SimpleDateFormat sdfWithTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                Date date = sdfWithTime.parse(product.getProductLastUpdatedDate());
                if (date != null) {
                    Log.d(TAG, "Parsed " + product.getProductName() + " last updated date with time: " + date.getTime());
                    return date.getTime();
                }
            } catch (Exception e1) {
                try {
                    // Try without time (just date)
                    SimpleDateFormat sdfDateOnly = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date date = sdfDateOnly.parse(product.getProductLastUpdatedDate());
                    if (date != null) {
                        Log.d(TAG, "Parsed " + product.getProductName() + " last updated date without time: " + date.getTime());
                        return date.getTime();
                    }
                } catch (Exception e2) {
                    Log.d(TAG, "Could not parse last updated date: " + product.getProductLastUpdatedDate());
                }
            }
        }

        // Priority 3: Try to parse productCreatedDate
        if (product.getProductCreatedDate() != null &&
                !product.getProductCreatedDate().isEmpty()) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = sdf.parse(product.getProductCreatedDate());
                if (date != null) {
                    return date.getTime();
                }
            } catch (Exception e) {
                Log.d(TAG, "Could not parse created date: " + product.getProductCreatedDate());
            }
        }

        // Default: return 0 (very old)
        return 0L;
    }

    private void updateRecentProductsUI() {
        if (recentProductList.isEmpty()) {
            txtNoRecentProducts.setVisibility(View.VISIBLE);
            recyclerRecentProducts.setVisibility(View.GONE);
        } else {
            txtNoRecentProducts.setVisibility(View.GONE);
            recyclerRecentProducts.setVisibility(View.VISIBLE);

            // Already sorted by timestamp (newest first)
            recentProductAdapter.notifyDataSetChanged();
        }
    }


    // ==================== EXISTING DASHBOARD METHODS ====================
    private void loadTodaySales() {
        String todayDate = getCurrentDate();
        Query todayQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        todayQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalQty = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && todayDate.equals(order.getOrderDate()) && shouldCountForSales(order.getOrderStatus())) {
                        try {
                            totalQty += Integer.parseInt(order.getProductQty());
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing today's sales quantity");
                        }
                    }
                }
                txtTodaySales.setText(String.valueOf(totalQty));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTodaySales.setText("0");
            }
        });
    }

    private void loadTodayIncome() {
        String todayDate = getCurrentDate();
        Query todayQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        todayQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalNetIncome = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && todayDate.equals(order.getOrderDate()) && shouldCountForSales(order.getOrderStatus())) {
                        try {
                            int qty = Integer.parseInt(order.getProductQty());
                            double sellingPrice = Double.parseDouble(order.getProductSellingPrice());

                            // Simple calculation: 10% commission + ₹30 platform fee
                            double commissionPercentage = 10.0;
                            double commissionAmount = (sellingPrice * commissionPercentage) / 100;
                            double platformFee = 30.0;
                            double netAmount = (sellingPrice - commissionAmount - platformFee) * qty;

                            totalNetIncome += Math.round(netAmount);

                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error calculating today's income");
                        }
                    }
                }
                txtTodayIncome.setText("₹" + totalNetIncome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTodayIncome.setText("₹0");
            }
        });
    }
    // Add this helper method
    private void getProductDetails(String productId, ProductDetailsCallback callback) {
        if (productId == null || productId.isEmpty()) {
            callback.onDetailsReceived(null);
            return;
        }

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product")
                .child(productId);

        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = null;
                if (snapshot.exists()) {
                    product = snapshot.getValue(Product.class);
                }
                callback.onDetailsReceived(product);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onDetailsReceived(null);
            }
        });
    }

    // Add this interface
    interface ProductDetailsCallback {
        void onDetailsReceived(Product product);
    }


    private void loadTotalProducts() {
        Query productQuery = productRef.orderByChild("adminId").equalTo(firebaseUser.getUid());
        productQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtTotalProducts.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTotalProducts.setText("0");
            }
        });
    }

    private void loadPendingOrders() {
        Query pendingQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        pendingQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long pendingCount = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && shouldCountForPending(order.getOrderStatus())) {
                        pendingCount++;
                    }
                }
                txtPendingOrders.setText(String.valueOf(pendingCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtPendingOrders.setText("0");
            }
        });
    }

    private void loadYearlySales() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Query yearlyQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        yearlyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long yearlyQty = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && shouldCountForSales(order.getOrderStatus())) {  // CHANGED HERE
                        int orderYear = getYearFromDate(order.getOrderDate());
                        if (orderYear == currentYear) {
                            try {
                                yearlyQty += Integer.parseInt(order.getProductQty());
                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Error parsing yearly sales quantity");
                            }
                        }
                    }
                }
                txtYearlySales.setText(String.valueOf(yearlyQty));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtYearlySales.setText("0");
            }
        });
    }
    private void loadYearlyIncome() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Query yearlyQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        yearlyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AtomicLong yearlyNetIncome = new AtomicLong(0);
                AtomicInteger processedOrders = new AtomicInteger(0);
                int totalOrders = (int) snapshot.getChildrenCount();

                // If no orders for this year
                if (totalOrders == 0) {
                    txtYearlyIncome.setText("₹0");
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && shouldCountForSales(order.getOrderStatus())) {
                        int orderYear = getYearFromDate(order.getOrderDate());
                        if (orderYear == currentYear) {
                            try {
                                int qty = Integer.parseInt(order.getProductQty());
                                double sellingPrice = Double.parseDouble(order.getProductSellingPrice());
                                String productId = order.getProductId();

                                // Get product details to calculate correct commission
                                getProductDetails(productId, new ProductDetailsCallback() {
                                    @Override
                                    public void onDetailsReceived(Product product) {
                                        String category = product != null ? product.getProductCategory() : "General";
                                        String subCategory = product != null ? product.getProductSubCategory() : "General";

                                        // Use CommissionCalculator with category and subcategory
                                        int commissionPercentage = CommissionCalculator.getCommissionPercentage(category, subCategory);
                                        double commissionAmount = CommissionCalculator.calculateCommission(sellingPrice, category, subCategory);
                                        double platformFee = 30.0;
                                        double netAmount = (sellingPrice - commissionAmount - platformFee) * qty;

                                        yearlyNetIncome.addAndGet(Math.round(netAmount));

                                        int currentProcessed = processedOrders.incrementAndGet();

                                        // Update UI when all orders are processed
                                        if (currentProcessed == totalOrders) {
                                            txtYearlyIncome.setText("₹" + yearlyNetIncome.get());
                                        }

                                        Log.d(TAG, "Yearly Income Calc: " +
                                                (product != null ? product.getProductName() : "Unknown") +
                                                " | Price: ₹" + sellingPrice +
                                                " | Commission: " + commissionPercentage + "% = ₹" + commissionAmount +
                                                " | Net: ₹" + netAmount);
                                    }
                                });

                            } catch (NumberFormatException e) {
                                Log.e(TAG, "Error calculating yearly income");
                                int currentProcessed = processedOrders.incrementAndGet();
                                if (currentProcessed == totalOrders) {
                                    txtYearlyIncome.setText("₹" + yearlyNetIncome.get());
                                }
                            }
                        } else {
                            int currentProcessed = processedOrders.incrementAndGet();
                            if (currentProcessed == totalOrders) {
                                txtYearlyIncome.setText("₹" + yearlyNetIncome.get());
                            }
                        }
                    } else {
                        int currentProcessed = processedOrders.incrementAndGet();
                        if (currentProcessed == totalOrders) {
                            txtYearlyIncome.setText("₹" + yearlyNetIncome.get());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtYearlyIncome.setText("₹0");
            }
        });
    }
    // Helper method to get product details
    private void loadCancelledOrders() {
        Query cancelQuery = cancelRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        cancelQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtCancelledOrders.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtCancelledOrders.setText("0");
            }
        });
    }

    private void loadReturnedOrders() {
        Query returnQuery = returnRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        returnQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtReturnedOrders.setText(String.valueOf(snapshot.getChildrenCount()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtReturnedOrders.setText("0");
            }
        });
    }

    private void loadAvailableStock() {
        Query stockQuery = productRef.orderByChild("adminId").equalTo(firebaseUser.getUid());
        stockQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalStock = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null && product.getTotalStock() != null) {
                        try {
                            totalStock += Long.parseLong(product.getTotalStock());
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing available stock");
                        }
                    }
                }
                txtAvailableStock.setText(String.valueOf(totalStock));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtAvailableStock.setText("0");
            }
        });
    }

    private void loadMonthlyChart() {
        HashMap<Integer, Float> dayRevenueMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);

        Query monthlyQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        monthlyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dayRevenueMap.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && order.getOrderDate() != null && shouldCountForSales(order.getOrderStatus())) {
                        try {
                            String[] dateParts = order.getOrderDate().split("-");
                            if (dateParts.length == 3) {
                                int orderDay = Integer.parseInt(dateParts[0]);
                                int orderMonth = Integer.parseInt(dateParts[1]);
                                int orderYear = Integer.parseInt(dateParts[2]);

                                if (orderMonth == currentMonth && orderYear == currentYear) {
                                    int qty = Integer.parseInt(order.getProductQty());
                                    float price = Float.parseFloat(order.getProductSellingPrice());
                                    float revenue = qty * price;

                                    float currentRevenue = dayRevenueMap.getOrDefault(orderDay, 0f);
                                    dayRevenueMap.put(orderDay, currentRevenue + revenue);
                                }
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error processing chart data");
                        }
                    }
                }

                updateChart(dayRevenueMap, calendar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load monthly chart data");
            }
        });
    }

    private void updateChart(HashMap<Integer, Float> dayRevenueMap, Calendar calendar) {
        try {
            ArrayList<Entry> entries = new ArrayList<>();
            int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            for (int day = 1; day <= daysInMonth; day++) {
                float revenue = dayRevenueMap.getOrDefault(day, 0f);
                entries.add(new Entry(day, revenue));
            }

            if (!entries.isEmpty()) {
                LineDataSet lineDataSet = new LineDataSet(entries, "Daily Income");
                lineDataSet.setColor(getResources().getColor(R.color.green));
                lineDataSet.setValueTextSize(10f);
                lineDataSet.setLineWidth(2f);
                lineDataSet.setDrawCircles(true);
                lineDataSet.setCircleRadius(4f);
                lineDataSet.setDrawFilled(true);
                lineDataSet.setFillAlpha(100);
                lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

                LineData lineData = new LineData(lineDataSet);
                chartMonthlyPerformance.setData(lineData);
                chartMonthlyPerformance.getDescription().setEnabled(false);
                chartMonthlyPerformance.setTouchEnabled(true);
                chartMonthlyPerformance.setDragEnabled(true);
                chartMonthlyPerformance.setScaleEnabled(true);
                chartMonthlyPerformance.setPinchZoom(true);
                chartMonthlyPerformance.getLegend().setEnabled(false);
                chartMonthlyPerformance.animateX(1000);

                XAxis xAxis = chartMonthlyPerformance.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(6);

                chartMonthlyPerformance.invalidate();
            } else {
                chartMonthlyPerformance.clear();
                chartMonthlyPerformance.setNoDataText("No sales data available for this month");
                chartMonthlyPerformance.invalidate();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating chart: " + e.getMessage());
        }
    }

    // ==================== UTILITY METHODS ====================
    private String getCurrentDate() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            return sdf.format(new Date());
        } catch (Exception e) {
            Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            return String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month, year);
        }
    }

    private int getYearFromDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return -1;
        try {
            String[] parts = dateString.split("-");
            if (parts.length == 3) {
                return Integer.parseInt(parts[2]);
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing year from date");
        }
        return -1;
    }

    private boolean shouldCountForSales(String status) {
        if (status == null) return false;
        return "delivered".equals(status) || "new".equals(status) ||
                "confirmed".equals(status) || "shipped".equals(status) ||
                "shiping".equals(status) || "shipping".equals(status); // Include both spellings
    }

    private boolean shouldCountForPending(String status) {
        if (status == null) return false;
        return "pending".equals(status) || "processing".equals(status) ||
                "new".equals(status) || "confirmed".equals(status) ||
                "shipped".equals(status) || "shiping".equals(status) ||
                "shipping".equals(status);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HomeFragment resumed - refreshing recent products");

        // Force refresh data when returning from edit_product
        if (recentProductList != null && recentProductList.size() > 0) {
            Log.d(TAG, "Current recent products in memory: " + recentProductList.size());
            for (Product p : recentProductList) {
                Log.d(TAG, "In memory: " + p.getProductName() +
                        " | Updated: " + p.getProductLastUpdatedDate());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "HomeFragment paused");
    }
}