package com.example.uptrendseller.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrendseller.R;

import java.util.List;

import DataModel.Product;

public class ProductPerformanceAdapter extends RecyclerView.Adapter<ProductPerformanceAdapter.ViewHolder> {

    private List<Product> productList;
    private Context context;

    public ProductPerformanceAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public void updateList(List<Product> newList) {
        this.productList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_performance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Set product name
        holder.txtProductName.setText(product.getProductName());

        // Set sales count
        int salesCount = product.getSalesCount();
        holder.txtSalesCount.setText("Sold: " + salesCount);

        // Set stock information
        if (product.getTotalStock() != null && !product.getTotalStock().isEmpty()) {
            try {
                int stock = Integer.parseInt(product.getTotalStock());
                holder.txtStock.setText("Stock: " + stock);
            } catch (NumberFormatException e) {
                holder.txtStock.setText("Stock: N/A");
            }
        } else {
            holder.txtStock.setText("Stock: N/A");
        }

        // Set category
        if (product.getProductCategory() != null && !product.getProductCategory().isEmpty()) {
            holder.txtCategory.setText(product.getProductCategory());
        } else {
            holder.txtCategory.setText("General");
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtSalesCount, txtStock, txtCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtSalesCount = itemView.findViewById(R.id.txtSalesCount);
            txtStock = itemView.findViewById(R.id.txtStock);
            txtCategory = itemView.findViewById(R.id.txtCategory);
        }
    }
}