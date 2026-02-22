package com.example.uptrendseller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.uptrendseller.Adapter.CancelProductAdapter;
import com.example.uptrendseller.Adapter.RequestOnClick;
import com.example.uptrendseller.Adapter.ReturnProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.CancelProduct;
import DataModel.Return;

public class cancel_return extends AppCompatActivity implements RequestOnClick, SwipeRefreshLayout.OnRefreshListener {

    // UI Components
    private TextView btnBack;
    private LinearLayout tabCancel, tabReturn;
    private TextView txtCancelTab, txtReturnTab;
    private View viewTabIndicator;
    private RecyclerView recyclerViewCancel, recyclerViewReturn;
    private SwipeRefreshLayout swipeRefresh;
    private LinearLayout layoutEmpty;
    private ProgressBar progressBar;

    // Data
    private ArrayList<CancelProduct> cancelProductArrayList = new ArrayList<>();
    private ArrayList<Return> returnArrayList = new ArrayList<>();
    private CancelProductAdapter cancelProductAdapter;
    private ReturnProductAdapter returnProductAdapter;

    // Firebase
    private FirebaseUser firebaseUser;
    private DatabaseReference cancelRef, returnRef;

    // State
    private boolean isCancelTabSelected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_return);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        // Initialize Firebase
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            finish();
            return;
        }

        initViews();
        setupAdapters();
        setupClickListeners();
        loadCancelOrders();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        tabCancel = findViewById(R.id.tabCancel);
        tabReturn = findViewById(R.id.tabReturn);
        txtCancelTab = findViewById(R.id.txtCancelTab);
        txtReturnTab = findViewById(R.id.txtReturnTab);
        viewTabIndicator = findViewById(R.id.viewTabIndicator);
        recyclerViewCancel = findViewById(R.id.recyclerViewCancel);
        recyclerViewReturn = findViewById(R.id.recyclerViewReturn);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        progressBar = findViewById(R.id.progressBar);

        swipeRefresh.setOnRefreshListener(this);
    }

    private void setupAdapters() {
        // Cancel Adapter
        cancelProductAdapter = new CancelProductAdapter(this, cancelProductArrayList, this);
        recyclerViewCancel.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCancel.setAdapter(cancelProductAdapter);

        // Return Adapter
        returnProductAdapter = new ReturnProductAdapter(this, returnArrayList, this);
        recyclerViewReturn.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewReturn.setAdapter(returnProductAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        tabCancel.setOnClickListener(v -> {
            if (!isCancelTabSelected) {
                selectCancelTab();
            }
        });

        tabReturn.setOnClickListener(v -> {
            if (isCancelTabSelected) {
                selectReturnTab();
            }
        });
    }

    private void selectCancelTab() {
        isCancelTabSelected = true;

        // Update tab appearance
        txtCancelTab.setTextColor(getResources().getColor(R.color.green_meee));
        txtCancelTab.setTypeface(ResourcesCompat.getFont(this, R.font.dm_serif_display), Typeface.BOLD);

        txtReturnTab.setTextColor(getResources().getColor(R.color.black));
        txtReturnTab.setTypeface(ResourcesCompat.getFont(this, R.font.dm_serif_display), Typeface.NORMAL); // Normal font

        // Update tab indicator
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewTabIndicator.getLayoutParams();
        int margin16dp = (int) (16 * getResources().getDisplayMetrics().density);
        params.leftMargin = margin16dp;
        params.rightMargin = margin16dp;
        viewTabIndicator.setLayoutParams(params);

        // Update tab backgrounds
        tabCancel.setSelected(true);
        tabReturn.setSelected(false);

        // Show cancel orders
        showCancelOrders();
    }

    private void selectReturnTab() {
        isCancelTabSelected = false;

        // Update tab appearance
        txtReturnTab.setTextColor(getResources().getColor(R.color.green_meee));
        txtReturnTab.setTypeface(ResourcesCompat.getFont(this, R.font.dm_serif_display), Typeface.BOLD);

        txtCancelTab.setTextColor(getResources().getColor(R.color.black));
        txtCancelTab.setTypeface(ResourcesCompat.getFont(this, R.font.dm_serif_display), Typeface.NORMAL); // Normal font

        // Update tab indicator
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) viewTabIndicator.getLayoutParams();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int margin16dp = (int) (16 * getResources().getDisplayMetrics().density);
        int cardPadding = margin16dp * 2;
        int availableWidth = screenWidth - cardPadding;
        int tabWidth = availableWidth / 2;

        params.leftMargin = tabWidth + margin16dp;
        params.rightMargin = margin16dp;
        viewTabIndicator.setLayoutParams(params);

        // Update tab backgrounds
        tabReturn.setSelected(true);
        tabCancel.setSelected(false);

        // Load return orders if not loaded yet
        if (returnArrayList.isEmpty()) {
            loadReturnOrders();
        } else {
            showReturnOrders();
        }
    }
    private void showCancelOrders() {
        recyclerViewCancel.setVisibility(View.VISIBLE);
        recyclerViewReturn.setVisibility(View.GONE);

        // Show empty state if no cancel orders
        if (cancelProductArrayList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerViewCancel.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerViewCancel.setVisibility(View.VISIBLE);
        }
    }

    private void showReturnOrders() {
        recyclerViewCancel.setVisibility(View.GONE);
        recyclerViewReturn.setVisibility(View.VISIBLE);

        // Show empty state if no return orders
        if (returnArrayList.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerViewReturn.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            recyclerViewReturn.setVisibility(View.VISIBLE);
        }
    }

    private void loadCancelOrders() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        cancelRef = FirebaseDatabase.getInstance().getReference("Cancel");
        Query cancelQuery = cancelRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());

        cancelQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cancelProductArrayList.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    CancelProduct cancelProduct = productSnapshot.getValue(CancelProduct.class);
                    if (cancelProduct != null) {
                        cancelProduct.setNodeId(productSnapshot.getKey());
                        cancelProductArrayList.add(cancelProduct);
                    }
                }

                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                // Update UI based on current tab
                if (isCancelTabSelected) {
                    showCancelOrders();
                }

                // Notify adapter
                cancelProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(cancel_return.this, "Failed to load cancel orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadReturnOrders() {
        progressBar.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);

        returnRef = FirebaseDatabase.getInstance().getReference("Return");
        Query returnQuery = returnRef.orderByChild("sellerId").equalTo(firebaseUser.getUid());

        returnQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                returnArrayList.clear();

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Return returnProduct = productSnapshot.getValue(Return.class);
                    if (returnProduct != null) {
                        returnProduct.setNodeId(productSnapshot.getKey());
                        returnArrayList.add(returnProduct);
                    }
                }

                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                // Update UI based on current tab
                if (!isCancelTabSelected) {
                    showReturnOrders();
                }

                // Notify adapter
                returnProductAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(cancel_return.this, "Failed to load return orders", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void RequestOnClickListener(String nodeId, String productId, String userId, String qty, String size) {
        if (isCancelTabSelected) {
            // Open cancel order details activity
            Intent intent = new Intent(this, open_cancel_order.class);
            intent.putExtra("nodeId", nodeId);
            intent.putExtra("productId", productId);
            intent.putExtra("userId", userId);
            intent.putExtra("qty", qty);
            intent.putExtra("size", size);
            startActivity(intent);
        } else {
            // Open return order details activity
            Intent intent = new Intent(this, open_return_order.class);
            intent.putExtra("nodeId", nodeId);
            intent.putExtra("productId", productId);
            intent.putExtra("userId", userId);
            intent.putExtra("qty", qty);
            intent.putExtra("size", size);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {
        if (isCancelTabSelected) {
            loadCancelOrders();
        } else {
            loadReturnOrders();
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
        startActivity(intent);
        finish();
    }

}