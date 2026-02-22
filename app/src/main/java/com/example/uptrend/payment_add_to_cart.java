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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.adapteranddatamodel.DateAndTime;
import com.example.adapteranddatamodel.Pattern;
import com.example.uptrend.Adapter.CartAdapterPayment;
import com.example.uptrend.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import DataModel.Cart;
import DataModel.Order;
import DataModel.Product;
import DataModel.User;
import DataModel.UserAddress;

public class payment_add_to_cart extends AppCompatActivity {

    private TextView txtTotalOriginalPrice, txtTotalDiscountPrice, txtTotalPrice, txtUserMobile,txtTotalPriceCart;
    private RecyclerView recyclerViewCart;
    private RadioGroup radioGroupPayment;
    private RadioButton radioDebitCredit, radioUPI, radioDigitalWallet, radioNetBanking;
    private AppCompatButton btnPurchase;
    private String userMobile;
    private ArrayList<Cart> cartItems;
    private DatabaseReference orderRef, cartRef, productRef, userRef, userAddressRef;
    private User user;
    private static final int SMS_PERMISSION_CODE = 100;
    private AlertDialog successDialog;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private CartAdapterPayment cartAdapter;
    private ArrayList<Product> productList = new ArrayList<>(); // Initialize to prevent null

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_add_to_cart);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        // Initialize views
        txtTotalOriginalPrice = findViewById(R.id.txt_total_original_price);
        txtTotalDiscountPrice = findViewById(R.id.txt_total_discount_price);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTotalPriceCart = findViewById(R.id.total_price_cart); // Initialize total_price_cart

        txtUserMobile = findViewById(R.id.txt_user_mobile);
        recyclerViewCart = findViewById(R.id.recyclerView_cart);
        radioGroupPayment = findViewById(R.id.radioGroupPayment);
        radioDebitCredit = findViewById(R.id.radioDebitCredit);
        radioUPI = findViewById(R.id.radioUPI);
        radioDigitalWallet = findViewById(R.id.radioDigitalWallet);
        radioNetBanking = findViewById(R.id.radioNetBanking);
        btnPurchase = findViewById(R.id.btnPurchase);

        // Get data from intent
        Intent intent = getIntent();
        userMobile = intent.getStringExtra("userMobile");
        String cartJson = intent.getStringExtra("cartItemsJson");
        cartItems = new Gson().fromJson(cartJson, new TypeToken<ArrayList<Cart>>(){}.getType());

        // Initialize Firebase references
        userRef = FirebaseDatabase.getInstance().getReference("User");
        userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        orderRef = FirebaseDatabase.getInstance().getReference("Order");

        TextView backBtn = findViewById(R.id.back_btn); // Initialize back button
        backBtn.setOnClickListener(v -> handleBackPress());

        // Validate and deduplicate cart items
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show();
            Log.e("PaymentAddToCart", "Cart is empty");
            finish();
            return;
        }

        // Deduplicate cart items by productId
        HashSet<String> seenProductIds = new HashSet<>();
        ArrayList<Cart> uniqueCartItems = new ArrayList<>();
        for (Cart cartItem : cartItems) {
            if (cartItem != null && cartItem.getProductId() != null && cartItem.getQty() != null && seenProductIds.add(cartItem.getProductId())) {
                uniqueCartItems.add(cartItem);
            } else {
                Log.w("PaymentAddToCart", "Skipping duplicate or invalid cart item: productId=" + (cartItem != null ? cartItem.getProductId() : "null"));
            }
        }

        // Validate stock for unique cart items
        ArrayList<Cart> availableCartItems = new ArrayList<>();
        final int[] processedItems = {0};
        for (Cart cartItem : uniqueCartItems) {
            productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    processedItems[0]++;
                    if (snapshot.exists()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null && product.getOriginalPrice() != null && product.getSellingPrice() != null) {
                            try {
                                int cartQty = Integer.parseInt(cartItem.getQty());
                                if (cartItem.getProductSize() != null && !cartItem.getProductSize().equals("0") && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                    int productSize = Integer.parseInt(cartItem.getProductSize());
                                    if (productSize >= 0 && productSize < product.getProductSizes().size()) {
                                        String sizeStock = product.getProductSizes().get(productSize);
                                        if (sizeStock != null && Integer.parseInt(sizeStock) >= cartQty) {
                                            availableCartItems.add(cartItem);
                                        }
                                    }
                                } else if (product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) >= cartQty) {
                                    availableCartItems.add(cartItem);
                                }
                            } catch (NumberFormatException e) {
                                Log.e("PaymentAddToCart", "Error parsing stock or quantity for productId: " + cartItem.getProductId() + ", error: " + e.getMessage());
                            }
                        } else {
                            Log.e("PaymentAddToCart", "Product null or missing price fields for productId: " + cartItem.getProductId());
                        }
                    } else {
                        Log.e("PaymentAddToCart", "Product not found for productId: " + cartItem.getProductId());
                    }

                    if (processedItems[0] == uniqueCartItems.size()) {
                        runOnUiThread(() -> {
                            cartItems = availableCartItems; // Update cartItems with valid, unique items
                            if (cartItems.isEmpty()) {
                                Toast.makeText(payment_add_to_cart.this, "No available products in cart", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentAddToCart", "No available products in cart, finishing activity");
                                finish();
                            } else {
                                Log.d("PaymentAddToCart", "Validated cart items: " + cartItems.size() + ", Quantities: " + buildCartQuantitiesLog(cartItems));
                                fetchUserAndProductData();
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    processedItems[0]++;
                    Log.e("PaymentAddToCart", "Product fetch error for productId: " + cartItem.getProductId() + ", error: " + error.getMessage());
                    if (processedItems[0] == uniqueCartItems.size()) {
                        runOnUiThread(() -> {
                            cartItems = availableCartItems;
                            if (cartItems.isEmpty()) {
                                Toast.makeText(payment_add_to_cart.this, "No available products in cart", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentAddToCart", "No available products in cart, finishing activity");
                                finish();
                            } else {
                                Log.d("PaymentAddToCart", "Validated cart items: " + cartItems.size() + ", Quantities: " + buildCartQuantitiesLog(cartItems));
                                fetchUserAndProductData();
                            }
                        });
                    }
                }
            });
        }

        // Initialize BiometricPrompt
        Executor executor = Executors.newSingleThreadExecutor();
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                runOnUiThread(() -> {
                    Toast.makeText(payment_add_to_cart.this, "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
                    Log.e("PaymentAddToCart", "Authentication error: " + errString);
                });
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                runOnUiThread(() -> {
                    Log.d("PaymentAddToCart", "Authentication succeeded using device credentials");
                    processPurchase();
                });
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                runOnUiThread(() -> {
                    Toast.makeText(payment_add_to_cart.this, "Authentication failed. Please use your PIN, pattern, or password.", Toast.LENGTH_SHORT).show();
                    Log.e("PaymentAddToCart", "Authentication failed");
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
            runOnUiThread(() -> {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                txtUserMobile.setText("Mobile: N/A");
            });
            return;
        }

        userAddressRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot addressSnapshot : snapshot.getChildren()) {
                        UserAddress userAddress = addressSnapshot.getValue(UserAddress.class);
                        if (userAddress != null && userAddress.getMobileNo() != null && !userAddress.getMobileNo().isEmpty() && Pattern.isValidMobileNumber(userAddress.getMobileNo())) {
                            userMobile = userAddress.getMobileNo();
                            runOnUiThread(() -> {
                                txtUserMobile.setText("Mobile: " + userMobile);
                                Log.d("PaymentAddToCart", "Fetched userMobile from UserAddress: " + userMobile);
                            });
                            return;
                        }
                    }
                    runOnUiThread(() -> {
                        Toast.makeText(payment_add_to_cart.this, "No valid mobile number found", Toast.LENGTH_SHORT).show();
                        txtUserMobile.setText("Mobile: N/A");
                        Log.e("PaymentAddToCart", "No valid mobile number in UserAddress for userId: " + userId);
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(payment_add_to_cart.this, "No address found", Toast.LENGTH_SHORT).show();
                        txtUserMobile.setText("Mobile: N/A");
                        Log.e("PaymentAddToCart", "No UserAddress found for userId: " + userId);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    Toast.makeText(payment_add_to_cart.this, "Error fetching address: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    txtUserMobile.setText("Mobile: N/A");
                    Log.e("PaymentAddToCart", "Address fetch error: " + error.getMessage());
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
                runOnUiThread(() -> Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show());
            } else if (productList == null || productList.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "Product data not loaded", Toast.LENGTH_SHORT).show());
            } else if (userMobile == null || userMobile.isEmpty()) {
                runOnUiThread(() -> Toast.makeText(this, "No valid mobile number for purchase", Toast.LENGTH_SHORT).show());
            } else {
                BiometricManager biometricManager = BiometricManager.from(this);
                int authResult = biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                if (authResult == BiometricManager.BIOMETRIC_SUCCESS) {
                    biometricPrompt.authenticate(promptInfo);
                } else if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "PIN, pattern, or password required. Please set up a device lock screen in Settings > Security.", Toast.LENGTH_SHORT).show();
                        Log.e("PaymentAddToCart", "Device credentials not available, only biometric authentication supported");
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Device credential authentication not available. Please set up a PIN, pattern, or password.", Toast.LENGTH_SHORT).show();
                        Log.e("PaymentAddToCart", "No authentication methods available");
                    });
                }
            }
        });
    }

    private String buildCartQuantitiesLog(ArrayList<Cart> items) {
        StringBuilder log = new StringBuilder();
        for (Cart item : items) {
            log.append("ProductId: ").append(item.getProductId())
                    .append(", Quantity: ").append(item.getQty()).append("; ");
        }
        return log.length() > 0 ? log.toString() : "None";
    }

    private void fetchUserAndProductData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Fetch user data (unchanged)
        userRef.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    user = snapshot.getChildren().iterator().next().getValue(User.class);
                    Log.d("PaymentAddToCart", "User data fetched: userName=" + (user != null ? user.getUserName() : "null"));
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(payment_add_to_cart.this, "User data not found", Toast.LENGTH_SHORT).show();
                        Log.e("PaymentAddToCart", "User data not found for userId: " + userId);
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    Toast.makeText(payment_add_to_cart.this, "Error fetching user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PaymentAddToCart", "User fetch error: " + error.getMessage());
                });
            }
        });

        // Fetch product data for validated cart items
        productList.clear();
        ArrayList<Cart> validCartItems = new ArrayList<>();
        final int[] totalOriginalPrice = {0};
        final int[] totalDiscountPrice = {0};
        final int[] totalPrice = {0};
        final int[] processedItems = {0};

        for (Cart cartItem : cartItems) {
            productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    processedItems[0]++;
                    if (snapshot.exists()) {
                        Product product = snapshot.getValue(Product.class);
                        if (product != null && product.getOriginalPrice() != null && product.getSellingPrice() != null) {
                            try {
                                String productId = cartItem.getProductId();
                                int cartQty = Integer.parseInt(cartItem.getQty());
                                boolean isValid = false;
                                if (cartItem.getProductSize() != null && !cartItem.getProductSize().equals("0") && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                    int productSize = Integer.parseInt(cartItem.getProductSize());
                                    if (productSize >= 0 && productSize < product.getProductSizes().size()) {
                                        String sizeStock = product.getProductSizes().get(productSize);
                                        if (sizeStock != null && Integer.parseInt(sizeStock) >= cartQty) {
                                            isValid = true;
                                        }
                                    }
                                } else if (product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) >= cartQty) {
                                    isValid = true;
                                }
                                if (isValid) {
                                    validCartItems.add(cartItem);
                                    productList.add(product);
                                    int originalPrice = Integer.parseInt(product.getOriginalPrice());
                                    int sellingPrice = Integer.parseInt(product.getSellingPrice());
                                    totalOriginalPrice[0] += cartQty * originalPrice;
                                    totalDiscountPrice[0] += (originalPrice - sellingPrice) * cartQty;
                                    totalPrice[0] += cartQty * sellingPrice;
                                    Log.d("PaymentAddToCart", "Valid product added: productId=" + productId + ", qty=" + cartQty);
                                } else {
                                    Log.w("PaymentAddToCart", "Skipping productId: " + productId + " due to insufficient stock or invalid size");
                                }
                            } catch (NumberFormatException e) {
                                Log.e("PaymentAddToCart", "Price calculation error for productId: " + cartItem.getProductId() + ", error: " + e.getMessage());
                            }
                        } else {
                            Log.e("PaymentAddToCart", "Product null or missing price fields for productId: " + cartItem.getProductId());
                        }
                    } else {
                        Log.e("PaymentAddToCart", "Product not found for productId: " + cartItem.getProductId());
                    }

                    // Update UI after all products are processed
                    if (processedItems[0] == cartItems.size()) {
                        runOnUiThread(() -> {
                            cartItems = validCartItems; // Update cartItems to valid items only
                            if (productList.isEmpty()) {
                                Toast.makeText(payment_add_to_cart.this, "No valid products in cart", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentAddToCart", "No valid products found, navigating back to add_to_cart_product");
                                startActivity(new Intent(payment_add_to_cart.this, add_to_cart_product.class));
                                finish();
                                return;
                            }
                            if (productList.size() != cartItems.size()) {
                                Toast.makeText(payment_add_to_cart.this, "Invalid product data in cart", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentAddToCart", "Product list size (" + productList.size() + ") does not match cart items size (" + cartItems.size() + ")");
                                startActivity(new Intent(payment_add_to_cart.this, add_to_cart_product.class));
                                finish();
                                return;
                            }
                            // Log product count and quantities
                            Log.d("PaymentAddToCart", "Products fetched: " + productList.size() + ", Quantities: " + buildCartQuantitiesLog(cartItems));
                            txtTotalOriginalPrice.setText("₹ " + totalOriginalPrice[0]);
                            txtTotalDiscountPrice.setText("₹ " + totalDiscountPrice[0]);
                            txtTotalPrice.setText("₹ " + totalPrice[0]);
                            txtTotalPriceCart.setText("₹ " + totalPrice[0]); // Set total_price_cart
                            LinearLayoutManager layoutManager = new LinearLayoutManager(payment_add_to_cart.this);
                            layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            recyclerViewCart.setLayoutManager(layoutManager);
                            cartAdapter = new CartAdapterPayment(payment_add_to_cart.this, cartItems, productList);
                            recyclerViewCart.setAdapter(cartAdapter);
                            cartAdapter.notifyDataSetChanged();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    processedItems[0]++;
                    Log.e("PaymentAddToCart", "Product fetch error for productId: " + cartItem.getProductId() + ", error: " + error.getMessage());
                    if (processedItems[0] == cartItems.size()) {
                        runOnUiThread(() -> {
                            cartItems = validCartItems; // Update cartItems to valid items only
                            if (productList.isEmpty()) {
                                Toast.makeText(payment_add_to_cart.this, "No valid products in cart", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentAddToCart", "No valid products found, navigating back to add_to_cart_product");
                                startActivity(new Intent(payment_add_to_cart.this, add_to_cart_product.class));
                                finish();
                                return;
                            }
                            if (productList.size() != cartItems.size()) {
                                Toast.makeText(payment_add_to_cart.this, "Invalid product data in cart", Toast.LENGTH_SHORT).show();
                                Log.e("PaymentAddToCart", "Product list size (" + productList.size() + ") does not match cart items size (" + cartItems.size() + ")");
                                startActivity(new Intent(payment_add_to_cart.this, add_to_cart_product.class));
                                finish();
                                return;
                            }
                            // Log product count and quantities
                            Log.d("PaymentAddToCart", "Products fetched: " + productList.size() + ", Quantities: " + buildCartQuantitiesLog(cartItems));
                            txtTotalOriginalPrice.setText("₹ " + totalOriginalPrice[0]);
                            txtTotalDiscountPrice.setText("You save ₹ " + totalDiscountPrice[0]);
                            txtTotalPrice.setText("₹ " + totalPrice[0]);
                            txtTotalPriceCart.setText("₹ " + totalPrice[0]); // Set total_price_cart
                            LinearLayoutManager layoutManager = new LinearLayoutManager(payment_add_to_cart.this);
                            layoutManager.setReverseLayout(true);
                            layoutManager.setStackFromEnd(true);
                            recyclerViewCart.setLayoutManager(layoutManager);
                            cartAdapter = new CartAdapterPayment(payment_add_to_cart.this, cartItems, productList);
                            recyclerViewCart.setAdapter(cartAdapter);
                            cartAdapter.notifyDataSetChanged();
                        });
                    }
                }
            });
        }
    }
    private void processPurchase() {
        for (int i = 0; i < cartItems.size(); i++) {
            Cart cartItem = cartItems.get(i);
            Product product = productList.get(i);
            String adminId = product.getAdminId();
            if (adminId == null) {
                runOnUiThread(() -> Toast.makeText(this, "Invalid seller data for product: " + product.getProductName(), Toast.LENGTH_SHORT).show());
                continue;
            }

            Order order = new Order();
            order.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
            order.setProductId(cartItem.getProductId());
            order.setProductQty(cartItem.getQty());
            order.setOrderStatus("new");
            order.setDelliveryDate("");
            order.setShipingDate("");
            order.setSellerId(adminId);
            order.setOrderDate(DateAndTime.getDate());
            order.setOrderTime(DateAndTime.getTime());
            order.setProductSellingPrice(product.getSellingPrice());
            order.setProductOriginalPrice(product.getOriginalPrice());
            order.setProductSize(cartItem.getProductSize() != null ? cartItem.getProductSize() : "0");

            orderRef.push().setValue(order).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reduceProductStock(cartItem, product);
                    sendNotification(cartItem, product);
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to place order for: " + product.getProductName(), Toast.LENGTH_SHORT).show();
                        Log.e("PaymentAddToCart", "Failed to place order: " + task.getException().getMessage());
                    });
                }
            });
        }

        // Clear cart after processing
        cartRef.orderByChild("userId").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    cartSnapshot.getRef().removeValue();
                }
                runOnUiThread(() -> showSuccessDialog());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                runOnUiThread(() -> {
                    Toast.makeText(payment_add_to_cart.this, "Error clearing cart: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("PaymentAddToCart", "Cart clear error: " + error.getMessage());
                });
            }
        });
    }

    private void reduceProductStock(Cart cartItem, Product product) {
        productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
        if (cartItem.getProductSize() != null && !cartItem.getProductSize().equals("0")) {
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
                                int sizeIndex = Integer.parseInt(cartItem.getProductSize());
                                int cartQty = Integer.parseInt(cartItem.getQty());
                                if (sizeIndex >= 0 && sizeIndex < sizes.size()) {
                                    int sizeQty = Integer.parseInt(sizes.get(sizeIndex));
                                    if (sizeQty >= cartQty && totalStock >= cartQty) {
                                        sizeQty -= cartQty;
                                        totalStock -= cartQty;
                                        sizes.set(sizeIndex, String.valueOf(sizeQty));
                                        qtyRef.setValue(sizes);
                                        totalStockQtyRef.setValue(String.valueOf(totalStock));
                                        Log.d("PaymentAddToCart", "Stock updated for product: " + product.getProductName() + ", size: " + sizeIndex + ", new stock: " + sizeQty);
                                    } else {
                                        runOnUiThread(() -> Toast.makeText(payment_add_to_cart.this, "Insufficient stock for: " + product.getProductName(), Toast.LENGTH_SHORT).show());
                                    }
                                } else {
                                    runOnUiThread(() -> Toast.makeText(payment_add_to_cart.this, "Invalid size for: " + product.getProductName(), Toast.LENGTH_SHORT).show());
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(payment_add_to_cart.this, "Error updating stock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("PaymentAddToCart", "Stock update error: " + e.getMessage());
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(() -> {
                        Toast.makeText(payment_add_to_cart.this, "Error updating stock: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("PaymentAddToCart", "Stock update error: " + error.getMessage());
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
                                int cartQty = Integer.parseInt(cartItem.getQty());
                                if (totalStock >= cartQty) {
                                    totalStock -= cartQty;
                                    totalStockQtyRef.setValue(String.valueOf(totalStock));
                                    Log.d("PaymentAddToCart", "Total stock updated for product: " + product.getProductName() + ", new stock: " + totalStock);
                                } else {
                                    runOnUiThread(() -> Toast.makeText(payment_add_to_cart.this, "Insufficient stock for: " + product.getProductName(), Toast.LENGTH_SHORT).show());
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> {
                                    Toast.makeText(payment_add_to_cart.this, "Error updating stock: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.e("PaymentAddToCart", "Stock update error: " + e.getMessage());
                                });
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    runOnUiThread(() -> {
                        Toast.makeText(payment_add_to_cart.this, "Error updating stock: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("PaymentAddToCart", "Stock update error: " + error.getMessage());
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

        txtSuccessMessage.setText("You have successfully purchased your cart items!");

        successDialog = builder.create();
        successDialog.setCancelable(false); // Prevent manual dismissal
        successDialog.show();

        // Handle OK button click
        btnOk.setOnClickListener(v -> {
            if (successDialog != null && successDialog.isShowing()) {
                successDialog.dismiss();
            }
            startActivity(new Intent(payment_add_to_cart.this, complete_order.class));
            finish();
        });

        // Auto-navigate after 5 seconds
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (successDialog != null && successDialog.isShowing()) {
                successDialog.dismiss();
            }
            startActivity(new Intent(payment_add_to_cart.this, complete_order.class));
            finish();
        }, 5000);
    }

    private void sendNotification(Cart cartItem, Product product) {
        String message = "Thank you for your purchase! ₹" + (Integer.parseInt(product.getSellingPrice()) * Integer.parseInt(cartItem.getQty())) + " paid for " + product.getProductName() + ".";
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
                    Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Intent intent = new Intent(payment_add_to_cart.this, add_to_cart_product.class);
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
            runOnUiThread(() -> Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show());
        } else {
            runOnUiThread(() -> Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show());
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