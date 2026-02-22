package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.Manifest;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.CancelProduct;
import DataModel.Product;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;

public class open_cancel_order extends AppCompatActivity {
    private String productId, userId, qty, size, nodeId;
    LinearLayout layoutSize,comment_layout;
    TextView closeBtn;
    private TextView txtBrandName, txtProductName, txtProductPrice, txtProductColourName,
            txtProductSize, txtProductQty, txtUserName, txtUserAddress, txtCancelDate, txtCancelTime,txtReason,txtComment,productIdTextView;

    private DatabaseReference productRef, userAddressRef;
    TextView closeBtnCancel;
    private ImageSlider productImage;
    private ArrayList<SlideModel> slideModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_cancel_order);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        //Getting data From previous Activity.
        productId = getIntent().getStringExtra("productId");
        userId = getIntent().getStringExtra("userId");
        qty = getIntent().getStringExtra("qty");
        size = getIntent().getStringExtra("size");
        nodeId = getIntent().getStringExtra("nodeId");


        // Find product ID TextView
         productIdTextView = findViewById(R.id.product_id_txt);
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


        //FindViewBy Id
        productImage=findViewById(R.id.productImageCancel);
        txtBrandName=findViewById(R.id.productBrandNameCancel);
        txtProductName=findViewById(R.id.productNameCancel);
        txtProductPrice=findViewById(R.id.productPriceCancel);
        txtProductColourName=findViewById(R.id.productColourNameCancel);
        txtProductSize=findViewById(R.id.productSizeCancel);
        txtProductQty=findViewById(R.id.productQtyCancel);
        txtUserName=findViewById(R.id.userNameCancel);
        txtUserAddress=findViewById(R.id.userAddressCancel);
        txtCancelDate=findViewById(R.id.dateCancel);
        txtCancelTime=findViewById(R.id.timeCancel);
        txtReason=findViewById(R.id.reason_cancel);
        txtComment=findViewById(R.id.user_comment);

        comment_layout=findViewById(R.id.comment_layout);
        layoutSize=findViewById(R.id.layoutSizeCancel);
        closeBtnCancel=findViewById(R.id.closeBtnCancel);


        closeBtnCancel.setOnClickListener(v -> onBackPressed());


        displayUserDetails(userId);
        displayProductDetails(productId, qty, size);
        displayCancelDetails(nodeId);




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


    public void displayCancelDetails(String nodeId) {
        DatabaseReference cancelOrderRef = FirebaseDatabase.getInstance().getReference("Cancel").child(nodeId);
        cancelOrderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CancelProduct cancelProduct = snapshot.getValue(CancelProduct.class);
                            if (cancelProduct != null) {
                                txtReason.setText(cancelProduct.getCancelReason());
                                txtCancelDate.setText(cancelProduct.getCancelDate());
                                txtCancelTime.setText(cancelProduct.getCancelTime());
                                if (cancelProduct.getCancelComment() == null || cancelProduct.getCancelComment().isEmpty()) {
                                    comment_layout.setVisibility(View.GONE);
                                } else {
                                    comment_layout.setVisibility(View.VISIBLE);
                                    txtComment.setText(cancelProduct.getCancelComment());
                                }
                                // Fetch and display user mobile number
                                TextView userMobileCancel = findViewById(R.id.userMobileCancel);
                                if (userMobileCancel == null) {
                                    Log.e("OpenCancelOrder", "userMobileCancel TextView not found in layout for nodeID: " + nodeId);
                                    StyleableToast.makeText(open_cancel_order.this, "UI error: Mobile number display not available", R.style.UptrendToast).show();
                                    return;
                                }
                                userMobileCancel.setVisibility(View.VISIBLE);
                                DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
                                Query query = userAddressRef.orderByChild("userId").equalTo(cancelProduct.getUserId());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                            UserAddress userAddress = userSnapshot.getValue(UserAddress.class);
                                            if (userAddress != null && userAddress.getMobileNo() != null && !userAddress.getMobileNo().isEmpty()) {
                                                userMobileCancel.setText(userAddress.getMobileNo());
                                            } else {
                                                userMobileCancel.setText("N/A");
                                                Log.e("OpenCancelOrder", "User mobile number is null or empty for userId: " + cancelProduct.getUserId());
                                                StyleableToast.makeText(open_cancel_order.this, "User mobile number not found", R.style.UptrendToast).show();
                                            }
                                        } else {
                                            userMobileCancel.setText("N/A");
                                            Log.e("OpenCancelOrder", "User address not found for userId: " + cancelProduct.getUserId());
                                            StyleableToast.makeText(open_cancel_order.this, "User address not found", R.style.UptrendToast).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        userMobileCancel.setText("N/A");
                                        Log.e("OpenCancelOrder", "Error fetching user address: " + error.getMessage());
                                        StyleableToast.makeText(open_cancel_order.this, "Error fetching user mobile number", R.style.UptrendToast).show();
                                    }
                                });
                                // Handle payment button
                                AppCompatButton returnPaymentBtn = findViewById(R.id.return_payment_btn);
                                if (returnPaymentBtn == null) {
                                    Log.e("OpenCancelOrder", "return_payment_btn not found in layout for nodeID: " + nodeId);
                                    StyleableToast.makeText(open_cancel_order.this, "UI error: Payment button not available", R.style.UptrendToast).show();
                                    return;
                                }
                                String paymentStatus = cancelProduct.getPaymentStatus() != null ? cancelProduct.getPaymentStatus() : "pending";
                                if (paymentStatus.equals("pending")) {
                                    returnPaymentBtn.setVisibility(View.VISIBLE);
                                    returnPaymentBtn.setOnClickListener(v -> {
                                        String mobileNumber = userMobileCancel.getText().toString();
                                        if (mobileNumber.equals("N/A") || mobileNumber.isEmpty()) {
                                            StyleableToast.makeText(open_cancel_order.this, "Cannot send SMS: Invalid mobile number", R.style.UptrendToast).show();
                                            Log.e("OpenCancelOrder", "Invalid mobile number for nodeID: " + nodeId);
                                            return;
                                        }
                                        String message = "Your payment amount " + cancelProduct.getProductSellingPrice() + " is return";
                                        if (ContextCompat.checkSelfPermission(open_cancel_order.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                            try {
                                                SmsManager smsManager = SmsManager.getDefault();
                                                smsManager.sendTextMessage(mobileNumber, null, message, null, null);
                                                cancelOrderRef.child("paymentStatus").setValue("completed", (error, ref) -> {
                                                    if (error != null) {
                                                        Log.e("OpenCancelOrder", "Failed to update paymentStatus for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                                        StyleableToast.makeText(open_cancel_order.this, "Error updating payment status", R.style.UptrendToast).show();
                                                    } else {
                                                        Log.d("OpenCancelOrder", "Set paymentStatus to completed for nodeID: " + nodeId);
                                                        returnPaymentBtn.setVisibility(View.GONE);
                                                        StyleableToast.makeText(open_cancel_order.this, "Payment returned and SMS sent", R.style.UptrendToast).show();
                                                    }
                                                });
                                            } catch (Exception e) {
                                                Log.e("OpenCancelOrder", "Error sending SMS for nodeID: " + nodeId + ", Error: " + e.getMessage());
                                                StyleableToast.makeText(open_cancel_order.this, "Failed to send SMS: " + e.getMessage(), R.style.UptrendToast).show();
                                            }
                                        } else {
                                            ActivityCompat.requestPermissions(open_cancel_order.this,
                                                    new String[]{Manifest.permission.SEND_SMS},
                                                    100);
                                            Log.d("OpenCancelOrder", "Requesting SEND_SMS permission for nodeID: " + nodeId);
                                        }
                                    });
                                } else if (paymentStatus.equals("completed")) {
                                    returnPaymentBtn.setVisibility(View.GONE);
                                    Log.d("OpenCancelOrder", "NodeID: " + nodeId + ", Payment: completed");
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OpenCancelOrder", "Error fetching cancel details: " + error.getMessage());
                        StyleableToast.makeText(open_cancel_order.this, "Error fetching cancel details", R.style.UptrendToast).show();
                    }
                }
        );
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                TextView userMobileCancel = findViewById(R.id.userMobileCancel);
                if (userMobileCancel == null) {
                    Log.e("OpenCancelOrder", "userMobileCancel TextView not found in layout after permission granted for nodeID: " + nodeId);
                    StyleableToast.makeText(this, "UI error: Mobile number display not available", R.style.UptrendToast).show();
                    return;
                }
                String mobileNumber = userMobileCancel.getText().toString();
                if (mobileNumber.equals("N/A") || mobileNumber.isEmpty()) {
                    StyleableToast.makeText(this, "Cannot send SMS: Invalid mobile number", R.style.UptrendToast).show();
                    Log.e("OpenCancelOrder", "Invalid mobile number after permission granted for nodeID: " + nodeId);
                    return;
                }
                DatabaseReference cancelOrderRef = FirebaseDatabase.getInstance().getReference("Cancel").child(nodeId);
                cancelOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            CancelProduct cancelProduct = snapshot.getValue(CancelProduct.class);
                            if (cancelProduct != null) {
                                String message = "Your payment amount " + cancelProduct.getProductSellingPrice() + " is return";
                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(mobileNumber, null, message, null, null);
                                    cancelOrderRef.child("paymentStatus").setValue("completed", (error, ref) -> {
                                        if (error != null) {
                                            Log.e("OpenCancelOrder", "Failed to update paymentStatus for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                            StyleableToast.makeText(open_cancel_order.this, "Error updating payment status", R.style.UptrendToast).show();
                                        } else {
                                            Log.d("OpenCancelOrder", "Set paymentStatus to completed for nodeID: " + nodeId);
                                            AppCompatButton returnPaymentBtn = findViewById(R.id.return_payment_btn);
                                            if (returnPaymentBtn != null) {
                                                returnPaymentBtn.setVisibility(View.GONE);
                                            }
                                            StyleableToast.makeText(open_cancel_order.this, "Payment returned and SMS sent", R.style.UptrendToast).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e("OpenCancelOrder", "Error sending SMS after permission granted for nodeID: " + nodeId + ", Error: " + e.getMessage());
                                    StyleableToast.makeText(open_cancel_order.this, "Failed to send SMS: " + e.getMessage(), R.style.UptrendToast).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OpenCancelOrder", "Error fetching cancel details: " + error.getMessage());
                        StyleableToast.makeText(open_cancel_order.this, "Error fetching cancel details", R.style.UptrendToast).show();
                    }
                });
            } else {
                StyleableToast.makeText(this, "Cannot send SMS: Permission denied", R.style.UptrendToast).show();
                Log.e("OpenCancelOrder", "SEND_SMS permission denied for nodeID: " + nodeId);
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // This will automatically go back to the previous fragment
        // No extra code needed since fragment is in back stack

    }
}