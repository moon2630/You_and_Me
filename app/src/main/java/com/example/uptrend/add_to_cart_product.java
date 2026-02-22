package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapteranddatamodel.DateAndTime;
import com.example.uptrend.Adapter.CartAdapter;
import com.example.uptrend.Adapter.Onclick;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import DataModel.Cart;
import DataModel.Product;
import DataModel.UserAddress;

public class add_to_cart_product extends AppCompatActivity implements Onclick {

    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    int totalOriginalPrice = 0, totalDiscountPrice = 0, totalPrice = 0;
    LinearLayout layoutPriceDetails;
    TextView close_btn, txtName, txtAddress, txtMobileNo, txtTotalQty, txtTotalOriginalPrice, txtTotalDiscountPrice, totalOrderPrice, txtPrice,change_btn_A2C;
    private CardView cardViewAddress;
    private UserAddress userAddress;
    private DatabaseReference userAddressRef, cartRef, productRef;
    private FirebaseUser firebaseUser;
    private Query addressQuery, cartQuery;
    private String address;
    private Cart cart;
    private Product product;
    private ArrayList<Cart> cartArrayList;
    private ArrayList<Product> productArrayList;
    AppCompatButton btnBuyProductCart;
    private String userName, userEmail, userMobileNo;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart_product);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        change_btn_A2C = findViewById(R.id.change_btn_A2C);
        close_btn = findViewById(R.id.close_btn_add_to_cart);
        cardViewAddress = findViewById(R.id.cardViewAddress);
        txtName = findViewById(R.id.name_A2C);
        txtAddress = findViewById(R.id.address_A2C);
        txtMobileNo = findViewById(R.id.mobile_no_A2C);
        recyclerViewCart = findViewById(R.id.recycleView_product_A2C);
        txtTotalQty = findViewById(R.id.txtTotal);
        totalOrderPrice = findViewById(R.id.total_order_A2C);
        txtTotalDiscountPrice = findViewById(R.id.discount_price_A2C);
        txtTotalOriginalPrice = findViewById(R.id.total_price_A2C);
        txtPrice = findViewById(R.id.txtPrice);
        btnBuyProductCart = findViewById(R.id.btn_buy_add_to_cart);
        layoutPriceDetails = findViewById(R.id.layoutPriceDetails);



        change_btn_A2C.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), address_A2C.class);
            i.putExtra("status", "update");
            startActivity(i);
            finish();
        });

        close_btn.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), home.class));
            finish();
        });

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        hideShowAddressLayout();
        showCartProduct();
        calculation();

        btnBuyProductCart.setOnClickListener(view -> {
            if (cartArrayList == null || cartArrayList.isEmpty()) {
                Toast.makeText(add_to_cart_product.this, "Cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            final boolean[] hasAvailableProduct = {false}; // Use array to allow modification
            for (Cart cartItem : cartArrayList) {
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Product product = snapshot.getValue(Product.class);
                            if (product != null) {
                                if (cartItem.getProductSize() != null && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                    int productSize = Integer.parseInt(cartItem.getProductSize());
                                    if (productSize >= 0 && productSize < product.getProductSizes().size() && Integer.parseInt(product.getProductSizes().get(productSize)) > 0) {
                                        hasAvailableProduct[0] = true;
                                    }
                                } else if (product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) > 0) {
                                    hasAvailableProduct[0] = true;
                                }
                            }
                            if (cartArrayList.indexOf(cartItem) == cartArrayList.size() - 1) {
                                proceedToPayment(hasAvailableProduct[0]);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CartCheck", "Error fetching product: " + error.getMessage());
                    }
                });
            }
        });
    }

    public void calculation() {
        cartArrayList = new ArrayList<>();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        cartQuery = cartRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        cartQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartArrayList.clear();
                totalOriginalPrice = 0;
                totalDiscountPrice = 0;
                totalPrice = 0;
                final int[] totalQty = {0}; // Use array to allow modification in inner class
                for (DataSnapshot cartSnapShot : snapshot.getChildren()) {
                    cart = cartSnapShot.getValue(Cart.class);
                    cart.setCartId(cartSnapShot.getKey());
                    cartArrayList.add(cart);
                }
                for (Cart cartItem : cartArrayList) {
                    DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
                    productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                product = snapshot.getValue(Product.class);
                                if (product != null) {
                                    if (cartItem.getProductSize() != null && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                        int productSize = Integer.parseInt(cartItem.getProductSize());
                                        if (productSize >= 0 && productSize < product.getProductSizes().size()) {
                                            int stock = Integer.parseInt(product.getProductSizes().get(productSize));
                                            if (stock > 0) {
                                                int qty = Integer.parseInt(cartItem.getQty());
                                                int originalPrice = Integer.parseInt(product.getOriginalPrice());
                                                int sellingPrice = Integer.parseInt(product.getSellingPrice());
                                                totalOriginalPrice += (qty * originalPrice);
                                                totalDiscountPrice += ((originalPrice - sellingPrice) * qty);
                                                totalPrice += (qty * sellingPrice);
                                                totalQty[0] += qty;
                                            }
                                        }
                                    } else {
                                        if (product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) > 0) {
                                            int qty = Integer.parseInt(cartItem.getQty());
                                            int originalPrice = Integer.parseInt(product.getOriginalPrice());
                                            int sellingPrice = Integer.parseInt(product.getSellingPrice());
                                            totalOriginalPrice += (qty * originalPrice);
                                            totalDiscountPrice += ((originalPrice - sellingPrice) * qty);
                                            totalPrice += (qty * sellingPrice);
                                            totalQty[0] += qty;
                                        }
                                    }
                                }
                                if (cartArrayList.indexOf(cartItem) == cartArrayList.size() - 1) {
                                    txtTotalQty.setText(String.valueOf(totalQty[0]));
                                    updateUI();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("CartCalculation", "Error fetching product: " + error.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CartCalculation", "Error fetching cart: " + error.getMessage());
            }
        });
    }
    private void updateUI() {
        txtTotalOriginalPrice.setText(String.valueOf(totalOriginalPrice));
        txtTotalDiscountPrice.setText(String.valueOf(totalDiscountPrice));
        totalOrderPrice.setText(String.valueOf(totalPrice));
        txtPrice.setText(String.valueOf(totalPrice));
    }

    public void showCartProduct() {
        cartArrayList = new ArrayList<>();
        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        cartQuery = cartRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        cartQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartArrayList.clear();
                for (DataSnapshot cartSnapShot : snapshot.getChildren()) {
                    cart = cartSnapShot.getValue(Cart.class);
                    cart.setCartId(cartSnapShot.getKey());
                    cartArrayList.add(cart);
                }
                // Log cart item count and quantities
                StringBuilder quantitiesLog = new StringBuilder();
                for (Cart cartItem : cartArrayList) {
                    quantitiesLog.append("ProductId: ").append(cartItem.getProductId())
                            .append(", Quantity: ").append(cartItem.getQty()).append("; ");
                }
                Log.d("AddToCartProduct", "Cart items loaded: " + cartArrayList.size() + ", Quantities: " + (quantitiesLog.length() > 0 ? quantitiesLog.toString() : "None"));
                if (cartArrayList.size() == 0) {
                    layoutPriceDetails.setVisibility(View.GONE);
                    btnBuyProductCart.setEnabled(false);
                    txtPrice.setText("0");
                } else {
                    layoutPriceDetails.setVisibility(View.VISIBLE);
                    btnBuyProductCart.setEnabled(true);
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(add_to_cart_product.this);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);
                cartAdapter = new CartAdapter(getApplicationContext(), cartArrayList, add_to_cart_product.this);
                recyclerViewCart.setLayoutManager(layoutManager);
                recyclerViewCart.setAdapter(cartAdapter);
                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CartShow", "Error fetching cart: " + error.getMessage());
            }
        });
        fetchUser();
    }

    public void hideShowAddressLayout() {
        userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        addressQuery = userAddressRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        addressQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    cardViewAddress.setVisibility(View.VISIBLE);
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    userAddress = userSnapshot.getValue(UserAddress.class);
                    txtName.setText(userAddress.getFullName());
                    address = userAddress.getHouseNo() + " , " + userAddress.getRoadName() + " , " + userAddress.getCity() + "  " + userAddress.getPincode();
                    txtAddress.setText(address);
                    txtMobileNo.setText(userAddress.getMobileNo());
                } else {
                    cardViewAddress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AddressLayout", "Error fetching address: " + error.getMessage());
            }
        });
    }

    @Override
    public void ItemOnClickListener(String productId) {
        Intent i = new Intent(add_to_cart_product.this, open_product.class);
        i.putExtra("productId", productId);
        i.putExtra("activityName", "addToCartProduct");
        startActivity(i);
        finish();
    }

    public void fetchUser() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
        Query userQuery = userRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                userName = userSnapshot.child("userName").getValue(String.class);
                userEmail = userSnapshot.child("userEmail").getValue(String.class);
                userMobileNo = userSnapshot.child("userMobileNumber").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FetchUser", "Error fetching user: " + error.getMessage());
            }
        });
    }

    private void proceedToPayment(boolean hasAvailableProduct) {
        if (!hasAvailableProduct) {
            Toast.makeText(add_to_cart_product.this, "This product is out of stock, you cannot buy", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userMobileNo == null || userMobileNo.isEmpty() || userMobileNo.equals("525252525252")) {
            userAddressRef.orderByChild("userId").equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserAddress address = snapshot.getChildren().iterator().next().getValue(UserAddress.class);
                        if (address != null && address.getMobileNo() != null && !address.getMobileNo().isEmpty()) {
                            Intent i = new Intent(add_to_cart_product.this, payment_add_to_cart.class);
                            i.putExtra("userMobile", address.getMobileNo());
                            ArrayList<Cart> availableCartItems = new ArrayList<>();
                            for (Cart cartItem : cartArrayList) {
                                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
                                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Product product = snapshot.getValue(Product.class);
                                            if (product != null) {
                                                try {
                                                    int cartQty = Integer.parseInt(cartItem.getQty());
                                                    if (cartItem.getProductSize() != null && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                                        int productSize = Integer.parseInt(cartItem.getProductSize());
                                                        if (productSize >= 0 && productSize < product.getProductSizes().size() && Integer.parseInt(product.getProductSizes().get(productSize)) >= cartQty) {
                                                            availableCartItems.add(cartItem);
                                                        }
                                                    } else if (product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) >= cartQty) {
                                                        availableCartItems.add(cartItem);
                                                    }
                                                } catch (NumberFormatException e) {
                                                    Log.e("CartCheck", "Error parsing stock or quantity: " + e.getMessage());
                                                }
                                            }
                                            if (cartArrayList.indexOf(cartItem) == cartArrayList.size() - 1) {
                                                if (availableCartItems.isEmpty()) {
                                                    Toast.makeText(add_to_cart_product.this, "No available products in cart", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }
                                                String cartJson = new Gson().toJson(availableCartItems);
                                                i.putExtra("cartItemsJson", cartJson);
                                                Toast.makeText(add_to_cart_product.this, "Proceeding to payment", Toast.LENGTH_SHORT).show();
                                                startActivity(i);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("CartCheck", "Error fetching product: " + error.getMessage());
                                        if (cartArrayList.indexOf(cartItem) == cartArrayList.size() - 1) {
                                            Toast.makeText(add_to_cart_product.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        } else {
                            Intent i = new Intent(add_to_cart_product.this, address_A2C.class);
                            i.putExtra("status", "firsttime");
                            i.putExtra("activityName", "cart");
                            startActivity(i);
                            finish();
                        }
                    } else {
                        Intent i = new Intent(add_to_cart_product.this, address_A2C.class);
                        i.putExtra("status", "firsttime");
                        i.putExtra("activityName", "cart");
                        startActivity(i);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(add_to_cart_product.this, "Error fetching address: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Intent i = new Intent(add_to_cart_product.this, payment_add_to_cart.class);
            i.putExtra("userMobile", userMobileNo);
            ArrayList<Cart> availableCartItems = new ArrayList<>();
            for (Cart cartItem : cartArrayList) {
                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cartItem.getProductId());
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Product product = snapshot.getValue(Product.class);
                            if (product != null) {
                                try {
                                    int cartQty = Integer.parseInt(cartItem.getQty());
                                    if (cartItem.getProductSize() != null && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                        int productSize = Integer.parseInt(cartItem.getProductSize());
                                        if (productSize >= 0 && productSize < product.getProductSizes().size() && Integer.parseInt(product.getProductSizes().get(productSize)) >= cartQty) {
                                            availableCartItems.add(cartItem);
                                        }
                                    } else if (product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) >= cartQty) {
                                        availableCartItems.add(cartItem);
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("CartCheck", "Error parsing stock or quantity: " + e.getMessage());
                                }
                            }
                            if (cartArrayList.indexOf(cartItem) == cartArrayList.size() - 1) {
                                if (availableCartItems.isEmpty()) {
                                    Toast.makeText(add_to_cart_product.this, "No available products in cart", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                String cartJson = new Gson().toJson(availableCartItems);
                                i.putExtra("cartItemsJson", cartJson);
                                Toast.makeText(add_to_cart_product.this, "Proceeding to payment", Toast.LENGTH_SHORT).show();
                                startActivity(i);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("CartCheck", "Error fetching product: " + error.getMessage());
                        if (cartArrayList.indexOf(cartItem) == cartArrayList.size() - 1) {
                            Toast.makeText(add_to_cart_product.this, "Error fetching products", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    public String estimatedDeliveryDate() {
        String formattedEstimatedDeliveryDate = "";
        LocalDate todayDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            todayDate = LocalDate.now();
            LocalDate estimatedDeliveryDate = todayDate.plusDays(5);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, EEEE");
            formattedEstimatedDeliveryDate = estimatedDeliveryDate.format(formatter);
        }
        return formattedEstimatedDeliveryDate;
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), home.class);
        startActivity(intent);
        finish();
    }
}