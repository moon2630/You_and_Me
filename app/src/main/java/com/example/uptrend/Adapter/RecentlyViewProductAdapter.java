package com.example.uptrend.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uptrend.R;
import com.example.uptrend.open_product;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Product;
import DataModel.RecentlyViewProduct;

public class RecentlyViewProductAdapter extends RecyclerView.Adapter<RecentlyViewProductAdapter.RecentlyViewProductViewHolder> {
    private Context context;
    private ArrayList<RecentlyViewProduct> recentlyViewProductArrayList;
    private Onclick onclick;
    private String activityName; // Add activityName field

    public RecentlyViewProductAdapter(Context context, ArrayList<RecentlyViewProduct> recentlyViewProductArrayList, Onclick onclick, String activityName) {
        this.context = context;
        this.recentlyViewProductArrayList = recentlyViewProductArrayList;
        this.onclick = onclick;
        this.activityName = activityName; // Initialize activityName
    }

    @NonNull
    @Override
    public RecentlyViewProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recently_view_product_rv, parent, false);
        return new RecentlyViewProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentlyViewProductViewHolder holder, int position) {
        String productId = recentlyViewProductArrayList.get(position).getProductId();
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
                    holder.txtProductName.setText(product.getProductName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        holder.productImage.setImageResource(R.drawable.ic_launcher_background);

        loadProductImageFast(productId, holder);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, open_product.class);
            intent.putExtra("productId", productId);
            intent.putExtra("activityName", activityName);
            context.startActivity(intent);
        });
    }

    // Add this method for faster image loading
    private void loadProductImageFast(String productId, RecentlyViewProductViewHolder holder) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);

        // Use minimal listener for faster response
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null && product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                        // Load image with optimized settings for speed
                        Glide.with(context)
                                .load(product.getProductImages().get(0))
                                .placeholder(R.drawable.ic_launcher_background)
                                .error(R.drawable.ic_launcher_background)
                                .thumbnail(0.1f) // Load thumbnail first
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC) // Cache for faster loading
                                .priority(Priority.HIGH) // High priority for fast loading
                                .into(holder.productImage);

                        // Set product name
                        holder.txtProductName.setText(product.getProductName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Keep default image on error
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentlyViewProductArrayList.size();
    }

    public class RecentlyViewProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName;
        ShapeableImageView productImage;
        LinearLayout productContainer;

        public RecentlyViewProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.productName);
            productImage = itemView.findViewById(R.id.productImage);
            productContainer = itemView.findViewById(R.id.productContainer);
        }
    }
}