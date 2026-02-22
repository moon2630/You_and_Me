package com.example.uptrend.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.example.uptrend.open_product;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.CancelProduct;
import DataModel.Product;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{
    private Context context;
    private ArrayList<CancelProduct> cancelProductArrayList;

    public NotificationAdapter(Context context, ArrayList<CancelProduct> cancelProductArrayList) {
        this.context = context;
        this.cancelProductArrayList = cancelProductArrayList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification,parent,false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cancelProductArrayList.get(position).getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImageView);
                        holder.txtProductName.setText(product.getProductName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
        holder.txtProductPrice.setText(cancelProductArrayList.get(position).getProductSellingPrice());
        holder.txtOrderDate.setText(cancelProductArrayList.get(position).getCancelDate());
        holder.txtTime.setText(cancelProductArrayList.get(position).getCancelTime());
        String paymentStatus = cancelProductArrayList.get(position).getPaymentStatus() != null ? cancelProductArrayList.get(position).getPaymentStatus() : "pending";
        if (paymentStatus.equals("pending")) {
            holder.txtBlueCancelText.setVisibility(View.VISIBLE);
            holder.txtGreenCancelText.setVisibility(View.GONE);
        } else {
            holder.txtBlueCancelText.setVisibility(View.GONE);
            holder.txtGreenCancelText.setVisibility(View.VISIBLE);
        }

        holder.itemView.findViewById(R.id.cardViewCancel).setOnClickListener(v -> {
            Intent intent = new Intent(context, open_product.class);
            intent.putExtra("productId", cancelProductArrayList.get(position).getProductId());
            intent.putExtra("activityName", "notification");
            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return cancelProductArrayList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImageView;
        TextView txtProductName, txtOrderDate, txtProductPrice, txtTime, txtBlueCancelText, txtGreenCancelText;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            productImageView = itemView.findViewById(R.id.historyProductImageNO);
            txtProductName = itemView.findViewById(R.id.productNAme);
            txtOrderDate = itemView.findViewById(R.id.orderDateNO);
            txtTime = itemView.findViewById(R.id.ordertimeNO);
            txtProductPrice = itemView.findViewById(R.id.productPriceNotification);
            txtBlueCancelText = itemView.findViewById(R.id.blue_cancel_text);
            txtGreenCancelText = itemView.findViewById(R.id.green_cancel_text);
        }
    }
}
