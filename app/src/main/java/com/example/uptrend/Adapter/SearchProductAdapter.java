package com.example.uptrend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

import DataModel.Product;
import DataModel.Review;

public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.SearchProductViewHolder> {
    private Context context;
    private ArrayList<Product> searchProductArrayList;
    private Onclick onclick;

    public SearchProductAdapter(Context context, ArrayList<Product> searchProductArrayList, Onclick onclick) {
        this.context = context;
        this.searchProductArrayList = searchProductArrayList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public SearchProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_product, parent, false);
        return new SearchProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchProductViewHolder holder, int position) {
        Product product = searchProductArrayList.get(position);

        // Load product image
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ic_launcher_background);
        }

        // Set product details
        holder.txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "N/A");
        holder.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
        holder.txtSellingPrice.setText(product.getSellingPrice() != null ? product.getSellingPrice() : "0");
        holder.txtOriginalPrice.setText(product.getOriginalPrice() != null ? product.getOriginalPrice() : "0");

        // Calculate and display discount
        try {
            double discount = calculateDiscountPercentage(
                    Double.parseDouble(product.getOriginalPrice()),
                    Double.parseDouble(product.getSellingPrice()));
            DecimalFormat df = new DecimalFormat("#.##");
            holder.txtDiscountPer.setText(df.format(discount));
        } catch (NumberFormatException e) {
            holder.txtDiscountPer.setText("0");
        }

        // Fetch and display rating
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review");
        Query reviewQuery = reviewRef.orderByChild("productId").equalTo(product.getProductId());
        reviewQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float totalRating = 0;
                int totalReview = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (review != null && review.getProductStar() != null) {
                        totalReview++;
                        totalRating += Float.parseFloat(review.getProductStar());
                    }
                }
                if (totalReview == 0) {
                    holder.txtRating.setText("0.0");
                } else {
                    DecimalFormat df = new DecimalFormat("#.#");
                    holder.txtRating.setText(df.format(totalRating / totalReview));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.txtRating.setText("0.0");
            }
        });

        // CardView click listener
        holder.cardViewHistoryContainerSearch.setOnClickListener(v -> onclick.ItemOnClickListener(product.getProductId()));
    }

    @Override
    public int getItemCount() {
        return searchProductArrayList.size();
    }

    public static class SearchProductViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtBrandName, txtProductName, txtSellingPrice, txtOriginalPrice, txtDiscountPer, txtRating;
        CardView cardViewHistoryContainerSearch;

        public SearchProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.historyProductImageSearch);
            txtBrandName = itemView.findViewById(R.id.orderBrandNameNO);
            txtProductName = itemView.findViewById(R.id.productNAmeNo);
            txtSellingPrice = itemView.findViewById(R.id.product_selling_price_Search);
            txtOriginalPrice = itemView.findViewById(R.id.product_original_price_Search);
            txtDiscountPer = itemView.findViewById(R.id.txtDiscount01);
            txtRating = itemView.findViewById(R.id.txtTotalRatingSearch);
            cardViewHistoryContainerSearch = itemView.findViewById(R.id.cardViewHistoryContainerSearch);
        }
    }

    public double calculateDiscountPercentage(double originalPrice, double sellingPrice) {
        if (originalPrice == 0) return 0;
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }
}