package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.uptrend.Adapter.AllProductAdapter;
import com.example.uptrend.Adapter.CategoryAdapter;
import com.example.uptrend.Adapter.Onclick;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Product;

public class open_category_product extends AppCompatActivity implements Onclick {

    private String sortBy, value;
    private RecyclerView recyclerViewCategory;
    private ArrayList<Product> productArrayList;
    private DatabaseReference productRef;
    private Query categoryQuery, subCategoryQuery;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_category_product);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        //FindViewById
        recyclerViewCategory = findViewById(R.id.productCategory);





        sortBy = getIntent().getStringExtra("sortBy");
        value = getIntent().getStringExtra("value");

        TextView txtCategoryName = findViewById(R.id.get_categories_name);
        txtCategoryName.setText((value != null ? getDisplayCategoryName(value) : "Categories") + " categories");


        TextView close_btn_Cat = findViewById(R.id.close_btn_Cat);
        close_btn_Cat.setOnClickListener(v -> {
            Intent intent = new Intent(open_category_product.this, category_product.class);
            intent.putExtra("activityName", "openCategoryProduct");
            intent.putExtra("sortBy", sortBy);
            intent.putExtra("value", value);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });





        if (sortBy.equals("Category")) {
            displayAccordingCategory(value);
        } else if (sortBy.equals("SubCategory")) {
            displayAccordingSubCategory(value);
        }
    }

    public void displayAccordingCategory(String value) {
        productArrayList = new ArrayList<>();
        productArrayList.clear();
        productRef = FirebaseDatabase.getInstance().getReference("Product");
        categoryQuery = productRef.orderByChild("productCategory").startAt(value).endAt(value + "\uf8ff");
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Product product = dataSnapshot.getValue(Product.class);
                    if (product != null) {
                        productArrayList.add(product);
                    }
                }
                GridLayoutManager gridLayoutManager = new GridLayoutManager(open_category_product.this, 2, GridLayoutManager.VERTICAL, false);
                categoryAdapter = new CategoryAdapter(open_category_product.this, productArrayList, open_category_product.this);
                recyclerViewCategory.setLayoutManager(gridLayoutManager);
                recyclerViewCategory.setAdapter(categoryAdapter);



            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void displayAccordingSubCategory(String value) {
            productArrayList = new ArrayList<>();
            productArrayList.clear();
            productRef = FirebaseDatabase.getInstance().getReference("Product");
            categoryQuery = productRef.orderByChild("productSubCategory").startAt(value).endAt(value + "\uf8ff");
            categoryQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Product product = dataSnapshot.getValue(Product.class);
                        if (product != null) {
                            productArrayList.add(product);
                        }
                    }
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(open_category_product.this, 2, GridLayoutManager.VERTICAL, false);
                    categoryAdapter = new CategoryAdapter(open_category_product.this, productArrayList, open_category_product.this);
                    recyclerViewCategory.setLayoutManager(gridLayoutManager);
                    recyclerViewCategory.setAdapter(categoryAdapter);



                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    @Override
    public void ItemOnClickListener(String productId) {
        Intent i = new Intent(open_category_product.this, open_product.class);
        i.putExtra("productId", productId);
        i.putExtra("activityName", "openCategoryProduct");
        i.putExtra("sortBy", sortBy);
        i.putExtra("value", value);
        startActivity(i);
    }

    private String getDisplayCategoryName(String value) {
        switch (value) {
            case "Footware": return "Shoes";
            case "Smartphones": return "Mobiles";
            case "Sweaters and Hoodies": return "Hoodies";
            case "Jewellery": return "Jewellery";
            case "Shirts": return "Shirts";
            case "Chocolate": return "Chocolates";
            case "Jeans": return "Jeans";
            case "Teddy Bear": return "Teddy Bears";
            case "Beauty": return "Beauty";
            case "Watches": return "Watches";
            case "Sports": return "Sports";
            case "Saree": return "Sarees";
            case "EyeWear": return "Glasses";
            case "Dresses": return "Dresses";
            case "Art": return "Arts";
            default: return value;
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(open_category_product.this, category_product.class);
        intent.putExtra("activityName", "openCategoryProduct");
        intent.putExtra("sortBy", sortBy);
        intent.putExtra("value", value);
        startActivity(intent);
        finish();
    }

}