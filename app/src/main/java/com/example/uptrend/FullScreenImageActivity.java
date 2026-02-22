package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;

public class FullScreenImageActivity extends AppCompatActivity {
    private ImageView fullScreenImage;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix matrix = new Matrix();
    private float scale = 1f;
    private static final float MIN_SCALE = 1f;
    private static final float MAX_SCALE = 4f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);



        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }


        // Initialize views
        fullScreenImage = findViewById(R.id.fullScreenImage);
        TextView txtUsername = findViewById(R.id.txt_username);
        TextView txtRating = findViewById(R.id.txt_rating);
        TextView txtComment = findViewById(R.id.txt_comment);
        ImageView backBtn = findViewById(R.id.back_btn);

        // Get Intent extras
        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        String username = intent.getStringExtra("username");
        String rating = intent.getStringExtra("rating");
        String comment = intent.getStringExtra("comment");
        String productId = intent.getStringExtra("productId");

        // Load image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .error(R.drawable.ic_launcher_background)
                    .into(fullScreenImage);
            Log.d("FullScreenImageActivity", "Loading image: " + imageUrl);
        } else {
            fullScreenImage.setImageResource(R.drawable.ic_launcher_background);
            Log.w("FullScreenImageActivity", "No image URL provided");
        }

        // Set review details
        txtUsername.setText(username != null && !username.isEmpty() ? username : "Anonymous");
        txtRating.setText(rating != null && !rating.isEmpty() ? "Rating: " + rating : "Rating: 0");
        txtComment.setText(comment != null && !comment.isEmpty() ? comment : "No comment");

        // Initialize zoom functionality
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale *= detector.getScaleFactor();
                scale = Math.max(MIN_SCALE, Math.min(scale, MAX_SCALE));
                matrix.setScale(scale, scale, detector.getFocusX(), detector.getFocusY());
                fullScreenImage.setImageMatrix(matrix);
                return true;
            }
        });

        // Handle touch events for zoom
        fullScreenImage.setOnTouchListener((v, event) -> {
            scaleGestureDetector.onTouchEvent(event);
            return true;
        });

        // Back button click
        backBtn.setOnClickListener(v -> navigateToOpenProduct(productId));

        // Log review details
        Log.d("FullScreenImageActivity", "Review details - username: " + username + ", rating: " + rating + ", comment: " + comment + ", productId: " + productId);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        String productId = getIntent().getStringExtra("productId");
        navigateToOpenProduct(productId);
    }

    private void navigateToOpenProduct(String productId) {
        Intent intent = new Intent(this, open_product.class);
        if (productId != null && !productId.isEmpty()) {
            intent.putExtra("productId", productId);
        } else {
            Log.w("FullScreenImageActivity", "No productId provided for navigation");
        }
        startActivity(intent);
        finish();
    }
}