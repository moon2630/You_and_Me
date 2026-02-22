package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.uptrendseller.Adapter.LowStockAlertAdapter;
import com.example.uptrendseller.Adapter.ProductPerformanceAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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

import DataModel.Order;
import DataModel.Product;

public class report_selling extends AppCompatActivity {

    private TextView txtTodayNetIncome, txtTodayOrders;
    private TextView txtTotalNetIncome, txtTotalOrders, txtIncomePeriod, txtCurrentMonth,txtTotalSellingOrders;
    private TextView txtTopProductsCount, txtNoLowStock;
    private BarChart chartMonthlyIncome;
    private RecyclerView recyclerTopProducts, recyclerLowStock;
    private TextView btnBack;
    private PieChart chartOrderStatus;

    private FirebaseUser firebaseUser;
    private DatabaseReference orderRef, productRef;
    private ProductPerformanceAdapter topProductsAdapter;
    private LowStockAlertAdapter lowStockAdapter;
    private List<Product> topProductsList = new ArrayList<>();
    private List<Product> lowStockList = new ArrayList<>();

    private static final String TAG = "ReportCombinedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_selling);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            finish();
            return;
        }

        initViews();
        setupRecyclerViews();
        loadAllReports();
    }

    private void initViews() {
        txtTodayNetIncome = findViewById(R.id.txtTodayNetIncome);
        txtTodayOrders = findViewById(R.id.txtTodayOrders);
        txtTotalNetIncome = findViewById(R.id.txtTotalNetIncome);
        txtTotalOrders = findViewById(R.id.txtTotalOrders);
        txtIncomePeriod = findViewById(R.id.txtIncomePeriod);
        txtCurrentMonth = findViewById(R.id.txtCurrentMonth);
        txtTopProductsCount = findViewById(R.id.txtTopProductsCount);
        txtNoLowStock = findViewById(R.id.txtNoLowStock);
        chartMonthlyIncome = findViewById(R.id.chartMonthlyIncome);
        recyclerTopProducts = findViewById(R.id.recyclerTopProducts);
        recyclerLowStock = findViewById(R.id.recyclerLowStock);
        txtTotalSellingOrders = findViewById(R.id.txtTotalSellingOrders);
        chartOrderStatus = findViewById(R.id.chartOrderStatus);
        btnBack = findViewById(R.id.btnBack);

        // Set current month
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        txtCurrentMonth.setText(monthFormat.format(calendar.getTime()));

        btnBack.setOnClickListener(v -> onBackPressed());

    }

    private void setupRecyclerViews() {
        // Top Products Adapter
        topProductsAdapter = new ProductPerformanceAdapter(topProductsList, this);
        recyclerTopProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerTopProducts.setAdapter(topProductsAdapter);

        // Low Stock Adapter
        lowStockAdapter = new LowStockAlertAdapter(lowStockList, this);
        recyclerLowStock.setLayoutManager(new LinearLayoutManager(this));
        recyclerLowStock.setAdapter(lowStockAdapter);
    }

    private void loadAllReports() {
        initializeDatabaseReferences();
        loadTodayNetIncome();
        loadTotalNetIncome();
        loadMonthlyIncomeChart();
        loadTopSellingProducts();
        loadLowStockProducts();
        loadTotalSellingOrders();
        loadOrderStatusChart();
    }

    private void initializeDatabaseReferences() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        orderRef = database.getReference("Order");
        productRef = database.getReference("Product");
    }

    // ==================== TOTAL SELLING ORDERS ====================
    private void loadTotalSellingOrders() {
        Query ordersQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        ordersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalSellingOrders = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && shouldCountForDeliveredOnly(order.getOrderStatus())) {
                        // Count only delivered orders
                        totalSellingOrders++;
                    }
                }

                txtTotalSellingOrders.setText(String.valueOf(totalSellingOrders));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTotalSellingOrders.setText("0");
            }
        });
    }
    // ==================== ORDER STATUS CHART ====================
    private void loadOrderStatusChart() {
        Query ordersQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        ordersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int delivered = 0;
                int pending = 0;
                int cancelled = 0;
                int returned = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        String status = order.getOrderStatus();

                        if ("delivered".equals(status)) {
                            delivered++;
                        } else if ("pending".equals(status) || "processing".equals(status) ||
                            "new".equals(status) || "confirmed".equals(status) ||
                            "shipped".equals(status) || "shiping".equals(status) || // Add this
                            "shipping".equals(status)) { // Add this
                        pending++;
                        } else if ("cancelled".equals(status) || "canceled".equals(status)) {
                            cancelled++;
                        } else if ("returned".equals(status)) {
                            returned++;
                        }
                    }
                }

                updateOrderStatusChart(delivered, pending, cancelled, returned);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load order status data");
            }
        });
    }
    private void updateOrderStatusChart(int delivered, int pending, int cancelled, int returned) {
        try {
            ArrayList<PieEntry> entries = new ArrayList<>();

            if (delivered > 0) entries.add(new PieEntry(delivered, "Delivered"));
            if (pending > 0) entries.add(new PieEntry(pending, "In Progress"));
            if (cancelled > 0) entries.add(new PieEntry(cancelled, "Cancelled"));
            if (returned > 0) entries.add(new PieEntry(returned, "Returned"));

            if (!entries.isEmpty()) {
                PieDataSet dataSet = new PieDataSet(entries, "Order Status");

                // Set colors
                int[] colors = new int[]{
                        getResources().getColor(R.color.green),     // Delivered - Green
                        getResources().getColor(R.color.orange3333),    // In Progress - Orange
                        getResources().getColor(R.color.red),       // Cancelled - Red
                        getResources().getColor(R.color.blue)     // Returned - Purple
                };
                dataSet.setColors(colors);

                dataSet.setValueTextSize(12f);
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });

                PieData data = new PieData(dataSet);
                chartOrderStatus.setData(data);
                chartOrderStatus.getDescription().setEnabled(false);
                chartOrderStatus.setDrawEntryLabels(true);
                chartOrderStatus.setUsePercentValues(true);
                chartOrderStatus.setEntryLabelColor(Color.BLACK);
                chartOrderStatus.setEntryLabelTextSize(12f);
                chartOrderStatus.getLegend().setEnabled(true);
                chartOrderStatus.getLegend().setTextSize(12f);
                chartOrderStatus.setCenterText("Orders\n" + (delivered + pending + cancelled + returned));
                chartOrderStatus.setCenterTextSize(14f);
                chartOrderStatus.setCenterTextColor(Color.BLACK);
                chartOrderStatus.setTransparentCircleRadius(0f);
                chartOrderStatus.setHoleRadius(40f);
                chartOrderStatus.setTransparentCircleRadius(45f);
                chartOrderStatus.animateY(1000);
                chartOrderStatus.invalidate();
            } else {
                chartOrderStatus.clear();
                chartOrderStatus.setNoDataText("No order data available");
                chartOrderStatus.setNoDataTextColor(Color.GRAY);
                chartOrderStatus.invalidate();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating order status chart: " + e.getMessage());
        }
    }

    // 1. TODAY'S NET INCOME
    private void loadTodayNetIncome() {
        String todayDate = getCurrentDate();
        Query todayQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        todayQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long todayNetIncome = 0;
                int todayOrderCount = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && todayDate.equals(order.getOrderDate())) {
                        String orderStatus = order.getOrderStatus();
                        if (orderStatus != null && shouldCountForSales(orderStatus))
                        try {
                            int qty = Integer.parseInt(order.getProductQty());
                            double sellingPrice = Double.parseDouble(order.getProductSellingPrice());

                            // Calculate net amount
                            double commissionPercentage = 10.0;
                            double commissionAmount = (sellingPrice * commissionPercentage) / 100;
                            double platformFee = 30.0;
                            double netAmount = (sellingPrice - commissionAmount - platformFee) * qty;

                            todayNetIncome += Math.round(netAmount);
                            todayOrderCount++;

                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error calculating today's income");
                        }
                    }
                }

                txtTodayNetIncome.setText("₹" + todayNetIncome);
                txtTodayOrders.setText(todayOrderCount + " orders today");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTodayNetIncome.setText("₹0");
                txtTodayOrders.setText("0 orders today");
            }
        });
    }

    // 2. TOTAL NET INCOME (ALL TIME)
    private void loadTotalNetIncome() {
        Query incomeQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        incomeQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalNetIncome = 0;
                int totalOrderCount = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null) {
                        String orderStatus = order.getOrderStatus();
                        if (orderStatus != null && shouldCountForSales(orderStatus))
                        try {
                            int qty = Integer.parseInt(order.getProductQty());
                            double sellingPrice = Double.parseDouble(order.getProductSellingPrice());

                            double commissionPercentage = 10.0;
                            double commissionAmount = (sellingPrice * commissionPercentage) / 100;
                            double platformFee = 30.0;
                            double netAmount = (sellingPrice - commissionAmount - platformFee) * qty;

                            totalNetIncome += Math.round(netAmount);
                            totalOrderCount++;

                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error calculating total income");
                        }
                    }
                }

                txtTotalNetIncome.setText("₹" + totalNetIncome);
                txtTotalOrders.setText(totalOrderCount + " total orders");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtTotalNetIncome.setText("₹0");
                txtTotalOrders.setText("0 total orders");
            }
        });
    }

    // 3. MONTHLY INCOME CHART
    private void loadMonthlyIncomeChart() {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        Query monthlyQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        monthlyQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<Integer, Float> dayIncomeMap = new HashMap<>();

                // Initialize all days with 0
                for (int day = 1; day <= daysInMonth; day++) {
                    dayIncomeMap.put(day, 0f);
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && order.getOrderDate() != null) {
                        String orderStatus = order.getOrderStatus();
                        if (orderStatus != null && shouldCountForSales(orderStatus))
                        try {
                            String[] dateParts = order.getOrderDate().split("-");
                            if (dateParts.length == 3) {
                                int orderDay = Integer.parseInt(dateParts[0]);
                                int orderMonth = Integer.parseInt(dateParts[1]);
                                int orderYear = Integer.parseInt(dateParts[2]);

                                if (orderMonth == currentMonth && orderYear == currentYear) {
                                    int qty = Integer.parseInt(order.getProductQty());
                                    float price = Float.parseFloat(order.getProductSellingPrice());

                                    float commissionPercentage = 10f;
                                    float commissionAmount = (price * commissionPercentage) / 100;
                                    float platformFee = 30f;
                                    float netAmount = (price - commissionAmount - platformFee) * qty;

                                    float currentIncome = dayIncomeMap.getOrDefault(orderDay, 0f);
                                    dayIncomeMap.put(orderDay, currentIncome + netAmount);
                                }
                            }
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error processing chart data");
                        }
                    }
                }

                updateMonthlyChart(dayIncomeMap, daysInMonth);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load monthly chart data");
            }
        });
    }

    private void updateMonthlyChart(Map<Integer, Float> dayIncomeMap, int daysInMonth) {
        try {
            ArrayList<BarEntry> entries = new ArrayList<>();

            for (int day = 1; day <= daysInMonth; day++) {
                float income = dayIncomeMap.getOrDefault(day, 0f);
                entries.add(new BarEntry(day, income));
            }

            if (!entries.isEmpty()) {
                BarDataSet barDataSet = new BarDataSet(entries, "Daily Net Income");
                barDataSet.setColor(getResources().getColor(R.color.blue));
                barDataSet.setValueTextSize(10f);
                barDataSet.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return "₹" + Math.round(value);
                    }
                });

                BarData barData = new BarData(barDataSet);
                chartMonthlyIncome.setData(barData);
                chartMonthlyIncome.getDescription().setEnabled(false);
                chartMonthlyIncome.setTouchEnabled(true);
                chartMonthlyIncome.setDragEnabled(true);
                chartMonthlyIncome.setScaleEnabled(true);
                chartMonthlyIncome.setPinchZoom(true);
                chartMonthlyIncome.getLegend().setEnabled(false);
                chartMonthlyIncome.animateY(1000);

                // X-axis configuration
                XAxis xAxis = chartMonthlyIncome.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(6);
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value);
                    }
                });

                // Y-axis configuration
                YAxis leftAxis = chartMonthlyIncome.getAxisLeft();
                leftAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return "₹" + Math.round(value);
                    }
                });

                chartMonthlyIncome.getAxisRight().setEnabled(false);
                chartMonthlyIncome.invalidate();
            } else {
                chartMonthlyIncome.clear();
                chartMonthlyIncome.setNoDataText("No sales data for this month");
                chartMonthlyIncome.invalidate();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating chart: " + e.getMessage());
        }
    }


    private void loadTopSellingProducts() {
        // First, get sales count for each product
        Map<String, Integer> productSalesCount = new HashMap<>();

        Query salesQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());
        salesQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productSalesCount.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && order.getOrderStatus() != null) {
                        String orderStatus = order.getOrderStatus().toLowerCase().trim();

                        // Count only valid sales statuses
                        if (shouldCountForSales(orderStatus)) {
                            String productId = order.getProductId();
                            if (productId != null && !productId.isEmpty()) {
                                try {
                                    int qty = Integer.parseInt(order.getProductQty());
                                    int currentCount = productSalesCount.getOrDefault(productId, 0);
                                    productSalesCount.put(productId, currentCount + qty);
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Error parsing quantity for product: " + productId);
                                } catch (Exception e) {
                                    Log.e(TAG, "Error processing order for top products: " + e.getMessage());
                                }
                            }
                        }
                    }
                }

                Log.d(TAG, "Product sales count map size: " + productSalesCount.size());

                // Now get product details for top sellers
                if (!productSalesCount.isEmpty()) {
                    loadTopProductsDetails(productSalesCount);
                } else {
                    // Clear the list if no sales
                    topProductsList.clear();
                    txtTopProductsCount.setText("Top 0");
                    topProductsAdapter.updateList(new ArrayList<>(topProductsList));
                    Log.d(TAG, "No sales data found for top products");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product sales data: " + error.getMessage());
                // Clear the list on error
                topProductsList.clear();
                txtTopProductsCount.setText("Top 0");
                topProductsAdapter.updateList(new ArrayList<>(topProductsList));
            }
        });
    }

    private void loadTopProductsDetails(Map<String, Integer> productSalesCount) {
        if (productSalesCount == null || productSalesCount.isEmpty()) {
            topProductsList.clear();
            txtTopProductsCount.setText("Top 0");
            topProductsAdapter.updateList(new ArrayList<>(topProductsList));
            return;
        }

        Query productQuery = productRef.orderByChild("adminId").equalTo(firebaseUser.getUid());
        productQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                topProductsList.clear();

                // Create a temporary list to store products with sales count
                List<Product> tempList = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(dataSnapshot.getKey());
                        String productId = product.getProductId();

                        // Get sales count for this product
                        int salesCount = productSalesCount.getOrDefault(productId, 0);
                        product.setSalesCount(salesCount);

                        // Only add products that have sales
                        if (salesCount > 0) {
                            tempList.add(product);
                        }
                    }
                }

                // Sort by sales count (highest first)
                Collections.sort(tempList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        return Integer.compare(p2.getSalesCount(), p1.getSalesCount());
                    }
                });

                // Keep only top 5
                if (tempList.size() > 5) {
                    topProductsList = new ArrayList<>(tempList.subList(0, 5));
                } else {
                    topProductsList = new ArrayList<>(tempList);
                }

                // Update UI
                txtTopProductsCount.setText("Top " + topProductsList.size());
                topProductsAdapter.updateList(new ArrayList<>(topProductsList));

                Log.d(TAG, "Top products loaded: " + topProductsList.size());
                for (Product p : topProductsList) {
                    Log.d(TAG, "Product: " + p.getProductName() + ", Sales: " + p.getSalesCount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to load product details: " + error.getMessage());
                // Clear the list on error
                topProductsList.clear();
                txtTopProductsCount.setText("Top 0");
                topProductsAdapter.updateList(new ArrayList<>(topProductsList));
            }
        });
    }

    // UTILITY METHODS - Make sure this is updated too
    // 5. LOW STOCK PRODUCTS
    private void loadLowStockProducts() {
        Query lowStockQuery = productRef.orderByChild("adminId").equalTo(firebaseUser.getUid());
        lowStockQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lowStockList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(dataSnapshot.getKey());

                        // Check if product has low stock
                        if (isLowStockProduct(product)) {
                            lowStockList.add(product);
                        }
                    }
                }

                // Sort by lowest stock first
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
                Log.e(TAG, "Failed to load low stock products");
            }
        });
    }

    private boolean isLowStockProduct(Product product) {
        if (product.getTotalStock() == null || product.getTotalStock().isEmpty()) {
            return false;
        }

        try {
            int totalStock = Integer.parseInt(product.getTotalStock().trim());
            return totalStock <= 10;
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
            Log.e(TAG, "Error parsing stock value");
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
            lowStockAdapter.updateList(new ArrayList<>(lowStockList));
        }
    }

    // UTILITY METHODS
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    // Add this method for counting selling orders
    private boolean shouldCountForDeliveredOnly(String status) {
        return "delivered".equals(status); // Count ONLY delivered orders for total selling
    }
    private boolean shouldCountForSales(String status) {
        if (status == null) return false;

        String normalizedStatus = status.toLowerCase().trim();
        return "delivered".equals(normalizedStatus) ||
                "new".equals(normalizedStatus) ||
                "confirmed".equals(normalizedStatus) ||
                "shipped".equals(normalizedStatus) ||
                "shiping".equals(normalizedStatus) ||
                "shipping".equals(normalizedStatus) ||
                "processing".equals(normalizedStatus) ||
                "pending".equals(normalizedStatus);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, dashboard_admin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}