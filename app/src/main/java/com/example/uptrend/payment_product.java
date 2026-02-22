package com.example.uptrend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.adapteranddatamodel.DateAndTime;
import com.example.adapteranddatamodel.Pattern;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import DataModel.Order;
import DataModel.Product;
import DataModel.User;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;

public class payment_product extends AppCompatActivity {

    private TextView txtProductName, txtBrandName, txtSize, txtQuantity, txtDiscountPrice, txtOriginalPrice, txtTotalPrice,txtDiscountPercentage, txtCategory, txtUserMobile;
    private ShapeableImageView imgProduct;
    private RadioGroup radioGroupPayment;
    private RadioButton radioDebitCredit, radioUPI, radioDigitalWallet, radioNetBanking;
    private AppCompatButton btnPurchase;
    private String productId, userMobile, size, adminId;
    private Order order;
    private DatabaseReference orderRef, productRef, userRef, userAddressRef;
    private User user;
    private Product product;
    private static final int SMS_PERMISSION_CODE = 100;
    private AlertDialog successDialog;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_product);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        // Initialize views
        txtProductName = findViewById(R.id.txt_product_name);
        txtBrandName = findViewById(R.id.txt_brand_name);
        txtSize = findViewById(R.id.txt_size);
        txtQuantity = findViewById(R.id.txt_quantity);
        txtDiscountPrice = findViewById(R.id.txt_discount_price);
        txtOriginalPrice = findViewById(R.id.txt_original_price);
        txtDiscountPercentage = findViewById(R.id.txt_discount_percentage);
        txtCategory = findViewById(R.id.txt_category);
        txtUserMobile = findViewById(R.id.txt_user_mobile);
        imgProduct = findViewById(R.id.img_product);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radioDebitCredit = findViewById(R.id.radioDebitCredit);
        radioUPI = findViewById(R.id.radioUPI);
        radioDigitalWallet = findViewById(R.id.radioDigitalWallet);
        radioNetBanking = findViewById(R.id.radioNetBanking);
        btnPurchase = findViewById(R.id.btnPurchase);
        txtTotalPrice = findViewById(R.id.total_price); // Initialize total_price


        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        size = intent.getStringExtra("size");
        String quantity = intent.getStringExtra("quantity");

// Initialize Firebase references
        userRef = FirebaseDatabase.getInstance().getReference("User");
        userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);


        TextView backBtn = findViewById(R.id.back_btn); // Initialize back button

        backBtn.setOnClickListener(v -> handleBackPress());


// Validate productId
        if (productId == null) {
            runOnUiThread(() -> StyleableToast.makeText(this, "Invalid product data", R.style.UptrendToast).show());
            finish();
            return;
        }

// Initialize BiometricPrompt
        Executor executor = Executors.newSingleThreadExecutor();
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                runOnUiThread(() -> {
                    StyleableToast.makeText(payment_product.this, "Authentication error: " + errString, R.style.UptrendToast).show();
                    Log.e("PaymentProduct", "Biometric authentication error: " + errString);
                });
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                processPurchase(product != null ? product.getSellingPrice() : null, intent.getStringExtra("quantity"));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                runOnUiThread(() -> {
                    StyleableToast.makeText(payment_product.this, "Authentication failed", R.style.UptrendToast).show();
                    Log.e("PaymentProduct", "Biometric authentication failed");
                });
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Confirm Purchase")
                .setSubtitle("Enter your PIN, pattern, or password to complete your purchase")
                .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();

