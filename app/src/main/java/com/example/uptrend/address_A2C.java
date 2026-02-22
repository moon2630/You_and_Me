package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.adapteranddatamodel.Pattern;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import DataModel.Product;
import DataModel.User;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;

public class address_A2C extends AppCompatActivity {

    private TextView second_no, close_btnA;
    private LinearLayout layout_phone;
    private EditText txtAlternatePhoneNo, txtName, txtPhoneNo, txtPincode, txtState, txtCity, txtHouseNo, txtRoadName;
    private RadioGroup radioGroupTypeOfAddress;
    private TextView btnSaveAddress;
    private DatabaseReference userAddressRef;
    private FirebaseUser user;
    private UserAddress userAddress;
    private String status, activityName, productId;
    private RadioButton radioButtonWork, radioButtonHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_a2_c);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        // Initialize views
        second_no = findViewById(R.id.second_no_txt);
        txtAlternatePhoneNo = findViewById(R.id.second_no_address);
        layout_phone = findViewById(R.id.layout_second_no);
        close_btnA = findViewById(R.id.close_btn_address);
        txtName = findViewById(R.id.name_adress);
        txtPhoneNo = findViewById(R.id.phone_no_address);
        txtPincode = findViewById(R.id.pincode_address);
        txtState = findViewById(R.id.state_address);
        txtCity = findViewById(R.id.city_address);
        txtHouseNo = findViewById(R.id.house_adress);
        txtRoadName = findViewById(R.id.road_adress);
        btnSaveAddress = findViewById(R.id.save_address_btn);
        radioGroupTypeOfAddress = findViewById(R.id.radioGroupTypeOfAddress);
        radioButtonHome = findViewById(R.id.radioButtonHome);
        radioButtonWork = findViewById(R.id.radioButtonWork);

        userAddress = new UserAddress();
        userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Get data from intent
        status = getIntent().getStringExtra("status");
        activityName = getIntent().getStringExtra("activityName");
        productId = getIntent().getStringExtra("productId");

        // Log intent data for debugging
        Log.d("AddressA2C", "Received: status=" + status + ", activityName=" + activityName + ", productId=" + productId);

        // Validate user
        if (user == null) {
            StyleableToast.makeText(this, "Please log in to proceed", R.style.UptrendToast).show();
            startActivity(new Intent(this, signUp_and_logIn_page.class));
            finish();
            return;
        }

        // Show user address if updating
        if ("update".equals(status)) {
            showUserAddress();
        }

        second_no.setOnClickListener(view -> {
            int visibility = (layout_phone.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
            TransitionManager.beginDelayedTransition(layout_phone, new AutoTransition());
            layout_phone.setVisibility(visibility);
        });

        close_btnA.setOnClickListener(view -> handleBackPress());


        radioGroupTypeOfAddress.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            if (checkedId == R.id.radioButtonHome) {
                userAddress.setAddressType("Home");
            } else if (checkedId == R.id.radioButtonWork) {
                userAddress.setAddressType("Work");
            }
        });

        btnSaveAddress.setOnClickListener(view -> {
            // Validate and set user address fields
            String mobileNo = txtPhoneNo.getText().toString().trim();
            userAddress.setFullName(txtName.getText().toString().trim());
            userAddress.setMobileNo(mobileNo);
            userAddress.setAlternateMobileNo(txtAlternatePhoneNo.getText().toString().trim());
            userAddress.setPincode(txtPincode.getText().toString().trim());
            userAddress.setState(txtState.getText().toString().trim());
            userAddress.setCity(txtCity.getText().toString().trim());
            userAddress.setHouseNo(txtHouseNo.getText().toString().trim());
            userAddress.setRoadName(txtRoadName.getText().toString().trim());

            if (!validInput(userAddress)) {
                return;
            }

            if ("update".equals(status)) {
                updateAddress(userAddress);
            } else {
                userAddress.setUserId(user.getUid());
                userAddressRef.push().setValue(userAddress).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StyleableToast.makeText(address_A2C.this, "Address saved successfully", R.style.UptrendToast).show();
                        if ("openProduct".equals(activityName)) {
                            // Fetch product data to pass to payment_product
                            if (productId != null) {
                                DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
                                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            Product product = snapshot.getValue(Product.class);
                                            if (product != null) {
                                                Intent i = new Intent(address_A2C.this, payment_product.class);
                                                i.putExtra("productId", productId);
                                                i.putExtra("productName", product.getProductName());
                                                i.putExtra("brandName", product.getProductBrandName());
                                                i.putExtra("size", getIntent().getStringExtra("size") != null ? getIntent().getStringExtra("size") : "0");
                                                i.putExtra("quantity", "1");
                                                i.putExtra("image", product.getProductImages() != null && !product.getProductImages().isEmpty() ? product.getProductImages().get(0) : "");
                                                i.putExtra("price", product.getSellingPrice());
                                                i.putExtra("userMobile", mobileNo);
                                                Log.d("AddressA2C", "Passing to payment_product: userMobile=" + mobileNo);
                                                startActivity(i);
                                                finish();
                                            } else {
                                                StyleableToast.makeText(address_A2C.this, "Product data not found", R.style.UptrendToast).show();
                                                Log.e("AddressA2C", "Product is null for productId: " + productId);
                                            }
                                        } else {
                                            StyleableToast.makeText(address_A2C.this, "Product not found", R.style.UptrendToast).show();
                                            Log.e("AddressA2C", "Product not found for productId: " + productId);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        StyleableToast.makeText(address_A2C.this, "Error fetching product data: " + error.getMessage(), R.style.UptrendToast).show();
                                        Log.e("AddressA2C", "Product fetch error: " + error.getMessage());
                                    }
                                });
                            } else {
                                StyleableToast.makeText(address_A2C.this, "Invalid product data", R.style.UptrendToast).show();
                                Log.e("AddressA2C", "productId is null");
                                finish();
                            }
                        } else if ("cart".equals(activityName)) {
                            startActivity(new Intent(address_A2C.this, add_to_cart_product.class));
                            finish();
                        }
                    } else {
                        StyleableToast.makeText(address_A2C.this, "Failed to save address", R.style.UptrendToast).show();
                        Log.e("AddressA2C", "Failed to save address: " + task.getException().getMessage());
                    }
                });
            }
        });

        ChangeColour.changeColour(getApplicationContext(), txtName);
        ChangeColour.changeColour(getApplicationContext(), txtPhoneNo);
        ChangeColour.changeColour(getApplicationContext(), txtPincode);
        ChangeColour.changeColour(getApplicationContext(), txtState);
        ChangeColour.changeColour(getApplicationContext(), txtCity);
        ChangeColour.changeColour(getApplicationContext(), txtHouseNo);
        ChangeColour.changeColour(getApplicationContext(), txtRoadName);
    }

    public void updateAddress(UserAddress userAddress) {
        HashMap<String, Object> hashMapUser = new HashMap<>();
        hashMapUser.put("fullName", userAddress.getFullName());
        hashMapUser.put("mobileNo", userAddress.getMobileNo());
        hashMapUser.put("alternateMobileNo", userAddress.getAlternateMobileNo());
        hashMapUser.put("pincode", userAddress.getPincode());
        hashMapUser.put("state", userAddress.getState());
        hashMapUser.put("city", userAddress.getCity());
        hashMapUser.put("houseNo", userAddress.getHouseNo());
        hashMapUser.put("roadName", userAddress.getRoadName());
        hashMapUser.put("addressType", userAddress.getAddressType() != null ? userAddress.getAddressType() : "Home");
        hashMapUser.put("userId", userAddress.getUserId());
        Query query = userAddressRef.orderByChild("userId").equalTo(user.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserAddress").child(userSnapshot.getKey());
                    databaseReference.setValue(hashMapUser).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            StyleableToast.makeText(address_A2C.this, "Address updated successfully", R.style.UptrendToast).show();
                            startActivity(new Intent(getApplicationContext(), add_to_cart_product.class));
                            finish();
                        } else {
                            StyleableToast.makeText(address_A2C.this, "Failed to update address", R.style.UptrendToast).show();
                            Log.e("AddressA2C", "Failed to update address: " + task.getException().getMessage());
                        }
                    });
                } else {
                    StyleableToast.makeText(address_A2C.this, "No address found to update", R.style.UptrendToast).show();
                    Log.d("AddressA2C", "No address found for userId: " + user.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(address_A2C.this, "Error updating address: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("AddressA2C", "Address update error: " + error.getMessage());
            }
        });
    }

    public void showUserAddress() {
        Query addressQuery = userAddressRef.orderByChild("userId").equalTo(user.getUid());
        addressQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    userAddress = userSnapshot.getValue(UserAddress.class);
                    if (userAddress != null) {
                        txtName.setText(userAddress.getFullName() != null ? userAddress.getFullName() : "");
                        txtPhoneNo.setText(userAddress.getMobileNo() != null ? userAddress.getMobileNo() : "");
                        txtAlternatePhoneNo.setText(userAddress.getAlternateMobileNo() != null ? userAddress.getAlternateMobileNo() : "");
                        txtPincode.setText(userAddress.getPincode() != null ? userAddress.getPincode() : "");
                        txtState.setText(userAddress.getState() != null ? userAddress.getState() : "");
                        txtCity.setText(userAddress.getCity() != null ? userAddress.getCity() : "");
                        txtHouseNo.setText(userAddress.getHouseNo() != null ? userAddress.getHouseNo() : "");
                        txtRoadName.setText(userAddress.getRoadName() != null ? userAddress.getRoadName() : "");
                        String addressType = userAddress.getAddressType();
                        if ("Work".equals(addressType)) {
                            radioButtonWork.setChecked(true);
                            userAddress.setAddressType("Work");
                        } else {
                            radioButtonHome.setChecked(true);
                            userAddress.setAddressType("Home");
                        }
                        Log.d("AddressA2C", "Loaded address: mobileNo=" + userAddress.getMobileNo());
                    } else {
                        Log.e("AddressA2C", "UserAddress is null");
                    }
                } else {
                    Log.d("AddressA2C", "No address found for userId: " + user.getUid());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(address_A2C.this, "Error fetching address: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("AddressA2C", "Address fetch error: " + error.getMessage());
            }
        });
    }

    private void handleBackPress() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Cancel Address Entry");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to cancel entering your address?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {
                    Intent intent = new Intent(address_A2C.this, add_to_cart_product.class);
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

    public boolean validInput(UserAddress userAddress) {
        if (TextUtils.isEmpty(userAddress.getFullName())) {
            ChangeColour.errorColour(getApplicationContext(), txtName, "This Field Is Required");
            return false;
        }
        if (TextUtils.isEmpty(userAddress.getMobileNo())) {
            ChangeColour.errorColour(getApplicationContext(), txtPhoneNo, "This Field Is Required");
            return false;
        } else if (!Pattern.isValidMobileNumber(userAddress.getMobileNo())) {
            ChangeColour.errorColour(getApplicationContext(), txtPhoneNo, "Invalid Mobile Number");
            return false;
        }
        if (TextUtils.isEmpty(userAddress.getPincode())) {
            ChangeColour.errorColour(getApplicationContext(), txtPincode, "This Field Is Required");
            return false;
        }
        if (TextUtils.isEmpty(userAddress.getState())) {
            ChangeColour.errorColour(getApplicationContext(), txtState, "This Field Is Required");
            return false;
        }
        if (TextUtils.isEmpty(userAddress.getCity())) {
            ChangeColour.errorColour(getApplicationContext(), txtCity, "This Field Is Required");
            return false;
        }
        if (TextUtils.isEmpty(userAddress.getHouseNo())) {
            ChangeColour.errorColour(getApplicationContext(), txtHouseNo, "This Field Is Required");
            return false;
        }
        if (TextUtils.isEmpty(userAddress.getRoadName())) {
            ChangeColour.errorColour(getApplicationContext(), txtRoadName, "This Field Is Required");
            return false;
        }
        return true;
    }
}