package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uptrendseller.Adapter.InventoryProductAdapter;
import com.example.uptrendseller.Adapter.Onclick;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import DataModel.Product;

public class inventory_product extends AppCompatActivity implements Onclick {
    private DatabaseReference productNode;
    private FirebaseUser user;
    private Query productQuery;
    private ArrayList<Product> productArrayList;
    private ArrayList<Product> originalProductArrayList;
    private RecyclerView recyclerView;
    private InventoryProductAdapter productAdapter;
    TextView btnClose, sortTxt;

    // Add this variable for current sort type
    private String currentSortType = "newest"; // default

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_product);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        // findViewById of Widget
        recyclerView = findViewById(R.id.recyclerView);
        btnClose = findViewById(R.id.btnClose);
        sortTxt = findViewById(R.id.sort_txt);

        // Set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize ArrayList
        productArrayList = new ArrayList<>();
        originalProductArrayList = new ArrayList<>();

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), dashboard_admin.class));
                finish();
            }
        });

        // Sort button click listener
        sortTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSortDialog();
            }
        });

        // Making Instance of Object
        user = FirebaseAuth.getInstance().getCurrentUser();
        productNode = FirebaseDatabase.getInstance().getReference("Product");
        productQuery = productNode.orderByChild("adminId").equalTo(user.getUid());
        productQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productArrayList.clear();
                originalProductArrayList.clear();

                for (DataSnapshot productSnapShot : snapshot.getChildren()) {
                    Product product = productSnapShot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(productSnapShot.getKey());
                        productArrayList.add(product);
                        originalProductArrayList.add(product);
                    }
                }

                Log.d("DataLoad", "Loaded " + productArrayList.size() + " products");

             // Reverse the list to show newest first
                Collections.reverse(productArrayList);
                if (productAdapter == null) {
                    productAdapter = new InventoryProductAdapter(inventory_product.this, productArrayList, inventory_product.this);
                    recyclerView.setAdapter(productAdapter);
                    Log.d("AdapterDebug", "New adapter created");
                } else {
                    productAdapter.updateList(productArrayList);
                    Log.d("AdapterDebug", "Existing adapter updated");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(inventory_product.this, "Failed to load products", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSortDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.sort_dialog);

        RadioGroup radioGroup = bottomSheetDialog.findViewById(R.id.radioGroup);

        // Set current selection
        switch (currentSortType) {
            case "newest":
                ((RadioButton) bottomSheetDialog.findViewById(R.id.radioNewestDate)).setChecked(true);
                break;
            case "oldest":
                ((RadioButton) bottomSheetDialog.findViewById(R.id.radioOldestDate)).setChecked(true);
                break;
            case "high_price":
                ((RadioButton) bottomSheetDialog.findViewById(R.id.radioHighPrice)).setChecked(true);
                break;
            case "low_price":
                ((RadioButton) bottomSheetDialog.findViewById(R.id.radioLowPrice)).setChecked(true);
                break;
            case "a_to_z":
                ((RadioButton) bottomSheetDialog.findViewById(R.id.radioNameAToZ)).setChecked(true);
                break;
            case "z_to_a":
                ((RadioButton) bottomSheetDialog.findViewById(R.id.radioNameZToA)).setChecked(true);
                break;
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioNewestDate) {
                    currentSortType = "newest";
                    sortProducts("newest");
                    bottomSheetDialog.dismiss();
                } else if (checkedId == R.id.radioOldestDate) {
                    currentSortType = "oldest";
                    sortProducts("oldest");
                    bottomSheetDialog.dismiss();
                } else if (checkedId == R.id.radioHighPrice) {
                    currentSortType = "high_price";
                    sortProducts("high_price");
                    bottomSheetDialog.dismiss();
                } else if (checkedId == R.id.radioLowPrice) {
                    currentSortType = "low_price";
                    sortProducts("low_price");
                    bottomSheetDialog.dismiss();
                } else if (checkedId == R.id.radioNameAToZ) {
                    currentSortType = "a_to_z";
                    sortProducts("a_to_z");
                    bottomSheetDialog.dismiss();
                } else if (checkedId == R.id.radioNameZToA) {
                    currentSortType = "z_to_a";
                    sortProducts("z_to_a");
                    bottomSheetDialog.dismiss();
                }
            }
        });

        bottomSheetDialog.show();
    }

    private void sortProducts(String sortType) {
        if (originalProductArrayList == null || originalProductArrayList.isEmpty()) {
            Toast.makeText(this, "No products to sort", Toast.LENGTH_SHORT).show();
            return;
        }

        // Always sort from the original list
        ArrayList<Product> sortedList = new ArrayList<>(originalProductArrayList);

        switch (sortType) {
            case "newest":
                Collections.sort(sortedList, new Comparator<Product>() {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    @Override
                    public int compare(Product p1, Product p2) {
                        boolean hasDate1 = p1.getProductCreatedDate() != null && !p1.getProductCreatedDate().isEmpty();
                        boolean hasDate2 = p2.getProductCreatedDate() != null && !p2.getProductCreatedDate().isEmpty();

                        // Both have no date - equal
                        if (!hasDate1 && !hasDate2) return 0;
                        // Only p1 has no date - goes to bottom
                        if (!hasDate1) return 1;
                        // Only p2 has no date - goes to bottom
                        if (!hasDate2) return -1;

                        // Both have dates - compare
                        try {
                            Date date1 = sdf.parse(p1.getProductCreatedDate());
                            Date date2 = sdf.parse(p2.getProductCreatedDate());
                            return date2.compareTo(date1); // Newest first
                        } catch (ParseException e) {
                            return 0;
                        }
                    }
                });
                break;

            case "oldest":
                Collections.sort(sortedList, new Comparator<Product>() {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    @Override
                    public int compare(Product p1, Product p2) {
                        boolean hasDate1 = p1.getProductCreatedDate() != null && !p1.getProductCreatedDate().isEmpty();
                        boolean hasDate2 = p2.getProductCreatedDate() != null && !p2.getProductCreatedDate().isEmpty();

                        // Both have no date - equal
                        if (!hasDate1 && !hasDate2) return 0;
                        // Only p1 has no date - goes to bottom
                        if (!hasDate1) return 1;
                        // Only p2 has no date - goes to bottom
                        if (!hasDate2) return -1;

                        // Both have dates - compare
                        try {
                            Date date1 = sdf.parse(p1.getProductCreatedDate());
                            Date date2 = sdf.parse(p2.getProductCreatedDate());
                            return date1.compareTo(date2); // Oldest first
                        } catch (ParseException e) {
                            return 0;
                        }
                    }
                });
                break;

            case "high_price":
                Collections.sort(sortedList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        try {
                            String price1Str = p1.getSellingPrice() != null ? p1.getSellingPrice().trim() : "0";
                            String price2Str = p2.getSellingPrice() != null ? p2.getSellingPrice().trim() : "0";

                            double price1 = Double.parseDouble(price1Str);
                            double price2 = Double.parseDouble(price2Str);
                            return Double.compare(price2, price1); // High to low
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });
                break;

            case "low_price":
                Collections.sort(sortedList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        try {
                            String price1Str = p1.getSellingPrice() != null ? p1.getSellingPrice().trim() : "0";
                            String price2Str = p2.getSellingPrice() != null ? p2.getSellingPrice().trim() : "0";

                            double price1 = Double.parseDouble(price1Str);
                            double price2 = Double.parseDouble(price2Str);
                            return Double.compare(price1, price2); // Low to high
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    }
                });
                break;

            case "a_to_z":
                Collections.sort(sortedList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        String name1 = p1.getProductName() != null ? p1.getProductName().toLowerCase().trim() : "";
                        String name2 = p2.getProductName() != null ? p2.getProductName().toLowerCase().trim() : "";
                        return name1.compareTo(name2); // A to Z
                    }
                });
                break;

            case "z_to_a":
                Collections.sort(sortedList, new Comparator<Product>() {
                    @Override
                    public int compare(Product p1, Product p2) {
                        String name1 = p1.getProductName() != null ? p1.getProductName().toLowerCase().trim() : "";
                        String name2 = p2.getProductName() != null ? p2.getProductName().toLowerCase().trim() : "";
                        return name2.compareTo(name1); // Z to A
                    }
                });
                break;
        }

        // Update the display list
        productArrayList.clear();
        productArrayList.addAll(sortedList);

        // Update adapter
        if (productAdapter == null) {
            productAdapter = new InventoryProductAdapter(inventory_product.this, productArrayList, inventory_product.this);
            recyclerView.setAdapter(productAdapter);
        } else {
            productAdapter.updateList(productArrayList);
        }

        Log.d("SortDebug", "Sorted " + sortedList.size() + " products by: " + sortType);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void ItemOnClickListener(String productId) {
        Intent i = new Intent(inventory_product.this, edit_product.class);
        i.putExtra("productId", productId);
        startActivity(i);
        finish();
    }
}