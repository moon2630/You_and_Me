package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

public class listing_product extends AppCompatActivity {
    private AutoCompleteTextView spinnerCategory, spinnerSubCategory;
    private ArrayAdapter<String> adapterCategory, adapterSubCategory;
    private ArrayList<String> categoryList, subCategoryList;

    private TextView save_btn_category,using_txt,txtCurrentDate;

    private CardView guideCard;
    private String temp;
    private DatabaseReference categoryData, subCategoryData;
    private FirebaseUser user;
    private DatabaseReference databaseReference;
    private Product product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_product);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        String existingKey = getIntent().getStringExtra("Key");
        if (existingKey != null && !existingKey.isEmpty()) {
            // We have an existing product key, load the data
            loadExistingProduct(existingKey);
        }

        using_txt = findViewById(R.id.using_txt);
        guideCard = findViewById(R.id.guideCard);

        //findView by Id of Every Widget.
        spinnerCategory = findViewById(R.id.category);
        spinnerSubCategory = findViewById(R.id.subcategory);
        save_btn_category = findViewById(R.id.save_category_txt);

        // Making Instance of Category Node.
        categoryData = FirebaseDatabase.getInstance().getReference("Category");
        categoryList = new ArrayList<>();
        subCategoryList = new ArrayList<>();


        // Add this after setting content view
         txtCurrentDate = findViewById(R.id.txtCurrentDate);
        txtCurrentDate.setText("Date: " + DateHelper.getCurrentDate());



        adapterCategory = new ArrayAdapter<>(this,
                R.layout.spinner_dropdown_item,
                categoryList);
        adapterCategory.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategory);


        //getting Instance of Object
        databaseReference = FirebaseDatabase.getInstance().getReference("Product");
        user = FirebaseAuth.getInstance().getCurrentUser();



        //Fetching The Category From Database.
        categoryData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    temp = dataSnapshot.getValue(String.class);
                    categoryList.add(temp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /* Displaying SubCategory From The Database According to which Category User Select.
         For E.X User Select Book then all Sub Category of Book Will Display In Spinner.*/
        spinnerCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSubCategory.setText("");
                changeCategory(categoryList.get(i));
            }
        });


        using_txt .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (guideCard.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(guideCard, new AutoTransition());

                guideCard.setVisibility(var);
            }
        });



        save_btn_category.setOnClickListener(v -> {
            if (isValidInput()) {
                product = new Product();
                product.setProductCategory(spinnerCategory.getText().toString().trim());
                product.setProductSubCategory(spinnerSubCategory.getText().toString().trim());
                product.setAdminId(user.getUid());

                addData(product); // Now super fast
            }
        });

    }

    private void loadExistingProduct(String key) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(key);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && spinnerCategory != null && spinnerSubCategory != null) {
                    String categoryValue = snapshot.child("productCategory").getValue(String.class);
                    String subCategoryValue = snapshot.child("productSubCategory").getValue(String.class);

                    if (categoryValue != null) {
                        spinnerCategory.setText(categoryValue);
                        // Load subcategories for this category
                        changeCategory(categoryValue);

                        // Wait a moment for subcategories to load, then set the subcategory
                        new Handler().postDelayed(() -> {
                            if (subCategoryValue != null) {
                                spinnerSubCategory.setText(subCategoryValue);
                            }
                        }, 500); // Small delay to ensure subcategories are loaded
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(listing_product.this,
                        "Failed to load product data", R.style.UptrendToast).show();
            }
        });
    }
    private void addData(Product product) {
        Intent currentIntent = getIntent();

        // Check if we're updating an existing product
        if (currentIntent != null && currentIntent.hasExtra("Key")) {
            String existingKey = currentIntent.getStringExtra("Key");
            if (existingKey != null && !existingKey.isEmpty()) {
                // UPDATE EXISTING PRODUCT
                HashMap<String, Object> updateData = new HashMap<>();
                updateData.put("productCategory", product.getProductCategory());
                updateData.put("productSubCategory", product.getProductSubCategory());
                updateData.put("productStatus", "draft");

                databaseReference.child(existingKey).updateChildren(updateData).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Intent i = new Intent(listing_product.this, add_product_image.class);
                        i.putExtra("Category", product.getProductCategory());
                        i.putExtra("SubCategory", product.getProductSubCategory());
                        i.putExtra("Key", existingKey);
                        startActivity(i);
                        finish();
                    }
                });
                return;
            }
        }

        // CREATE NEW PRODUCT
        String newKey = databaseReference.push().getKey();
        if (newKey == null) return;

        HashMap<String, Object> initialData = new HashMap<>();
        initialData.put("productId", newKey);
        initialData.put("productCategory", product.getProductCategory());
        initialData.put("productSubCategory", product.getProductSubCategory());
        initialData.put("adminId", user.getUid());
        initialData.put("productStatus", "draft");
        initialData.put("timestamp", System.currentTimeMillis());
        // Note: productDisplayId will be generated in add_product_details
        initialData.put("productCreatedDate", DateHelper.getCurrentDate());


        databaseReference.child(newKey).updateChildren(initialData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent i = new Intent(listing_product.this, add_product_image.class);
                i.putExtra("Category", product.getProductCategory());
                i.putExtra("SubCategory", product.getProductSubCategory());
                i.putExtra("Key", newKey);
                startActivity(i);
                finish();
            }
        });
    }


    public boolean isValidInput() {
        if (TextUtils.isEmpty(spinnerCategory.getText().toString())) {
            Toast.makeText(this, "Please Select Category", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(spinnerSubCategory.getText().toString())) {
            Toast.makeText(this, "Please Select Subcategory", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent currentIntent = getIntent();

        // Check if we're editing an existing product
        if (currentIntent != null && currentIntent.hasExtra("Key")) {
            String existingKey = currentIntent.getStringExtra("Key");
            if (existingKey != null && !existingKey.isEmpty()) {
                // Show confirmation dialog for deleting product
                showDeleteConfirmationDialog(existingKey);
                return;
            }
        }

        // For new product creation or if no key exists, just go back
        Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
        startActivity(intent);
        finish();
    }

    // Method to show delete confirmation dialog
    // Method to show delete confirmation dialog with custom style
    private void showDeleteConfirmationDialog(String productKey) {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Delete Product");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to delete this product? All product data will be permanently deleted.");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("Yes, Delete", (d, which) -> {
                    deleteProductFromDatabase(productKey);
                })
                .setNegativeButton("Cancel", (d, which) -> {
                    d.dismiss();
                    // If user cancels, go to dashboard
                    Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
                    startActivity(intent);
                    finish();
                })
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.blue));
            Typeface customFont = ResourcesCompat.getFont(this, R.font.caudex);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(customFont, Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont, Typeface.BOLD);
        }

        // Make dialog non-cancelable
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    // Method to delete product from database
    private void deleteProductFromDatabase(String productKey) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productKey);
        productRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Product deleted successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to delete product: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
                    startActivity(intent);
                    finish();
                });
    }
    //This Method Will Change The SubCategory Data According to which Category Will Select
    public void changeCategory(String category) {
        subCategoryData = FirebaseDatabase.getInstance().getReference("Subcategory").child(category);
        subCategoryData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                subCategoryList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String temp = dataSnapshot.getValue(String.class);
                    if (temp != null) subCategoryList.add(temp);
                }

                // THIS IS THE ONLY CORRECT WAY
                adapterSubCategory = new ArrayAdapter<>(listing_product.this,
                        R.layout.spinner_dropdown_item,
                        subCategoryList);
                adapterSubCategory.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerSubCategory.setAdapter(adapterSubCategory);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(listing_product.this, "Failed to load subcategories", R.style.UptrendToast).show();
            }
        });

    }
}