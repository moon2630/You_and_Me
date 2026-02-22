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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.uptrendseller.Adapter.CommissionAdapter;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import DataModel.Commission;
import DataModel.Order;
import io.github.muddz.styleabletoast.StyleableToast;

public class commission_notification extends AppCompatActivity {
    private RecyclerView recyclerViewCommissions;
    private TextView txtPendingAmount, txtCreditedAmount, txtDebitedAmount, txtNextPayout;
    private TextView txtEmptyState, txtCommissionDate,back20;
    private ProgressBar progressBar;
    private TextView btnAll, btnPending, btnCredited, btnDebited;
    private DatabaseReference commissionRef;
    private FirebaseUser firebaseUser;
    private ArrayList<Commission> commissionList = new ArrayList<>();
    private ArrayList<Commission> filteredList = new ArrayList<>();
    private CommissionAdapter commissionAdapter;
    private String currentFilter = "all";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commission_notification);

        // Set status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        Log.d("CommissionDebug", "Commission Activity Started");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupFilterButtons();
        loadCommissionData();

        checkAndCreateCommissions();
        updateCommissionStatusAutomatically();
        back20.setOnClickListener(v -> onBackPressed());


        Log.d("CommissionDebug", "Expected workflow for ₹1500 order:");
        Log.d("CommissionDebug", "1. Day 0 - Pending: Commission=₹150, Credited: Net=₹1320");
        Log.d("CommissionDebug", "2. Day 13 - Credited: Pending=₹0, Credited=₹1320, Debited=₹150");
        Log.d("CommissionDebug", "Note: Credited amount stays ₹1320, Commission moves from Pending to Debited");
    }

    private void initViews() {
        recyclerViewCommissions = findViewById(R.id.recyclerViewCommissions);
        txtPendingAmount = findViewById(R.id.txtPendingAmount);
        txtCreditedAmount = findViewById(R.id.txtCreditedAmount);
        txtDebitedAmount = findViewById(R.id.txtDebitedAmount);
        txtNextPayout = findViewById(R.id.txtNextPayout);
        txtEmptyState = findViewById(R.id.txtEmptyState);
        txtCommissionDate = findViewById(R.id.txtCommissionDate);
        progressBar = findViewById(R.id.progressBar);
        btnAll = findViewById(R.id.btnAll);
        btnPending = findViewById(R.id.btnPending);
        btnCredited = findViewById(R.id.btnCredited);
        btnDebited = findViewById(R.id.btnDebited);
        back20 = findViewById(R.id.back20);

        txtEmptyState.setVisibility(View.VISIBLE);


        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        txtCommissionDate.setText("As of " + sdf.format(new Date()));
    }

    private void setupRecyclerView() {
        commissionAdapter = new CommissionAdapter(this, filteredList);
        recyclerViewCommissions.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCommissions.setAdapter(commissionAdapter);
    }

    private void setupFilterButtons() {
        btnAll.setOnClickListener(v -> {
            currentFilter = "all";
            Log.d("FilterClick", "All button clicked");

            // Hide progress bar instantly when filtering
            progressBar.setVisibility(View.GONE);

            filterCommissions();
            updateButtonStates();
        });

        btnPending.setOnClickListener(v -> {
            currentFilter = "pending";
            Log.d("FilterClick", "Pending button clicked");

            // Hide progress bar instantly when filtering
            progressBar.setVisibility(View.GONE);

            filterCommissions();
            updateButtonStates();
        });

        btnCredited.setOnClickListener(v -> {
            currentFilter = "credited";
            Log.d("FilterClick", "Credited button clicked");

            // Hide progress bar instantly when filtering
            progressBar.setVisibility(View.GONE);

            filterCommissions();
            updateButtonStates();
        });

        btnDebited.setOnClickListener(v -> {
            currentFilter = "debited";
            Log.d("FilterClick", "Debited button clicked");

            // Hide progress bar instantly when filtering
            progressBar.setVisibility(View.GONE);

            filterCommissions();
            updateButtonStates();
        });

        updateButtonStates();
    }
    private void updateButtonStates() {
        btnAll.setAlpha(currentFilter.equals("all") ? 1.0f : 0.5f);
        btnPending.setAlpha(currentFilter.equals("pending") ? 1.0f : 0.5f);
        btnCredited.setAlpha(currentFilter.equals("credited") ? 1.0f : 0.5f);
        btnDebited.setAlpha(currentFilter.equals("debited") ? 1.0f : 0.5f);
    }

    private void loadCommissionData() {
        if (firebaseUser == null) return;

        // Show progress only when initially loading data
        if (commissionList.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);
            txtEmptyState.setVisibility(View.GONE);
            recyclerViewCommissions.setVisibility(View.GONE);
        }

        commissionRef = FirebaseDatabase.getInstance().getReference("Commissions")
                .child(firebaseUser.getUid());

        commissionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commissionList.clear();
                final double[] pendingAmount = {0};
                final double[] creditedAmount = {0};
                final double[] debitedAmount = {0};
                final int[] pendingCount = {0};

                Log.d("CommissionDebug", "=== LOADING COMMISSIONS ===");

                // Check if snapshot is empty
                if (!snapshot.exists()) {
                    // No commission data found
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        txtEmptyState.setVisibility(View.VISIBLE);
                        recyclerViewCommissions.setVisibility(View.GONE);

                        txtPendingAmount.setText("₹0");
                        txtCreditedAmount.setText("₹0");
                        txtDebitedAmount.setText("₹0");
                        txtNextPayout.setText("No pending payouts");

                        filteredList.clear();
                        commissionAdapter.notifyDataSetChanged();
                    });
                    return;
                }

                // Temporary list for async checks
                List<Commission> validCommissions = new ArrayList<>();
                List<String> commissionsToDelete = new ArrayList<>();

                for (DataSnapshot commissionSnapshot : snapshot.getChildren()) {
                    Commission commission = commissionSnapshot.getValue(Commission.class);

                    if (commission != null) {
                        String orderId = commission.getOrderId();
                        String commissionKey = commissionSnapshot.getKey();

                        // Check if order exists
                        checkIfOrderExists(orderId, new OrderExistsCallback() {
                            @Override
                            public void onResult(boolean orderExists) {
                                if (orderExists) {
                                    // Order exists - add to valid list
                                    commission.setNodeId(commissionKey);
                                    validCommissions.add(commission);

                                    // Calculate amounts
                                    try {
                                        double commissionAmt = Double.parseDouble(commission.getCommissionAmount());
                                        double platformFee = Double.parseDouble(commission.getPlatformFee());
                                        double netAmt = Double.parseDouble(commission.getNetAmount());
                                        String status = commission.getStatus();
                                        double totalCommissionAndFee = commissionAmt + platformFee;

                                        if ("pending".equals(status)) {
                                            pendingAmount[0] += totalCommissionAndFee;
                                            creditedAmount[0] += netAmt;
                                            pendingCount[0]++;
                                        } else if ("credited".equals(status)) {
                                            creditedAmount[0] += netAmt;
                                            debitedAmount[0] += totalCommissionAndFee;
                                        } else if ("debited".equals(status)) {
                                            creditedAmount[0] += netAmt;
                                            debitedAmount[0] += totalCommissionAndFee;
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("CommissionDebug", "Error parsing amount");
                                    }
                                } else {
                                    // Order doesn't exist - mark for deletion
                                    commissionsToDelete.add(commissionKey);
                                }

                                // Check if all commissions processed
                                if (validCommissions.size() + commissionsToDelete.size() == snapshot.getChildrenCount()) {
                                    // Delete orphaned commissions
                                    for (String key : commissionsToDelete) {
                                        commissionRef.child(key).removeValue();
                                        Log.d("CommissionDebug", "Deleted orphaned commission: " + key);
                                    }

                                    // Update the main list
                                    commissionList.clear();
                                    commissionList.addAll(validCommissions);

                                    // Update UI
                                    updateCommissionUI(pendingAmount[0], creditedAmount[0], debitedAmount[0], pendingCount[0]);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    txtEmptyState.setVisibility(View.VISIBLE);
                    recyclerViewCommissions.setVisibility(View.GONE);

                    txtPendingAmount.setText("₹0");
                    txtCreditedAmount.setText("₹0");
                    txtDebitedAmount.setText("₹0");
                    txtNextPayout.setText("No pending payouts");

                    filteredList.clear();
                    commissionAdapter.notifyDataSetChanged();
                });

                Log.e("CommissionDebug", "Firebase error: " + error.getMessage());
                StyleableToast.makeText(commission_notification.this,
                        "Error loading commission data", R.style.UptrendToast).show();
            }
        });
    }
    // Helper method to check if order exists
    private void checkIfOrderExists(String orderId, OrderExistsCallback callback) {
        if (orderId == null || orderId.isEmpty()) {
            callback.onResult(false);
            return;
        }

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                callback.onResult(snapshot.exists());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onResult(false);
            }
        });
    }

    // Update UI method
    private void updateCommissionUI(double pendingAmount, double creditedAmount,
                                    double debitedAmount, int pendingCount) {
        runOnUiThread(() -> {
            txtPendingAmount.setText(String.format("₹%.0f", pendingAmount));
            txtCreditedAmount.setText(String.format("₹%.0f", creditedAmount));
            txtDebitedAmount.setText(String.format("₹%.0f", debitedAmount));

            if (pendingCount > 0) {
                txtNextPayout.setText(String.format("%d commission(s) pending", pendingCount));
            } else {
                txtNextPayout.setText("No pending payouts");
            }

            filterCommissions();
            progressBar.setVisibility(View.GONE);

            if (filteredList.isEmpty()) {
                txtEmptyState.setVisibility(View.VISIBLE);
            } else {
                txtEmptyState.setVisibility(View.GONE);
            }

            Log.d("CommissionDebug", "=== FINAL SUMMARY ===");
            Log.d("CommissionDebug", "Valid commissions: " + commissionList.size());
            Log.d("CommissionDebug", "✓ PENDING: ₹" + pendingAmount);
            Log.d("CommissionDebug", "✓ CREDITED: ₹" + creditedAmount);
            Log.d("CommissionDebug", "✓ DEBITED: ₹" + debitedAmount);
        });
    }

    // Interface for callback
    interface OrderExistsCallback {
        void onResult(boolean orderExists);
    }

    private void checkAndCreateCommissions() {
        if (firebaseUser == null) return;

        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order");
        Query sellerQuery = orderRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());

        sellerQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                    String orderKey = orderSnapshot.getKey();
                    Order order = orderSnapshot.getValue(Order.class);

                    if (order != null && orderKey != null) {
                        order.setNodeId(orderKey);

                        DatabaseReference commissionRef = FirebaseDatabase.getInstance().getReference("Commissions")
                                .child(firebaseUser.getUid())
                                .child(orderKey);

                        commissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot commissionSnapshot) {
                                if (!commissionSnapshot.exists()) {
                                    createCommissionForOrder(order, orderKey);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("CommissionDebug", "Error checking commission: " + error.getMessage());
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CommissionDebug", "Error checking orders: " + error.getMessage());
            }
        });
    }

    private void createCommissionForOrder(Order order, String orderKey) {
        try {
            double sellingPrice = order.getProductSellingPrice() != null ?
                    Double.parseDouble(order.getProductSellingPrice()) : 1500.0;

            getProductDetails(order.getProductId(), new ProductDetailsCallback() {
                @Override
                public void onDetailsReceived(DataModel.Product product) {
                    String category = product != null ? product.getProductCategory() : "General";
                    String subCategory = product != null ? product.getProductSubCategory() : "General";

                    // Use the CommissionCalculator with category and subcategory
                    int commissionPercentage = CommissionCalculator.getCommissionPercentage(category, subCategory);
                    double commissionAmount = CommissionCalculator.calculateCommission(sellingPrice, category, subCategory);
                    double platformFee = 30.0;
                    double netAmount = sellingPrice - commissionAmount - platformFee;

                    Log.d("CommissionDebug", "Creating commission:");
                    Log.d("CommissionDebug", "  Selling Price: ₹" + sellingPrice);
                    Log.d("CommissionDebug", "  Category: " + category + ", SubCategory: " + subCategory);
                    Log.d("CommissionDebug", "  Commission: " + commissionPercentage + "% = ₹" + commissionAmount);
                    Log.d("CommissionDebug", "  Platform Fee: ₹" + platformFee);
                    Log.d("CommissionDebug", "  Net Amount: ₹" + netAmount);

                    Commission commission = new Commission();
                    commission.setOrderId(orderKey);
                    commission.setProductId(order.getProductId());
                    commission.setUserId(order.getUserId());
                    commission.setSellerId(order.getSellerId());
                    commission.setProductSellingPrice(String.format("%.2f", sellingPrice));
                    commission.setCommissionAmount(String.format("%.2f", commissionAmount));
                    commission.setPlatformFee(String.format("%.2f", platformFee));
                    commission.setNetAmount(String.format("%.2f", netAmount));
                    commission.setCategory(category);
                    commission.setSubCategory(subCategory);
                    commission.setCommissionPercentage(commissionPercentage);
                    commission.setStatus("pending");
                    commission.setOrderDate(order.getOrderDate());
                    commission.setGeneratedDate(getCurrentDate());
                    commission.setProductName(product != null && product.getProductName() != null ?
                            product.getProductName() : "Product");

                    // Calculate dates
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                        Date orderDate = sdf.parse(order.getOrderDate());

                        if (orderDate != null) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(orderDate);

                            // Credit date (13 days after order)
                            calendar.add(Calendar.DAY_OF_YEAR, 13);
                            String creditDate = sdf.format(calendar.getTime());

                            commission.setPayoutDate("Debit on: " + creditDate);
                        }
                    } catch (Exception e) {
                        commission.setPayoutDate("-");
                    }

                    DatabaseReference commissionRef = FirebaseDatabase.getInstance().getReference("Commissions")
                            .child(firebaseUser.getUid())
                            .child(orderKey);

                    commissionRef.setValue(commission)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("CommissionDebug", "✓ Commission created: " + orderKey);
                                StyleableToast.makeText(commission_notification.this,
                                        "Commission created", R.style.UptrendToast).show();
                            })
                            .addOnFailureListener(e -> {
                                Log.e("CommissionDebug", "Failed to create commission: " + e.getMessage());
                            });
                }
            });

        } catch (Exception e) {
            Log.e("CommissionDebug", "Error creating commission: " + e.getMessage());
        }
    }

    private void updateCommissionStatusAutomatically() {
        if (firebaseUser == null) return;

        commissionRef = FirebaseDatabase.getInstance().getReference("Commissions")
                .child(firebaseUser.getUid());

        commissionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot commissionSnapshot : snapshot.getChildren()) {
                    Commission commission = commissionSnapshot.getValue(Commission.class);

                    if (commission != null) {
                        String commissionId = commissionSnapshot.getKey();
                        String status = commission.getStatus();

                        // Check if pending commission should be credited
                        if ("pending".equals(status) && shouldAutoCreditCommission(commission)) {
                            autoMarkAsCredited(commissionId, commission);
                        }

                        // No need for auto-debit - commission already moved to debited when credited
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CommissionDebug", "Error checking auto-updates: " + error.getMessage());
            }
        });
    }

    private boolean shouldAutoCreditCommission(Commission commission) {
        try {
            if (commission.getOrderDate() == null) return false;

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date orderDate = sdf.parse(commission.getOrderDate());

            if (orderDate == null) return false;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(orderDate);
            calendar.add(Calendar.DAY_OF_YEAR, 13); // 13 days after order

            Date creditDate = calendar.getTime();
            Date currentDate = new Date();

            // FOR TESTING: Change to 0 days for immediate effect
            // calendar.add(Calendar.DAY_OF_YEAR, 0);
            // creditDate = calendar.getTime();

            return !currentDate.before(creditDate);

        } catch (Exception e) {
            Log.e("AutoCredit", "Error: " + e.getMessage());
            return false;
        }
    }

    private void autoMarkAsCredited(String commissionId, Commission commission) {
        DatabaseReference commissionRef = FirebaseDatabase.getInstance().getReference("Commissions")
                .child(firebaseUser.getUid())
                .child(commissionId);

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("status", "credited");
        updates.put("creditedDate", getCurrentDate());
        updates.put("autoCredited", true);

        commissionRef.updateChildren(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AutoCredit", "✓ Commission moved to credited: " + commissionId);

                    try {
                        double commissionAmt = Double.parseDouble(commission.getCommissionAmount());
                        double platformFee = Double.parseDouble(commission.getPlatformFee());
                        double netAmt = Double.parseDouble(commission.getNetAmount());
                        double totalCommissionAndFee = commissionAmt + platformFee;

                        Log.d("CommissionDebug", "=== STATUS TRANSITION ===");
                        Log.d("CommissionDebug", "Commission: ₹" + commissionAmt);
                        Log.d("CommissionDebug", "Platform Fee: ₹" + platformFee);
                        Log.d("CommissionDebug", "Total Pending/Debited: ₹" + totalCommissionAndFee);
                        Log.d("CommissionDebug", "Net Amount (Credited): ₹" + netAmt);
                        Log.d("CommissionDebug", "");
                        Log.d("CommissionDebug", "Before (Day 0):");
                        Log.d("CommissionDebug", "  PENDING: ₹" + totalCommissionAndFee);
                        Log.d("CommissionDebug", "  CREDITED: ₹" + netAmt);
                        Log.d("CommissionDebug", "  DEBITED: ₹0");
                        Log.d("CommissionDebug", "");
                        Log.d("CommissionDebug", "After (Day 13):");
                        Log.d("CommissionDebug", "  PENDING: ₹0");
                        Log.d("CommissionDebug", "  CREDITED: ₹" + netAmt + " (unchanged)");
                        Log.d("CommissionDebug", "  DEBITED: ₹" + totalCommissionAndFee);
                        Log.d("CommissionDebug", "==============================================");

                    } catch (Exception e) {
                        Log.e("CommissionDebug", "Error logging transition: " + e.getMessage());
                    }

                    StyleableToast.makeText(this, "Commission credited", R.style.UptrendToast).show();
                    loadCommissionData(); // Reload to update UI
                })
                .addOnFailureListener(e -> {
                    Log.e("AutoCredit", "Failed to credit: " + commissionId + ", Error: " + e.getMessage());
                });
    }

    private void filterCommissions() {
        filteredList.clear();

        // Hide progress bar when filtering (filtering is instant)
        progressBar.setVisibility(View.GONE);

        Log.d("CommissionFilter", "Applying filter: " + currentFilter);

        // Pass current filter to adapter
        commissionAdapter.setCurrentFilter(currentFilter);

        for (Commission commission : commissionList) {
            boolean shouldAdd = false;
            String status = commission.getStatus();

            if (currentFilter.equals("all")) {
                shouldAdd = true;
            }
            else if (currentFilter.equals("pending")) {
                shouldAdd = "pending".equals(status);
            }
            else if (currentFilter.equals("credited")) {
                shouldAdd = "pending".equals(status) || "credited".equals(status) || "debited".equals(status);
            }
            else if (currentFilter.equals("debited")) {
                shouldAdd = "credited".equals(status) || "debited".equals(status);
            }

            if (shouldAdd) {
                filteredList.add(commission);
            }
        }

        runOnUiThread(() -> {
            commissionAdapter.updateList(new ArrayList<>(filteredList));

            // Show/hide empty state based on filtered list
            if (filteredList.isEmpty()) {
                txtEmptyState.setVisibility(View.VISIBLE);
                recyclerViewCommissions.setVisibility(View.GONE);
            } else {
                txtEmptyState.setVisibility(View.GONE);
                recyclerViewCommissions.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createTestCommissionsForDebugging() {
        if (firebaseUser == null) return;

        Log.d("CommissionTest", "=== CREATING TEST COMMISSIONS ===");

        // Test 1: Pending Commission (Day 0)
        DatabaseReference testRef1 = FirebaseDatabase.getInstance().getReference("Commissions")
                .child(firebaseUser.getUid())
                .child("test_pending");

        Commission pendingCommission = new Commission();
        pendingCommission.setProductName("Test Product - Pending");
        pendingCommission.setProductSellingPrice("1500.00");
        pendingCommission.setCommissionAmount("150.00");
        pendingCommission.setPlatformFee("30.00");
        pendingCommission.setNetAmount("1320.00");
        pendingCommission.setCommissionPercentage(10);
        pendingCommission.setStatus("pending");
        pendingCommission.setOrderDate("13-12-2025");
        pendingCommission.setGeneratedDate("13-12-2025");

        testRef1.setValue(pendingCommission)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CommissionTest", "✓ Created PENDING commission");
                    Log.d("CommissionTest", "  Amounts: Commission=₹150, Net=₹1320");
                    Log.d("CommissionTest", "  Expected: Pending=₹150, Credited=₹1320, Debited=₹0");
                });

        // Test 2: Credited Commission (Day 13)
        DatabaseReference testRef2 = FirebaseDatabase.getInstance().getReference("Commissions")
                .child(firebaseUser.getUid())
                .child("test_credited");

        Commission creditedCommission = new Commission();
        creditedCommission.setProductName("Test Product - Credited");
        creditedCommission.setProductSellingPrice("1500.00");
        creditedCommission.setCommissionAmount("150.00");
        creditedCommission.setPlatformFee("30.00");
        creditedCommission.setNetAmount("1320.00");
        creditedCommission.setCommissionPercentage(10);
        creditedCommission.setStatus("credited");
        creditedCommission.setOrderDate("01-12-2025");
        creditedCommission.setGeneratedDate("01-12-2025");
        creditedCommission.setCreditedDate("14-12-2025");

        testRef2.setValue(creditedCommission)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CommissionTest", "✓ Created CREDITED commission");
                    Log.d("CommissionTest", "  Amounts: Commission=₹150, Net=₹1320");
                    Log.d("CommissionTest", "  Expected: Pending=₹0, Credited=₹1320, Debited=₹150");
                });

        // Test 3: Debited Commission (Already processed)
        DatabaseReference testRef3 = FirebaseDatabase.getInstance().getReference("Commissions")
                .child(firebaseUser.getUid())
                .child("test_debited");

        Commission debitedCommission = new Commission();
        debitedCommission.setProductName("Test Product - Debited");
        debitedCommission.setProductSellingPrice("1500.00");
        debitedCommission.setCommissionAmount("150.00");
        debitedCommission.setPlatformFee("30.00");
        debitedCommission.setNetAmount("1320.00");
        debitedCommission.setCommissionPercentage(10);
        debitedCommission.setStatus("debited");
        debitedCommission.setOrderDate("01-11-2025");
        debitedCommission.setGeneratedDate("01-11-2025");
        debitedCommission.setCreditedDate("14-11-2025");
        debitedCommission.setDebitedDate("27-11-2025");

        testRef3.setValue(debitedCommission)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CommissionTest", "✓ Created DEBITED commission");
                    Log.d("CommissionTest", "  Amounts: Commission=₹150, Net=₹1320");
                    Log.d("CommissionTest", "  Expected: Pending=₹0, Credited=₹1320, Debited=₹150");
                });
    }
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
                DataModel.Product product = null;
                if (snapshot.exists()) {
                    product = snapshot.getValue(DataModel.Product.class);
                }
                callback.onDetailsReceived(product);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onDetailsReceived(null);
            }
        });
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, dashboard_admin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    interface ProductDetailsCallback {
        void onDetailsReceived(DataModel.Product product);
    }
}