package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.uptrend.Adapter.AllProductAdapter;
import com.example.uptrend.Adapter.Onclick;
import com.example.uptrend.Adapter.RecentlyViewProductAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import DataModel.Cart;
import DataModel.Product;
import DataModel.RecentlyViewProduct;
import DataModel.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class home extends AppCompatActivity implements Onclick {

    ImageSlider imageSlider;
    CardView searchCard;
    BottomNavigationView bottomNavigationView;
    RecyclerView recycleViewProduct;
    private CartNotificationHelper cartNotificationHelper;
    private TextView cartNotificationText;
    private DatabaseReference cartRef;
    private ValueEventListener cartValueEventListener;
    private CircleImageView selectImageView;
    private TextView txtUserNameHome, txtSetGreeting;
    private DatabaseReference userRef;
    private Query userQuery;
    private DatabaseReference productReference, recentlyProductRef;
    private ArrayList<Product> productArrayList;
    private AllProductAdapter productAdapter;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout layoutRecentlyViewProduct;
    private RecyclerView recyclerViewRecentlyProduct;
    private FirebaseUser user;
    private ArrayList<RecentlyViewProduct> recentlyViewProductArrayList;
    private CardView cardViewNike, cardViewApple, cardViewZara, cardViewRolex, cardViewPuma, cardViewLoreal, cardViewCadbury, cardViewLevis, cardViewCartier;
    private RecentlyViewProductAdapter recentlyViewProductAdapter;
    private ProgressBar progressBar;
    private boolean productsLoaded = false;
    private boolean recentlyViewedLoaded = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenyouuu));
        }

        searchCard = findViewById(R.id.searchCard);
        imageSlider = findViewById(R.id.imageSlider);
        recycleViewProduct = findViewById(R.id.recycleView_product);
        recyclerViewRecentlyProduct = findViewById(R.id.recyclerViewRecentlyProduct);
        layoutRecentlyViewProduct = findViewById(R.id.layoutRecentlyProduct);
        progressBar = findViewById(R.id.progressBar);
        selectImageView = findViewById(R.id.selectImage);
        txtUserNameHome = findViewById(R.id.txtUserNameHome);
        txtSetGreeting = findViewById(R.id.txtSetGreeting);

        // Set greeting immediately (local, doesn't need Firebase)
        setDynamicGreeting();
        loadUserProfileImmediately();
        startBrandLogoAnimation();
        setupCartNotification();

        cartNotificationHelper = new CartNotificationHelper(
                findViewById(R.id.add_to_cart_notification),
                "HomeActivity"
        );


        // Show ProgressBar initially for product loading only
        progressBar.setVisibility(View.VISIBLE);

        // Brand Logo FindViewById
        cardViewNike = findViewById(R.id.cardViewNike);
        cardViewApple = findViewById(R.id.cardViewApple);
        cardViewZara = findViewById(R.id.cardViewZara);
        cardViewRolex = findViewById(R.id.cardViewRolex);
        cardViewPuma = findViewById(R.id.cardViewPuma);
        cardViewLoreal = findViewById(R.id.cardViewLoreal);
        cardViewCadbury = findViewById(R.id.cardViewCadbury);
        cardViewLevis = findViewById(R.id.cardViewLevis);
        cardViewCartier = findViewById(R.id.cardViewCartier);
        cartNotificationText = findViewById(R.id.add_to_cart_notification);


        cardViewNike.setOnClickListener(v -> openBrandActivity("Nike"));
        cardViewApple.setOnClickListener(v -> openBrandActivity("Apple"));
        cardViewZara.setOnClickListener(v -> openBrandActivity("Zara"));
        cardViewRolex.setOnClickListener(v -> openBrandActivity("Rolex"));
        cardViewPuma.setOnClickListener(v -> openBrandActivity("Puma"));
        cardViewLoreal.setOnClickListener(v -> openBrandActivity("Loreal"));
        cardViewCadbury.setOnClickListener(v -> openBrandActivity("Cadbury"));
        cardViewLevis.setOnClickListener(v -> openBrandActivity("Levi's"));
        cardViewCartier.setOnClickListener(v -> openBrandActivity("Cartier"));

        user = FirebaseAuth.getInstance().getCurrentUser();
        recentlyViewProductArrayList = new ArrayList<>();

        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.autoimg_hoodie, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_shoes, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_kurta, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_jeans2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_saree, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_watch, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_jacket, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_mobile, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_watch, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_chocolate, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.autoimg_perfume, ScaleTypes.FIT));
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);


        selectImageView.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, user_profile.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });
        TextView categories1 = findViewById(R.id.categories1);
        categories1.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, category_product.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView account1 = findViewById(R.id.account1);
        account1.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, account_user.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        TextView bag1 = findViewById(R.id.bag1);
        bag1.setOnClickListener(v -> {
            Intent intent = new Intent(home.this, add_to_cart_product.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });

        searchCard.setOnClickListener(v -> {
            Intent i = new Intent(home.this, search_product.class);
            i.putExtra("activityName", "home");
            startActivity(i);
        });

        // Initialize RecyclerView for products (starts after profile loads)
        loadProducts();

        showRecentlyProduct();
    }

    private void startBrandLogoAnimation() {
        // Wait for layout to be drawn
        findViewById(android.R.id.content).postDelayed(() -> {
            CardView[] brandCards = {
                    cardViewNike, cardViewApple, cardViewZara, cardViewRolex,
                    cardViewPuma, cardViewLoreal, cardViewCadbury, cardViewLevis, cardViewCartier
            };

            // Animate each card one by one
            for (int i = 0; i < brandCards.length; i++) {
                CardView card = brandCards[i];
                if (card != null) {
                    card.setScaleX(0f);
                    card.setScaleY(0f);
                    card.setAlpha(0f);

                    card.animate()
                            .scaleX(1f)
                            .scaleY(1f)
                            .alpha(1f)
                            .setDuration(600)
                            .setStartDelay(i * 350) // Stagger animation
                            .setInterpolator(new OvershootInterpolator(1.2f))
                            .start();
                }
            }
        }, 700); // Start after 500ms delay
    }

    private void checkLoadingComplete() {
        if (productsLoaded && recentlyViewedLoaded) {
            progressBar.setVisibility(View.GONE);

            // Add animation for recently viewed products
            startRecentlyViewedAnimation();

            // Add animation for main products
            startMainProductsAnimation();
        }
    }

    private void startRecentlyViewedAnimation() {
        if (recyclerViewRecentlyProduct.getLayoutManager() == null) return;

        int childCount = recyclerViewRecentlyProduct.getLayoutManager().getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerViewRecentlyProduct.getLayoutManager().getChildAt(i);
            if (child != null) {
                child.setTranslationX(-100f);
                child.setAlpha(0f);

                child.animate()
                        .translationX(0f)
                        .alpha(1f)
                        .setDuration(500)
                        .setStartDelay(i * 200) // Stagger animation
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }
    }

    private void startMainProductsAnimation() {
        if (recycleViewProduct.getLayoutManager() == null) return;

        int childCount = recycleViewProduct.getLayoutManager().getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recycleViewProduct.getLayoutManager().getChildAt(i);
            if (child != null) {
                child.setTranslationY(-50f);
                child.setAlpha(0f);

                child.animate()
                        .translationY(0f)
                        .alpha(1f)
                        .setDuration(600)
                        .setStartDelay(i * 150) // Stagger animation
                        .setInterpolator(new DecelerateInterpolator())
                        .start();
            }
        }
    }

    @Override
    public void ItemOnClickListener(String productId) {
        productAlreadyAvailableInRecently(productId);
        Intent i = new Intent(home.this, open_product.class);
        i.putExtra("productId", productId);
        startActivity(i);
    }

    private void setDynamicGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;

        if (hour >= 6 && hour < 12) {
            greeting = "Good Morning";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Good Evening";
        } else {
            greeting = "Good Night";
        }

        txtSetGreeting.setText(greeting);
    }

    private void loadUserProfileImmediately() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("User");
            userQuery = userRef.orderByChild("userId").equalTo(currentUser.getUid());

            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                        User user = userSnapshot.getValue(User.class);

                        if (user != null) {
                            if (user.getUserName() != null && !user.getUserName().isEmpty()) {
                                txtUserNameHome.setText("Hii " + user.getUserName());
                            } else {
                                txtUserNameHome.setText("Hii User");
                            }

// Load avatar image (priority) or fallback to default
                            if (user.getAvatarImage() != null && !user.getAvatarImage().isEmpty()) {
                                // Load custom avatar as background
                                int resourceId = getAvatarResourceId(user.getAvatarImage());
                                if (resourceId != 0) {
                                    selectImageView.setBackgroundResource(resourceId);
                                } else {
                                    selectImageView.setBackgroundResource(R.drawable.vector_profile);
                                }
                            } else {
                                // Always default to vector_profile
                                selectImageView.setBackgroundResource(R.drawable.vector_profile);
                            }
                        }
                    } else {
                        // If no user data exists yet
                        txtUserNameHome.setText("User");
                        selectImageView.setImageResource(R.drawable.vector_profile);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error but still show default values
                    txtUserNameHome.setText("User");
                    selectImageView.setImageResource(R.drawable.vector_profile);
                    Log.e("HomeActivity", "Error loading user profile: " + error.getMessage());
                }
            });
        } else {
            // If no user logged in
            txtUserNameHome.setText("Guest");
            selectImageView.setImageResource(R.drawable.vector_profile);
        }
    }

    private int getAvatarResourceId(String avatarName) {
        switch (avatarName) {
            case "vector_man1":
                return R.drawable.vector_man1;
            case "vector_man2":
                return R.drawable.vector_man2;
            case "vector_man3":
                return R.drawable.vector_man3;
            case "vector_man4":
                return R.drawable.vector_man4;
            case "vector_man5":
                return R.drawable.vector_man5;
            case "vector_women1":
                return R.drawable.vector_women1;
            case "vector_women2":
                return R.drawable.vector_women2;
            case "vector_women3":
                return R.drawable.vector_women3;
            case "vector_women4":
                return R.drawable.vector_women4;
            case "vector_women5":
                return R.drawable.vector_women5;
            case "vector_profile":
                return R.drawable.vector_profile;
            default:
                // Default to vector_profile for any unknown avatar name
                return R.drawable.vector_profile;
        }
    }

    private void loadProducts() {
        productArrayList = new ArrayList<>();
        productReference = FirebaseDatabase.getInstance().getReference("Product");
        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                productArrayList.clear();
                for (DataSnapshot productSnapShot : snapshot.getChildren()) {
                    Product product = productSnapShot.getValue(Product.class);
                    if (product != null) {
                        product.setProductId(productSnapShot.getKey());
                        productArrayList.add(product);
                    }
                }
                Collections.shuffle(productArrayList);
                gridLayoutManager = new GridLayoutManager(home.this, 2, GridLayoutManager.VERTICAL, false);
                // In loadProducts() method, after setting adapter:
                productAdapter = new AllProductAdapter(home.this, productArrayList, home.this);
                recycleViewProduct.setLayoutManager(gridLayoutManager);
                recycleViewProduct.setAdapter(productAdapter);

                recycleViewProduct.post(() -> {
                    productsLoaded = true;
                    checkLoadingComplete();
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error and mark products as loaded to hide ProgressBar
                productsLoaded = true;
                checkLoadingComplete();
                Log.e("HomeActivity", "Error loading products: " + error.getMessage());
            }
        });
    }

    private void setupCartNotification() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            cartNotificationText.setVisibility(View.GONE);
            return;
        }

        cartRef = FirebaseDatabase.getInstance().getReference("Cart");
        cartValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int cartCount = 0;

                // Count all cart items for the current user
                for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                    Cart cart = cartSnapshot.getValue(Cart.class);
                    if (cart != null && cart.getUserId() != null &&
                            cart.getUserId().equals(currentUser.getUid())) {
                        cartCount++;
                    }
                }

                // Update UI
                if (cartCount > 0) {
                    cartNotificationText.setText(String.valueOf(cartCount));
                    cartNotificationText.setVisibility(View.VISIBLE);
                } else {
                    cartNotificationText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeActivity", "Error loading cart count: " + error.getMessage());
            }
        };

        // Listen for cart changes
        cartRef.addValueEventListener(cartValueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartNotificationHelper != null) {
            cartNotificationHelper.cleanup();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadUserProfileImmediately();
        setDynamicGreeting();
    }

    public void productAlreadyAvailableInRecently(String productId) {
        if (productId == null || productId.isEmpty()) {
            return;
        }

        DatabaseReference recentlyProductRef = FirebaseDatabase.getInstance().getReference("RecentlyViewProduct");
        Query recentlyProductQuery = recentlyProductRef.orderByChild("userId_productId")
                .equalTo(user.getUid() + "_" + productId);

        recentlyProductQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String currentTimeStamp = String.valueOf(System.currentTimeMillis());
                RecentlyViewProduct newProduct = new RecentlyViewProduct();
                newProduct.setUserId(user.getUid());
                newProduct.setProductId(productId);
                newProduct.setTimeStamp(currentTimeStamp);
                newProduct.setUserId_productId(user.getUid() + "_" + productId);

                boolean productAlreadyInRecently = false;
                String existingKey = null;

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    RecentlyViewProduct recentlyViewProduct = productSnapshot.getValue(RecentlyViewProduct.class);
                    if (recentlyViewProduct != null && recentlyViewProduct.getProductId().equals(productId)) {
                        existingKey = productSnapshot.getKey();
                        productAlreadyInRecently = true;
                        break;
                    }
                }

                int existingIndex = -1;
                for (int i = 0; i < recentlyViewProductArrayList.size(); i++) {
                    if (recentlyViewProductArrayList.get(i).getProductId().equals(productId)) {
                        existingIndex = i;
                        break;
                    }
                }

                if (existingIndex != -1) {
                    recentlyViewProductArrayList.remove(existingIndex);
                } else if (recentlyViewProductArrayList.size() >= 6) {
                    recentlyViewProductArrayList.remove(recentlyViewProductArrayList.size() - 1);
                    Query oldestQuery = recentlyProductRef.orderByChild("userId").equalTo(user.getUid())
                            .limitToFirst(1);
                    oldestQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot oldestSnapshot : snapshot.getChildren()) {
                                oldestSnapshot.getRef().removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("HomeActivity", "Error removing oldest: " + error.getMessage());
                        }
                    });
                }

                recentlyViewProductArrayList.add(0, newProduct);

                if (productAlreadyInRecently && existingKey != null) {
                    recentlyProductRef.child(existingKey).setValue(newProduct);
                } else {
                    recentlyProductRef.push().setValue(newProduct);
                }

                if (recentlyViewProductArrayList.size() > 0) {
                    layoutRecentlyViewProduct.setVisibility(View.VISIBLE);
                    if (recentlyViewProductAdapter == null) {
                        recentlyViewProductAdapter = new RecentlyViewProductAdapter(home.this, recentlyViewProductArrayList, home.this, "home");
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(home.this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewRecentlyProduct.setLayoutManager(linearLayoutManager);
                        recyclerViewRecentlyProduct.setAdapter(recentlyViewProductAdapter);
                    } else {
                        recentlyViewProductAdapter.notifyDataSetChanged();
                    }
                } else {
                    layoutRecentlyViewProduct.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("HomeActivity", "Error checking recently viewed: " + error.getMessage());
            }
        });
    }

    public void showRecentlyProduct() {
        recentlyViewProductArrayList = new ArrayList<>();
        recentlyProductRef = FirebaseDatabase.getInstance().getReference("RecentlyViewProduct");
        Query query = recentlyProductRef.orderByChild("userId").equalTo(user.getUid()).limitToLast(6);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recentlyViewProductArrayList.clear();
                for (DataSnapshot productSnapShot : snapshot.getChildren()) {
                    RecentlyViewProduct recentlyViewProduct = productSnapShot.getValue(RecentlyViewProduct.class);
                    if (recentlyViewProduct != null && recentlyViewProduct.getTimeStamp() != null) {
                        recentlyViewProductArrayList.add(recentlyViewProduct);
                    }
                }
                Collections.sort(recentlyViewProductArrayList, (p1, p2) -> {
                    try {
                        return Long.compare(Long.parseLong(p2.getTimeStamp()), Long.parseLong(p1.getTimeStamp()));
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                });
                if (recentlyViewProductArrayList.size() > 0) {
                    layoutRecentlyViewProduct.setVisibility(View.VISIBLE);
                    recentlyViewProductAdapter = new RecentlyViewProductAdapter(home.this, recentlyViewProductArrayList, home.this, "home");
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(home.this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewRecentlyProduct.setLayoutManager(linearLayoutManager);
                    recyclerViewRecentlyProduct.setAdapter(recentlyViewProductAdapter);
                } else {
                    layoutRecentlyViewProduct.setVisibility(View.GONE);
                }

                recentlyViewedLoaded = true;
                checkLoadingComplete();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                layoutRecentlyViewProduct.setVisibility(View.GONE);
                recentlyViewedLoaded = true;
                checkLoadingComplete();
                Log.e("HomeActivity", "Error loading recently viewed: " + error.getMessage());
            }
        });
    }

    public void openBrandActivity(String brandName) {
        Intent i = new Intent(home.this, open_brand_product.class);
        i.putExtra("brandName", brandName);
        startActivity(i);
    }
}