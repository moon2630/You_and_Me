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
import com.theophrast.ui.widget.SquareImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import DataModel.Product;
import DataModel.Review;

public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.ProductViewHolder> {
    private Context context;
    private ArrayList<Product> productArrayList;
    private Onclick onclick;

    public AllProductAdapter(Context context, ArrayList<Product> productArrayList, Onclick onclick) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.raw_product_rv, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);
        Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
        holder.txtProductName.setText(product.getProductName());
        holder.txtProductSellingPrice.setText(product.getSellingPrice());
        holder.txtProductOriginalPrice.setText(product.getOriginalPrice());
        holder.txtBrandName.setText(product.getProductBrandName());
        Double discount = calculateDiscountPercentage(Double.parseDouble(product.getOriginalPrice()),
                Double.parseDouble(product.getSellingPrice()));
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedDiscountPercentage = df.format(discount);
        holder.txtDiscountPer.setText(formattedDiscountPercentage);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.ItemOnClickListener(product.getProductId());
            }
        });

        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review");
        Query reviewQuery = reviewRef.orderByChild("productId").equalTo(product.getProductId());
        reviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                float total = 0;
                int totalReview = (int) snapshot.getChildrenCount();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (review != null && review.getProductStar() != null) {
                        try {
                            total += Float.parseFloat(review.getProductStar());
                        } catch (NumberFormatException e) {
                            // Skip invalid rating values
                        }
                    }
                }
                if (totalReview == 0 || Float.isNaN(total / totalReview)) {
                    holder.txtTotalRating.setText("0.0");
                } else {
                    DecimalFormat ratingFormat = new DecimalFormat("#.#");
                    holder.txtTotalRating.setText(ratingFormat.format(total / totalReview));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.txtTotalRating.setText("0.0");
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtProductName, txtProductSellingPrice, txtBrandName, txtProductOriginalPrice, txtDiscountPer, txtTotalRating;
        CardView cardView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            txtProductName = itemView.findViewById(R.id.productName);
            txtProductSellingPrice = itemView.findViewById(R.id.productSellingPrice);
            cardView = itemView.findViewById(R.id.cardView);
            txtBrandName = itemView.findViewById(R.id.brandName);
            txtProductOriginalPrice = itemView.findViewById(R.id.text_product_original_price);
            txtDiscountPer = itemView.findViewById(R.id.txtDiscount01);
            txtTotalRating = itemView.findViewById(R.id.txtTotalRating);
        }
    }

    public double calculateDiscountPercentage(double originalPrice, double sellingPrice) {
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }
}