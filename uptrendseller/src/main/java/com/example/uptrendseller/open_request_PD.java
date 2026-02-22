package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.adapteranddatamodel.DateAndTime;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Order;
import DataModel.Product;
import DataModel.UserAddress;

public class open_request_PD extends AppCompatActivity {
    private String productId, userId, qty, size, nodeId;
    private LinearLayout processing_layout, shipping_layout, delivered_layout;
    private LinearLayout layoutSize;
    TextView closeBtn;
    private TextView txtBrandName, txtProductName, txtProductPrice, txtProductColourName,
            txtProductSize, txtProductQty, txtUserName, txtUserAddress, txtPaymentPrice, txtOrderDate,
            txtShipingDate, txtDeliveryDate, txtDate2,productIdTextView;
    private DatabaseReference productRef, userAddressRef;
    private ImageSlider productImage;
    private ArrayList<SlideModel> slideModelArrayList;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_request_pd);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        //FindViewById of All Widget.
        productImage = findViewById(R.id.productImageOR);
        txtBrandName = findViewById(R.id.productBrandName);
        txtProductName = findViewById(R.id.productName);
        txtProductPrice = findViewById(R.id.productPrice);
        txtProductColourName = findViewById(R.id.productColourName);
        txtProductSize = findViewById(R.id.productSize);
        txtProductQty = findViewById(R.id.productQty);
        txtUserName = findViewById(R.id.userName);
        txtUserAddress = findViewById(R.id.userAddress);
        txtPaymentPrice = findViewById(R.id.productPaymentPrice);
        layoutSize = findViewById(R.id.layoutSize);
        processing_layout = findViewById(R.id.processing_layout);
        shipping_layout = findViewById(R.id.shipping_layout);
        txtShipingDate = findViewById(R.id.shipingDAte);
        delivered_layout = findViewById(R.id.delivered_layout);
        txtDeliveryDate = findViewById(R.id.delivedId);
        txtDate2 = findViewById(R.id.shipindDay2);
        txtOrderDate = findViewById(R.id.orderDate);
        productIdTextView = findViewById(R.id.product_id_txt);


        closeBtn = findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), order_Details.class));
                finish();
            }
        });

        //Getting data From previous Activity.
        productId = getIntent().getStringExtra("productId");
        userId = getIntent().getStringExtra("userId");
        qty = getIntent().getStringExtra("qty");
        size = getIntent().getStringExtra("size");
        nodeId = getIntent().getStringExtra("nodeId");


        if (productIdTextView != null) {
            // Get product ID from Firebase
            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String displayId = snapshot.child("productDisplayId").getValue(String.class);
                        if (displayId != null && !displayId.isEmpty()) {
                            productIdTextView.setText(displayId);
                        } else {
                            productIdTextView.setText("Not set ID");
                        }
                    } else {
                        productIdTextView.setText("Not set ID");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    productIdTextView.setText("Error loading ID");
                }
            });
        }


        displayUserDetails(userId);

        displayProductDetails(productId, qty, size);

        displayOrderStatus(nodeId);
    }
    public void displayUserDetails(String userId) {
        userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        Query query = userAddressRef.orderByChild("userId").equalTo(userId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    UserAddress userAddress = userSnapshot.getValue(UserAddress.class);

                    String address = userAddress.getHouseNo() + " , " + userAddress.getRoadName() + " , " + userAddress.getCity() + " - " + userAddress.getPincode();
                    txtUserName.setText(userAddress.getFullName());
                    txtUserAddress.setText(address);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


    public void displayProductDetails(String productId, String qty, String size) {
        slideModelArrayList = new ArrayList<>();
        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);
                for (int i = 0; i < product.getProductImages().size(); i++) {
                    slideModelArrayList.add(new SlideModel(product.getProductImages().get(i), ScaleTypes.FIT));
                }
                productImage.setImageList(slideModelArrayList, ScaleTypes.FIT);
                txtBrandName.setText(product.getProductBrandName());
                txtProductName.setText(product.getProductName());
                txtProductPrice.setText(product.getSellingPrice());
                txtProductColourName.setText(product.getProductColour());
                txtProductQty.setText(qty);
                long totalPrice = Integer.parseInt(qty) * Long.parseLong(product.getSellingPrice());
                txtPaymentPrice.setText(String.valueOf(totalPrice));
                if (getProductSize(product.getProductCategory(), size).equals("no")) {
                    layoutSize.setVisibility(View.GONE);
                } else {
                    layoutSize.setVisibility(View.VISIBLE);
                    txtProductSize.setText(getProductSize(product.getProductCategory(), size));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void displayOrderStatus(String nodeId) {
        DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(nodeId);
        orderRef.addValueEventListener(new ValueEventListener() { // Changed to addValueEventListener
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null && order.getOrderStatus() != null) {
                        String orderStatus = order.getOrderStatus();

                        Log.d("OpenRequestPD", "Displaying order status: " + orderStatus +
                                " for nodeId: " + nodeId);

                        // Always hide all layouts first
                        processing_layout.setVisibility(View.GONE);
                        shipping_layout.setVisibility(View.GONE);
                        delivered_layout.setVisibility(View.GONE);

                        // Show layout based on CURRENT status only (don't recalculate)
                        if ("new".equals(orderStatus)) {
                            processing_layout.setVisibility(View.VISIBLE);
                            txtOrderDate.setText(DateAndTime.convertDateFormat(order.getOrderDate()));
                        } else if ("shiping".equals(orderStatus) || "shipping".equals(orderStatus)) {
                            shipping_layout.setVisibility(View.VISIBLE);
                            txtOrderDate.setText(DateAndTime.convertDateFormat(order.getOrderDate()));
                            txtShipingDate.setText(DateAndTime.convertDateFormat(order.getShipingDate()));
                        } else if ("delivered".equals(orderStatus)) {
                            delivered_layout.setVisibility(View.VISIBLE);
                            txtOrderDate.setText(DateAndTime.convertDateFormat(order.getOrderDate()));
                            txtDate2.setText(DateAndTime.convertDateFormat(order.getShipingDate()));
                            txtDeliveryDate.setText(DateAndTime.convertDateFormat(order.getDelliveryDate()));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OpenRequestPD", "Error loading order status: " + error.getMessage());
            }
        });
    }

    public String getProductSize(String category, String index) {
        String size = "";
        if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
            if (index.equals("0")) size = "S";
            else if (index.equals("1")) size = "M";
            else if (index.equals("2")) size = "L";
            else if (index.equals("3")) size = "XL";
            else if (index.equals("4")) size = "XXL";
        } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
            if (index.equals("0")) size = "28";
            else if (index.equals("1")) size = "30";
            else if (index.equals("2")) size = "32";
            else if (index.equals("3")) size = "34";
            else if (index.equals("4")) size = "36";
            else if (index.equals("5")) size = "38";
            else if (index.equals("6")) size = "40";

        } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
            if (index.equals("0")) size = "6";
            else if (index.equals("1")) size = "7";
            else if (index.equals("2")) size = "8";
            else if (index.equals("3")) size = "9";
            else if (index.equals("3")) size = "10";
        } else {
            size = "no";
        }
        return size;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),order_Details.class));
        finish();
    }
}