package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import DataModel.Product;
import DataModel.Return;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;

public class open_return_order extends AppCompatActivity {

    private String productId, userId, qty, size, nodeId;
    LinearLayout layoutSize, comment_layout, upiLayout, bankLayout;
    TextView closeBtn;
    private TextView txtBrandName, txtProductName, txtProductPrice, txtProductColourName,productIdTextView,
            txtProductSize, txtProductQty, txtUserName, txtUserAddress, txtReturnDate, txtReturnTime, txtReason, txtComment, txtUPI, txtAccountHolderName, txtAccountNumber;

    private DatabaseReference productRef, userAddressRef;
    private ImageSlider productImage;
    private ArrayList<SlideModel> slideModelArrayList;

    private TextView txtDeliveryDate, txtReturnDate2, txtReturnDate3, txtPickUpDate3, txtReturnDate4, txtPickUpDate4, txtRefundDate4;
    private LinearLayout Return_layout1, Return_layout2, Return_layout3;
    TextView btnCloseReturn55;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_return_order);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        // Getting data from previous Activity
        productId = getIntent().getStringExtra("productId");
        userId = getIntent().getStringExtra("userId");
        qty = getIntent().getStringExtra("qty");
        size = getIntent().getStringExtra("size");
        nodeId = getIntent().getStringExtra("nodeId");


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

        btnCloseReturn55 = findViewById(R.id.closeBtnReturn);

        // FindViewById
        productImage = findViewById(R.id.productImageReturn);
        txtBrandName = findViewById(R.id.productBrandNameReturn);
        txtProductName = findViewById(R.id.productNameReturn);
        txtProductPrice = findViewById(R.id.productPriceReturn);
        txtProductColourName = findViewById(R.id.productColourNameReturn);
        txtProductSize = findViewById(R.id.productSizeReturn);
        txtProductQty = findViewById(R.id.productQtyReturn);
        txtUserName = findViewById(R.id.userNameReturn);
        txtUserAddress = findViewById(R.id.userAddressReturn);
        txtReturnDate = findViewById(R.id.dateReturn);
        txtReturnTime = findViewById(R.id.timeReturn);
        txtReason = findViewById(R.id.reason_Return);
        txtComment = findViewById(R.id.user_comment_Return);
        txtUPI = findViewById(R.id.upi_ID);
        txtAccountHolderName = findViewById(R.id.account_holder_name_return);
        txtAccountNumber = findViewById(R.id.account_holder_number_return);
        txtDeliveryDate = findViewById(R.id.deliveryDate);
        txtReturnDate2 = findViewById(R.id.returnDate2);
        txtReturnDate3 = findViewById(R.id.txtReturnDate3);
        txtPickUpDate3 = findViewById(R.id.txtPickUpDate3);
        txtReturnDate4 = findViewById(R.id.txtReturnDate4);
        txtPickUpDate4 = findViewById(R.id.txtPickUpDate4);
        txtRefundDate4 = findViewById(R.id.txtRefundDate4);

        Return_layout1 = findViewById(R.id.Return_layout1);
        Return_layout2 = findViewById(R.id.Return_layout2);
        upiLayout = findViewById(R.id.user_uid_layout);
        bankLayout = findViewById(R.id.user_bank_layout);
        comment_layout = findViewById(R.id.comment_layout_Return);
        layoutSize = findViewById(R.id.layoutSizeReturn);
        Return_layout3 = findViewById(R.id.Return_layout3);



        btnCloseReturn55.setOnClickListener(v -> onBackPressed());


        displayUserDetails(userId);
        displayProductDetails(productId, qty, size);
        displayReturnDetails(nodeId);
        displayReturnOrderStatus(nodeId);
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
                    if (userAddress != null) {
                        String address = userAddress.getHouseNo() + ", " + userAddress.getRoadName() + ", " +
                                userAddress.getCity() + " - " + userAddress.getPincode();
                        txtUserName.setText(userAddress.getFullName() != null ? userAddress.getFullName() : "N/A");
                        txtUserAddress.setText(address != null ? address : "N/A");
                        Log.d("OpenReturnOrder", "User details loaded for userId: " + userId);
                    } else {
                        txtUserName.setText("N/A");
                        txtUserAddress.setText("N/A");
                        Log.e("OpenReturnOrder", "UserAddress is null for userId: " + userId);
                        StyleableToast.makeText(open_return_order.this, "User address data not found", R.style.UptrendToast).show();
                    }
                } else {
                    txtUserName.setText("N/A");
                    txtUserAddress.setText("N/A");
                    Log.e("OpenReturnOrder", "User address not found for userId: " + userId);
                    StyleableToast.makeText(open_return_order.this, "User address not found", R.style.UptrendToast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtUserName.setText("N/A");
                txtUserAddress.setText("N/A");
                Log.e("OpenReturnOrder", "Error fetching user address for userId: " + userId + ", Error: " + error.getMessage());
                StyleableToast.makeText(open_return_order.this, "Error fetching user address", R.style.UptrendToast).show();
            }
        });
    }

    public void displayProductDetails(String productId, String qty, String size) {
        slideModelArrayList = new ArrayList<>();
        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                            for (int i = 0; i < product.getProductImages().size(); i++) {
                                slideModelArrayList.add(new SlideModel(product.getProductImages().get(i), ScaleTypes.FIT));
                            }
                            productImage.setImageList(slideModelArrayList, ScaleTypes.FIT);
                        } else {
                            Log.w("OpenReturnOrder", "No product images for productId: " + productId);
                        }
                        txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "N/A");
                        txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                        txtProductPrice.setText(product.getSellingPrice() != null ? product.getSellingPrice() : "0");
                        txtProductColourName.setText(product.getProductColour() != null ? product.getProductColour() : "N/A");
                        txtProductQty.setText(qty != null ? qty : "0");
                        if (getProductSize(product.getProductCategory(), size).equals("no")) {
                            layoutSize.setVisibility(View.GONE);
                        } else {
                            layoutSize.setVisibility(View.VISIBLE);
                            txtProductSize.setText(getProductSize(product.getProductCategory(), size));
                        }
                        Log.d("OpenReturnOrder", "Product details loaded for productId: " + productId);
                    } else {
                        txtBrandName.setText("N/A");
                        txtProductName.setText("N/A");
                        txtProductPrice.setText("0");
                        txtProductColourName.setText("N/A");
                        txtProductQty.setText("0");
                        layoutSize.setVisibility(View.GONE);
                        Log.e("OpenReturnOrder", "Product is null for productId: " + productId);
                        StyleableToast.makeText(open_return_order.this, "Product data not found", R.style.UptrendToast).show();
                    }
                } else {
                    txtBrandName.setText("N/A");
                    txtProductName.setText("N/A");
                    txtProductPrice.setText("0");
                    txtProductColourName.setText("N/A");
                    txtProductQty.setText("0");
                    layoutSize.setVisibility(View.GONE);
                    Log.e("OpenReturnOrder", "Product not found for productId: " + productId);
                    StyleableToast.makeText(open_return_order.this, "Product not found", R.style.UptrendToast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtBrandName.setText("N/A");
                txtProductName.setText("N/A");
                txtProductPrice.setText("0");
                txtProductColourName.setText("N/A");
                txtProductQty.setText("0");
                layoutSize.setVisibility(View.GONE);
                Log.e("OpenReturnOrder", "Error fetching product for productId: " + productId + ", Error: " + error.getMessage());
                StyleableToast.makeText(open_return_order.this, "Error fetching product data", R.style.UptrendToast).show();
            }
        });
    }

    public void displayReturnDetails(String nodeId) {
        DatabaseReference returnOrderRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId);
        returnOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Return returnProduct = snapshot.getValue(Return.class);
                    if (returnProduct != null) {
                        txtReason.setText(returnProduct.getReturnReason() != null ? returnProduct.getReturnReason() : "N/A");
                        txtReturnDate.setText(returnProduct.getReturnDate() != null ? returnProduct.getReturnDate() : "N/A");
                        txtReturnTime.setText(returnProduct.getReturnTime() != null ? returnProduct.getReturnTime() : "N/A");
                        if (returnProduct.getReturnComment() == null || returnProduct.getReturnComment().isEmpty()) {
                            comment_layout.setVisibility(View.GONE);
                        } else {
                            comment_layout.setVisibility(View.VISIBLE);
                            txtComment.setText(returnProduct.getReturnComment());
                        }
                        if ("upi".equals(returnProduct.getRefundType())) {
                            upiLayout.setVisibility(View.VISIBLE);
                            bankLayout.setVisibility(View.GONE);
                            txtUPI.setText(returnProduct.getUpiNo() != null ? returnProduct.getUpiNo() : "N/A");
                        } else if ("account".equals(returnProduct.getRefundType())) {
                            bankLayout.setVisibility(View.VISIBLE);
                            upiLayout.setVisibility(View.GONE);
                            txtAccountHolderName.setText(returnProduct.getAccountName() != null ? returnProduct.getAccountName() : "N/A");
                            txtAccountNumber.setText(returnProduct.getAccountNumber() != null ? returnProduct.getAccountNumber() : "N/A");
                        } else {
                            upiLayout.setVisibility(View.GONE);
                            bankLayout.setVisibility(View.GONE);
                        }
                        Log.d("OpenReturnOrder", "Return details loaded for nodeId: " + nodeId);
                    } else {
                        txtReason.setText("N/A");
                        txtReturnDate.setText("N/A");
                        txtReturnTime.setText("N/A");
                        comment_layout.setVisibility(View.GONE);
                        upiLayout.setVisibility(View.GONE);
                        bankLayout.setVisibility(View.GONE);
                        Log.e("OpenReturnOrder", "Return is null for nodeId: " + nodeId);
                        StyleableToast.makeText(open_return_order.this, "Return data not found", R.style.UptrendToast).show();
                    }
                } else {
                    txtReason.setText("N/A");
                    txtReturnDate.setText("N/A");
                    txtReturnTime.setText("N/A");
                    comment_layout.setVisibility(View.GONE);
                    upiLayout.setVisibility(View.GONE);
                    bankLayout.setVisibility(View.GONE);
                    Log.e("OpenReturnOrder", "Return not found for nodeId: " + nodeId);
                    StyleableToast.makeText(open_return_order.this, "Return not found", R.style.UptrendToast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                txtReason.setText("N/A");
                txtReturnDate.setText("N/A");
                txtReturnTime.setText("N/A");
                comment_layout.setVisibility(View.GONE);
                upiLayout.setVisibility(View.GONE);
                bankLayout.setVisibility(View.GONE);
                Log.e("OpenReturnOrder", "Error fetching return for nodeId: " + nodeId + ", Error: " + error.getMessage());
                StyleableToast.makeText(open_return_order.this, "Error fetching return details", R.style.UptrendToast).show();
            }
        });
    }

    public void displayReturnOrderStatus(String nodeId) {
        DatabaseReference returnOrderRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId);
        returnOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Return returnProduct = snapshot.getValue(Return.class);
                    if (returnProduct != null) {
                        // Initialize layouts and button as GONE
                        Return_layout1.setVisibility(View.GONE);
                        Return_layout2.setVisibility(View.GONE);
                        Return_layout3.setVisibility(View.GONE);
                        findViewById(R.id.return_payment_btn).setVisibility(View.GONE);
                        TextView userMobileReturn = findViewById(R.id.userMobileReturn);
                        userMobileReturn.setVisibility(View.VISIBLE);
                        // Fetch user mobile number
                        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
                        Query query = userAddressRef.orderByChild("userId").equalTo(returnProduct.getUserId());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                    UserAddress userAddress = userSnapshot.getValue(UserAddress.class);
                                    if (userAddress != null && userAddress.getMobileNo() != null && !userAddress.getMobileNo().isEmpty()) {
                                        userMobileReturn.setText(userAddress.getMobileNo());
                                    } else {
                                        userMobileReturn.setText("N/A");
                                        Log.e("OpenReturnOrder", "User mobile number is null or empty for userId: " + returnProduct.getUserId());
                                        StyleableToast.makeText(open_return_order.this, "User mobile number not found", R.style.UptrendToast).show();
                                    }
                                } else {
                                    userMobileReturn.setText("N/A");
                                    Log.e("OpenReturnOrder", "User address not found for userId: " + returnProduct.getUserId());
                                    StyleableToast.makeText(open_return_order.this, "User address not found", R.style.UptrendToast).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                userMobileReturn.setText("N/A");
                                Log.e("OpenReturnOrder", "Error fetching user address for userId: " + returnProduct.getUserId() + ", Error: " + error.getMessage());
                                StyleableToast.makeText(open_return_order.this, "Error fetching user mobile number", R.style.UptrendToast).show();
                            }
                        });
                        // Check current date against refund date
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            try {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                                LocalDate currentDate = LocalDate.now();
                                LocalDate refundDate = LocalDate.parse(returnProduct.getRefundDate(), formatter);
                                // Inside the try-catch block in displayReturnOrderStatus():
                                if (returnProduct.getReturnStatus().equals("return")) {
                                    Return_layout1.setVisibility(View.VISIBLE);
                                    txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                    txtReturnDate2.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                    Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: return, Return_layout1: VISIBLE");
                                } else if (returnProduct.getReturnStatus().equals("pickup")) {
                                    // Show appropriate layout based on payment status and date
                                    if (currentDate.isBefore(refundDate)) {
                                        Return_layout1.setVisibility(View.VISIBLE);
                                        txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                        txtReturnDate2.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                        Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: pickup, Before refund date, Return_layout1: VISIBLE");
                                    } else {
                                        // On or after refund date
                                        String paymentStatus = returnProduct.getPaymentStatus() != null ? returnProduct.getPaymentStatus() : "pending";
                                        if (paymentStatus.equals("pending")) {
                                            findViewById(R.id.return_payment_btn).setVisibility(View.VISIBLE);
                                            Return_layout2.setVisibility(View.VISIBLE);
                                            txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                            txtReturnDate3.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                            txtPickUpDate3.setText(DateAndTime.convertDateFormat(returnProduct.getPickupDate()));
                                            Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: pickup, On/after refund date, Payment: pending, Return_layout2: VISIBLE");
                                        } else if (paymentStatus.equals("completed")) {
                                            Return_layout3.setVisibility(View.VISIBLE);
                                            txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                            txtReturnDate4.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                            txtPickUpDate4.setText(DateAndTime.convertDateFormat(returnProduct.getPickupDate()));
                                            txtRefundDate4.setText(DateAndTime.convertDateFormat(returnProduct.getRefundDate()));
                                            Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: pickup, Payment: completed, Return_layout3: VISIBLE");
                                        }
                                    }
                                } else if (returnProduct.getReturnStatus().equals("refund")) {
                                    String paymentStatus = returnProduct.getPaymentStatus() != null ? returnProduct.getPaymentStatus() : "pending";
                                    if (paymentStatus.equals("pending")) {
                                        findViewById(R.id.return_payment_btn).setVisibility(View.VISIBLE);
                                        Return_layout2.setVisibility(View.VISIBLE);
                                        txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                        txtReturnDate3.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                        txtPickUpDate3.setText(DateAndTime.convertDateFormat(returnProduct.getPickupDate()));
                                        Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: refund, Payment: pending, Return_layout2: VISIBLE");
                                    } else if (paymentStatus.equals("completed")) {
                                        Return_layout3.setVisibility(View.VISIBLE);
                                        txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                        txtReturnDate4.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                        txtPickUpDate4.setText(DateAndTime.convertDateFormat(returnProduct.getPickupDate()));
                                        txtRefundDate4.setText(DateAndTime.convertDateFormat(returnProduct.getRefundDate()));
                                        Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: refund, Payment: completed, Return_layout3: VISIBLE");
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("OpenReturnOrder", "Date parsing error for nodeID: " + nodeId + ", Error: " + e.getMessage());
                                StyleableToast.makeText(open_return_order.this, "Error processing dates", R.style.UptrendToast).show();
                            }
                        } else {
                            Log.w("OpenReturnOrder", "Android version below O, date checks skipped for nodeID: " + nodeId);
                        }
                    } else {
                        Log.e("OpenReturnOrder", "Return is null for nodeID: " + nodeId);
                        StyleableToast.makeText(open_return_order.this, "Return data not found", R.style.UptrendToast).show();
                    }
                } else {
                    Log.e("OpenReturnOrder", "Return not found for nodeID: " + nodeId);
                    StyleableToast.makeText(open_return_order.this, "Return not found", R.style.UptrendToast).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OpenReturnOrder", "Error fetching return for nodeID: " + nodeId + ", Error: " + error.getMessage());
                StyleableToast.makeText(open_return_order.this, "Error fetching return data", R.style.UptrendToast).show();
            }
        });
    }
    public String getProductSize(String category, String index) {
        String size = "";
        if (category != null && index != null) {
            if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
                switch (index) {
                    case "0": size = "S"; break;
                    case "1": size = "M"; break;
                    case "2": size = "L"; break;
                    case "3": size = "XL"; break;
                    case "4": size = "XXL"; break;
                }
            } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
                switch (index) {
                    case "0": size = "28"; break;
                    case "1": size = "30"; break;
                    case "2": size = "32"; break;
                    case "3": size = "34"; break;
                    case "4": size = "36"; break;
                    case "5": size = "38"; break;
                    case "6": size = "40"; break;
                }
            } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
                switch (index) {
                    case "0": size = "6"; break;
                    case "1": size = "7"; break;
                    case "2": size = "8"; break;
                    case "3": size = "9"; break;
                    case "4": size = "10"; break;
                }
            } else {
                size = "no";
            }
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
                // Permission granted, retry sending SMS
                TextView userMobileReturn = findViewById(R.id.userMobileReturn);
                String mobileNumber = userMobileReturn.getText().toString();
                if (mobileNumber.equals("N/A") || mobileNumber.isEmpty()) {
                    StyleableToast.makeText(this, "Cannot send SMS: Invalid mobile number", R.style.UptrendToast).show();
                    Log.e("OpenReturnOrder", "Invalid mobile number after permission granted");
                    return;
                }
                DatabaseReference returnOrderRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId);
                returnOrderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Return returnProduct = snapshot.getValue(Return.class);
                            if (returnProduct != null) {
                                String message = "Your product payment " + returnProduct.getProductSellingPrice() + " is successful is return";
                                try {
                                    SmsManager smsManager = SmsManager.getDefault();
                                    smsManager.sendTextMessage(mobileNumber, null, message, null, null);
                                    returnOrderRef.child("paymentStatus").setValue("completed", (error, ref) -> {
                                        if (error != null) {
                                            Log.e("OpenReturnOrder", "Failed to update paymentStatus for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                            StyleableToast.makeText(open_return_order.this, "Error updating payment status", R.style.UptrendToast).show();
                                        } else {
                                            Log.d("OpenReturnOrder", "Set paymentStatus to completed for nodeID: " + nodeId);
                                            findViewById(R.id.return_payment_btn).setVisibility(View.GONE);
                                            Return_layout2.setVisibility(View.GONE);
                                            Return_layout3.setVisibility(View.VISIBLE);
                                            txtDeliveryDate.setText(DateAndTime.convertDateFormat(returnProduct.getDeliveryDAte()));
                                            txtReturnDate4.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
                                            txtPickUpDate4.setText(DateAndTime.convertDateFormat(returnProduct.getPickupDate()));
                                            txtRefundDate4.setText(DateAndTime.convertDateFormat(returnProduct.getRefundDate()));
                                            Log.d("OpenReturnOrder", "NodeID: " + nodeId + ", Status: refund, Payment: completed, Return_layout3: VISIBLE");
                                            StyleableToast.makeText(open_return_order.this, "Payment completed and SMS sent", R.style.UptrendToast).show();
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e("OpenReturnOrder", "Error sending SMS after permission granted for nodeID: " + nodeId + ", Error: " + e.getMessage());
                                    StyleableToast.makeText(open_return_order.this, "Failed to send SMS: " + e.getMessage(), R.style.UptrendToast).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OpenReturnOrder", "Error fetching return for nodeID: " + nodeId + ", Error: " + error.getMessage());
                        StyleableToast.makeText(open_return_order.this, "Error fetching return data", R.style.UptrendToast).show();
                    }
                });
            } else {
                StyleableToast.makeText(this, "Cannot send SMS: Permission denied", R.style.UptrendToast).show();
                Log.e("OpenReturnOrder", "SEND_SMS permission denied for nodeID: " + nodeId);
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
