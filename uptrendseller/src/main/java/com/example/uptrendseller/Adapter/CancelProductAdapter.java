package com.example.uptrendseller.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrendseller.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.CancelProduct;
import DataModel.Product;
import DataModel.UserAddress;

public class CancelProductAdapter extends RecyclerView.Adapter<CancelProductAdapter.CancelProductViewHolder>{
    private Context context;
    private ArrayList<CancelProduct> cancelProductArrayList;
    private RequestOnClick requestOnClick;

    public CancelProductAdapter(Context context, ArrayList<CancelProduct> cancelProductArrayList, RequestOnClick requestOnClick) {
        this.context = context;
        this.cancelProductArrayList = cancelProductArrayList;
        this.requestOnClick = requestOnClick;
    }

    @NonNull
    @Override
    public CancelProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cancel_and_return,parent,false);
        return new CancelProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CancelProductViewHolder holder, int position) {
        CancelProduct cancelProduct = cancelProductArrayList.get(position);
        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        Query userQuery = userAddressRef.orderByChild("userId").equalTo(cancelProduct.getUserId());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    UserAddress userAddress = userSnapshot.getValue(UserAddress.class);
                    holder.txtUserName.setText("Order cancelled by " + userAddress.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CancelProductAdapter", "Error fetching user address: " + error.getMessage());
            }
        });

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cancelProduct.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        holder.txtProductName.setText(product.getProductName());
                        Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);

                        int qty = Integer.parseInt(cancelProduct.getProductQty());
                        long price = Long.parseLong(product.getSellingPrice());
                        long totalPrice = qty * price;
                        holder.txtPrice.setText("Payment ₹" + totalPrice);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CancelProductAdapter", "Error fetching product: " + error.getMessage());
            }
        });

        holder.txtQty.setText("Qty : " + cancelProduct.getProductQty());
        holder.txtDate.setText(cancelProduct.getCancelDate());
        holder.txtStatus.setText("Status : Cancel");

        TextView productPaymentSuccessful = holder.itemView.findViewById(R.id.product_payment_successful);
        String paymentStatus = cancelProduct.getPaymentStatus() != null ? cancelProduct.getPaymentStatus() : "pending";
        if (paymentStatus.equals("completed")) {
            productPaymentSuccessful.setVisibility(View.VISIBLE);
        } else {
            productPaymentSuccessful.setVisibility(View.GONE);
        }

        holder.cardViewContainerCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestOnClick.RequestOnClickListener(
                        cancelProduct.getNodeId(),
                        cancelProduct.getProductId(),
                        cancelProduct.getUserId(),
                        cancelProduct.getProductQty(),
                        cancelProduct.getProductSize()
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return cancelProductArrayList.size();
    }

    public class CancelProductViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView productImage;
        CardView cardViewContainerCancel;
        TextView txtProductName, txtQty, txtPrice, txtUserName, txtStatus, txtDate;

        public CancelProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_CR);
            txtProductName = itemView.findViewById(R.id.product_name_CR);
            txtQty = itemView.findViewById(R.id.product_qty_CR);
            txtPrice = itemView.findViewById(R.id.productTotalPriceCR);
            txtUserName = itemView.findViewById(R.id.txtUserNameCR);
            txtStatus = itemView.findViewById(R.id.productStatusCR);
            txtDate = itemView.findViewById(R.id.orderDateCR);
            cardViewContainerCancel = itemView.findViewById(R.id.cardViewContainerCancel);
        }
    }
}