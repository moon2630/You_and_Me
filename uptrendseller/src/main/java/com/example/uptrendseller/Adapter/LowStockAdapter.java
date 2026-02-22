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

import java.util.List;

import DataModel.Product;

public class LowStockAdapter extends RecyclerView.Adapter<LowStockAdapter.ViewHolder> {

    private List<Product> lowStockList;
    private Context context;
    private OnLowStockClickListener listener;

    public interface OnLowStockClickListener {
        void onLowStockItemClick(Product product);
    }

    public LowStockAdapter(List<Product> lowStockList, Context context, OnLowStockClickListener listener) {
        this.lowStockList = lowStockList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_low_stock, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = lowStockList.get(position);

        // Set product name
        holder.txtProductName.setText(product.getProductName());

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

        // Set stock information
        String stockInfo = getStockInfo(product);
        holder.txtStock.setText(stockInfo);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onLowStockItemClick(product);
            } else {
                // Fallback: open edit_product directly
                Intent intent = new Intent(context, edit_product.class);
                intent.putExtra("productId", product.getProductId());
                context.startActivity(intent);
            }
        });
    }

    private String getStockInfo(Product product) {
        StringBuilder stockInfo = new StringBuilder();

        if (product.getTotalStock() != null && !product.getTotalStock().isEmpty()) {
            try {
                int totalStock = Integer.parseInt(product.getTotalStock());

                // For products with sizes
                if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                    int lowSizeCount = 0;
                    for (String sizeQty : product.getProductSizes()) {
                        try {
                            int quantity = Integer.parseInt(sizeQty);
                            if (quantity <= 3 && quantity > 0) {
                                lowSizeCount++;
                            }
                        } catch (NumberFormatException e) {
                            // Skip invalid size values
                        }
                    }

                    if (lowSizeCount > 0) {
                        stockInfo.append("⚠ ").append(lowSizeCount).append(" size(s) low");
                    } else if (totalStock <= 10) {
                        stockInfo.append("Stock: ").append(totalStock).append(" units");
                    }
                } else {
                    // For products without sizes
                    if (totalStock <= 3) {
                        stockInfo.append("⚠ Critical: ").append(totalStock).append(" left");
                    } else if (totalStock <= 10) {
                        stockInfo.append("⚠ Low: ").append(totalStock).append(" left");
                    }
                }

            } catch (NumberFormatException e) {
                stockInfo.append("Stock: N/A");
            }
        }

        return stockInfo.toString().isEmpty() ? "Stock: N/A" : stockInfo.toString();
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