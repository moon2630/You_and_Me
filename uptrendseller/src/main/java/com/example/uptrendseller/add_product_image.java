package com.example.uptrendseller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.uptrendseller.Adapter.ProductImageAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;

public class add_product_image extends AppCompatActivity {
    private static final int Read_Permission = 101;
    private ViewPager2 viewPager2;
    private TextView save_image_btn, txt,backButton,txtCurrentDate;
    private AppCompatButton btnSelectImage, shine_btn;
    private ArrayList<Uri> uri = new ArrayList<>();
    private ProductImageAdapter productImageAdapter;
    private Handler sliderHandler = new Handler();
    private String category, key, SubCategory;
    private StorageReference storageReference;
    private loadingDialog2 loading;
    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem() + 1);
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_image);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }


        Intent intent = getIntent();
        if (intent != null) {
            key = intent.getStringExtra("Key");
            category = intent.getStringExtra("Category");
            SubCategory = intent.getStringExtra("SubCategory");
        }
        // LOAD EXISTING IMAGES IF ANY - with null check
        if (key != null && !key.isEmpty()) {
            loadExistingImages(key);
        }

        //FindView By Id of Widget
        viewPager2 = findViewById(R.id.viewPager2);
        btnSelectImage = findViewById(R.id.selectImage);
        //save image
        save_image_btn = findViewById(R.id.save_image_txt);
        txt = findViewById(R.id.text_hide);


        txtCurrentDate = findViewById(R.id.txtCurrentDate);
        txtCurrentDate.setText("Date: " + DateHelper.getCurrentDate());

         backButton = findViewById(R.id.back_add_product_image);
        backButton.setOnClickListener(v -> onBackPressed());


        txt.postDelayed(new Runnable() {
            @Override
            public void run() {
                txt.setVisibility(View.GONE);
            }

        }, 4200);

        //GetIng the Category from previous activity and store in variable.
        category = getIntent().getStringExtra("Category");
        key = getIntent().getStringExtra("Key");
        SubCategory = getIntent().getStringExtra("SubCategory");


        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(add_product_image.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Read_Permission);
        }

        productImageAdapter = new ProductImageAdapter(uri, viewPager2);
        viewPager2.setAdapter(productImageAdapter);

        viewPager2.setOffscreenPageLimit(3);
        viewPager2.setClipChildren(false);
        viewPager2.setClipToPadding(false);


        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1 - Math.abs(position);
                page.setScaleY(0.85f + r * 0.14f);
            }
        });


        //getting Instance of Object
        storageReference = FirebaseStorage.getInstance().getReference();
        loading = new loadingDialog2(this);

        save_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri.size() >= 2 && uri.size() <= 5) {
                    loading.show();
                    uploadMultipleImage(uri);
                } else if (uri.isEmpty()) {
                    StyleableToast.makeText(getApplicationContext(), "Please select at least 2 images", R.style.UptrendToast).show();
                } else if (uri.size() == 1) {
                    StyleableToast.makeText(getApplicationContext(), "Please select at least 1 more image (2-5 required)", R.style.UptrendToast).show();
                } else {
                    StyleableToast.makeText(getApplicationContext(), "Select 2 to 5 images only", R.style.UptrendToast).show();
                }
            }
        });

        viewPager2.setPageTransformer(transformer);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 2000);
            }
        });


        AppCompatButton deleteImageBtn = findViewById(R.id.delete_image);
        deleteImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAllImages();
            }
        });

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uri.clear();
                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select picture"), 1);
            }
        });
    }
    private void loadExistingImages(String productKey) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productKey);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("productImages")) {
                    ArrayList<String> imageUrls = (ArrayList<String>) snapshot.child("productImages").getValue();
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        uri.clear();

                        for (String url : imageUrls) {
                            try {
                                Uri imageUri = Uri.parse(url);
                                uri.add(imageUri);
                            } catch (Exception e) {
                                Log.e("LoadImages", "Invalid URL: " + url);
                            }
                        }

                        if (productImageAdapter != null) {
                            productImageAdapter.updateData(uri);
                            viewPager2.setAdapter(productImageAdapter);
                        }

                        // Show delete button, hide select button if images exist
                        if (!uri.isEmpty()) {
                            btnSelectImage.setVisibility(View.GONE);
                            findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
                        }

                        // UPDATE: Hide text_hide when images exist
                        txt.setVisibility(View.GONE);

                    } else {
                        // UPDATE: Show text_hide when no images in database
                        txt.setVisibility(View.VISIBLE);
                    }
                } else {
                    // UPDATE: Show text_hide when no productImages node exists
                    txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoadImages", "Failed to load images: " + error.getMessage());
            }
        });
    }
    // Add this method to update the image slider directly
    private void uploadMultipleImage(ArrayList<Uri> imageUri) {
        // Check if there are images to upload
        if (imageUri == null || imageUri.isEmpty()) {
            loading.cancel();
            StyleableToast.makeText(this, "No images selected", R.style.UptrendToast).show();
            return;
        }

        Product product = new Product();
        ArrayList<String> list = new ArrayList<>();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(key);

        int totalImages = imageUri.size();
        final int[] uploadedCount = {0}; // Counter for uploaded images

        for (Uri imageUriItem : imageUri) {
            // Check if it's a content URI (new image) or http URI (already uploaded)
            String uriString = imageUriItem.toString();

            if (uriString.startsWith("content://") || uriString.startsWith("file://")) {
                // This is a new image that needs to be uploaded
                StorageReference imageRef = storageReference.child("Product Images/images" + UUID.randomUUID().toString());
                imageRef.putFile(imageUriItem)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                list.add(uri.toString());
                                uploadedCount[0]++;

                                // Check if all images have been uploaded
                                if (uploadedCount[0] == totalImages) {
                                    updateDatabaseWithImages(databaseReference, list);
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            loading.cancel();
                            StyleableToast.makeText(this, "Failed to upload image: " + e.getMessage(), R.style.UptrendToast).show();
                            uploadedCount[0]++;

                            // Continue with other images even if one fails
                            if (uploadedCount[0] == totalImages && !list.isEmpty()) {
                                updateDatabaseWithImages(databaseReference, list);
                            }
                        });
            } else if (uriString.startsWith("http")) {
                // This is already uploaded image (URL from Firebase), just add to list
                list.add(uriString);
                uploadedCount[0]++;

                // Check if all images have been processed
                if (uploadedCount[0] == totalImages) {
                    updateDatabaseWithImages(databaseReference, list);
                }
            }
        }
    }
    private void deleteAllImages() {
        if (key != null && !key.isEmpty()) {
            loading.show();

            // Show text_hide when all images are deleted
            txt.setVisibility(View.VISIBLE);
            // Clear local URI list
            uri.clear();

            // Clear Firebase images
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(key);
            HashMap<String, Object> updates = new HashMap<>();
            updates.put("productImages", new ArrayList<String>()); // Set empty array

            databaseReference.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> {
                        loading.cancel();
                        StyleableToast.makeText(this, "All images deleted successfully", R.style.UptrendToast).show();

                        // Update adapter
                        if (productImageAdapter != null) {
                            productImageAdapter.updateData(uri);
                        }

                        // Show select button, hide delete button
                        btnSelectImage.setVisibility(View.VISIBLE);
                        findViewById(R.id.delete_image).setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e -> {
                        loading.cancel();
                        StyleableToast.makeText(this, "Failed to delete images", R.style.UptrendToast).show();
                    });
        }
    }
    // Helper method to update database with images
    private void updateDatabaseWithImages(DatabaseReference databaseReference, ArrayList<String> imageUrls) {
        if (imageUrls.isEmpty()) {
            loading.cancel();
            StyleableToast.makeText(this, "No images to save", R.style.UptrendToast).show();
            return;
        }
        HashMap<String, Object> image = new HashMap<>();
        image.put("productImages", imageUrls);
        image.put("productId", key);

        databaseReference.updateChildren(image)
                .addOnSuccessListener(aVoid -> {
                    loading.cancel();
                    StyleableToast.makeText(this, "Images Saved Successfully", R.style.UptrendToast).show();

                    // Navigate to next activity
                    Intent i = new Intent(getApplicationContext(), add_product_details.class);
                    i.putExtra("Category", category);
                    i.putExtra("Key", key);
                    i.putExtra("SubCategory", SubCategory);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    loading.cancel();
                    StyleableToast.makeText(this, "Failed to save images: " + e.getMessage(), R.style.UptrendToast).show();
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            uri.clear(); // Clear previous selection

            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();
                for (int i = 0; i < x; i++) {
                    uri.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                uri.add(data.getData());
            }

            productImageAdapter.notifyDataSetChanged();

            // Show delete button if images are selected, hide select button
            if (!uri.isEmpty()) {
                btnSelectImage.setVisibility(View.GONE);
                findViewById(R.id.delete_image).setVisibility(View.VISIBLE);
                // UPDATE: Hide text_hide when images are selected
                txt.setVisibility(View.GONE);
            } else {
                // UPDATE: Show text_hide when no images selected
                txt.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }
    @Override
    protected void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 2000);
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (key != null) {
            Intent intent = new Intent(add_product_image.this, listing_product.class);
            intent.putExtra("Key", key);
            if (category != null) intent.putExtra("Category", category);
            if (SubCategory != null) intent.putExtra("SubCategory", SubCategory);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}
