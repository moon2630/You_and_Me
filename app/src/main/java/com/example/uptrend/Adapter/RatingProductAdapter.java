package com.example.uptrend.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.example.uptrend.open_product;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import DataModel.Order;
import DataModel.Product;
import DataModel.Review;
import DataModel.User;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;

public class RatingProductAdapter extends RecyclerView.Adapter<RatingProductAdapter.RatingProductViewHolder> {
    private Context context;
    private ArrayList<Order> orderArrayList;
    private ArrayList<Uri> selectedImages = new ArrayList<>();
    private static final int IMAGE_PICK_REQUEST = 100;
    private ImageView currentImageView;

    public RatingProductAdapter(Context context, ArrayList<Order> orderArrayList) {
        this.context = context;
        this.orderArrayList = orderArrayList;
    }

    @NonNull
    @Override
    public RatingProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.reviews_product, parent, false);
        return new RatingProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingProductViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        selectedImages.clear(); // Clear previous selections for this position

        // Load product details
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(order.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        holder.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "Unknown Product");
                        holder.txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "Unknown Brand");
                        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                            Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
                        } else {
                            holder.productImage.setImageResource(android.R.drawable.ic_menu_gallery);
                        }
                    }
                } else {
                    Log.w("RatingProductAdapter", "Product not found for productId: " + order.getProductId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(context, "Error loading product details: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("RatingProductAdapter", "Product fetch error: " + error.getMessage());
            }
        });

        // Load existing review if any
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review");
        Query query = reviewRef.orderByChild("userId").equalTo(order.getUserId());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean foundReview = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review dataReview = dataSnapshot.getValue(Review.class);
                    if (dataReview != null && dataReview.getProductId().equals(order.getProductId())) {
                        foundReview = true;
                        holder.ratingBar.setRating(dataReview.getProductStar() != null ? Float.parseFloat(dataReview.getProductStar()) : 0);
                        holder.ratingBar.setEnabled(false); // Disable RatingBar for existing review
                        // Load existing images if any
                        if (dataReview.getReviewImages() != null) {
                            List<String> images = dataReview.getReviewImages();
                            if (images.size() > 0) Glide.with(context).load(images.get(0)).into(holder.img1);
                            if (images.size() > 1) Glide.with(context).load(images.get(1)).into(holder.img2);
                            if (images.size() > 2) Glide.with(context).load(images.get(2)).into(holder.img3);
                        }
                        // Load existing comment if any
                        if (dataReview.getComment() != null) {
                            holder.reviewComment.setText(dataReview.getComment());
                            holder.reviewComment.setEnabled(false); // Disable editing if review exists
                        }
                        holder.img1.setEnabled(false);
                        holder.img2.setEnabled(false);
                        holder.img3.setEnabled(false);
                        holder.btnSubmit.setVisibility(View.GONE); // Hide submit button for existing review
                    }
                }
                if (!foundReview) {
                    holder.ratingBar.setRating(0);
                    holder.ratingBar.setEnabled(true); // Enable RatingBar for new review
                    holder.img1.setImageResource(R.drawable.vector_add_image);
                    holder.img2.setImageResource(R.drawable.vector_add_image);
                    holder.img3.setImageResource(R.drawable.vector_add_image);
                    holder.reviewComment.setText("");
                    holder.reviewComment.setEnabled(true);
                    holder.img1.setEnabled(true);
                    holder.img2.setEnabled(true);
                    holder.img3.setEnabled(true);
                    holder.btnSubmit.setText("Submit Rating");
                    holder.btnSubmit.setVisibility(View.VISIBLE); // Show submit button for new review
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(context, "Error loading review: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("RatingProductAdapter", "Review fetch error: " + error.getMessage());
            }
        });

        // Image selection listeners
        holder.img1.setOnClickListener(v -> {
            if (holder.reviewComment.isEnabled()) {
                currentImageView = holder.img1;
                openImagePicker();
            }
        });
        holder.img2.setOnClickListener(v -> {
            if (holder.reviewComment.isEnabled()) {
                currentImageView = holder.img2;
                openImagePicker();
            }
        });
        holder.img3.setOnClickListener(v -> {
            if (holder.reviewComment.isEnabled()) {
                currentImageView = holder.img3;
                openImagePicker();
            }
        });

        // Submit button listener
        // Inside onBindViewHolder, replace the btnSubmit.setOnClickListener with:
        holder.btnSubmit.setOnClickListener(v -> {
            if (holder.ratingBar.getRating() == 0) {
                StyleableToast.makeText(context, "Please select a rating", R.style.UptrendToast).show();
                return;
            }
            if (selectedImages.isEmpty()) {
                StyleableToast.makeText(context, "Please select at least one image", R.style.UptrendToast).show();
                return;
            }
            String comment = holder.reviewComment.getText().toString().trim();
            if (comment.isEmpty()) {
                StyleableToast.makeText(context, "Please enter a comment", R.style.UptrendToast).show();
                return;
            }
            if (comment.length() > 500) {
                StyleableToast.makeText(context, "Comment cannot exceed 500 characters", R.style.UptrendToast).show();
                return;
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
            if (userId == null) {
                StyleableToast.makeText(context, "Please log in to submit a review", R.style.UptrendToast).show();
                return;
            }

            // Show ProgressBar
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.btnSubmit.setEnabled(false); // Disable button to prevent multiple clicks

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    final String finalUsername;
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        finalUsername = (user != null && user.getUserName() != null && !user.getUserName().isEmpty())
                                ? user.getUserName() : "Anonymous";
                        Log.d("RatingProductAdapter", "Fetched username from User: " + finalUsername + " for userId: " + userId);
                    } else {
                        Log.w("RatingProductAdapter", "User snapshot does not exist for userId: " + userId);
                        finalUsername = "Anonymous";
                    }

                    if (finalUsername.equals("Anonymous")) {
                        DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
                        Query addressQuery = addressRef.orderByChild("userId").equalTo(userId);
                        addressQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot addressSnapshot) {
                                String addressUsername = finalUsername;
                                if (addressSnapshot.exists()) {
                                    for (DataSnapshot addrSnapshot : addressSnapshot.getChildren()) {
                                        UserAddress address = addrSnapshot.getValue(UserAddress.class);
                                        if (address != null && address.getFullName() != null && !address.getFullName().isEmpty()) {
                                            addressUsername = address.getFullName();
                                            Log.d("RatingProductAdapter", "Fetched fullName from UserAddress: " + addressUsername + " for userId: " + userId);
                                            break;
                                        }
                                    }
                                } else {
                                    Log.w("RatingProductAdapter", "No UserAddress found for userId: " + userId);
                                }
                                saveOrUpdateReview(userId, order.getProductId(), holder.ratingBar.getRating(), comment, selectedImages, addressUsername, holder);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                holder.progressBar.setVisibility(View.GONE);
                                holder.btnSubmit.setEnabled(true);
                                Log.e("RatingProductAdapter", "Error fetching UserAddress for userId: " + userId + ", error: " + error.getMessage());
                                saveOrUpdateReview(userId, order.getProductId(), holder.ratingBar.getRating(), comment, selectedImages, finalUsername, holder);
                            }
                        });
                    } else {
                        saveOrUpdateReview(userId, order.getProductId(), holder.ratingBar.getRating(), comment, selectedImages, finalUsername, holder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    holder.progressBar.setVisibility(View.GONE);
                    holder.btnSubmit.setEnabled(true);
                    Log.e("RatingProductAdapter", "Error fetching username for userId: " + userId + ", error: " + error.getMessage());
                    saveOrUpdateReview(userId, order.getProductId(), holder.ratingBar.getRating(), comment, selectedImages, "Anonymous", holder);
                }
            });
        });
        // Card click to open product
        holder.itemView.findViewById(R.id.cardViewReview).setOnClickListener(v -> {
            Intent intent = new Intent(context, open_product.class);
            intent.putExtra("productId", order.getProductId());
            intent.putExtra("activityName", "ratingProducts");
            context.startActivity(intent);
        });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(intent, IMAGE_PICK_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            if (selectedImages.size() < 3 && !selectedImages.contains(imageUri)) {
                selectedImages.add(imageUri);
                Glide.with(context).load(imageUri).into(currentImageView);
            } else if (selectedImages.size() >= 3) {
                StyleableToast.makeText(context, "Maximum 3 images allowed", R.style.UptrendToast).show();
            }
        }
    }

    private void uploadImagesAndSaveReview(DatabaseReference reviewRef, Review review, ArrayList<Uri> imageUris, RatingProductViewHolder holder) {
        ArrayList<String> imageUrls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("ReviewImages");

        for (Uri uri : imageUris) {
            StorageReference imageRef = storageRef.child("review_" + UUID.randomUUID().toString());
            imageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(url -> {
                    imageUrls.add(url.toString());
                    if (imageUrls.size() == imageUris.size()) {
                        review.setReviewImages(imageUrls);
                        reviewRef.setValue(review).addOnCompleteListener(task -> {
                            holder.progressBar.setVisibility(View.GONE); // Hide ProgressBar
                            holder.btnSubmit.setEnabled(true); // Re-enable button
                            if (task.isSuccessful()) {
                                // Log review data to console
                                Log.d("RatingProductAdapter", "Review submitted: " +
                                        "userId=" + review.getUserId() +
                                        ", productId=" + review.getProductId() +
                                        ", rating=" + review.getProductStar() +
                                        ", comment=" + review.getComment() +
                                        ", imageUrls=" + imageUrls +
                                        ", username=" + review.getUserName());
                                StyleableToast.makeText(context, "Review submitted successfully", R.style.UptrendToast).show();
                                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                                    holder.btnSubmit.setVisibility(View.GONE);
                                    holder.ratingBar.setEnabled(false);
                                    holder.reviewComment.setEnabled(false);
                                    holder.img1.setEnabled(false);
                                    holder.img2.setEnabled(false);
                                    holder.img3.setEnabled(false);
                                }
                            } else {
                                StyleableToast.makeText(context, "Failed to submit review", R.style.UptrendToast).show();
                                Log.e("RatingProductAdapter", "Failed to submit review: " + task.getException().getMessage());
                            }
                        });
                    }
                });
            }).addOnFailureListener(e -> {
                holder.progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                holder.btnSubmit.setEnabled(true); // Re-enable button
                StyleableToast.makeText(context, "Failed to upload image", R.style.UptrendToast).show();
                Log.e("RatingProductAdapter", "Image upload error: " + e.getMessage());
            });
        }
    }

    private void uploadImagesAndUpdateReview(DatabaseReference reviewRef, float rating, String comment, ArrayList<Uri> imageUris, RatingProductViewHolder holder) {
        ArrayList<String> imageUrls = new ArrayList<>();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("ReviewImages");

        if (imageUris.isEmpty()) {
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("productStar", String.valueOf(rating));
            updates.put("comment", comment);
            reviewRef.updateChildren(updates).addOnCompleteListener(task -> {
                holder.progressBar.setVisibility(View.GONE); // Hide ProgressBar
                holder.btnSubmit.setEnabled(true); // Re-enable button
                if (task.isSuccessful()) {
                    // Log review data to console
                    Log.d("RatingProductAdapter", "Review updated: " +
                            "userId=" + reviewRef.getParent().child("userId").getKey() +
                            ", productId=" + reviewRef.getParent().child("productId").getKey() +
                            ", rating=" + rating +
                            ", comment=" + comment +
                            ", imageUrls=[]");
                    StyleableToast.makeText(context, "Review updated successfully", R.style.UptrendToast).show();
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        holder.btnSubmit.setVisibility(View.GONE);
                        holder.ratingBar.setEnabled(false);
                        holder.reviewComment.setEnabled(false);
                        holder.img1.setEnabled(false);
                        holder.img2.setEnabled(false);
                        holder.img3.setEnabled(false);
                    }
                } else {
                    StyleableToast.makeText(context, "Failed to update review", R.style.UptrendToast).show();
                    Log.e("RatingProductAdapter", "Failed to update review: " + task.getException().getMessage());
                }
            });
            return;
        }

        for (Uri uri : imageUris) {
            StorageReference imageRef = storageRef.child("review_" + UUID.randomUUID().toString());
            imageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(url -> {
                    imageUrls.add(url.toString());
                    if (imageUrls.size() == imageUris.size()) {
                        HashMap<String, Object> updates = new HashMap<>();
                        updates.put("productStar", String.valueOf(rating));
                        updates.put("comment", comment);
                        updates.put("reviewImages", imageUrls);
                        reviewRef.updateChildren(updates).addOnCompleteListener(task -> {
                            holder.progressBar.setVisibility(View.GONE); // Hide ProgressBar
                            holder.btnSubmit.setEnabled(true); // Re-enable button
                            if (task.isSuccessful()) {
                                // Log review data to console
                                Log.d("RatingProductAdapter", "Review updated: " +
                                        "userId=" + reviewRef.getParent().child("userId").getKey() +
                                        ", productId=" + reviewRef.getParent().child("productId").getKey() +
                                        ", rating=" + rating +
                                        ", comment=" + comment +
                                        ", imageUrls=" + imageUrls);
                                StyleableToast.makeText(context, "Review updated successfully", R.style.UptrendToast).show();
                                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                                    holder.btnSubmit.setVisibility(View.GONE);
                                    holder.ratingBar.setEnabled(false);
                                    holder.reviewComment.setEnabled(false);
                                    holder.img1.setEnabled(false);
                                    holder.img2.setEnabled(false);
                                    holder.img3.setEnabled(false);
                                }
                            } else {
                                StyleableToast.makeText(context, "Failed to update review", R.style.UptrendToast).show();
                                Log.e("RatingProductAdapter", "Failed to update review: " + task.getException().getMessage());
                            }
                        });
                    }
                });
            }).addOnFailureListener(e -> {
                holder.progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                holder.btnSubmit.setEnabled(true); // Re-enable button
                StyleableToast.makeText(context, "Failed to upload image", R.style.UptrendToast).show();
                Log.e("RatingProductAdapter", "Image upload error: " + e.getMessage());
            });
        }
    }

    private void saveOrUpdateReview(String userId, String productId, float rating, String comment, ArrayList<Uri> imageUris, String username, RatingProductViewHolder holder) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Review");
        Query userQuery = ref.orderByChild("userId").equalTo(userId);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean reviewFound = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    if (review != null && review.getProductId().equals(productId)) {
                        reviewFound = true;
                        review.setUserName(username);
                        review.setProductStar(String.valueOf(rating));
                        review.setComment(comment);
                        uploadImagesAndUpdateReview(dataSnapshot.getRef(), rating, comment, imageUris, holder);
                    }
                }
                if (!reviewFound) {
                    Review review = new Review();
                    review.setUserId(userId);
                    review.setUserName(username);
                    review.setProductId(productId);
                    review.setProductStar(String.valueOf(rating));
                    review.setComment(comment);
                    String reviewKey = ref.push().getKey();
                    if (reviewKey != null) {
                        review.setReviewId(reviewKey);
                        uploadImagesAndSaveReview(ref.child(reviewKey), review, imageUris, holder);
                    } else {
                        Log.e("RatingProductAdapter", "Failed to generate review key for userId: " + userId);
                        StyleableToast.makeText(context, "Error submitting review", R.style.UptrendToast).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(context, "Error checking review: " + error.getMessage(), R.style.UptrendToast).show();
                Log.e("RatingProductAdapter", "Error checking review for userId: " + userId + ", error: " + error.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public static class RatingProductViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtProductName, txtBrandName, btnSubmit;
        RatingBar ratingBar;
        ShapeableImageView img1, img2, img3;
        EditText reviewComment;
        ProgressBar progressBar; // Add ProgressBar

        public RatingProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImageA2C);
            txtProductName = itemView.findViewById(R.id.productNameRA);
            txtBrandName = itemView.findViewById(R.id.productBrandNameRA);
            ratingBar = itemView.findViewById(R.id.productReview);
            btnSubmit = itemView.findViewById(R.id.submit_rating_btn);
            img1 = itemView.findViewById(R.id.img1);
            img2 = itemView.findViewById(R.id.img2);
            img3 = itemView.findViewById(R.id.img3);
            reviewComment = itemView.findViewById(R.id.review_comment);
            progressBar = itemView.findViewById(R.id.progressBar); // Initialize ProgressBar
        }
    }
}