package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.uptrendseller.Adapter.OrderRequestAdapter;
import com.example.uptrendseller.Adapter.RequestOnClick;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Order;

public class order_Details extends AppCompatActivity implements RequestOnClick {
    private RecyclerView recyclerViewRequestProduct;
    OrderRequestAdapter orderRequestAdapter;
    private DatabaseReference orderRef;
    private ArrayList<Order> orderArrayList;
    private Order order;
    private FirebaseUser firebaseUser;
    TextView closeBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        closeBtn=findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),dashboard_admin.class));
                finish();
            }
        });

        recyclerViewRequestProduct = findViewById(R.id.requestProductRecyclerView);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        displayRequestProduct(firebaseUser.getUid());


    }

    public void displayRequestProduct(String adminId) {
        orderArrayList=new ArrayList<>();
        orderRef = FirebaseDatabase.getInstance().getReference("Order");
        Query adminQuery = orderRef.orderByChild("sellerId").equalTo(adminId);
        adminQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                orderArrayList.clear();
                for(DataSnapshot productSnapShot:snapshot.getChildren()){
                    order=productSnapShot.getValue(Order.class);
                        order.setNodeId(productSnapShot.getKey());
                        orderArrayList.add(order);
                }
                if (orderArrayList != null) {
                    orderRequestAdapter = new OrderRequestAdapter(order_Details.this, orderArrayList,order_Details
                            .this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(order_Details.this, LinearLayoutManager.VERTICAL, false);
                    recyclerViewRequestProduct.setLayoutManager(linearLayoutManager);
                    recyclerViewRequestProduct.setAdapter(orderRequestAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), dashboard_admin.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void RequestOnClickListener(String nodeId, String productId, String userId, String qty, String size) {
        Intent i=new Intent(order_Details.this,open_request_PD.class);
        i.putExtra("nodeId",nodeId);
        i.putExtra("productId",productId);
        i.putExtra("userId",userId);
        i.putExtra("qty",qty);
        i.putExtra("size",size);
        startActivity(i);
        finish();
    }


}