// Fetch and validate mobile number
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            runOnUiThread(() -> StyleableToast.makeText(this, "User not authenticated", R.style.UptrendToast).show());
            finish();
            return;
        }

        userAddressRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserAddress userAddress = snapshot.getChildren().iterator().next().getValue(UserAddress.class);
                    if (userAddress != null && userAddress.getMobileNo() != null && !userAddress.getMobileNo().isEmpty() && Pattern.isValidMobileNumber(userAddress.getMobileNo())) {
                        userMobile = userAddress.getMobileNo();
                        runOnUiThread(() -> {
                            txtUserMobile.setText("Mobile: " + userMobile);
                            Log.d("PaymentProduct", "Fetched userMobile from UserAddress: " + userMobile);
                        });
                        fetchUserAndProductData(quantity);
                    } else {
                        runOnUiThread(() -> {
                            StyleableToast.makeText(payment_product.this, "No valid mobile number found", R.style.UptrendToast).show();
                            txtUserMobile.setText("Mobile: N/A");
                            Log.e("PaymentProduct", "Invalid or missing mobile number in UserAddress for userId: " + userId);
                            finish();
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        StyleableToast.makeText(payment_product.this, "No address found", R.style.UptrendToast).show();
                        txtUserMobile.setText("Mobile: N/A");
                        Log.e("PaymentProduct", "No UserAddress found for userId: " + userId);
                        finish();
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    StyleableToast.makeText(payment_product.this, "Error fetching address: " + error.getMessage(), R.style.UptrendToast).show();
                    Log.e("PaymentProduct", "Address fetch error: " + error.getMessage());
                    txtUserMobile.setText("Mobile: N/A");
                    finish();
                });
            }
        });

        // Request SMS permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        }

        // Purchase button click
        btnPurchase.setOnClickListener(v -> {
            if (radioGroupPayment.getCheckedRadioButtonId() == -1) {
                runOnUiThread(() -> StyleableToast.makeText(this, "Please select a payment method", R.style.UptrendToast).show());
            } else if (product == null) {
                runOnUiThread(() -> StyleableToast.makeText(this, "Product data not loaded", R.style.UptrendToast).show());
            } else if (userMobile == null || userMobile.isEmpty()) {
                runOnUiThread(() -> StyleableToast.makeText(this, "No valid mobile number for purchase", R.style.UptrendToast).show());
            } else {
                BiometricManager biometricManager = BiometricManager.from(this);
                if (biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS) {
                    biometricPrompt.authenticate(promptInfo);
                } else {
                    runOnUiThread(() -> {
                        StyleableToast.makeText(this, "Device credential authentication not available", R.style.UptrendToast).show();
                        Log.e("PaymentProduct", "Device credential authentication not available");
                    });
                }
            }
        });
    }

    private void fetchUserAndProductData(String quantity) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Fetch user data (unchanged)
        userRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getChildren().iterator().next().getValue(User.class);
                    Log.d("PaymentProduct", "User data fetched: userName=" + (user != null ? user.getUserName() : "null"));
                } else {
                    runOnUiThread(() -> {
                        StyleableToast.makeText(payment_product.this, "User data not found", R.style.UptrendToast).show();
                        Log.e("PaymentProduct", "User data not found for userId: " + userId);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    StyleableToast.makeText(payment_product.this, "Error fetching user data: " + error.getMessage(), R.style.UptrendToast).show();
                    Log.e("PaymentProduct", "User fetch error: " + error.getMessage());
                });
            }
        });

        // Fetch product data
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    product = snapshot.getValue(Product.class);
                    if (product != null) {
                        adminId = snapshot.child("adminId").getValue(String.class);
                        runOnUiThread(() -> {
                            txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                            txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "N/A");
                            txtCategory.setText("Category: " + (product.getProductCategory() != null ? product.getProductCategory() : "N/A"));
                            String sizeText = "N/A";
                            if (size != null && !size.equals("0") && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                try {
                                    int sizeIndex = Integer.parseInt(size);
                                    if (product.getProductCategory() != null) {
                                        if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
                                            String[] shirtSizes = {"S", "M", "L", "XL", "XXL"};
                                            if (sizeIndex >= 0 && sizeIndex < shirtSizes.length) {
                                                sizeText = shirtSizes[sizeIndex];
                                            }
                                        } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
                                            String[] jeansSizes = {"28", "30", "32", "34", "36", "38", "40"};
                                            if (sizeIndex >= 0 && sizeIndex < jeansSizes.length) {
                                                sizeText = jeansSizes[sizeIndex];
                                            }
                                        } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
                                            String[] shoeSizes = {"6", "7", "8", "9", "10"};
                                            if (sizeIndex >= 0 && sizeIndex < shoeSizes.length) {
                                                sizeText = shoeSizes[sizeIndex];
                                            }
                                        }
                                    }
                                } catch (NumberFormatException e) {
                                    sizeText = "N/A";
                                }
                            }
                            txtSize.setText("" + sizeText);
                            txtQuantity.setText("" + (quantity != null ? quantity : "1"));
                            String sellingPrice = product.getSellingPrice();
                            String originalPrice = product.getOriginalPrice();
                            txtDiscountPrice.setText("₹ " + (sellingPrice != null ? sellingPrice : "0"));
                            txtOriginalPrice.setText("₹ " + (originalPrice != null ? originalPrice : sellingPrice != null ? sellingPrice : "0"));
                            if (sellingPrice != null && originalPrice != null) {
                                try {
                                    double sellPrice = Double.parseDouble(sellingPrice);
                                    double origPrice = Double.parseDouble(originalPrice);
                                    int qty = quantity != null ? Integer.parseInt(quantity) : 1;
                                    double totalPrice = sellPrice * qty; // Calculate total price
                                    txtTotalPrice.setText("₹ " + String.format("%.0f", totalPrice)); // Set total price
                                    if (origPrice > sellPrice) {
                                        double savings = origPrice - sellPrice;
                                        txtDiscountPercentage.setText("₹ " + String.format("%.0f", savings));
                                    } else {
                                        txtDiscountPercentage.setText("");
                                    }
                                } catch (NumberFormatException e) {
                                    txtDiscountPercentage.setText("");
                                    txtTotalPrice.setText("₹ 0"); // Fallback for total price
                                    Log.e("PaymentProduct", "Price parsing error: " + e.getMessage());
                                }
                            } else {
                                txtDiscountPercentage.setText("");
                                txtTotalPrice.setText("₹ 0"); // Fallback for total price
                            }
                            if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                                Glide.with(payment_product.this).load(product.getProductImages().get(0)).into(imgProduct);
                            }
                        });
                    } else {
                        runOnUiThread(() -> {
                            StyleableToast.makeText(payment_product.this, "Product data is null", R.style.UptrendToast).show();
                            Log.e("PaymentProduct", "Product is null for productId: " + productId);
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        StyleableToast.makeText(payment_product.this, "Product not found", R.style.UptrendToast).show();
                        Log.e("PaymentProduct", "Product not found for productId: " + productId);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    StyleableToast.makeText(payment_product.this, "Error fetching product details: " + error.getMessage(), R.style.UptrendToast).show();
                    Log.e("PaymentProduct", "Product fetch error: " + error.getMessage());
                });
            }
        });
    }
    private void processPurchase(String price, String quantity) {
        if (price == null || quantity == null || adminId == null) {
            runOnUiThread(() -> StyleableToast.makeText(this, "Invalid order data", R.style.UptrendToast).show());
            return;
        }

        order = new Order();
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        order.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        order.setProductId(productId);
        order.setProductQty(quantity);
        order.setOrderStatus("new");
        order.setDelliveryDate("");
        order.setShipingDate("");
        order.setSellerId(adminId);
        order.setOrderDate(DateAndTime.getDate());
        order.setOrderTime(DateAndTime.getTime());
        order.setProductSellingPrice(price);
        order.setProductOriginalPrice(product.getOriginalPrice() != null ? product.getOriginalPrice() : price);
        order.setProductSize(size != null ? size : "0");

        orderRef.push().setValue(order).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                reduceProductStock();
                sendNotification();
                runOnUiThread(this::showSuccessDialog);
            } else {
                runOnUiThread(() -> {
                    StyleableToast.makeText(this, "Failed to place order", R.style.UptrendToast).show();
                    Log.e("PaymentProduct", "Failed to place order: " + task.getException().getMessage());
                });
            }
        });
    }

    private void reduceProductStock() {
        if (size != null && !size.equals("0")) {
            DatabaseReference qtyRef = productRef.child("productSizes");
            DatabaseReference totalStockQtyRef = productRef.child("totalStock");
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        ArrayList<String> sizes = (ArrayList<String>) snapshot.child("productSizes").getValue();
                        String totalStockStr = snapshot.child("totalStock").getValue(String.class);
                        if (sizes != null && totalStockStr != null) {
                            try {
                                int totalStock = Integer.parseInt(totalStockStr);
                                int sizeIndex = Integer.parseInt(size);
                                if (sizeIndex >= 0 && sizeIndex < sizes.size()) {
                                    int sizeQty = Integer.parseInt(sizes.get(sizeIndex));
                                    if (sizeQty > 0) {
                                        sizeQty--;
                                        totalStock--;
                                        sizes.set(sizeIndex, String.valueOf(sizeQty));
                                        qtyRef.setValue(sizes);
                                        totalStockQtyRef.setValue(String.valueOf(totalStock));
                                    } else {
                                        runOnUiThread(() -> StyleableToast.makeText(payment_product.this, "Selected size out of stock", R.style.UptrendToast).show());
                                    }
                                } else {
                                    runOnUiThread(() -> StyleableToast.makeText(payment_product.this, "Invalid size selected", R.style.UptrendToast).show());
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    StyleableToast.makeText(payment_product.this, "Error updating stock: " + e.getMessage(), R.style.UptrendToast).show();
                                    Log.e("PaymentProduct", "Stock update error: " + e.getMessage());
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(() -> {
                        StyleableToast.makeText(payment_product.this, "Error updating stock: " + error.getMessage(), R.style.UptrendToast).show();
                        Log.e("PaymentProduct", "Stock update error: " + error.getMessage());
                    });
                }
            });
        } else {
            DatabaseReference totalStockQtyRef = productRef.child("totalStock");
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String totalStockStr = snapshot.child("totalStock").getValue(String.class);
                        if (totalStockStr != null) {
                            try {
                                int totalStock = Integer.parseInt(totalStockStr);
                                if (totalStock > 0) {
                                    totalStock--;
                                    totalStockQtyRef.setValue(String.valueOf(totalStock));
                                } else {
                                    runOnUiThread(() -> StyleableToast.makeText(payment_product.this, "Product out of stock", R.style.UptrendToast).show());
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    StyleableToast.makeText(payment_product.this, "Error updating stock: " + e.getMessage(), R.style.UptrendToast).show();
                                    Log.e("PaymentProduct", "Stock update error: " + e.getMessage());
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(() -> {
                        StyleableToast.makeText(payment_product.this, "Error updating stock: " + error.getMessage(), R.style.UptrendToast).show();
                        Log.e("PaymentProduct", "Stock update error: " + error.getMessage());
                    });
                }
            });
        }
    }

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_purchase_success, null);
        builder.setView(dialogView);

        TextView txtSuccessMessage = dialogView.findViewById(R.id.txt_success_message);
        AppCompatButton btnOk = dialogView.findViewById(R.id.btn_ok);

        txtSuccessMessage.setText("You have successfully purchased " + (product != null && product.getProductName() != null ? product.getProductName() : "your product") + "!");

        successDialog = builder.create();
        successDialog.setCancelable(false); // Prevent manual dismissal
        successDialog.show();

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            if (successDialog != null && successDialog.isShowing()) {
                successDialog.dismiss();
            }
            startActivity(new Intent(payment_product.this, complete_order.class));
            finish();
        });

        // Auto-navigate after 5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (successDialog != null && successDialog.isShowing()) {
                successDialog.dismiss();
            }
            startActivity(new Intent(payment_product.this, complete_order.class));
            finish();
        }, 5000);
    }
    private void sendNotification() {
        String message = "Thank you for your purchase! ₹" + order.getProductSellingPrice() + " paid for " + (product != null && product.getProductName() != null ? product.getProductName() : "Uptrend product") + ".";
        Log.d("SMS", "Sending SMS to " + userMobile + ": " + message);
        if (user != null) {
            NotificationHelper.makeNotification(this, user.getUserName());
        }
        if (userMobile != null && !userMobile.isEmpty() && ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(userMobile, null, message, null, null);
                runOnUiThread(() -> Log.d("SMS", "SMS sent successfully to " + userMobile));
            } catch (Exception e) {
                runOnUiThread(() -> {
                    StyleableToast.makeText(this, "Failed to send SMS: " + e.getMessage(), R.style.UptrendToast).show();
                    Log.e("SMS", "Error sending SMS: " + e.getMessage());
                });
            }
        } else {
            runOnUiThread(() -> Log.d("SMS", "SMS permission not granted or userMobile is invalid"));
        }
    }

    private void handleBackPress() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Cancel Purchase");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to cancel this product purchase?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {
                    Intent intent = new Intent(payment_product.this, open_product.class);
                    intent.putExtra("productId", productId);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);

            Typeface customFont = ResourcesCompat.getFont(this, R.font.caudex);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(customFont, Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont, Typeface.BOLD);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        handleBackPress();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            runOnUiThread(() -> StyleableToast.makeText(this, "SMS permission granted", R.style.UptrendToast).show());
        } else {
            runOnUiThread(() -> StyleableToast.makeText(this, "SMS permission denied", R.style.UptrendToast).show());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (successDialog != null && successDialog.isShowing()) {
            successDialog.dismiss();
        }
    }
}