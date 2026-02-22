package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.uptrend.Adapter.WishListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.LikeProduct;

public class wishlist_poduct extends AppCompatActivity {
    RecyclerView recyclerViewLikeProduct;
    GridLayoutManager gridLayoutManager;
    ArrayList<LikeProduct> likeProductArrayList;
    DatabaseReference likeProductRef;
    Query userQuery;
    FirebaseUser firebaseUser;
    WishListAdapter wishListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist_poduct);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }



        recyclerViewLikeProduct=findViewById(R.id.recyclerLikeProduct);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        displayProduct();


        TextView close_btn_wishlist = findViewById(R.id.close_btn_wishlist);
        close_btn_wishlist.setOnClickListener(v -> {
            Intent intent = new Intent(wishlist_poduct.this, account_user.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });


    }

    public void displayProduct(){
        likeProductArrayList=new ArrayList<>();
        likeProductRef= FirebaseDatabase.getInstance().getReference("WishListProduct");
        userQuery=likeProductRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                likeProductArrayList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    LikeProduct likeProduct=dataSnapshot.getValue(LikeProduct.class);
                    likeProduct.setNodeId(dataSnapshot.getKey());
                    likeProductArrayList.add(likeProduct);
                }
                gridLayoutManager=new GridLayoutManager(wishlist_poduct.this,2, LinearLayoutManager.VERTICAL,false);
                recyclerViewLikeProduct.setLayoutManager(gridLayoutManager);
                wishListAdapter=new WishListAdapter(wishlist_poduct.this,likeProductArrayList);
                recyclerViewLikeProduct.setAdapter(wishListAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), account_user.class);
        startActivity(intent);
        finish();
    }
}