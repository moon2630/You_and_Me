package com.example.uptrend.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.FullScreenImageActivity;
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

import DataModel.Review;

public class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ImageViewHolder> {
    private final Context context;
    private final List<String> imageUrls;
    private final Review review;

    public ReviewImageAdapter(Context context, List<String> imageUrls, Review review) {
        this.context = context;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
        this.review = review != null ? review : new Review(); // Fallback to empty Review to avoid null crashes
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imageView);
            Log.d("ReviewImageAdapter", "Loading image at position " + position + ": " + imageUrl);
            holder.imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("username", review.getUserName() != null ? review.getUserName() : "Anonymous");
                intent.putExtra("rating", review.getProductStar() != null ? review.getProductStar() : "0");
                intent.putExtra("comment", review.getComment() != null ? review.getComment() : "No comment");
                intent.putExtra("productId", review.getProductId() != null ? review.getProductId() : "");
                Log.d("ReviewImageAdapter", "Opening FullScreenImageActivity with imageUrl: " + imageUrl + ", username: " + review.getUserName());
                context.startActivity(intent);
            });
        } else {
            holder.imageView.setImageResource(R.drawable.ic_launcher_background);
            holder.imageView.setOnClickListener(null);
            Log.w("ReviewImageAdapter", "Empty or null imageUrl at position: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}