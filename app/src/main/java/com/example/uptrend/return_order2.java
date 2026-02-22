package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.example.adapteranddatamodel.DateAndTime;
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;
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

import DataModel.Order;
import DataModel.Product;
import DataModel.Return;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class return_order2 extends AppCompatActivity {
    private String orderId, reason, comment, returnType;
    private DatabaseReference orderRef, productRef;
    private AppCompatButton continueReturnBtn;
    private TextView terms_txt;
    private FirebaseUser firebaseUser;
    private ShapeableImageView productImage;
    private EditText upiId, accountName, accountNumber;
    private TextView txtProductName, txtPrice, txtQty, txtProductColour, productSize, txtRefundAmount, txtUserName, txtAddress, txtMobileNumber;
    private RadioButton upi_btn, bank_account_btn, radioButtonRefund;
    private LinearLayout upi_layout, bank_account_layout;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order2);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        terms_txt = findViewById(R.id.terms_txt);
        upi_layout = findViewById(R.id.upi_layout);
        upi_btn = findViewById(R.id.upi_btn);
        bank_account_layout = findViewById(R.id.bank_account_layout);
        bank_account_btn = findViewById(R.id.bank_account_btn);
        continueReturnBtn = findViewById(R.id.return2Continuebtn);
        productImage = findViewById(R.id.historyProductImageReturn2);
        txtProductName = findViewById(R.id.productNameReturn2);
        txtPrice = findViewById(R.id.productPriceReturn2);
        txtQty = findViewById(R.id.txtQty);
        txtProductColour = findViewById(R.id.productColour);
        productSize = findViewById(R.id.txtSize);
        txtRefundAmount = findViewById(R.id.refundAmount);
        txtUserName = findViewById(R.id.userName);
        txtMobileNumber = findViewById(R.id.mobileNumber);
        txtAddress = findViewById(R.id.txtAddress);
        radioButtonRefund = findViewById(R.id.refund);
        upiId = findViewById(R.id.upiId);
        accountName = findViewById(R.id.accountName);
        accountNumber = findViewById(R.id.accountNumber);

        TextView closeBtnReturn2 = findViewById(R.id.close_btn_Return2);

        orderId = getIntent().getStringExtra("orderId");
        reason = getIntent().getStringExtra("reason");
        comment = getIntent().getStringExtra("comment");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (orderId == null || firebaseUser == null) {
            StyleableToast.makeText(this, "Invalid order or user data", R.style.UptrendToast).show();
            finish();
            return;
        }

        closeBtnReturn2.setOnClickListener(v -> handleBackPress());

        upi_btn.setOnClickListener(view -> {
            if (upi_btn.isChecked()) {
                int visibility = (upi_layout.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(upi_layout, new AutoTransition());
                upi_layout.setVisibility(visibility);
                bank_account_layout.setVisibility(View.GONE);
                bank_account_btn.setChecked(false);
                returnType = "upi";
            }
        });

        bank_account_btn.setOnClickListener(view -> {
            if (bank_account_btn.isChecked()) {
                int visibility = (bank_account_layout.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(bank_account_layout, new AutoTransition());
                bank_account_layout.setVisibility(visibility);
                upi_layout.setVisibility(View.GONE);
                upi_btn.setChecked(false);
                returnType = "account";
            }
        });

        terms_txt.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), terms_condition.class)));

        continueReturnBtn.setOnClickListener(view -> {
            if (!radioButtonRefund.isChecked()) {
                StyleableToast.makeText(this, "Please select a refund option", R.style.UptrendToast).show();
                return;
            }
            if (!upi_btn.isChecked() && !bank_account_btn.isChecked()) {
                StyleableToast.makeText(this, "Please select a payment method", R.style.UptrendToast).show();
                return;
            }
            if (upi_btn.isChecked() && upiId.getText().toString().trim().isEmpty()) {
                StyleableToast.makeText(this, "Please enter a valid UPI ID", R.style.UptrendToast).show();
                return;
            }
            if (bank_account_btn.isChecked() && (accountName.getText().toString().trim().isEmpty() || accountNumber.getText().toString().trim().isEmpty())) {
                StyleableToast.makeText(this, "Please enter valid bank account details", R.style.UptrendToast).show();
                return;
            }
            returnProcess(orderId, reason, comment, returnType, () -> {
                Intent i = new Intent(getApplicationContext(), splash_return_pd.class);
                i.putExtra("status", "return");
                startActivity(i);
                finish();
            });
        });

        displayProductDetails(orderId);
        showAddress(firebaseUser.getUid());
    }

    public interface OnReturnProcessListener {
        void onReturnProcessed();
    }

    public void returnProcess(String orderId, String reason, String comment, String paymentType, OnReturnProcessListener listener) {
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order == null) {
                        StyleableToast.makeText(return_order2.this, "Order data is null", R.style.UptrendToast).show();
                        Log.e("ReturnOrder2", "Order is null for orderId: " + orderId);
                        listener.onReturnProcessed();
                        return;
                    }
                    Return returnProduct = new Return();
                    returnProduct.setUserId(order.getUserId());
                    returnProduct.setSellerId(order.getSellerId());
                    returnProduct.setProductId(order.getProductId());
                    returnProduct.setProductSize(order.getProductSize());
                    returnProduct.setProductQty(order.getProductQty());
                    returnProduct.setReturnDate(DateAndTime.getDate());
                    returnProduct.setReturnTime(DateAndTime.getTime());
                    returnProduct.setProductOriginalPrice(order.getProductOriginalPrice());
                    returnProduct.setProductSellingPrice(order.getProductSellingPrice());
                    returnProduct.setReturnComment(comment);
                    returnProduct.setReturnReason(reason);
                    returnProduct.setRefundType(paymentType);
                    returnProduct.setUpiNo(upi_btn.isChecked() ? upiId.getText().toString().trim() : "");
                    returnProduct.setAccountName(bank_account_btn.isChecked() ? accountName.getText().toString().trim() : "");
                    returnProduct.setAccountNumber(bank_account_btn.isChecked() ? accountNumber.getText().toString().trim() : "");
                    returnProduct.setReturnStatus("return");
                    returnProduct.setOrderDate(order.getOrderDate());
                    returnProduct.setShipingDate(order.getShipingDate());
                    returnProduct.setDeliveryDAte(order.getDelliveryDate());
                    returnProduct.setPaymentStatus("pending");
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                        try {
                            LocalDate returnDate = LocalDate.parse(DateAndTime.getDate(), formatter);
                            returnProduct.setPickupDate(returnDate.plusDays(2).format(formatter));
                            returnProduct.setRefundDate(returnDate.plusDays(4).format(formatter));
                            LocalDate currentDate = LocalDate.now();
                            if (currentDate.isBefore(returnDate.plusDays(2))) {
                                returnProduct.setReturnStatus("return");
                            } else if (currentDate.isBefore(returnDate.plusDays(4))) {
                                returnProduct.setReturnStatus("pickup");
                            } else {
                                returnProduct.setReturnStatus("refund");
                            }
                        } catch (Exception e) {
                            StyleableToast.makeText(return_order2.this, "Error setting return dates: " + e.getMessage(), R.style.UptrendToast).show();
                            Log.e("ReturnOrder2", "Date parsing error for orderId: " + orderId + ", Error: " + e.getMessage());
                            listener.onReturnProcessed();
                            return;
                        }
                    } else {
                        returnProduct.setPickupDate(getProjectedDate(2));
                        returnProduct.setRefundDate(getProjectedDate(4));
                    }
                    saveReturnAndRemoveOrder(returnProduct, orderRef, listener);
                } else {
                    StyleableToast.makeText(return_order2.this, "Order not found", R.style.UptrendToast).show();
                    Log.e("ReturnOrder2", "Order not found for orderId: " + orderId);
                    listener.onReturnProcessed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(return_order2.this, "Error fetching order: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("ReturnOrder2", "Order fetch error for orderId: " + orderId + ", Error: " + error.getMessage());
                listener.onReturnProcessed();
            }
        });
    }

    private void saveReturnAndRemoveOrder(Return returnProduct, DatabaseReference orderRef, OnReturnProcessListener listener) {
        DatabaseReference returnRef = FirebaseDatabase.getInstance().getReference("Return");
        String returnId = returnRef.push().getKey();
        returnProduct.setNodeId(returnId);
        returnRef.child(returnId).setValue(returnProduct, (error, ref) -> {
            if (error != null) {
                StyleableToast.makeText(return_order2.this, "Error saving return: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("ReturnOrder2", "Failed to save return for orderId: " + orderId + ", Error: " + error.getMessage());
            } else {
                orderRef.removeValue((error2, ref2) -> {
                    if (error2 != null) {
                        StyleableToast.makeText(return_order2.this, "Error removing order: " + error2.getMessage(), R.style.UptrendToast).show();
                        Log.e("ReturnOrder2", "Failed to remove order for orderId: " + orderId + ", Error: " + error2.getMessage());
                    } else {
                        Log.d("ReturnOrder2", "Successfully processed return and removed order for orderId: " + orderId);
                    }
                    listener.onReturnProcessed();
                });
            }
        });
    }

    public void displayProductDetails(String orderId) {
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order == null) {
                        StyleableToast.makeText(return_order2.this, "Order data is null", R.style.UptrendToast).show();
                        Log.e("ReturnOrder2", "Order is null for orderId: " + orderId);
                        return;
                    }
                    txtQty.setText(order.getProductQty());
                    try {
                        int totalPrice = Integer.parseInt(order.getProductQty()) * Integer.parseInt(order.getProductSellingPrice());
                        txtPrice.setText(String.valueOf(totalPrice));
                        txtRefundAmount.setText(String.valueOf(totalPrice));
                    } catch (NumberFormatException e) {
                        StyleableToast.makeText(return_order2.this, "Error calculating price: " + e.getMessage(), R.style.UptrendToast).show();
                        Log.e("ReturnOrder2", "Price calculation error for orderId: " + orderId + ", Error: " + e.getMessage());
                        txtPrice.setText("0");
                        txtRefundAmount.setText("0");
                    }
                    productRef = FirebaseDatabase.getInstance().getReference("Product").child(order.getProductId());
                    productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Product product = snapshot.getValue(Product.class);
                                if (product == null) {
                                    StyleableToast.makeText(return_order2.this, "Product data is null", R.style.UptrendToast).show();
                                    Log.e("ReturnOrder2", "Product is null for productId: " + order.getProductId());
                                    return;
                                }
                                txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                                txtProductColour.setText(product.getProductColour() != null ? product.getProductColour() : "N/A");
                                String size = getProductSize(product.getProductCategory(), order.getProductSize());
                                if (size.equals("no")) {
                                    productSize.setText("");
                                } else {
                                    productSize.setText(size + " , ");
                                }
                                if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                                    Glide.with(getApplicationContext()).load(product.getProductImages().get(0)).into(productImage);
                                } else {
                                    productImage.setImageResource(R.drawable.ic_launcher_background);
                                    Log.w("ReturnOrder2", "Product images are null or empty for productId: " + order.getProductId());
                                }
                            } else {
                                StyleableToast.makeText(return_order2.this, "Product not found", R.style.UptrendToast).show();
                                Log.e("ReturnOrder2", "Product not found for productId: " + order.getProductId());
                                txtProductName.setText("N/A");
                                txtProductColour.setText("N/A");
                                productImage.setImageResource(R.drawable.ic_launcher_background);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            StyleableToast.makeText(return_order2.this, "Error fetching product: " + error.getMessage(), R.style.UptrendToast).show();
                            Log.e("ReturnOrder2", "Product fetch error for productId: " + order.getProductId() + ", Error: " + error.getMessage());
                            txtProductName.setText("N/A");
                            txtProductColour.setText("N/A");
                            productImage.setImageResource(R.drawable.ic_launcher_background);
                        }
                    });
                } else {
                    StyleableToast.makeText(return_order2.this, "Order not found", R.style.UptrendToast).show();
                    Log.e("ReturnOrder2", "Order not found for orderId: " + orderId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(return_order2.this, "Error fetching order: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("ReturnOrder2", "Order fetch error for orderId: " + orderId + ", Error: " + error.getMessage());
            }
        });
    }

    public void showAddress(String userId) {
        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        Query userQuery = userAddressRef.orderByChild("userId").equalTo(userId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    UserAddress userAddress = userSnapshot.getValue(UserAddress.class);
                    if (userAddress != null) {
                        txtUserName.setText(userAddress.getFullName() != null ? userAddress.getFullName() : "N/A");
                        String address = userAddress.getHouseNo() + " , " + userAddress.getRoadName() + " , " + userAddress.getCity() + " , " + userAddress.getPincode();
                        txtAddress.setText(address);
                        txtMobileNumber.setText(userAddress.getMobileNo() != null ? userAddress.getMobileNo() : "N/A");
                    } else {
                        StyleableToast.makeText(return_order2.this, "User address data is null", R.style.UptrendToast).show();
                        Log.e("ReturnOrder2", "User address is null for userId: " + userId);
                    }
                } else {
                    StyleableToast.makeText(return_order2.this, "User address not found", R.style.UptrendToast).show();
                    Log.e("ReturnOrder2", "User address not found for userId: " + userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(return_order2.this, "Error fetching user address: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("ReturnOrder2", "User address fetch error for userId: " + userId + ", Error: " + error.getMessage());
            }
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
                Log.e("ReturnOrder2", "Invalid size index: " + index);
            }
        } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
            String[] jeansSizes = {"28", "30", "32", "34", "36", "38", "40"};
            try {
                int idx = Integer.parseInt(index);
                if (idx >= 0 && idx < jeansSizes.length) size = jeansSizes[idx];
            } catch (NumberFormatException e) {
                Log.e("ReturnOrder2", "Invalid size index: " + index);
            }
        } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
            String[] shoeSizes = {"6", "7", "8", "9", "10"};
            try {
                int idx = Integer.parseInt(index);
                if (idx >= 0 && idx < shoeSizes.length) size = shoeSizes[idx];
            } catch (NumberFormatException e) {
                Log.e("ReturnOrder2", "Invalid size index: " + index);
            }
        } else {
            size = "no";
        }
        return size.isEmpty() ? "no" : size;
    }

    public String getProjectedDate(int daysToAdd) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                LocalDate todayDate = LocalDate.now();
                LocalDate estimatedDeliveryDate = todayDate.plusDays(daysToAdd);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                return estimatedDeliveryDate.format(formatter);
            } catch (Exception e) {
                Log.e("ReturnOrder2", "Error calculating projected date: " + e.getMessage());
                return "";
            }
        }
        return "";
    }

    private void handleBackPress() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Cancel Return Process");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to cancel the return process?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {
                    Intent intent = new Intent(return_order2.this, open_history_pd.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("status", "order");
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
}