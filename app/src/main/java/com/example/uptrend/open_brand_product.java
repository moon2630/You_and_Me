package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.uptrend.Adapter.BrandAdapter;
import com.example.uptrend.Adapter.Onclick;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Product;

public class open_brand_product extends AppCompatActivity implements Onclick {
   private String brandName;
   private RecyclerView recyclerViewBrand;
   private TextView txtBrandName;
   private ArrayList<Product> productArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_brand_product);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }




        brandName=getIntent().getStringExtra("brandName");
        recyclerViewBrand=findViewById(R.id.productBrand);
        txtBrandName=findViewById(R.id.Brand_name);


        txtBrandName.setText(brandName + " products");
        displayProduct(brandName);




        TextView close_btn_Brand = findViewById(R.id.close_btn_Brand);
        close_btn_Brand.setOnClickListener(v -> {
            Intent intent = new Intent(open_brand_product.this, home.class);
            intent.putExtra("activityName", "openBrandProduct");
            intent.putExtra("brandName", brandName);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });


    }

    public void displayProduct(String brandName){
        productArrayList=new ArrayList<>();
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference("Product");
        Query brandQuery=productRef.orderByChild("productBrandName").equalTo(brandName);
        brandQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Product product=dataSnapshot.getValue(Product.class);
                    if(product!=null){
                        productArrayList.add(product);
                    }
                }
                GridLayoutManager gridLayoutManager=new GridLayoutManager(open_brand_product.this,2, GridLayoutManager.VERTICAL, false);
                BrandAdapter brandAdapter=new BrandAdapter(open_brand_product.this,productArrayList,open_brand_product.this);
                recyclerViewBrand.setLayoutManager(gridLayoutManager);
                recyclerViewBrand.setAdapter(brandAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void ItemOnClickListener(String productId) {
        Intent i = new Intent(open_brand_product.this, open_product.class);
        i.putExtra("productId", productId);
        i.putExtra("activityName", "openBrandProduct");
        i.putExtra("brandName", brandName);
        startActivity(i);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(open_brand_product.this, home.class);
        intent.putExtra("activityName", "openBrandProduct");
        intent.putExtra("brandName", brandName);
        startActivity(intent);
        finish();
    }
}