package com.example.uptrendseller.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrendseller.R;

import java.util.List;

import DataModel.Product;

public class LowStockAlertAdapter extends RecyclerView.Adapter<LowStockAlertAdapter.ViewHolder> {

    private List<Product> lowStockList;
    private Context context;

    public LowStockAlertAdapter(List<Product> lowStockList, Context context) {
        this.lowStockList = lowStockList;
        this.context = context;
    }

    public void updateList(List<Product> newList) {
        this.lowStockList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_low_stock_alert, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = lowStockList.get(position);

        // Set product name
        holder.txtProductName.setText(product.getProductName());

        // Set stock information with color coding
        if (product.getTotalStock() != null && !product.getTotalStock().isEmpty()) {
            try {
                int stock = Integer.parseInt(product.getTotalStock());

                if (stock <= 3) {
                    holder.txtStock.setText("CRITICAL: " + stock + " left");
                    holder.txtStock.setTextColor(Color.RED);
                } else if (stock <= 10) {
                    holder.txtStock.setText("LOW: " + stock + " left");
                    holder.txtStock.setTextColor(Color.parseColor("#FF9800")); // Orange
                } else {
                    holder.txtStock.setText("Stock: " + stock);
                    holder.txtStock.setTextColor(Color.parseColor("#4CAF50")); // Green
                }
            } catch (NumberFormatException e) {
                holder.txtStock.setText("Stock: N/A");
                holder.txtStock.setTextColor(Color.GRAY);
            }
        } else {
            holder.txtStock.setText("Stock: N/A");
            holder.txtStock.setTextColor(Color.GRAY);
        }

        // Set category
        String category = product.getProductCategory();
        String subCategory = product.getProductSubCategory();

        if (subCategory != null && !subCategory.isEmpty()) {
            holder.txtCategory.setText(subCategory);
        } else if (category != null && !category.isEmpty()) {
            holder.txtCategory.setText(category);
        } else {
            holder.txtCategory.setText("General");
        }
    }

    @Override
    public int getItemCount() {
        return lowStockList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtStock, txtCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtStock = itemView.findViewById(R.id.txtStock);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}