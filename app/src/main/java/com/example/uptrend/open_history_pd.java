package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.adapteranddatamodel.DateAndTime;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import DataModel.Order;
import DataModel.Product;
import DataModel.Return;
import io.github.muddz.styleabletoast.StyleableToast;

public class open_history_pd extends AppCompatActivity {
    String orderId, status;
    CardView completeOrderLayoutTop, returnOrderLayoutTop;
    TextView close_btn_RA;
    AppCompatButton return_order_btn, cancel_order_btn2;
    LinearLayout returnOrderBottomLayout;
    DatabaseReference orderRef, productRef, requestRef;
    ShapeableImageView productImage;
    TextView productSize, productName, productColour, storeName, productPrice, productOriginalPrice,productIdTextView, productDiscountPrice, productTotalPrice, productTotalAmount, txtReturnDate2;
    LinearLayout layoutSize, layoutQuntityTotal, cancel_return_layout, layoutPriceDetailsHI;
    LinearLayout first_purchasing_layout, shipping_layout, delivered_layout, return_layout1, return_layout2, return_layout3;
    TextView txtOrderDate, shipingDate, orderShipped2, deliveredDate, outOfDelivedDate, txtTotalQty, deliverDate2, returnDate, txtDelivery3, retrunDate2, pickupDate, deliveryDate4, returnDate4, pickupdate4, refund;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_history_pd);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        productImage = findViewById(R.id.productImage);
        productSize = findViewById(R.id.productSize);
        productName = findViewById(R.id.productName);
        productColour = findViewById(R.id.productColour);
        storeName = findViewById(R.id.storeName);
        productPrice = findViewById(R.id.productPrice);
        productDiscountPrice = findViewById(R.id.sellingPrice);
        productOriginalPrice = findViewById(R.id.total_price_HI);
        productTotalPrice = findViewById(R.id.total_order_A2C);
        productTotalAmount = findViewById(R.id.totalAmt);
        layoutSize = findViewById(R.id.layoutSize);
        first_purchasing_layout = findViewById(R.id.first_purchasing_layout);
        shipping_layout = findViewById(R.id.shipping_layout);
        delivered_layout = findViewById(R.id.delivered_layout);
        txtOrderDate = findViewById(R.id.txtOrderDAte);
        shipingDate = findViewById(R.id.shipingDate);
        orderShipped2 = findViewById(R.id.orderShipped2);
        deliveredDate = findViewById(R.id.deliveredDate);
        outOfDelivedDate = findViewById(R.id.outOfDelivedDate);
        layoutQuntityTotal = findViewById(R.id.layoutQuntityTotal);
        return_order_btn = findViewById(R.id.return_order_btn);
        cancel_order_btn2 = findViewById(R.id.cancel_order_btn);
        txtTotalQty = findViewById(R.id.txtTotalHI);
        cancel_return_layout = findViewById(R.id.cancel_return_layout);
        returnOrderLayoutTop = findViewById(R.id.returnOrderLayoutTop);
        completeOrderLayoutTop = findViewById(R.id.completeOrderLayoutTop);
        layoutPriceDetailsHI = findViewById(R.id.layoutPriceDetailsHI);
        returnOrderBottomLayout = findViewById(R.id.returnOrderBottomLayout);
        txtReturnDate2 = findViewById(R.id.txtReturnDate2);
        deliverDate2 = findViewById(R.id.deliverDate2);
        return_layout1 = findViewById(R.id.return_layout1);
        returnDate = findViewById(R.id.returnDate);
        return_layout2 = findViewById(R.id.return_layout2);
        txtDelivery3 = findViewById(R.id.txtDelivery3);
        retrunDate2 = findViewById(R.id.retrunDate2);
        pickupDate = findViewById(R.id.pickupDate);
        return_layout3 = findViewById(R.id.return_layout3);
        deliveryDate4 = findViewById(R.id.deliveryDate4);
        returnDate4 = findViewById(R.id.returnDate4);
        pickupdate4 = findViewById(R.id.pickupdate4);
        refund = findViewById(R.id.refund);
        close_btn_RA = findViewById(R.id.close_btn_RA);

        orderId = getIntent().getStringExtra("orderId");
        status = getIntent().getStringExtra("status");

         productIdTextView = findViewById(R.id.product_id_txt);
        if (productIdTextView != null) {
            // We'll load the product ID later when we fetch order details
            productIdTextView.setText("Loading...");
        }

        if (status == null || orderId == null) {
            StyleableToast.makeText(this, "Invalid order data", R.style.UptrendToast).show();
            if (productIdTextView != null) {
                productIdTextView.setText("Not set ID");
            }
            finish();
            return;
        }
        if (status == null || orderId == null) {
            StyleableToast.makeText(this, "Invalid order data", R.style.UptrendToast).show();
            finish();
            return;
        }

        if (status.equals("order")) {
            completeOrderLayoutTop.setVisibility(View.VISIBLE);
            returnOrderLayoutTop.setVisibility(View.GONE);
            layoutPriceDetailsHI.setVisibility(View.VISIBLE);
            returnOrderBottomLayout.setVisibility(View.GONE);
            displayOrderDetails(orderId);
        } else if (status.equals("return")) {
            completeOrderLayoutTop.setVisibility(View.GONE);
            returnOrderLayoutTop.setVisibility(View.VISIBLE);
            cancel_return_layout.setVisibility(View.GONE);
            layoutPriceDetailsHI.setVisibility(View.GONE);
            returnOrderBottomLayout.setVisibility(View.VISIBLE);
            displayReturnOrderDetail(orderId);
        }

        close_btn_RA.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), complete_order.class));
            finish();
        });

        return_order_btn.setOnClickListener(view -> {
            Intent i = new Intent(open_history_pd.this, return_order.class);
            i.putExtra("orderId", orderId);
            startActivity(i);
            finish();
        });

        cancel_order_btn2.setOnClickListener(view -> {
            Intent i = new Intent(open_history_pd.this, cancel_order.class);
            i.putExtra("orderId", orderId);
            startActivity(i);
            finish();
        });
    }

    public String getProductSize(String category, String index) {
        if (category == null || index == null) return "no";
        String size = "";
        if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
            String[] shirtSizes = {"S", "M", "L", "XL", "XXL"};
            try {
                int idx = Integer.parseInt(index);
                if (idx >= 0 && idx < shirtSizes.length) size = shirtSizes[idx];
            } catch (NumberFormatException e) {
                Log.e("OpenHistoryPd", "Invalid size index: " + index);
            }
        } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
            String[] jeansSizes = {"28", "30", "32", "34", "36", "38", "40"};
            try {
                int idx = Integer.parseInt(index);
                if (idx >= 0 && idx < jeansSizes.length) size = jeansSizes[idx];
            } catch (NumberFormatException e) {
                Log.e("OpenHistoryPd", "Invalid size index: " + index);
            }
        } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
            String[] shoeSizes = {"6", "7", "8", "9", "10"};
            try {
                int idx = Integer.parseInt(index);
                if (idx >= 0 && idx < shoeSizes.length) size = shoeSizes[idx];
            } catch (NumberFormatException e) {
                Log.e("OpenHistoryPd", "Invalid size index: " + index);
            }
        } else {
            size = "no";
        }
        return size.isEmpty() ? "no" : size;
    }

    public interface OnTrackingUpdateListener {
        void onTrackingUpdated();
    }

    // Replace the updateTrackingDate method in open_history_pd.java with this:
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

                // Always update shipping and delivery dates
                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderID);
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Always update dates if they don't exist
                            orderRef.child("shipingDate").setValue(shippingDay);
                            orderRef.child("delliveryDate").setValue(deliveryDay);

                            // Check current status
                            String currentStatus = snapshot.child("orderStatus").getValue(String.class);
                            String newStatus = currentStatus; // Keep current status by default

                            // Only auto-update if current status is not "delivered", "cancelled", or "returned"
                            if (!"delivered".equals(currentStatus) &&
                                    !"cancelled".equals(currentStatus) &&
                                    !"canceled".equals(currentStatus) &&
                                    !"returned".equals(currentStatus)) {

                                // Determine new status based on date
                                if (currentDate.isBefore(shippingDate)) {
                                    newStatus = "new";
                                } else if (currentDate.isBefore(deliveryDate)) {
                                    newStatus = "shiping";
                                } else {
                                    newStatus = "delivered";
                                }

                                // Only update if status changed
                                if (!newStatus.equals(currentStatus)) {
                                    orderRef.child("orderStatus").setValue(newStatus);
                                }
                            }

                            listener.onTrackingUpdated();

                        } else {
                            Log.e("OpenHistoryPd", "Order snapshot does not exist for orderID: " + orderID);
                            listener.onTrackingUpdated();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast.makeText(open_history_pd.this, "Error checking order status: " + error.getMessage(), R.style.UptrendToast).show();
                        Log.e("OpenHistoryPd", "Order status check error for orderID: " + orderID + ", Error: " + error.getMessage());
                        listener.onTrackingUpdated();
                    }
                });
            } catch (Exception e) {
                StyleableToast.makeText(open_history_pd.this, "Error updating tracking: " + e.getMessage(), R.style.UptrendToast).show();
                Log.e("OpenHistoryPd", "Tracking update error for orderID: " + orderID + ", Error: " + e.getMessage());
                listener.onTrackingUpdated();
            }
        } else {
            listener.onTrackingUpdated();
        }
    }
    public void updateTrackingStatus(String nodeId, String returnDate, OnTrackingUpdateListener listener) {
        if (nodeId == null || returnDate == null || returnDate.trim().isEmpty()) {
            Log.e("OpenHistoryPd", "Invalid input: nodeId=" + nodeId + ", returnDate=" + returnDate);
            listener.onTrackingUpdated();
            return;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate parsedReturnDate = LocalDate.parse(returnDate, formatter);
                LocalDate pickupDate = parsedReturnDate.plusDays(2);
                LocalDate refundDate = parsedReturnDate.plusDays(4);
                String pickupDay = pickupDate.format(formatter);
                String refundDay = refundDate.format(formatter);
                LocalDate currentDate = LocalDate.now();
                String newStatus;
                if (currentDate.isBefore(pickupDate)) {
                    newStatus = "return";
                } else if (currentDate.isBefore(refundDate)) {
                    newStatus = "pickup";
                } else {
                    newStatus = "refund";
                }
                DatabaseReference returnRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId);
                returnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String currentStatus = snapshot.child("returnStatus").getValue(String.class);
                            String currentPickupDate = snapshot.child("pickupDate").getValue(String.class);
                            String currentRefundDate = snapshot.child("refundDate").getValue(String.class);
                            Log.d("OpenHistoryPd", "NodeID: " + nodeId + ", Current returnStatus: " + currentStatus + ", pickupDate: " + currentPickupDate + ", refundDate: " + currentRefundDate);
                            if (currentPickupDate == null || currentPickupDate.isEmpty() || !currentPickupDate.equals(pickupDay)) {
                                returnRef.child("pickupDate").setValue(pickupDay, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("OpenHistoryPd", "Failed to set pickupDate for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("OpenHistoryPd", "Set pickupDate for nodeID: " + nodeId + " to " + pickupDay);
                                    }
                                });
                            }
                            if (currentRefundDate == null || currentRefundDate.isEmpty() || !currentRefundDate.equals(refundDay)) {
                                returnRef.child("refundDate").setValue(refundDay, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("OpenHistoryPd", "Failed to set refundDate for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("OpenHistoryPd", "Set refundDate for nodeID: " + nodeId + " to " + refundDay);
                                    }
                                });
                            }
                            if (currentStatus == null || !currentStatus.equals(newStatus)) {
                                returnRef.child("returnStatus").setValue(newStatus, (error, ref) -> {
                                    if (error != null) {
                                        StyleableToast.makeText(open_history_pd.this, "Error updating return status: " + error.getMessage(), R.style.UptrendToast).show();
                                        Log.e("OpenHistoryPd", "Failed to set returnStatus for nodeID: " + nodeId + " to " + newStatus + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("OpenHistoryPd", "Set returnStatus for nodeID: " + nodeId + " to " + newStatus);
                                    }
                                    listener.onTrackingUpdated();
                                });
                            } else {
                                Log.d("OpenHistoryPd", "returnStatus not updated for nodeID: " + nodeId + ", already set to: " + currentStatus);
                                listener.onTrackingUpdated();
                            }
                        } else {
                            Log.e("OpenHistoryPd", "Return snapshot does not exist for nodeID: " + nodeId);
                            listener.onTrackingUpdated();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast.makeText(open_history_pd.this, "Error checking return status: " + error.getMessage(), R.style.UptrendToast).show();
                        Log.e("OpenHistoryPd", "Return status check error for nodeID: " + nodeId + ", Error: " + error.getMessage());
                        listener.onTrackingUpdated();
                    }
                });
            } catch (Exception e) {
                Log.e("OpenHistoryPd", "Date parsing error for nodeID: " + nodeId + ", returnDate: " + returnDate + ", Error: " + e.getMessage());
                listener.onTrackingUpdated();
            }
        } else {
            listener.onTrackingUpdated();
        }
    }
    public void displayOrderDetails(String orderId) {
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order == null) {
                        StyleableToast.makeText(open_history_pd.this, "Order data is null", R.style.UptrendToast).show();
                        Log.e("OpenHistoryPd", "Order is null for orderId: " + orderId);
                        return;
                    }
                    updateTrackingDate(order.getOrderDate(), orderId, () -> {
                        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot updatedSnapshot) {
                                if (updatedSnapshot.exists()) {
                                    Order updatedOrder = updatedSnapshot.getValue(Order.class);
                                    if (updatedOrder == null) {
                                        StyleableToast.makeText(open_history_pd.this, "Updated order data is null", R.style.UptrendToast).show();
                                        Log.e("OpenHistoryPd", "Updated order is null for orderId: " + orderId);
                                        return;
                                    }
                                    productRef = FirebaseDatabase.getInstance().getReference("Product").child(updatedOrder.getProductId());
                                    productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Product product = snapshot.getValue(Product.class);
                                                if (product == null) {
                                                    StyleableToast.makeText(open_history_pd.this, "Product data is null", R.style.UptrendToast).show();
                                                    Log.e("OpenHistoryPd", "Product is null for productId: " + updatedOrder.getProductId());
                                                    return;
                                                }

                                                loadProductId(updatedOrder.getProductId(), productIdTextView);

                                                productName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                                                productColour.setText(product.getProductColour() != null ? product.getProductColour() : "N/A");
                                                productPrice.setText(product.getSellingPrice() != null ? product.getSellingPrice() : "0");

                                                try {
                                                    long totalOriginalPrice = Integer.parseInt(updatedOrder.getProductQty()) * Integer.parseInt(product.getOriginalPrice());
                                                    productOriginalPrice.setText(String.valueOf(totalOriginalPrice));
                                                    long discountPrice = Long.parseLong(product.getOriginalPrice()) - Long.parseLong(product.getSellingPrice());
                                                    discountPrice = discountPrice * Integer.parseInt(updatedOrder.getProductQty());
                                                    productDiscountPrice.setText(String.valueOf(discountPrice));
                                                    long totalPrice = totalOriginalPrice - discountPrice;
                                                    productTotalAmount.setText(String.valueOf(totalPrice));
                                                    productTotalPrice.setText(String.valueOf(totalPrice));
                                                } catch (NumberFormatException e) {
                                                    productOriginalPrice.setText("0");
                                                    productDiscountPrice.setText("0");
                                                    productTotalAmount.setText("0");
                                                    productTotalPrice.setText("0");
                                                    Log.e("OpenHistoryPd", "Price calculation error: " + e.getMessage());
                                                }
                                                if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                                                    Glide.with(getApplicationContext()).load(product.getProductImages().get(0)).into(productImage);
                                                } else {
                                                    productImage.setImageResource(R.drawable.vector_account);
                                                }
                                                String size = getProductSize(product.getProductCategory(), updatedOrder.getProductSize());
                                                if (size.equals("no")) {
                                                    layoutSize.setVisibility(View.GONE);
                                                } else {
                                                    layoutSize.setVisibility(View.VISIBLE);
                                                    productSize.setText(size);
                                                }
                                                displayStoreName(updatedOrder.getSellerId());
                                                String orderStatus = updatedOrder.getOrderStatus();
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                Date currentDate = new Date(System.currentTimeMillis()); // Current date: 23-07-2025
                                                String currentDateStr = dateFormat.format(currentDate);
                                                Log.d("OpenHistoryPd", "OrderID: " + orderId + ", CurrentDate: " + currentDateStr + ", OrderStatus: " + orderStatus);
                                                // Initialize layouts as GONE
                                                first_purchasing_layout.setVisibility(View.GONE);
                                                shipping_layout.setVisibility(View.GONE);
                                                delivered_layout.setVisibility(View.GONE);
                                                return_layout1.setVisibility(View.GONE);
                                                return_layout2.setVisibility(View.GONE);
                                                return_layout3.setVisibility(View.GONE);
                                                cancel_return_layout.setVisibility(View.GONE);
                                                cancel_order_btn2.setVisibility(View.GONE);
                                                return_order_btn.setVisibility(View.GONE);
                                                if (orderStatus.equals("new")) {
                                                    first_purchasing_layout.setVisibility(View.VISIBLE);
                                                    txtOrderDate.setText(DateAndTime.convertDateFormat(updatedOrder.getOrderDate()));
                                                    cancel_return_layout.setVisibility(View.VISIBLE);
                                                    cancel_order_btn2.setVisibility(View.VISIBLE);
                                                    Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: new, cancel_return_layout: VISIBLE, cancel_order_btn2: VISIBLE");
                                                } else if (orderStatus.equals("shiping")) {
                                                    shipping_layout.setVisibility(View.VISIBLE);
                                                    txtOrderDate.setText(DateAndTime.convertDateFormat(updatedOrder.getOrderDate()));
                                                    shipingDate.setText(DateAndTime.convertDateFormat(updatedOrder.getShipingDate()));
                                                    Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: shiping, cancel_return_layout: GONE");
                                                } else if (orderStatus.equals("delivered")) {
                                                    delivered_layout.setVisibility(View.VISIBLE);
                                                    txtOrderDate.setText(DateAndTime.convertDateFormat(updatedOrder.getOrderDate()));
                                                    orderShipped2.setText(DateAndTime.convertDateFormat(updatedOrder.getShipingDate()));
                                                    deliveredDate.setText(DateAndTime.convertDateFormat(updatedOrder.getDelliveryDate()));
                                                    outOfDelivedDate.setText(DateAndTime.convertDateFormat(updatedOrder.getDelliveryDate()));
                                                    try {
                                                        Date parsedDeliveryDate = dateFormat.parse(updatedOrder.getDelliveryDate());
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.setTime(parsedDeliveryDate);
                                                        calendar.add(Calendar.DAY_OF_MONTH, 7); // 7-day return window
                                                        Date returnWindowEndDate = calendar.getTime();
                                                        Log.d("OpenHistoryPd", "OrderID: " + orderId + ", DeliveryDate: " + updatedOrder.getDelliveryDate() + ", ReturnWindowEnd: " + dateFormat.format(returnWindowEndDate));
                                                        DatabaseReference returnRef = FirebaseDatabase.getInstance().getReference("Return").child(orderId);
                                                        returnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot returnSnapshot) {
                                                                if (returnSnapshot.exists()) {
                                                                    Return returnProduct = returnSnapshot.getValue(Return.class);
                                                                    if (returnProduct != null) {
                                                                        updateTrackingStatus(returnProduct.getNodeId(), returnProduct.getReturnDate(), () -> {
                                                                            returnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                @Override
                                                                                public void onDataChange(@NonNull DataSnapshot updatedReturnSnapshot) {
                                                                                    Return updatedReturn = updatedReturnSnapshot.getValue(Return.class);
                                                                                    if (updatedReturn != null) {
                                                                                        completeOrderLayoutTop.setVisibility(View.GONE);
                                                                                        returnOrderLayoutTop.setVisibility(View.VISIBLE);
                                                                                        layoutPriceDetailsHI.setVisibility(View.GONE);
                                                                                        returnOrderBottomLayout.setVisibility(View.VISIBLE);
                                                                                        if (updatedReturn.getReturnStatus().equals("return")) {
                                                                                            return_layout1.setVisibility(View.VISIBLE);
                                                                                            return_layout2.setVisibility(View.GONE);
                                                                                            return_layout3.setVisibility(View.GONE);
                                                                                            txtOrderDate.setText(DateAndTime.convertDateFormat(updatedReturn.getOrderDate()));
                                                                                            deliverDate2.setText(DateAndTime.convertDateFormat(updatedReturn.getDeliveryDAte()));
                                                                                            returnDate.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                                                            Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: delivered, Return status: return, return_layout1: VISIBLE");
                                                                                        } else if (updatedReturn.getReturnStatus().equals("pickup")) {
                                                                                            return_layout1.setVisibility(View.GONE);
                                                                                            return_layout2.setVisibility(View.VISIBLE);
                                                                                            return_layout3.setVisibility(View.GONE);
                                                                                            txtOrderDate.setText(DateAndTime.convertDateFormat(updatedReturn.getOrderDate()));
                                                                                            txtDelivery3.setText(DateAndTime.convertDateFormat(updatedReturn.getDeliveryDAte()));
                                                                                            retrunDate2.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                                                            pickupDate.setText(DateAndTime.convertDateFormat(updatedReturn.getPickupDate()));
                                                                                            Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: delivered, Return status: pickup, return_layout2: VISIBLE");
                                                                                        } else if (updatedReturn.getReturnStatus().equals("refund")) {
                                                                                            return_layout1.setVisibility(View.GONE);
                                                                                            return_layout2.setVisibility(View.GONE);
                                                                                            return_layout3.setVisibility(View.VISIBLE);
                                                                                            txtOrderDate.setText(DateAndTime.convertDateFormat(updatedReturn.getOrderDate()));
                                                                                            deliveryDate4.setText(DateAndTime.convertDateFormat(updatedReturn.getDeliveryDAte()));
                                                                                            returnDate4.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                                                            pickupdate4.setText(DateAndTime.convertDateFormat(updatedReturn.getPickupDate()));
                                                                                            refund.setText(DateAndTime.convertDateFormat(updatedReturn.getRefundDate()));
                                                                                            Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: delivered, Return status: refund, return_layout3: VISIBLE");
                                                                                        }
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onCancelled(@NonNull DatabaseError error) {
                                                                                    Log.e("OpenHistoryPd", "Return re-fetch error for orderID: " + orderId + ", Error: " + error.getMessage());
                                                                                }
                                                                            });
                                                                        });
                                                                    }
                                                                } else if (currentDate.before(returnWindowEndDate)) {
                                                                    cancel_return_layout.setVisibility(View.VISIBLE);
                                                                    cancel_order_btn2.setVisibility(View.GONE);
                                                                    return_order_btn.setVisibility(View.VISIBLE);
                                                                    Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: delivered, Within 7-day return window, Not returned, cancel_return_layout: VISIBLE, return_order_btn: VISIBLE");
                                                                } else {
                                                                    cancel_return_layout.setVisibility(View.GONE);
                                                                    cancel_order_btn2.setVisibility(View.GONE);
                                                                    return_order_btn.setVisibility(View.GONE);
                                                                    Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Status: delivered, Return window closed, cancel_return_layout: GONE");
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {
                                                                Log.e("OpenHistoryPd", "Return check error for orderID: " + orderId + ", Error: " + error.getMessage());
                                                                cancel_return_layout.setVisibility(View.GONE);
                                                                cancel_order_btn2.setVisibility(View.GONE);
                                                                return_order_btn.setVisibility(View.GONE);
                                                            }
                                                        });
                                                    } catch (Exception e) {
                                                        Log.e("OpenHistoryPd", "Delivery date parsing error for orderID: " + orderId + ", Error: " + e.getMessage());
                                                        cancel_return_layout.setVisibility(View.GONE);
                                                        cancel_order_btn2.setVisibility(View.GONE);
                                                        return_order_btn.setVisibility(View.GONE);
                                                    }
                                                }
                                                if (updatedOrder.getProductQty().equals("1")) {
                                                    layoutQuntityTotal.setVisibility(View.GONE);
                                                } else {
                                                    layoutQuntityTotal.setVisibility(View.VISIBLE);
                                                    txtTotalQty.setText(updatedOrder.getProductQty());
                                                }
                                            } else {
                                                StyleableToast.makeText(open_history_pd.this, "Product not found", R.style.UptrendToast).show();
                                                Log.e("OpenHistoryPd", "Product not found for productId: " + updatedOrder.getProductId());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            StyleableToast.makeText(open_history_pd.this, "Error fetching product: " + error.getMessage(), R.style.UptrendToast).show();
                                            Log.e("OpenHistoryPd", "Product fetch error: " + error.getMessage());
                                        }
                                    });
                                } else {
                                    StyleableToast.makeText(open_history_pd.this, "Updated order not found", R.style.UptrendToast).show();
                                    Log.e("OpenHistoryPd", "Updated order not found for orderId: " + orderId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                StyleableToast.makeText(open_history_pd.this, "Error fetching updated order: " + error.getMessage(), R.style.UptrendToast).show();
                                Log.e("OpenHistoryPd", "Order re-fetch error for orderID: " + orderId + ", Error: " + error.getMessage());
                            }
                        });
                    });
                } else {
                    StyleableToast.makeText(open_history_pd.this, "Order not found", R.style.UptrendToast).show();
                    Log.e("OpenHistoryPd", "Order not found for orderId: " + orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(open_history_pd.this, "Error fetching order: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("OpenHistoryPd", "Order fetch error: " + orderId + ", Error: " + error.getMessage());
            }
        });
    }

    public void displayReturnOrderDetail(String orderId) {
        requestRef = FirebaseDatabase.getInstance().getReference("Return").child(orderId);
        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Return returnProduct = snapshot.getValue(Return.class);
                    if (returnProduct == null) {
                        StyleableToast.makeText(open_history_pd.this, "Return data is null", R.style.UptrendToast).show();
                        Log.e("OpenHistoryPd", "Return is null for orderId: " + orderId);
                        return;
                    }
                    updateTrackingStatus(returnProduct.getNodeId(), returnProduct.getReturnDate(), () -> {
                        requestRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot updatedSnapshot) {
                                if (updatedSnapshot.exists()) {
                                    Return updatedReturn = updatedSnapshot.getValue(Return.class);
                                    if (updatedReturn == null) {
                                        StyleableToast.makeText(open_history_pd.this, "Updated return data is null", R.style.UptrendToast).show();
                                        Log.e("OpenHistoryPd", "Updated return is null for orderId: " + orderId);
                                        return;
                                    }
                                    productRef = FirebaseDatabase.getInstance().getReference("Product").child(updatedReturn.getProductId());
                                    productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                Product product = snapshot.getValue(Product.class);
                                                if (product == null) {
                                                    StyleableToast.makeText(open_history_pd.this, "Product data is null", R.style.UptrendToast).show();
                                                    Log.e("OpenHistoryPd", "Product is null for productId: " + updatedReturn.getProductId());
                                                    return;
                                                }
                                                loadProductId(updatedReturn.getProductId(), productIdTextView);

                                                productName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                                                productColour.setText(product.getProductColour() != null ? product.getProductColour() : "N/A");
                                                productPrice.setText(product.getSellingPrice() != null ? product.getSellingPrice() : "0");
                                                try {
                                                    long totalOriginalPrice = Integer.parseInt(updatedReturn.getProductQty()) * Integer.parseInt(product.getOriginalPrice());
                                                    productOriginalPrice.setText(String.valueOf(totalOriginalPrice));
                                                    long discountPrice = Long.parseLong(product.getOriginalPrice()) - Long.parseLong(product.getSellingPrice());
                                                    discountPrice = discountPrice * Integer.parseInt(updatedReturn.getProductQty());
                                                    productDiscountPrice.setText(String.valueOf(discountPrice));
                                                    long totalPrice = totalOriginalPrice - discountPrice;
                                                    productTotalAmount.setText(String.valueOf(totalPrice));
                                                    productTotalPrice.setText(String.valueOf(totalPrice));
                                                } catch (NumberFormatException e) {
                                                    productOriginalPrice.setText("0");
                                                    productDiscountPrice.setText("0");
                                                    productTotalAmount.setText("0");
                                                    productTotalPrice.setText("0");
                                                    Log.e("OpenHistoryPd", "Price calculation error: " + e.getMessage());
                                                }
                                                if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                                                    Glide.with(getApplicationContext()).load(product.getProductImages().get(0)).into(productImage);
                                                } else {
                                                    productImage.setImageResource(R.drawable.vector_account);
                                                }
                                                String size = getProductSize(product.getProductCategory(), updatedReturn.getProductSize());
                                                if (size.equals("no")) {
                                                    layoutSize.setVisibility(View.GONE);
                                                } else {
                                                    layoutSize.setVisibility(View.VISIBLE);
                                                    productSize.setText(size);
                                                }
                                                displayStoreName(updatedReturn.getSellerId());
                                                txtReturnDate2.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                // Initialize layouts as GONE
                                                return_layout1.setVisibility(View.GONE);
                                                return_layout2.setVisibility(View.GONE);
                                                return_layout3.setVisibility(View.GONE);
                                                String paymentStatus = updatedReturn.getPaymentStatus() != null ? updatedReturn.getPaymentStatus() : "pending";
                                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                    try {
                                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                                        LocalDate currentDate = LocalDate.now();
                                                        LocalDate refundDate = LocalDate.parse(updatedReturn.getRefundDate(), formatter);
                                                        if (updatedReturn.getReturnStatus().equals("return") || (updatedReturn.getReturnStatus().equals("pickup") && currentDate.isBefore(refundDate))) {
                                                            return_layout1.setVisibility(View.VISIBLE);
                                                            txtOrderDate.setText(DateAndTime.convertDateFormat(updatedReturn.getOrderDate()));
                                                            deliverDate2.setText(DateAndTime.convertDateFormat(updatedReturn.getDeliveryDAte()));
                                                            returnDate.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                            Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Return status: return or pickup before refund date, return_layout1: VISIBLE");
                                                        } else if ((updatedReturn.getReturnStatus().equals("pickup") || updatedReturn.getReturnStatus().equals("refund")) && !currentDate.isBefore(refundDate) && paymentStatus.equals("pending")) {
                                                            return_layout2.setVisibility(View.VISIBLE);
                                                            txtOrderDate.setText(DateAndTime.convertDateFormat(updatedReturn.getOrderDate()));
                                                            txtDelivery3.setText(DateAndTime.convertDateFormat(updatedReturn.getDeliveryDAte()));
                                                            retrunDate2.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                            pickupDate.setText(DateAndTime.convertDateFormat(updatedReturn.getPickupDate()));
                                                            Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Return status: pickup or refund, On/after refund date, Payment: pending, return_layout2: VISIBLE");
                                                        } else if (updatedReturn.getReturnStatus().equals("refund") && paymentStatus.equals("completed")) {
                                                            return_layout3.setVisibility(View.VISIBLE);
                                                            txtOrderDate.setText(DateAndTime.convertDateFormat(updatedReturn.getOrderDate()));
                                                            deliveryDate4.setText(DateAndTime.convertDateFormat(updatedReturn.getDeliveryDAte()));
                                                            returnDate4.setText(DateAndTime.convertDateFormat(updatedReturn.getReturnDate()));
                                                            pickupdate4.setText(DateAndTime.convertDateFormat(updatedReturn.getPickupDate()));
                                                            refund.setText(DateAndTime.convertDateFormat(updatedReturn.getRefundDate()));
                                                            Log.d("OpenHistoryPd", "OrderID: " + orderId + ", Return status: refund, Payment: completed, return_layout3: VISIBLE");
                                                        }
                                                    } catch (Exception e) {
                                                        Log.e("OpenHistoryPd", "Date parsing error for orderID: " + orderId + ", Error: " + e.getMessage());
                                                        StyleableToast.makeText(open_history_pd.this, "Error processing dates", R.style.UptrendToast).show();
                                                    }
                                                } else {
                                                    Log.w("OpenHistoryPd", "Android version below O, date checks skipped for orderID: " + orderId);
                                                }
                                            } else {
                                                StyleableToast.makeText(open_history_pd.this, "Product not found", R.style.UptrendToast).show();
                                                Log.e("OpenHistoryPd", "Product not found for productId: " + updatedReturn.getProductId());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            StyleableToast.makeText(open_history_pd.this, "Error fetching product: " + error.getMessage(), R.style.UptrendToast).show();
                                            Log.e("OpenHistoryPd", "Product fetch error: " + error.getMessage());
                                        }
                                    });
                                } else {
                                    StyleableToast.makeText(open_history_pd.this, "Return order not found", R.style.UptrendToast).show();
                                    Log.e("OpenHistoryPd", "Return order not found for orderId: " + orderId);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                StyleableToast.makeText(open_history_pd.this, "Error fetching return order: " + error.getMessage(), R.style.UptrendToast).show();
                                Log.e("OpenHistoryPd", "Return order fetch error: " + error.getMessage());
                            }
                        });
                    });
                } else {
                    StyleableToast.makeText(open_history_pd.this, "Return order not found", R.style.UptrendToast).show();
                    Log.e("OpenHistoryPd", "Return order not found for orderId: " + orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(open_history_pd.this, "Error fetching return order: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("OpenHistoryPd", "Return order fetch error: " + error.getMessage());
            }
        });
    }

    public void displayStoreName(String adminId) {
        if (adminId == null) {
            storeName.setText("N/A");
            return;
        }
        DatabaseReference adminNode = FirebaseDatabase.getInstance().getReference("AdminStoreInformation");
        Query sellerNodeQuery = adminNode.orderByChild("adminId").equalTo(adminId);
        sellerNodeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot sellerSnapshot = snapshot.getChildren().iterator().next();
                    String txtStoreName = sellerSnapshot.child("storeName").getValue(String.class);
                    storeName.setText(txtStoreName != null ? txtStoreName : "N/A");
                } else {
                    storeName.setText("N/A");
                    Log.e("OpenHistoryPd", "Store not found for adminId: " + adminId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(open_history_pd.this, "Error fetching store: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("OpenHistoryPd", "Store fetch error: " + error.getMessage());
            }
        });
    }


    private void loadProductId(String productId, TextView textView) {
        if (textView == null || productId == null) {
            return;
        }

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String displayId = snapshot.child("productDisplayId").getValue(String.class);
                    if (displayId != null && !displayId.isEmpty()) {
                        textView.setText(displayId);
                    } else {
                        textView.setText("Not set ID");
                    }
                } else {
                    textView.setText("Not set ID");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                textView.setText("Error loading ID");
            }
        });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), complete_order.class);
        startActivity(intent);
        finish();
    }
}