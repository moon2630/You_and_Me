package com.example.uptrenddelivery;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrenddelivery.Adapter.NewOrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Order;

public class new_orders extends AppCompatActivity {

    RecyclerView rvNewOrders;
    TextView tvNoNewOrders;
    ProgressBar progressBar;
    NewOrderAdapter adapter;
    ArrayList<Order> orderList;
    DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_orders);

        rvNewOrders = findViewById(R.id.rvNewOrders);
        tvNoNewOrders = findViewById(R.id.tvNoNewOrders);
        progressBar = findViewById(R.id.progressBar);

        orderList = new ArrayList<>();
        adapter = new NewOrderAdapter(this, orderList);
        rvNewOrders.setLayoutManager(new LinearLayoutManager(this));
        rvNewOrders.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");

    }
}