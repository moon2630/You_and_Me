package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import io.github.muddz.styleabletoast.StyleableToast;

public class add_product_manufacture_details extends AppCompatActivity {
    private EditText txtGenericName,txtManufactureName,txtPackerDetails;
    private TextView txtSaveDetails,backButton,txtCurrentDate;
    private DatabaseReference databaseReference;
    private String key;
    private loadingDialog2 loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_manufacture_details);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        //Finding Id of Every Widget
        txtGenericName=findViewById(R.id.generic_name);
        txtManufactureName=findViewById(R.id.manufacture_details);
        txtPackerDetails=findViewById(R.id.Packers_details);
        txtSaveDetails=findViewById(R.id.save_manufacture_txt);



        txtCurrentDate = findViewById(R.id.txtCurrentDate);
        txtCurrentDate.setText("Date: " + DateHelper.getCurrentDate());

        backButton = findViewById(R.id.back_add_product_manufacture_details);
        backButton.setOnClickListener(v -> onBackPressed());

        key=getIntent().getStringExtra("Key");
        databaseReference= FirebaseDatabase.getInstance().getReference("Product").child(key);
        // Load existing manufacture details if any
        loadExistingManufactureDetails();

        loading=new loadingDialog2(add_product_manufacture_details.this);


        //Saving The Data
        txtSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidInput()) {
                    loading.show();

                    HashMap<String, Object> product = new HashMap<>();
                    // NULL SAFE TEXT GETTERS
                    product.put("productGenericName", getSafeText(txtGenericName));
                    product.put("productManufactureDetails", getSafeText(txtManufactureName));
                    product.put("productPackerDetail", getSafeText(txtPackerDetails));

                    databaseReference.updateChildren(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loading.dismiss();

                            if (task.isSuccessful()) {
                                // Get category and subcategory for billing
                                Intent currentIntent = getIntent();
                                String category = currentIntent.getStringExtra("Category");
                                String subCategory = currentIntent.getStringExtra("SubCategory");

                                // Fetch price data from database before navigating
                                fetchPriceDataAndNavigateToBilling(category, subCategory);
                            } else {
                                StyleableToast.makeText(getApplicationContext(),
                                        "Failed to save details. Please try again.",
                                        R.style.UptrendToast).show();
                            }
                        }
                    });
                }
            }
        });

        ChangeColour.changeColour(getApplicationContext(),txtGenericName);
        ChangeColour.changeColour(getApplicationContext(),txtManufactureName);
        ChangeColour.changeColour(getApplicationContext(),txtPackerDetails);
    }

    private void loadExistingManufactureDetails() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load generic name
                    String genericName = snapshot.child("productGenericName").getValue(String.class);
                    if (genericName != null && txtGenericName != null) {
                        txtGenericName.setText(genericName);
                    }

                    // Load manufacture details
                    String manufactureDetails = snapshot.child("productManufactureDetails").getValue(String.class);
                    if (manufactureDetails != null && txtManufactureName != null) {
                        txtManufactureName.setText(manufactureDetails);
                    }

                    // Load packer details
                    String packerDetails = snapshot.child("productPackerDetail").getValue(String.class);
                    if (packerDetails != null && txtPackerDetails != null) {
                        txtPackerDetails.setText(packerDetails);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Silent fail - no toast needed
            }
        });
    }

    private void fetchPriceDataAndNavigateToBilling(String category, String subCategory) {
        // Fetch the latest price data from database
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String originalPrice = snapshot.child("originalPrice").getValue(String.class);
                    String sellingPrice = snapshot.child("sellingPrice").getValue(String.class);

                    // Navigate to billing activity with all data
                    navigateToBillingActivity(category, subCategory,
                            originalPrice != null ? originalPrice : "0",
                            sellingPrice != null ? sellingPrice : "0");
                } else {
                    // If no data found, navigate with defaults
                    navigateToBillingActivity(category, subCategory, "0", "0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // On error, navigate with defaults
                navigateToBillingActivity(category, subCategory, "0", "0");
            }
        });
    }

    private void navigateToBillingActivity(String category, String subCategory,
                                           String originalPrice, String sellingPrice) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StyleableToast.makeText(getApplicationContext(),
                        "Product Manufacture Details Saved Successfully",
                        R.style.UptrendToast).show();

                // Navigate to billing activity instead of inventory
                Intent billingIntent = new Intent(getApplicationContext(), add_product_billing.class);
                billingIntent.putExtra("Key", key);

                if (category != null) billingIntent.putExtra("Category", category);
                if (subCategory != null) billingIntent.putExtra("SubCategory", subCategory);

                billingIntent.putExtra("originalPrice", originalPrice);
                billingIntent.putExtra("sellingPrice", sellingPrice);

                startActivity(billingIntent);
                finish();
            }
        }, 1000);
    }
    private String getSafeText(EditText editText) {
        if (editText == null || editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }
    /*
    `   This Method Will Check The EditText Is Empty or not
     */
    public boolean isValidInput(){
        if(TextUtils.isEmpty(txtGenericName.getText().toString())){
            ChangeColour.errorColour(getApplicationContext(),txtGenericName,"This Filed Is Required");
            return false;
        }
        if(TextUtils.isEmpty(txtManufactureName.getText().toString())){
            ChangeColour.errorColour(getApplicationContext(),txtManufactureName,"This Filed Is Required");
            return false;
        }
        if(TextUtils.isEmpty(txtPackerDetails.getText().toString())){
            ChangeColour.errorColour(getApplicationContext(),txtPackerDetails,"This Filed Is Required");
            return false;
        }
        return true;
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent backIntent = new Intent(add_product_manufacture_details.this, add_product_details.class);
        backIntent.putExtra("Key", key);

        // Get category and subcategory from the current intent
        Intent currentIntent = getIntent();
        if (currentIntent != null) {
            String category = currentIntent.getStringExtra("Category");
            String subCategory = currentIntent.getStringExtra("SubCategory");

            if (category != null) backIntent.putExtra("Category", category);
            if (subCategory != null) backIntent.putExtra("SubCategory", subCategory);
        }

        startActivity(backIntent);
        finish();
    }
}