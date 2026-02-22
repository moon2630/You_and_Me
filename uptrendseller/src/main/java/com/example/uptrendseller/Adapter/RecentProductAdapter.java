package com.example.uptrendseller.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrendseller.R;
import com.example.uptrendseller.edit_product;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import DataModel.Product;

public class RecentProductAdapter extends RecyclerView.Adapter<RecentProductAdapter.ViewHolder> {

    private List<Product> recentProductList;
    private Context context;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    private OnRecentProductClickListener listener;

    public interface OnRecentProductClickListener {
        void onRecentProductClick(Product product);
    }

    public RecentProductAdapter(List<Product> recentProductList, Context context, OnRecentProductClickListener listener) {
        this.recentProductList = recentProductList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = recentProductList.get(position);

        // Set product name
        holder.txtProductName.setText(product.getProductName());

        // Set price
        if (product.getSellingPrice() != null && !product.getSellingPrice().isEmpty()) {
            holder.txtPrice.setText("₹" + product.getSellingPrice());
        } else {
            holder.txtPrice.setText("₹0");
        }

        // Set category
        String category = product.getProductCategory();
        String subCategory = product.getProductSubCategory();
        if (subCategory != null && !subCategory.isEmpty()) {
            holder.txtCategory.setText(subCategory);
        } else if (category != null && !category.isEmpty()) {
            holder.txtCategory.setText(category);
        } else {
            holder.txtCategory.setText("Uncategorized");
        }

        // Set date
        if (product.getProductCreatedDate() != null && !product.getProductCreatedDate().isEmpty()) {
            holder.txtDate.setText("Added: " + product.getProductCreatedDate());
        } else if (product.getTimestamp() > 0) {
            String date = dateFormat.format(new Date(product.getTimestamp()));
            holder.txtDate.setText("Added: " + date);
        } else {
            holder.txtDate.setText("Date: N/A");
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecentProductClick(product);
            } else {
                // Fallback: open edit_product directly
                Intent intent = new Intent(context, edit_product.class);
                intent.putExtra("productId", product.getProductId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentProductList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtPrice, txtCategory, txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtCategory = itemView.findViewById(R.id.txtCategory);
            txtDate = itemView.findViewById(R.id.txtDate);
        }
    }
}