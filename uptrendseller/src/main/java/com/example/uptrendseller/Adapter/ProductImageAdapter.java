package com.example.uptrendseller.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.uptrendseller.R;

import java.util.ArrayList;
import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ProductImageViewHolder> {

    private List<Uri> uriList;
    private Context context;
    private boolean isForViewPager = false;



    // Constructor for ViewPager2 (with ViewPager2)
    public ProductImageAdapter(List<Uri> uriList, ViewPager2 viewPager) {
        this.uriList = uriList != null ? uriList : new ArrayList<>();
        this.context = viewPager.getContext();
        this.isForViewPager = true;
    }

    // Fallback constructor for backwards compatibility
    @NonNull
    @Override
    public ProductImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_image, parent, false);
        return new ProductImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductImageViewHolder holder, int position) {
        Uri imageUri = uriList.get(position);

        if (imageUri != null) {
            Glide.with(context)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
        }
    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public void updateData(List<Uri> newUriList) {
        if (newUriList != null) {
            this.uriList = newUriList;
            notifyDataSetChanged();
        }
    }

    public static class ProductImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ProductImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}