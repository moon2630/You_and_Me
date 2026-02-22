package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.uptrend.Adapter.RatingProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

import DataModel.Order;
import io.github.muddz.styleabletoast.StyleableToast;

public class rating_products extends AppCompatActivity {
    RecyclerView productReview;
    DatabaseReference orderRef;
    FirebaseUser firebaseUser;
    ArrayList<Order> orderArrayList;

    private static final int READ_PERMISSION = 101;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_products);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
        }
        productReview=findViewById(R.id.productReview);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

        displayProductReview(firebaseUser.getUid());


        TextView close_btn_RA = findViewById(R.id.close_btn_RA);
        close_btn_RA.setOnClickListener(v -> {
            Intent intent = new Intent(rating_products.this, account_user.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });
    }

    public interface OnTrackingUpdateListener {
        void onTrackingUpdated();
    }

    public void updateTrackingDate(String orderDate, String orderID, OnTrackingUpdateListener listener) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate parsedOrderDate = LocalDate.parse(orderDate, formatter);
                LocalDate shippingDate = parsedOrderDate.plusDays(2);
                LocalDate deliveryDate = parsedOrderDate.plusDays(4);
                String shippingDay = shippingDate.format(formatter);
                String deliveryDay = deliveryDate.format(formatter);
                LocalDate currentDate = LocalDate.now();
                String newStatus;
                if (currentDate.isBefore(shippingDate)) {
                    newStatus = "new";
                } else if (currentDate.isBefore(deliveryDate)) {
                    newStatus = "shiping";
                } else {
                    newStatus = "delivered";
                }
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderID);
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String currentStatus = snapshot.child("orderStatus").getValue(String.class);
                            String currentShippingDate = snapshot.child("shipingDate").getValue(String.class);
                            String currentDeliveryDate = snapshot.child("delliveryDate").getValue(String.class);
                            Log.d("RatingProducts", "OrderID: " + orderID + ", Current orderStatus: " + currentStatus + ", shipingDate: " + currentShippingDate + ", delliveryDate: " + currentDeliveryDate);
                            if (currentShippingDate == null || currentShippingDate.isEmpty() || !currentShippingDate.equals(shippingDay)) {
                                orderRef.child("shipingDate").setValue(shippingDay, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("RatingProducts", "Failed to set shipingDate for orderID: " + orderID + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("RatingProducts", "Set shipingDate for orderID: " + orderID + " to " + shippingDay);
                                    }
                                });
                            }
                            if (currentDeliveryDate == null || currentDeliveryDate.isEmpty() || !currentDeliveryDate.equals(deliveryDay)) {
                                orderRef.child("delliveryDate").setValue(deliveryDay, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("RatingProducts", "Failed to set delliveryDate for orderID: " + orderID + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("RatingProducts", "Set delliveryDate for orderID: " + orderID + " to " + deliveryDay);
                                    }
                                });
                            }
                            if (currentStatus == null || !currentStatus.equals(newStatus)) {
                                orderRef.child("orderStatus").setValue(newStatus, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("RatingProducts", "Failed to set orderStatus for orderID: " + orderID + " to " + newStatus + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("RatingProducts", "Set orderStatus for orderID: " + orderID + " to " + newStatus);
                                    }
                                    listener.onTrackingUpdated();
                                });
                            } else {
                                Log.d("RatingProducts", "orderStatus not updated for orderID: " + orderID + ", already set to: " + currentStatus);
                                listener.onTrackingUpdated();
                            }
                        } else {
                            Log.e("RatingProducts", "Order snapshot does not exist for orderID: " + orderID);
                            listener.onTrackingUpdated();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast.makeText(rating_products.this, "Error checking order status: " + error.getMessage(), R.style.UptrendToast).show();
                        Log.e("RatingProducts", "Order status check error for orderID: " + orderID + ", Error: " + error.getMessage());
                        listener.onTrackingUpdated();
                    }
                });
            } catch (Exception e) {
                StyleableToast.makeText(rating_products.this, "Error updating tracking: " + e.getMessage(), R.style.UptrendToast).show();
                Log.e("RatingProducts", "Tracking update error for orderID: " + orderID + ", Error: " + e.getMessage());
                listener.onTrackingUpdated();
            }
        } else {
            listener.onTrackingUpdated();
        }
    }

    public void displayProductReview(String userId) {
        orderArrayList = new ArrayList<>();
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        Query userQuery = orderRef.orderByChild("userId").equalTo(userId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashSet<String> reviewedProducts = new HashSet<>();
                ArrayList<Order> tempOrderList = new ArrayList<>();
                int totalOrders = (int) snapshot.getChildrenCount();
                AtomicInteger updatedOrders = new AtomicInteger(0);

                if (totalOrders == 0) {
                    updateRecyclerView();
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Order order = dataSnapshot.getValue(Order.class);
                    if (order != null && order.getOrderDate() != null) {
                        updateTrackingDate(order.getOrderDate(), dataSnapshot.getKey(), () -> {
                            orderRef.child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot updatedSnapshot) {
                                    Order updatedOrder = updatedSnapshot.getValue(Order.class);
                                    if (updatedOrder != null && updatedOrder.getOrderStatus().equals("delivered")) {
                                        if (!reviewedProducts.contains(updatedOrder.getProductId())) {
                                            tempOrderList.add(updatedOrder);
                                            reviewedProducts.add(updatedOrder.getProductId());
                                        }
                                    }
                                    if (updatedOrders.incrementAndGet() == totalOrders) {
                                        orderArrayList.clear();
                                        orderArrayList.addAll(tempOrderList);
                                        Collections.reverse(orderArrayList);
                                        updateRecyclerView();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("RatingProducts", "Error re-fetching order: " + error.getMessage());
                                    if (updatedOrders.incrementAndGet() == totalOrders) {
                                        orderArrayList.clear();
                                        orderArrayList.addAll(tempOrderList);
                                        Collections.reverse(orderArrayList);
                                        updateRecyclerView();
                                    }
                                }
                            });
                        });
                    } else {
                        Log.w("RatingProducts", "Invalid order or orderDate for orderID: " + dataSnapshot.getKey());
                        if (updatedOrders.incrementAndGet() == totalOrders) {
                            orderArrayList.clear();
                            orderArrayList.addAll(tempOrderList);
                            Collections.reverse(orderArrayList);
                            updateRecyclerView();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(rating_products.this, "Error fetching orders: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("RatingProducts", "Order fetch error: " + error.getMessage());
                updateRecyclerView();
            }
        });
    }

    private void updateRecyclerView() {
        RatingProductAdapter ratingProductAdapter = new RatingProductAdapter(rating_products.this, orderArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(rating_products.this, LinearLayoutManager.VERTICAL, false);
        productReview.setLayoutManager(linearLayoutManager);
        productReview.setAdapter(ratingProductAdapter);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            StyleableToast.makeText(this, "Storage permission required to select images", R.style.UptrendToast).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (productReview.getAdapter() != null) {
            ((RatingProductAdapter) productReview.getAdapter()).onActivityResult(requestCode, resultCode, data);

        }
    }

}