package com.example.uptrend.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrend.R;
import com.example.uptrend.forgot_password;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import DataModel.Review;
import DataModel.User;
import DataModel.UserAddress;
import io.github.muddz.styleabletoast.StyleableToast;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private final Context context;
    private final ArrayList<Review> reviewList;

    public ReviewAdapter(Context context, ArrayList<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList != null ? reviewList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        if (review == null) {
            Log.e("ReviewAdapter", "Review is null at position: " + position);
            holder.txtUsername.setText("Anonymous");
            holder.txtComment.setText("No comment");
            holder.txtRating.setText("0");
            holder.thumbUpNumber.setText("0");
            holder.thumbDownNumber.setText("0");
            return;
        }

        // Set review data
        holder.txtComment.setText(review.getComment() != null && !review.getComment().isEmpty() ? review.getComment() : "No comment");
        holder.txtRating.setText(review.getProductStar() != null && !review.getProductStar().isEmpty() ? review.getProductStar() : "0");
        holder.thumbUpNumber.setText(String.valueOf(review.getThumbUpNumber()));
        holder.thumbDownNumber.setText(String.valueOf(review.getThumbDownNumber()));

        // Initialize image adapter for review images
        List<String> reviewImages = review.getReviewImages() != null ? review.getReviewImages() : new ArrayList<>();
        ReviewImageAdapter imageAdapter = new ReviewImageAdapter(context, reviewImages, review);
        holder.recyclerViewImages.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.recyclerViewImages.setAdapter(imageAdapter);

        // Handle like/dislike logic
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId == null) {
            holder.thumbUpCheckBox.setEnabled(false);
            holder.thumbDownCheckBox.setEnabled(false);
            return;
        }

        holder.thumbUpCheckBox.setEnabled(true);
        holder.thumbDownCheckBox.setEnabled(true);
        // Set initial checkbox states
        boolean isLiked = review.getLikedBy() != null && review.getLikedBy().contains(userId);
        boolean isDisliked = review.getDislikedBy() != null && review.getDislikedBy().contains(userId);
        holder.thumbUpCheckBox.setChecked(isLiked);
        holder.thumbDownCheckBox.setChecked(isDisliked);

        // Prevent recursive calls
        holder.thumbUpCheckBox.setOnCheckedChangeListener(null);
        holder.thumbDownCheckBox.setOnCheckedChangeListener(null);

        // Like checkbox listener
        holder.thumbUpCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != isLiked) { // Only process if state changed
                if (isChecked) {
                    // User likes the review
                    updateReviewVote(review.getReviewId(), userId, true, isDisliked, holder, false);
                    StyleableToast.makeText(context, "Thanks for voting",R.style.UptrendToast).show();
                } else {
                    // User unlikes the review
                    updateReviewVote(review.getReviewId(), userId, true, false, holder, true);
                    StyleableToast.makeText(context, "Thanks for voting",R.style.UptrendToast).show();
                }
            }
        });

        // Dislike checkbox listener
        holder.thumbDownCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != isDisliked) { // Only process if state changed
                if (isChecked) {
                    // User dislikes the review
                    updateReviewVote(review.getReviewId(), userId, false, isLiked, holder, false);
                    StyleableToast.makeText(context, "Thanks for voting",R.style.UptrendToast).show();
                } else {
                    // User undislikes the review
                    updateReviewVote(review.getReviewId(), userId, false, false, holder, true);
                    StyleableToast.makeText(context, "Thanks for voting",R.style.UptrendToast).show();
                }
            }
        });

        // Fetch username
        String reviewUserId = review.getUserId();
        if (reviewUserId == null || reviewUserId.isEmpty()) {
            holder.txtUsername.setText("Anonymous");
            Log.e("ReviewAdapter", "Invalid userId: null or empty for review at position: " + position);
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User").child(reviewUserId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String finalUsername;
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null && user.getUserName() != null && !user.getUserName().isEmpty()) {
                        finalUsername = user.getUserName();
                        Log.d("ReviewAdapter", "Fetched username from User: " + finalUsername + " for userId: " + reviewUserId);
                    } else {
                        Log.w("ReviewAdapter", "UserName is null or empty for userId: " + reviewUserId);
                        finalUsername = "Anonymous";
                    }
                } else {
                    Log.w("ReviewAdapter", "User snapshot does not exist for userId: " + reviewUserId);
                    finalUsername = "Anonymous";
                }

                if (finalUsername.equals("Anonymous")) {
                    DatabaseReference addressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
                    Query addressQuery = addressRef.orderByChild("userId").equalTo(reviewUserId);
                    addressQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot addressSnapshot) {
                            String addressUsername = finalUsername;
                            if (addressSnapshot.exists()) {
                                for (DataSnapshot addrSnapshot : addressSnapshot.getChildren()) {
                                    UserAddress address = addrSnapshot.getValue(UserAddress.class);
                                    if (address != null && address.getFullName() != null && !address.getFullName().isEmpty()) {
                                        addressUsername = address.getFullName();
                                        Log.d("ReviewAdapter", "Fetched fullName from UserAddress: " + addressUsername + " for userId: " + reviewUserId);
                                        break;
                                    }
                                }
                            } else {
                                Log.w("ReviewAdapter", "No UserAddress found for userId: " + reviewUserId);
                            }

                            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                                holder.txtUsername.setText(addressUsername);
                            }

                            String reviewId = review.getReviewId();
                            if (reviewId != null && !reviewId.isEmpty()) {
                                FirebaseDatabase.getInstance().getReference("Review").child(reviewId)
                                        .child("userName").setValue(addressUsername, (error, ref) -> {
                                            if (error != null) {
                                                Log.e("ReviewAdapter", "Failed to update userName for reviewId: " + reviewId + ", error: " + error.getMessage());
                                            }
                                        });
                            } else {
                                Log.e("ReviewAdapter", "ReviewId is null for userId: " + reviewUserId);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                                holder.txtUsername.setText(finalUsername);
                            }
                            Log.e("ReviewAdapter", "Error fetching UserAddress for userId: " + reviewUserId + ", error: " + error.getMessage());
                        }
                    });
                } else {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        holder.txtUsername.setText(finalUsername);
                    }

                    String reviewId = review.getReviewId();
                    if (reviewId != null && !reviewId.isEmpty()) {
                        FirebaseDatabase.getInstance().getReference("Review").child(reviewId)
                                .child("userName").setValue(finalUsername, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("ReviewAdapter", "Failed to update userName for reviewId: " + reviewId + ", error: " + error.getMessage());
                                    }
                                });
                    } else {
                        Log.e("ReviewAdapter", "ReviewId is null for userId: " + reviewUserId);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    holder.txtUsername.setText("Anonymous");
                }
                Log.e("ReviewAdapter", "Error fetching username for userId: " + reviewUserId + ", error: " + error.getMessage());
            }
        });
    }

    private void updateReviewVote(String reviewId, String userId, boolean isLike, boolean isSwitch, ReviewViewHolder holder, boolean isRemove) {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review").child(reviewId);
        reviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Log.e("ReviewAdapter", "Review not found for reviewId: " + reviewId);
                    StyleableToast.makeText(context, "Error voting on review",R.style.UptrendToast).show();

                    return;
                }
                Review review = snapshot.getValue(Review.class);
                if (review == null) {
                    Log.e("ReviewAdapter", "Review data is null for reviewId: " + reviewId);
                    StyleableToast.makeText(context, "Error voting on review",R.style.UptrendToast).show();

                    return;
                }

                // Initialize lists if null
                List<String> likedBy = review.getLikedBy() != null ? new ArrayList<>(review.getLikedBy()) : new ArrayList<>();
                List<String> dislikedBy = review.getDislikedBy() != null ? new ArrayList<>(review.getDislikedBy()) : new ArrayList<>();
                int thumbUpNumber = review.getThumbUpNumber();
                int thumbDownNumber = review.getThumbDownNumber();

                if (isLike) {
                    if (isRemove && likedBy.contains(userId)) {
                        likedBy.remove(userId);
                        thumbUpNumber = Math.max(0, thumbUpNumber - 1);
                    } else if (!isRemove && !likedBy.contains(userId)) {
                        likedBy.add(userId);
                        thumbUpNumber++;
                        if (isSwitch && dislikedBy.contains(userId)) {
                            dislikedBy.remove(userId);
                            thumbDownNumber = Math.max(0, thumbDownNumber - 1);
                        }
                    }
                } else {
                    if (isRemove && dislikedBy.contains(userId)) {
                        dislikedBy.remove(userId);
                        thumbDownNumber = Math.max(0, thumbDownNumber - 1);
                    } else if (!isRemove && !dislikedBy.contains(userId)) {
                        dislikedBy.add(userId);
                        thumbDownNumber++;
                        if (isSwitch && likedBy.contains(userId)) {
                            likedBy.remove(userId);
                            thumbUpNumber = Math.max(0, thumbUpNumber - 1);
                        }
                    }
                }

                // Update Firebase
                reviewRef.child("thumbUpNumber").setValue(thumbUpNumber);
                reviewRef.child("thumbDownNumber").setValue(thumbDownNumber);
                reviewRef.child("likedBy").setValue(likedBy);
                reviewRef.child("dislikedBy").setValue(dislikedBy);

                // Update UI
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    holder.thumbUpNumber.setText(String.valueOf(thumbUpNumber));
                    holder.thumbDownNumber.setText(String.valueOf(thumbDownNumber));
                    // Update checkbox states without triggering listeners
                    holder.thumbUpCheckBox.setOnCheckedChangeListener(null);
                    holder.thumbDownCheckBox.setOnCheckedChangeListener(null);
                    holder.thumbUpCheckBox.setChecked(likedBy.contains(userId));
                    holder.thumbDownCheckBox.setChecked(dislikedBy.contains(userId));
                    // Reattach listeners
                    holder.thumbUpCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked != (review.getLikedBy() != null && review.getLikedBy().contains(userId))) {
                            updateReviewVote(review.getReviewId(), userId, true, review.getDislikedBy() != null && review.getDislikedBy().contains(userId), holder, !isChecked);
                            StyleableToast.makeText(context, "Thanks for voting",R.style.UptrendToast).show();
                        }
                    });
                    holder.thumbDownCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        if (isChecked != (review.getDislikedBy() != null && review.getDislikedBy().contains(userId))) {
                            updateReviewVote(review.getReviewId(), userId, false, review.getLikedBy() != null && review.getLikedBy().contains(userId), holder, !isChecked);
                            StyleableToast.makeText(context, "Thanks for voting",R.style.UptrendToast).show();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ReviewAdapter", "Error updating vote for reviewId: " + reviewId + ", error: " + error.getMessage());
                StyleableToast.makeText(context, "Error voting on review",R.style.UptrendToast).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public void updateReviews(ArrayList<Review> reviews) {
        this.reviewList.clear();
        this.reviewList.addAll(reviews != null ? reviews : new ArrayList<>());
        notifyDataSetChanged();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtComment, txtRating, thumbUpNumber, thumbDownNumber;
        RecyclerView recyclerViewImages;
        CheckBox thumbUpCheckBox, thumbDownCheckBox;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txt_username);
            txtComment = itemView.findViewById(R.id.txt_comment);
            txtRating = itemView.findViewById(R.id.txt_rating);
            recyclerViewImages = itemView.findViewById(R.id.recyclerView_images);
            thumbUpCheckBox = itemView.findViewById(R.id.thumb_up_checkBox);
            thumbDownCheckBox = itemView.findViewById(R.id.thumb_down_checkBox);
            thumbUpNumber = itemView.findViewById(R.id.thumb_up_number);
            thumbDownNumber = itemView.findViewById(R.id.thumb_down_number);
        }
    }
}