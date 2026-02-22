package com.example.uptrend;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.adapteranddatamodel.DateAndTime;
import com.example.uptrend.Adapter.Onclick;
import com.example.uptrend.Adapter.ReviewAdapter;
import com.example.uptrend.Adapter.SuggestionProductAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import DataModel.Cart;
import DataModel.LikeProduct;
import DataModel.Order;
import DataModel.Product;
import DataModel.Review;
import DataModel.User;
import io.github.muddz.styleabletoast.StyleableToast;

public class open_product extends AppCompatActivity implements Onclick {
    CheckBox likeCheckBox;
    int totalReview=0;
    float rating,total;
    Order order;

    private LinearLayout  size_shirt_layout, size_shoes_layout,
            size_jeans_layout, layout_fabric, layout_washing, layout_weight, layout_occasion, layout_all_details, layoutOtherCategory,
            mobileLayoutVisible,mobileLayoutGone;

    private RatingBar ratingBar;

    private String productId;
    private TextView txtBrandName, txtProductName, txtSellingPrice, txtOriginalPrice, txtDiscount, txtRating, txtRatingCount,
            date_txt,
            color_txt, color_name_txt, all_detils_txt,
        few_left_shirt, few_left_shoes,
            few_left_jeans, fabric_name_txt, quantity_txt, washing_txt, weight_txt, occasion_txt, mrp_txt,
            market_txt, quntity2_txt, import_txt, mobile_txt, email_txt, share_btn, rating_txt,
            ratingBar_TOTAL, back_btn, selection_txt, txtOtherCategory,txt_rating,txt_rating_count,
            ram_txt,rom_storage_txt,processor_txt,rear_camera_txt,front_camera_txt,battery_txt;

    private RelativeLayout  size_S1, size_S2, size_S3, size_S4, size_S5, size_SH1, size_SH2,
            size_SH3, size_SH4, size_SH5, size_J1, size_J2, size_J3, size_J4, size_J5 ,size_J6,size_J7;
    private DatabaseReference productReference, userReference, productRootNodeReference, cartRef;
    private Query query, categoryQuery;

    private double discount;
    private Product product;
    private AppCompatButton btnBuyNow, btnAddToCart;
    private Cart cart;

    private User user;

    private String number;
    private FirebaseUser firebaseUser;
    ImageSlider imageSlider;
    ArrayList<SlideModel> slideModels;
    CardView sizeCardView;
    RecyclerView recyclerViewSuggestedProduct;
    SuggestionProductAdapter suggestionProductAdapter;
    ArrayList<Product> suggestedProducts;
    ScrollView scrollView;
    TextView txtSizeChart01, txtSizeChart02, txtSizeChart03;
    private DatabaseReference wishListRef;

    private String selectedSize = null;


    private boolean showAllReviews = false; // Tracks whether all reviews are shown


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_product);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenyouuu));
        }


        size_S1 = findViewById(R.id.radioButtonS);
        size_S2 = findViewById(R.id.radioButtonM);
        size_S3 = findViewById(R.id.radioButtonL);
        size_S4 = findViewById(R.id.radioButtonXL);
        size_S5 = findViewById(R.id.radioButtonXXl);



        size_SH1 = findViewById(R.id.radioButton6);
        size_SH2 = findViewById(R.id.radioButton7);
        size_SH3 = findViewById(R.id.radioButton8);
        size_SH4 = findViewById(R.id.radioButton9);
        size_SH5 = findViewById(R.id.radioButton10);


        size_J1 = findViewById(R.id.radioButton28);
        size_J2 = findViewById(R.id.radioButton30);
        size_J3 = findViewById(R.id.radioButton32);
        size_J4 = findViewById(R.id.radioButton34);
        size_J5 = findViewById(R.id.radioButton36);
        size_J6 = findViewById(R.id.radioButton38);
        size_J7 = findViewById(R.id.radioButton40);

        size_S1.setOnClickListener(v -> {
            setSizeBackground(size_S1, true);
            setSizeBackground(size_S2, false);
            setSizeBackground(size_S3, false);
            setSizeBackground(size_S4, false);
            setSizeBackground(size_S5, false);
            checkProductStock(few_left_shirt, Integer.parseInt(product.getProductSizes().get(0)));
            selectedSize = "0";
            cart.setProductSize("0");
            order.setProductSize("0");
        });

        size_S2.setOnClickListener(v -> {
            setSizeBackground(size_S1, false);
            setSizeBackground(size_S2, true);
            setSizeBackground(size_S3, false);
            setSizeBackground(size_S4, false);
            setSizeBackground(size_S5, false);
            checkProductStock(few_left_shirt, Integer.parseInt(product.getProductSizes().get(1)));
            selectedSize = "1";
            cart.setProductSize("1");
            order.setProductSize("1");
        });

        size_S3.setOnClickListener(v -> {
            setSizeBackground(size_S1, false);
            setSizeBackground(size_S2, false);
            setSizeBackground(size_S3, true);
            setSizeBackground(size_S4, false);
            setSizeBackground(size_S5, false);
            checkProductStock(few_left_shirt, Integer.parseInt(product.getProductSizes().get(2)));
            selectedSize = "2";
            cart.setProductSize("2");
            order.setProductSize("2");
        });

        size_S4.setOnClickListener(v -> {
            setSizeBackground(size_S1, false);
            setSizeBackground(size_S2, false);
            setSizeBackground(size_S3, false);
            setSizeBackground(size_S4, true);
            setSizeBackground(size_S5, false);
            checkProductStock(few_left_shirt, Integer.parseInt(product.getProductSizes().get(3)));
            selectedSize = "3";
            cart.setProductSize("3");
            order.setProductSize("3");
        });

        size_S5.setOnClickListener(v -> {
            setSizeBackground(size_S1, false);
            setSizeBackground(size_S2, false);
            setSizeBackground(size_S3, false);
            setSizeBackground(size_S4, false);
            setSizeBackground(size_S5, true);
            checkProductStock(few_left_shirt, Integer.parseInt(product.getProductSizes().get(4)));
            selectedSize = "4";
            cart.setProductSize("4");
            order.setProductSize("4");
        });

// Jeans size click listeners
        size_J1.setOnClickListener(v -> {
            setSizeBackground(size_J1, true);
            setSizeBackground(size_J2, false);
            setSizeBackground(size_J3, false);
            setSizeBackground(size_J4, false);
            setSizeBackground(size_J5, false);
            setSizeBackground(size_J6, false);
            setSizeBackground(size_J7, false);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(0)));
            selectedSize = "0";
            cart.setProductSize("0");
            order.setProductSize("0");
        });

        size_J2.setOnClickListener(v -> {
            setSizeBackground(size_J1, false);
            setSizeBackground(size_J2, true);
            setSizeBackground(size_J3, false);
            setSizeBackground(size_J4, false);
            setSizeBackground(size_J5, false);
            setSizeBackground(size_J6, false);
            setSizeBackground(size_J7, false);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(1)));
            selectedSize = "1";
            cart.setProductSize("1");
            order.setProductSize("1");
        });

        size_J3.setOnClickListener(v -> {
            setSizeBackground(size_J1, false);
            setSizeBackground(size_J2, false);
            setSizeBackground(size_J3, true);
            setSizeBackground(size_J4, false);
            setSizeBackground(size_J5, false);
            setSizeBackground(size_J6, false);
            setSizeBackground(size_J7, false);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(2)));
            selectedSize = "2";
            cart.setProductSize("2");
            order.setProductSize("2");
        });

        size_J4.setOnClickListener(v -> {
            setSizeBackground(size_J1, false);
            setSizeBackground(size_J2, false);
            setSizeBackground(size_J3, false);
            setSizeBackground(size_J4, true);
            setSizeBackground(size_J5, false);
            setSizeBackground(size_J6, false);
            setSizeBackground(size_J7, false);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(3)));
            selectedSize = "3";
            cart.setProductSize("3");
            order.setProductSize("3");
        });

        size_J5.setOnClickListener(v -> {
            setSizeBackground(size_J1, false);
            setSizeBackground(size_J2, false);
            setSizeBackground(size_J3, false);
            setSizeBackground(size_J4, false);
            setSizeBackground(size_J5, true);
            setSizeBackground(size_J6, false);
            setSizeBackground(size_J7, false);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(4)));
            selectedSize = "4";
            cart.setProductSize("4");
            order.setProductSize("4");
        });

        size_J6.setOnClickListener(v -> {
            setSizeBackground(size_J1, false);
            setSizeBackground(size_J2, false);
            setSizeBackground(size_J3, false);
            setSizeBackground(size_J4, false);
            setSizeBackground(size_J5, false);
            setSizeBackground(size_J6, true);
            setSizeBackground(size_J7, false);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(5)));
            selectedSize = "5";
            cart.setProductSize("5");
            order.setProductSize("5");
        });

        size_J7.setOnClickListener(v -> {
            setSizeBackground(size_J1, false);
            setSizeBackground(size_J2, false);
            setSizeBackground(size_J3, false);
            setSizeBackground(size_J4, false);
            setSizeBackground(size_J5, false);
            setSizeBackground(size_J6, false);
            setSizeBackground(size_J7, true);
            checkProductStock(few_left_jeans, Integer.parseInt(product.getProductSizes().get(6)));
            selectedSize = "6";
            cart.setProductSize("6");
            order.setProductSize("6");
        });


// Shoes size click listeners
        size_SH1.setOnClickListener(v -> {
            setSizeBackground(size_SH1, true);
            setSizeBackground(size_SH2, false);
            setSizeBackground(size_SH3, false);
            setSizeBackground(size_SH4, false);
            setSizeBackground(size_SH5, false);
            checkProductStock(few_left_shoes, Integer.parseInt(product.getProductSizes().get(0)));
            selectedSize = "0";
            cart.setProductSize("0");
            order.setProductSize("0");
        });

        size_SH2.setOnClickListener(v -> {
            setSizeBackground(size_SH1, false);
            setSizeBackground(size_SH2, true);
            setSizeBackground(size_SH3, false);
            setSizeBackground(size_SH4, false);
            setSizeBackground(size_SH5, false);
            checkProductStock(few_left_shoes, Integer.parseInt(product.getProductSizes().get(1)));
            selectedSize = "1";
            cart.setProductSize("1");
            order.setProductSize("1");
        });

        size_SH3.setOnClickListener(v -> {
            setSizeBackground(size_SH1, false);
            setSizeBackground(size_SH2, false);
            setSizeBackground(size_SH3, true);
            setSizeBackground(size_SH4, false);
            setSizeBackground(size_SH5, false);
            checkProductStock(few_left_shoes, Integer.parseInt(product.getProductSizes().get(2)));
            selectedSize = "2";
            cart.setProductSize("2");
            order.setProductSize("2");
        });

        size_SH4.setOnClickListener(v -> {
            setSizeBackground(size_SH1, false);
            setSizeBackground(size_SH2, false);
            setSizeBackground(size_SH3, false);
            setSizeBackground(size_SH4, true);
            setSizeBackground(size_SH5, false);
            checkProductStock(few_left_shoes, Integer.parseInt(product.getProductSizes().get(3)));
            selectedSize = "3";
            cart.setProductSize("3");
            order.setProductSize("3");
        });

        size_SH5.setOnClickListener(v -> {
            setSizeBackground(size_SH1, false);
            setSizeBackground(size_SH2, false);
            setSizeBackground(size_SH3, false);
            setSizeBackground(size_SH4, false);
            setSizeBackground(size_SH5, true);
            checkProductStock(few_left_shoes, Integer.parseInt(product.getProductSizes().get(4)));
            selectedSize = "4";
            cart.setProductSize("4");
            order.setProductSize("4");
        });

        likeCheckBox = findViewById(R.id.likeCheckBox);




        txtBrandName = findViewById(R.id.txt_brand_name);
        txtProductName = findViewById(R.id.txt_product_name);
        txtRating = findViewById(R.id.txt_rating);
        txtRatingCount = findViewById(R.id.txt_rating_count);
        txtSellingPrice = findViewById(R.id.txt_product_selling_price);
        txtOriginalPrice = findViewById(R.id.text_product_original_price);
        txtDiscount = findViewById(R.id.txtDiscount);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        imageSlider = findViewById(R.id.imageSlider);
        txtSizeChart01 = findViewById(R.id.txtSizeChart01);


        //**********
        txtSizeChart02 = findViewById(R.id.txtSizeChart02);
        txtSizeChart03 = findViewById(R.id.txtSizeChart03);
        date_txt = findViewById(R.id.date_txt);
        color_name_txt = findViewById(R.id.get_PD_color);
        color_txt = findViewById(R.id.color_name_txt);


        //for shirt size layout
        size_shirt_layout = findViewById(R.id.layout_shirt_size);

        few_left_shirt = findViewById(R.id.few_left_txt_shirt);


        //for shoes size layout
        size_shoes_layout = findViewById(R.id.layout_shoes_size);

        few_left_shoes = findViewById(R.id.few_left_txt_shoes);


        //for jeans size layout
        size_jeans_layout = findViewById(R.id.layout_jeans_size);

        few_left_jeans = findViewById(R.id.jeans_left_txt_shirt);

        //CardView For Size Layout
        sizeCardView = findViewById(R.id.sizeCardView);

        //for PD details
        all_detils_txt = findViewById(R.id.all_details_txt);
        fabric_name_txt = findViewById(R.id.fabric_name_txt);
        quantity_txt = findViewById(R.id.quantity_txt);
        washing_txt = findViewById(R.id.washing_txt);
        weight_txt = findViewById(R.id.weight_txt);
        occasion_txt = findViewById(R.id.occasion_txt);
        mrp_txt = findViewById(R.id.mrp_details_txt);
        market_txt = findViewById(R.id.market_details);
        quntity2_txt = findViewById(R.id.quntity2_details);
        import_txt = findViewById(R.id.import_details);
        mobile_txt = findViewById(R.id.mobile_txt);
        email_txt = findViewById(R.id.email_txt);
        share_btn = findViewById(R.id.share_btn);
        back_btn = findViewById(R.id.back_btn);
        selection_txt = findViewById(R.id.selection_txt);
        btnAddToCart = findViewById(R.id.btnAddToCart);


        //RATING BAR
        rating_txt = findViewById(R.id.rating_txt);
        ratingBar = findViewById(R.id.rating_Bar);
        ratingBar_TOTAL = findViewById(R.id.ratingBar_total_count);
        txt_rating=findViewById(R.id.txt_rating);
        txt_rating_count=findViewById(R.id.txt_rating_count);


        //
        layout_fabric = findViewById(R.id.fabric_layout);
        layout_washing = findViewById(R.id.layout_washing);
        layout_weight = findViewById(R.id.layout_weight);
        layout_occasion = findViewById(R.id.layout_occasion);
        layout_all_details = findViewById(R.id.layout_all_details_HS);



        //Layout For Other Category Stock Error
        layoutOtherCategory = findViewById(R.id.layoutOtherCategory);
        txtOtherCategory = findViewById(R.id.product_not_available);

        scrollView = findViewById(R.id.scrollView);
        mobileLayoutGone=findViewById(R.id.mobileLayoutGone);
        mobileLayoutVisible=findViewById(R.id.mobileLayoutVisible);

        //Mobile

        ram_txt=findViewById(R.id.ram_txt);
        rom_storage_txt=findViewById(R.id.rom_storage_txt);
        processor_txt=findViewById(R.id.processor_txt);
        rear_camera_txt=findViewById(R.id.rear_camera_txt);
        front_camera_txt=findViewById(R.id.front_camera_txt);
        battery_txt=findViewById(R.id.battery_txt);

        recyclerViewSuggestedProduct = findViewById(R.id.recyclerViewSuggestedProduct);
        /* geting product id from home activity so we can fetch product data
            and Displaying Product Details .
         */
        productId = getIntent().getStringExtra("productId");
        changeProduct(productId);

        String callingActivity = getIntent().getStringExtra("activityName");
        String searchQuery = getIntent().getStringExtra("searchQuery");


        // In onCreate, after initializing existing views
        RecyclerView recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ReviewAdapter reviewAdapter = new ReviewAdapter(this, new ArrayList<>());
        recyclerViewReviews.setAdapter(reviewAdapter);

        displayReviews(productId, reviewAdapter);


        // Update likeCheckBox OnCheckedChangeListener
        likeCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                StyleableToast.makeText(open_product.this, isChecked ? "Add to Favourites" : "Remove from Favourites", R.style.UptrendToast).show();
            }
            addProductInWishList();
        });

        // giving intent to Mobile no so Dialed pad can open
        mobile_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = mobile_txt.getText().toString().trim();
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(number)));
                startActivity(intent);
            }
        });

        all_detils_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (layout_all_details.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(layout_all_details, new AutoTransition());
                layout_all_details.setVisibility(var);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductInWishList();
                navigateBack();
            }
        });

        TextView otherRatingShow = findViewById(R.id.other_rating_show);
        otherRatingShow.setOnClickListener(v -> {
            showAllReviews = true;
            displayReviews(productId, reviewAdapter); // Refresh reviews to show all
        });

        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (productId == null || productId.isEmpty() || product == null || product.getProductName() == null) {
                    StyleableToast.makeText(open_product.this, "Unable to share product", R.style.UptrendToast).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.setPackage("com.whatsapp"); // Prefer WhatsApp
                String encodedProductId = Uri.encode(productId);
                String productName = product.getProductName();
                String shareText = "Check out this product on UpTrend: " + productName  + "\nhttps://uptrend.com/product/" + encodedProductId;
                intent.putExtra(Intent.EXTRA_TEXT, shareText);
                try {
                    startActivity(Intent.createChooser(intent, "Share Product"));
                    Log.d("OpenProduct", "Shared link: " + shareText);
                } catch (ActivityNotFoundException e) {
                    StyleableToast.makeText(open_product.this, "WhatsApp not installed", R.style.UptrendToast).show();
                    Log.e("OpenProduct", "Error sharing: " + e.getMessage());
                }
            }
        });
        Uri uri = getIntent().getData();
        if (uri != null) {
            Log.d("OpenProduct", "Received deep link: " + uri.toString());

            if ("uptrend".equals(uri.getScheme()) && "product".equals(uri.getHost())) {
                productId = uri.getLastPathSegment();
            } else if ("https".equals(uri.getScheme()) && "uptrend.com".equals(uri.getHost()) && uri.getPath() != null) {
                List<String> segments = uri.getPathSegments();
                if (segments.size() > 1 && "product".equals(segments.get(0))) {
                    productId = segments.get(1);
                }
            }

            if (productId != null && !productId.isEmpty()) {
                changeProduct(productId); // ✅ now it's safe
            } else {
                StyleableToast.makeText(this, "Invalid product link", R.style.UptrendToast).show();
                Log.e("OpenProduct", "Deep link productId is null or empty");
            }
        } else {
            Log.d("OpenProduct", "No deep link URI received");
        }

        // Initialize your views after this...


        email_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://mail.google.com/chat/u/0/#chat/home");
            }
        });



        /*
                Geting user email and User mobile number
         */
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userReference = FirebaseDatabase.getInstance().getReference("User");
        query = userReference.orderByChild("userId").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                user = userSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        btnBuyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (product.getProductSizes() != null) {
                    if (isRadioButtonChecked()) {
                        if (firebaseUser != null) {
                            checkAddress(firebaseUser.getUid());
                        } else {
                            StyleableToast.makeText(open_product.this, "Please log in to proceed", R.style.UptrendToast).show();
                            startActivity(new Intent(open_product.this, address_A2C.class)); // Redirect to login
                        }
                    } else {
                        StyleableToast.makeText(open_product.this, "Please Select Product Size", R.style.UptrendToast).show();
                    }
                } else {
                    if (firebaseUser != null) {
                        checkAddress(firebaseUser.getUid());
                    } else {
                        StyleableToast.makeText(open_product.this, "Please log in to proceed", R.style.UptrendToast).show();
                        startActivity(new Intent(open_product.this, address_A2C.class)); // Redirect to login
                    }
                }
            }
        });
        cart = new Cart();
        order = new Order();

        // Inside onCreate, update btnAddToCart click listener
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (firebaseUser == null) {
                    StyleableToast.makeText(open_product.this, "Please log in to add to cart", R.style.UptrendToast).show();
                    startActivity(new Intent(open_product.this, address_A2C.class));
                    return;
                }

                cartRef = FirebaseDatabase.getInstance().getReference("Cart");
                // Check if product already exists in cart for the selected size
                Query productQuery = cartRef.orderByChild("userId").equalTo(firebaseUser.getUid());
                productQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean alreadyInCart = false;
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            Cart existingCart = cartSnapshot.getValue(Cart.class);
                            if (existingCart != null && existingCart.getProductId().equals(productId) &&
                                    (selectedSize == null || selectedSize.equals(existingCart.getProductSize()))) {
                                alreadyInCart = true;
                                break;
                            }
                        }

                        if (alreadyInCart) {
                            StyleableToast.makeText(open_product.this, "Product already in cart", R.style.UptrendToast).show();
                            btnAddToCart.setEnabled(false);
                            updateCartUI(); // Update UI to reflect cart status
                            return;
                        }

                        // Check stock before adding to cart
                        productReference = FirebaseDatabase.getInstance().getReference("Product").child(productId);
                        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    StyleableToast.makeText(open_product.this, "Product not found", R.style.UptrendToast).show();
                                    return;
                                }

                                Product product = snapshot.getValue(Product.class);
                                if (product == null) {
                                    StyleableToast.makeText(open_product.this, "Error loading product data", R.style.UptrendToast).show();
                                    Log.e("OpenProduct", "Product data is null for productId: " + productId);
                                    return;
                                }

                                boolean canAddToCart = false;
                                TextView stockTextView = txtOtherCategory; // Default for non-sized products
                                if (product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
                                    if (selectedSize == null) {
                                        StyleableToast.makeText(open_product.this, "Please select product size", R.style.UptrendToast).show();
                                        return;
                                    }
                                    try {
                                        int sizeIndex = Integer.parseInt(selectedSize);
                                        if (sizeIndex >= 0 && sizeIndex < product.getProductSizes().size()) {
                                            int stock = Integer.parseInt(product.getProductSizes().get(sizeIndex));
                                            if (stock >= 1) {
                                                canAddToCart = true;
                                                if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
                                                    stockTextView = few_left_shirt;
                                                } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
                                                    stockTextView = few_left_jeans;
                                                } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
                                                    stockTextView = few_left_shoes;
                                                }
                                            } else {
                                                StyleableToast.makeText(open_product.this, "Selected size out of stock", R.style.UptrendToast).show();
                                                btnAddToCart.setEnabled(false);
                                                checkProductStock(stockTextView, stock);
                                            }
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("OpenProduct", "Error parsing stock or size: " + e.getMessage());
                                        StyleableToast.makeText(open_product.this, "Error checking stock", R.style.UptrendToast).show();
                                        return;
                                    }
                                } else {
                                    try {
                                        int stock = Integer.parseInt(product.getTotalStock());
                                        if (stock >= 1) {
                                            canAddToCart = true;
                                        } else {
                                            StyleableToast.makeText(open_product.this, "Product out of stock", R.style.UptrendToast).show();
                                            btnAddToCart.setEnabled(false);
                                            checkProductStock(txtOtherCategory, stock);
                                        }
                                    } catch (NumberFormatException e) {
                                        Log.e("OpenProduct", "Error parsing stock: " + e.getMessage());
                                        StyleableToast.makeText(open_product.this, "Error checking stock", R.style.UptrendToast).show();
                                        return;
                                    }
                                }

                                if (canAddToCart) {
                                    cart.setAdminId(product.getAdminId());
                                    cart.setProductId(productId);
                                    cart.setUserId(firebaseUser.getUid());
                                    cart.setQty("1");
                                    cart.setProductSize(selectedSize != null ? selectedSize : "0");
                                    cart.setOriginalPrice(product.getOriginalPrice());
                                    cart.setSellingPrice(product.getSellingPrice());
                                    cartRef.push().setValue(cart).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            StyleableToast.makeText(open_product.this, "Product added to cart", R.style.UptrendToast).show();
                                            btnAddToCart.setEnabled(false);
                                            updateCartUI(); // Update UI after adding to cart
                                        } else {
                                            StyleableToast.makeText(open_product.this, "Failed to add to cart", R.style.UptrendToast).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                StyleableToast.makeText(open_product.this, "Error checking stock", R.style.UptrendToast).show();
                                Log.e("OpenProduct", "Stock check error: " + error.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast.makeText(open_product.this, "Error checking cart", R.style.UptrendToast).show();
                        Log.e("OpenProduct", "Cart check error: " + error.getMessage());
                    }
                });
            }
        });
        /*
                Checking Stock when user Select the Size of Product.
         */

        /*
                Displaying CheckBox Checked When Product is Available in
                WishList Node.
         */
        wishListRef = FirebaseDatabase.getInstance().getReference("WishListProduct");
        checkProductInWishList(productId, wishListRef, firebaseUser, new com.example.uptrend.Adapter.ValueEventListener() {
            @Override
            public void result(boolean alreadyProduct) {
                if (alreadyProduct) {
                    likeCheckBox.setChecked(true);
                }
            }
        });

        displayRating(productId);

    }



    private void updateCartUI() {
        productReference = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product == null) {
                        Log.e("OpenProduct", "Product data is null for productId: " + productId);
                        return;
                    }

                    TextView stockTextView = txtOtherCategory;
                    if (product.getProductCategory() != null) {
                        if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
                            stockTextView = few_left_shirt;
                        } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
                            stockTextView = few_left_jeans;
                        } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
                            stockTextView = few_left_shoes;
                        }
                    }

                    if (product.getProductSizes() != null && selectedSize != null) {
                        try {
                            int sizeIndex = Integer.parseInt(selectedSize);
                            if (sizeIndex >= 0 && sizeIndex < product.getProductSizes().size()) {
                                int stock = Integer.parseInt(product.getProductSizes().get(sizeIndex));
                                if (stock == 0) {
                                    checkProductStock(stockTextView, stock);
                                } else {
                                    stockTextView.setVisibility(View.VISIBLE);
                                    stockTextView.setText("Product already in cart");
                                    layoutOtherCategory.setVisibility(product.getProductSizes().isEmpty() ? View.VISIBLE : View.GONE);
                                }
                            }
                        } catch (NumberFormatException e) {
                            Log.e("OpenProduct", "Error parsing stock or size: " + e.getMessage());
                        }
                    } else {
                        try {
                            int stock = Integer.parseInt(product.getTotalStock());
                            if (stock == 0) {
                                checkProductStock(txtOtherCategory, stock);
                            } else {
                                txtOtherCategory.setVisibility(View.VISIBLE);
                                txtOtherCategory.setText("Product already in cart");
                                layoutOtherCategory.setVisibility(View.VISIBLE);
                            }
                        } catch (NumberFormatException e) {
                            Log.e("OpenProduct", "Error parsing stock: " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OpenProduct", "Error fetching product for UI update: " + error.getMessage());
            }
        });
    }
    public void checkProductInWishList(String productId, DatabaseReference wishListRef, FirebaseUser firebaseUser, com.example.uptrend.Adapter.ValueEventListener listener) {
        Query query = wishListRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean alreadyProduct = false;
                for (DataSnapshot productSnapShot : snapshot.getChildren()) {
                    LikeProduct likeProduct = productSnapShot.getValue(LikeProduct.class);
                    if (likeProduct != null && likeProduct.getProductId().equals(productId)) {
                        alreadyProduct = true;
                        break;
                    }
                }
                listener.result(alreadyProduct);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkAddress(String userId) {
        DatabaseReference addressReference = FirebaseDatabase.getInstance().getReference("UserAddress");
        Query addressQuery = addressReference.orderByChild("userId").equalTo(userId);
        addressQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Ensure user data is available before proceeding
                    if (user != null) {
                        Intent intent = new Intent(open_product.this, payment_product.class);
                        intent.putExtra("productId", productId);
                        intent.putExtra("productName", product.getProductName());
                        intent.putExtra("brandName", product.getProductBrandName());
                        intent.putExtra("size", selectedSize != null ? selectedSize : "0");
                        intent.putExtra("quantity", "1");
                        intent.putExtra("image", product.getProductImages().get(0));
                        intent.putExtra("price", product.getSellingPrice());
                        intent.putExtra("userMobile", user.getUserMobileNumber());
                        startActivity(intent);
                    } else {
                        // Re-fetch user data if not available
                        userReference.orderByChild("userId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                if (userSnapshot.exists()) {
                                    user = userSnapshot.getChildren().iterator().next().getValue(User.class);
                                    Intent intent = new Intent(open_product.this, payment_product.class);
                                    intent.putExtra("productId", productId);
                                    intent.putExtra("productName", product.getProductName());
                                    intent.putExtra("brandName", product.getProductBrandName());
                                    intent.putExtra("size", selectedSize != null ? selectedSize : "0");
                                    intent.putExtra("quantity", "1");
                                    intent.putExtra("image", product.getProductImages().get(0));
                                    intent.putExtra("price", product.getSellingPrice());
                                    intent.putExtra("userMobile", user.getUserMobileNumber());
                                    startActivity(intent);
                                } else {
                                    StyleableToast.makeText(open_product.this, "User data not found", R.style.UptrendToast).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                StyleableToast.makeText(open_product.this, "Error fetching user data", R.style.UptrendToast).show();
                            }
                        });
                    }
                } else {
                    Intent i = new Intent(open_product.this, address_A2C.class);
                    i.putExtra("status", "firsttime");
                    i.putExtra("activityName", "openProduct");
                    i.putExtra("productId", productId);
                    startActivity(i);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(open_product.this, "Error checking address", R.style.UptrendToast).show();
            }
        });
    }

    // Update checkAddress method

    public void addProductInWishList() {
        if (likeCheckBox.isChecked()) {
            wishListRef = FirebaseDatabase.getInstance().getReference("WishListProduct");
            checkProductInWishList(productId, wishListRef, firebaseUser, new com.example.uptrend.Adapter.ValueEventListener() {
                @Override
                public void result(boolean alreadyProduct) {
                    if (alreadyProduct) {
                    } else {
                        LikeProduct likeProduct = new LikeProduct();
                        likeProduct.setUserId(firebaseUser.getUid());
                        likeProduct.setProductId(productId);
                        likeProduct.setQty("1");
                        likeProduct.setAdminId(product.getAdminId());
                        likeProduct.setProductOriginalPrice(product.getOriginalPrice());
                        likeProduct.setProductSellingPrice(product.getSellingPrice());
                        if (product.getProductSizes() != null && selectedSize != null) {
                            likeProduct.setProductSize(selectedSize);
                        } else {
                            likeProduct.setProductSize("0");
                        }
                        wishListRef.push().setValue(likeProduct);
                    }
                }
            });
        } else {
            wishListRef = FirebaseDatabase.getInstance().getReference("WishListProduct");
            Query query = wishListRef.orderByChild("userId").equalTo(firebaseUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        LikeProduct likeProduct = dataSnapshot.getValue(LikeProduct.class);
                        if (likeProduct.getProductId().equals(productId)) {
                            DatabaseReference deleteRef = dataSnapshot.getRef();
                            deleteRef.removeValue();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }


    public boolean isRadioButtonChecked() {
        return selectedSize != null;
    }


    

    // Add method to set size background
    private void setSizeBackground(RelativeLayout textView, boolean isSelected) {
        textView.setBackgroundResource(isSelected ? R.drawable.radio_effect : R.drawable.radio_selector);
    }
    /*
            This Method Will Change Product Details According to
            Product Id. And Display It Details.
     */

    public void changeProduct(String productId) {


        if (productId == null || productId.isEmpty()) {
            Log.e("OpenProduct", "changeProduct() called with null or empty productId");
            return;
        }

        slideModels = new ArrayList<>();
        productReference = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    StyleableToast.makeText(open_product.this, "Product not found", R.style.UptrendToast).show();
                    Log.e("OpenProduct", "Product snapshot does not exist for productId: " + productId);
                    return;
                }

                product = snapshot.getValue(Product.class);
                if (product == null) {
                    StyleableToast.makeText(open_product.this, "Error loading product data", R.style.UptrendToast).show();
                    Log.e("OpenProduct", "Product data is null for productId: " + productId);
                    return;
                }

                // Update basic product details
                txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "N/A");
                txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                txtSellingPrice.setText(product.getSellingPrice() != null ? product.getSellingPrice() : "0");
                txtOriginalPrice.setText(product.getOriginalPrice() != null ? product.getOriginalPrice() : "0");
                try {
                    discount = calculateDiscountPercentage(
                            Double.parseDouble(product.getOriginalPrice() != null ? product.getOriginalPrice() : "0"),
                            Double.parseDouble(product.getSellingPrice() != null ? product.getSellingPrice() : "0")
                    );
                    DecimalFormat df = new DecimalFormat("#.##");
                    txtDiscount.setText(df.format(discount));
                } catch (NumberFormatException e) {
                    txtDiscount.setText("0");
                    Log.e("OpenProduct", "Error calculating discount: " + e.getMessage());
                }

                // Update image slider
                if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                    for (String image : product.getProductImages()) {
                        slideModels.add(new SlideModel(image, ScaleTypes.FIT));
                    }
                    imageSlider.setImageList(slideModels, ScaleTypes.FIT);
                }

                // Update other product details
                date_txt.setText(estimatedDeliveryDate());
                color_name_txt.setText(product.getProductColour() != null ? product.getProductColour() : "N/A");
                setTextViewBackgroundTint(color_txt, getColorResourceId(product.getProductColour() != null ? product.getProductColour() : ""));
                mrp_txt.setText(product.getSellingPrice() != null ? product.getSellingPrice() : "0");
                market_txt.setText(product.getProductManufactureDetails() != null ? product.getProductManufactureDetails() : "N/A");
                import_txt.setText(product.getProductPackerDetail() != null ? product.getProductPackerDetail() : "N/A");
                quntity2_txt.setText(product.getProductPacking() != null ? product.getProductPacking() : "N/A");
                weight_txt.setText(product.getProductWeight() != null ? product.getProductWeight() : "N/A");
                selection_txt.setText(product.getProductSuitFor() != null ? product.getProductSuitFor() : "N/A");
                selectedSize = null;

                // Initialize UI based on product category
                sizeCardView.setVisibility(View.GONE);
                size_shirt_layout.setVisibility(View.GONE);
                size_jeans_layout.setVisibility(View.GONE);
                size_shoes_layout.setVisibility(View.GONE);
                layout_fabric.setVisibility(View.VISIBLE);
                layout_washing.setVisibility(View.VISIBLE);
                layout_occasion.setVisibility(View.VISIBLE);
                layoutOtherCategory.setVisibility(View.GONE);
                mobileLayoutGone.setVisibility(View.VISIBLE);
                mobileLayoutVisible.setVisibility(View.GONE);
                few_left_shirt.setVisibility(View.GONE);
                few_left_jeans.setVisibility(View.GONE);
                few_left_shoes.setVisibility(View.GONE);

                // Check cart status
                cartRef = FirebaseDatabase.getInstance().getReference("Cart");
                Query productQuery = cartRef.orderByChild("userId").equalTo(firebaseUser != null ? firebaseUser.getUid() : "");
                productQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean alreadyInCart = false;
                        String cartSize = null;
                        for (DataSnapshot cartSnapshot : snapshot.getChildren()) {
                            Cart existingCart = cartSnapshot.getValue(Cart.class);
                            if (existingCart != null && existingCart.getProductId().equals(productId)) {
                                alreadyInCart = true;
                                cartSize = existingCart.getProductSize();
                                break;
                            }
                        }

                        // Update UI based on product category
                        if (product.getProductCategory() != null && (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)"))) {
                            sizeCardView.setVisibility(View.VISIBLE);
                            size_shirt_layout.setVisibility(View.VISIBLE);
                            layout_occasion.setVisibility(View.VISIBLE);
                            mobileLayoutGone.setVisibility(View.VISIBLE);
                            mobileLayoutVisible.setVisibility(View.GONE);
                            fabric_name_txt.setText(product.getProductFabric() != null ? product.getProductFabric() : "N/A");
                            washing_txt.setText(product.getProductWashcare() != null ? product.getProductWashcare() : "N/A");
                            occasion_txt.setText(product.getProductOccasion() != null ? product.getProductOccasion() : "N/A");
                            setSizeBackground(size_S1, false);
                            setSizeBackground(size_S2, false);
                            setSizeBackground(size_S3, false);
                            setSizeBackground(size_S4, false);
                            setSizeBackground(size_S5, false);
                            txtSizeChart01.setOnClickListener(v -> {
                                Intent i = new Intent(getApplicationContext(), size_chart_shirts.class);
                                i.putExtra("chart", product.getProductCategory());
                                i.putExtra("productId", productId);
                                startActivity(i);
                                finish();
                            });
                            if (alreadyInCart && cartSize != null) {
                                try {
                                    int sizeIndex = Integer.parseInt(cartSize);
                                    if (product.getProductSizes() != null && sizeIndex >= 0 && sizeIndex < product.getProductSizes().size()) {
                                        int stock = Integer.parseInt(product.getProductSizes().get(sizeIndex));
                                        checkProductStock(few_left_shirt, stock);
                                        btnAddToCart.setEnabled(false);
                                        few_left_shirt.setVisibility(View.VISIBLE);
                                        few_left_shirt.setText("Product already in cart");
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing cart size or stock: " + e.getMessage());
                                }
                            }
                        } else if (product.getProductCategory() != null && (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)"))) {
                            sizeCardView.setVisibility(View.VISIBLE);
                            size_jeans_layout.setVisibility(View.VISIBLE);
                            layout_occasion.setVisibility(View.VISIBLE);
                            mobileLayoutGone.setVisibility(View.VISIBLE);
                            mobileLayoutVisible.setVisibility(View.GONE);
                            fabric_name_txt.setText(product.getProductFabric() != null ? product.getProductFabric() : "N/A");
                            washing_txt.setText(product.getProductWashcare() != null ? product.getProductWashcare() : "N/A");
                            occasion_txt.setText(product.getProductOccasion() != null ? product.getProductOccasion() : "N/A");
                            setSizeBackground(size_J1, false);
                            setSizeBackground(size_J2, false);
                            setSizeBackground(size_J3, false);
                            setSizeBackground(size_J4, false);
                            setSizeBackground(size_J5, false);
                            setSizeBackground(size_J6, false);
                            setSizeBackground(size_J7, false);
                            txtSizeChart03.setOnClickListener(v -> {
                                Intent i = new Intent(getApplicationContext(), size_chart_jeans.class);
                                i.putExtra("chart", product.getProductCategory());
                                i.putExtra("productId", productId);
                                startActivity(i);
                                finish();
                            });
                            if (alreadyInCart && cartSize != null) {
                                try {
                                    int sizeIndex = Integer.parseInt(cartSize);
                                    if (product.getProductSizes() != null && sizeIndex >= 0 && sizeIndex < product.getProductSizes().size()) {
                                        int stock = Integer.parseInt(product.getProductSizes().get(sizeIndex));
                                        checkProductStock(few_left_jeans, stock);
                                        btnAddToCart.setEnabled(false);
                                        few_left_jeans.setVisibility(View.VISIBLE);
                                        few_left_jeans.setText("Product already in cart");
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing cart size or stock: " + e.getMessage());
                                }
                            }
                        } else if (product.getProductCategory() != null && (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)"))) {
                            sizeCardView.setVisibility(View.VISIBLE);
                            size_shoes_layout.setVisibility(View.VISIBLE);
                            layout_fabric.setVisibility(View.GONE);
                            layout_washing.setVisibility(View.GONE);
                            mobileLayoutGone.setVisibility(View.VISIBLE);
                            mobileLayoutVisible.setVisibility(View.GONE);
                            setSizeBackground(size_SH1, false);
                            setSizeBackground(size_SH2, false);
                            setSizeBackground(size_SH3, false);
                            setSizeBackground(size_SH4, false);
                            setSizeBackground(size_SH5, false);
                            txtSizeChart02.setOnClickListener(v -> {
                                Intent i = new Intent(getApplicationContext(), size_chart_shoes.class);
                                i.putExtra("chart", product.getProductCategory());
                                i.putExtra("productId", productId);
                                startActivity(i);
                                finish();
                            });
                            if (alreadyInCart && cartSize != null) {
                                try {
                                    int sizeIndex = Integer.parseInt(cartSize);
                                    if (product.getProductSizes() != null && sizeIndex >= 0 && sizeIndex < product.getProductSizes().size()) {
                                        int stock = Integer.parseInt(product.getProductSizes().get(sizeIndex));
                                        checkProductStock(few_left_shoes, stock);
                                        btnAddToCart.setEnabled(false);
                                        few_left_shoes.setVisibility(View.VISIBLE);
                                        few_left_shoes.setText("Product already in cart");
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing cart size or stock: " + e.getMessage());
                                }
                            }
                        } else if (product.getProductSubCategory() != null && product.getProductSubCategory().equals("Smartphones")) {
                            mobileLayoutGone.setVisibility(View.GONE);
                            layoutOtherCategory.setVisibility(View.GONE);
                            mobileLayoutVisible.setVisibility(View.VISIBLE);
                            ram_txt.setText(product.getRam() != null ? product.getRam() : "N/A");
                            rom_storage_txt.setText(product.getStorage() != null ? product.getStorage() : "N/A");
                            processor_txt.setText(product.getProcessor() != null ? product.getProcessor() : "N/A");
                            rear_camera_txt.setText(product.getRearCamera() != null ? product.getRearCamera() : "N/A");
                            front_camera_txt.setText(product.getFrontCamera() != null ? product.getFrontCamera() : "N/A");
                            battery_txt.setText(product.getBattery() != null ? product.getBattery() : "N/A");
                            if (alreadyInCart) {
                                try {
                                    int stock = Integer.parseInt(product.getTotalStock());
                                    checkProductStock(txtOtherCategory, stock);
                                    btnAddToCart.setEnabled(false);
                                    layoutOtherCategory.setVisibility(View.VISIBLE);
                                    txtOtherCategory.setVisibility(View.VISIBLE);
                                    txtOtherCategory.setText("Product already in cart");
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing stock: " + e.getMessage());
                                }
                            } else {
                                try {
                                    int stock = Integer.parseInt(product.getTotalStock());
                                    checkProductStock(txtOtherCategory, stock);
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing stock: " + e.getMessage());
                                }
                            }
                        } else {
                            mobileLayoutGone.setVisibility(View.VISIBLE);
                            mobileLayoutVisible.setVisibility(View.GONE);
                            layout_fabric.setVisibility(View.GONE);
                            layout_washing.setVisibility(View.GONE);
                            layout_occasion.setVisibility(View.GONE);
                            if (alreadyInCart) {
                                try {
                                    int stock = Integer.parseInt(product.getTotalStock());
                                    checkProductStock(txtOtherCategory, stock);
                                    btnAddToCart.setEnabled(false);
                                    layoutOtherCategory.setVisibility(View.VISIBLE);
                                    txtOtherCategory.setVisibility(View.VISIBLE);
                                    txtOtherCategory.setText("Product already in cart");
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing stock: " + e.getMessage());
                                }
                            } else {
                                try {
                                    int stock = Integer.parseInt(product.getTotalStock());
                                    checkProductStock(txtOtherCategory, stock);
                                } catch (NumberFormatException e) {
                                    Log.e("OpenProduct", "Error parsing stock: " + e.getMessage());
                                }
                            }
                        }
                        suggestionProduct(product.getProductCategory() != null ? product.getProductCategory() : "");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OpenProduct", "Cart check error: " + error.getMessage());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(open_product.this, "Error loading product", R.style.UptrendToast).show();
                Log.e("OpenProduct", "Product fetch error: " + error.getMessage());
            }
        });
    }
    /*
        suggestionProduct Method Will display Product According to its suggestions
     */

    public void suggestionProduct(String category) {
        suggestedProducts = new ArrayList<>();
        productRootNodeReference = FirebaseDatabase.getInstance().getReference("Product");
        categoryQuery = productRootNodeReference.orderByChild("productCategory").equalTo(category);
        categoryQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot productSnapShot : snapshot.getChildren()) {
                    Product product1 = productSnapShot.getValue(Product.class);
                    suggestedProducts.add(product1);
                }
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(open_product.this, LinearLayoutManager.HORIZONTAL, false);
                suggestionProductAdapter = new SuggestionProductAdapter(open_product.this, suggestedProducts, open_product.this);
                recyclerViewSuggestedProduct.setLayoutManager(linearLayoutManager);
                recyclerViewSuggestedProduct.setAdapter(suggestionProductAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // This Method Will CheckProductStock and Display error massage

    public void checkProductStock(TextView txtErrorMessage, int stock) {
        if (stock <= 3 && stock >= 1) {
            txtErrorMessage.setVisibility(View.VISIBLE);
            txtErrorMessage.setText("Hurry, only few items left");
            btnBuyNow.setEnabled(true);
            btnAddToCart.setEnabled(true);
        } else if (stock == 0) {
            txtErrorMessage.setVisibility(View.VISIBLE);
            txtErrorMessage.setText("Out of stock");
            btnBuyNow.setEnabled(false);
            btnAddToCart.setEnabled(false);
        } else {
            txtErrorMessage.setVisibility(View.GONE);
            btnBuyNow.setEnabled(true);
            btnAddToCart.setEnabled(true);
        }
    }

    public double calculateDiscountPercentage(double originalPrice, double sellingPrice) {
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }

    public void displayReviews(String productId, ReviewAdapter adapter) {
        DatabaseReference reviewRef = FirebaseDatabase.getInstance().getReference("Review");
        Query productReviewQuery = reviewRef.orderByChild("productId").equalTo(productId);
        productReviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Review> reviews = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Review review = dataSnapshot.getValue(Review.class);
                    reviews.add(review);
                }
                // Show only the first review initially, or all if showAllReviews is true
                ArrayList<Review> displayedReviews = new ArrayList<>();
                if (!reviews.isEmpty()) {
                    displayedReviews.add(reviews.get(0)); // Add the first review
                    if (showAllReviews) {
                        displayedReviews.addAll(reviews.subList(1, reviews.size())); // Add remaining reviews
                    }
                }
                adapter.updateReviews(displayedReviews);
                // Update visibility of other_rating_show TextView
                TextView otherRatingShow = findViewById(R.id.other_rating_show);
                if (reviews.size() > 1 && !showAllReviews) {
                    otherRatingShow.setVisibility(View.VISIBLE);
                    otherRatingShow.setText("Show " + (reviews.size() - 1) + " more reviews");
                } else {
                    otherRatingShow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("OpenProduct", "Error fetching reviews: " + error.getMessage());
            }
        });
    }
    public void displayRating(String productId){
        DatabaseReference reviewRef=FirebaseDatabase.getInstance().getReference("Review");
        Query productReviewQuery=reviewRef.orderByChild("productId").equalTo(productId);
        productReviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total=0;
                rating=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    totalReview= (int) snapshot.getChildrenCount();
                    Review review=dataSnapshot.getValue(Review.class);
                    rating= Float.parseFloat(review.getProductStar());
                    total+=rating;
                }
                if(totalReview==0){
                    rating_txt.setText("0.0");
                    ratingBar.setRating(total/totalReview);
                    ratingBar_TOTAL.setText(String.valueOf(totalReview));
                    txtRating.setText("0.0");
                    txtRatingCount.setText("0");
                }else{
                    rating_txt.setText(String.valueOf(total/totalReview));
                    ratingBar.setRating(total/totalReview);
                    ratingBar_TOTAL.setText(String.valueOf(totalReview));
                    txt_rating.setText(String.valueOf(total/totalReview));
                    txtRatingCount.setText(String.valueOf(totalReview));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void gotoUrl(String s) {
        try {
            Uri uri = Uri.parse(s);
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "no website link", Toast.LENGTH_SHORT).show();
        }

    }

    public String estimatedDeliveryDate() {
        String formattedEstimatedDeliveryDate = "";
        LocalDate todayDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            todayDate = LocalDate.now();
            LocalDate estimatedDeliveryDate = todayDate.plusDays(4);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, EEEE");
            formattedEstimatedDeliveryDate = estimatedDeliveryDate.format(formatter);

        }
        return formattedEstimatedDeliveryDate;
    }

    //This Method Will Change The Textview BackgroundTint According which Colour is Selected.
    @SuppressLint("RestrictedApi")
    private void setTextViewBackgroundTint(TextView textView, int colorResId) {
        // Use AppCompatTextView if you're working with the AppCompat library
        if (textView instanceof AppCompatTextView) {
            ((AppCompatTextView) textView).setSupportBackgroundTintList(
                    ContextCompat.getColorStateList(this, colorResId)
            );
        } else {
            // For standard TextView
            textView.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, colorResId)
            );
        }
    }

    private int getColorResourceId(String colorName) {
        // Map color names to color resource IDs
        switch (colorName) {
            case "Aquamarine":
                return R.color.Aquamarine;
            case "Azure":
                return R.color.Azure;
            case "Black":
                return R.color.black;
            case "Brown":
                return R.color.Brown;
            case "Coral":
                return R.color.Coral;
            case "Crimson":
                return R.color.Crimson;
            case "Cyan":
                return R.color.Cyan;
            case "Golden":
                return R.color.Golden;
            case "Gray":
                return R.color.Gray;
            case "Green":
                return R.color.Green;
            case "Hot Pink":
                return R.color.Hot_Pink;
            case "Lime":
                return R.color.Lime;
            case "Magent":
                return R.color.Magent;
            case "Maroon":
                return R.color.Maroon;
            case "Navy Blue":
                return R.color.Navy_Blue;
            case "Olive":
                return R.color.Olive;
            case "Orange":
                return R.color.Orange;
            case "Purple":
                return R.color.Purple;
            case "Red":
                return R.color.red;
            case "Royal Blue":
                return R.color.Royal_Blue;
            case "Silver":
                return R.color.Silver;
            case "Teal":
                return R.color.Teal;
            case "Wheat":
                return R.color.Wheat;
            case "White":
                return R.color.white;
            case "Yellow":
                return R.color.yellow;
            default:
                return R.color.transparent;
        }
    }

    @Override
    public void ItemOnClickListener(String productId) {

        changeProduct(productId);
        scrollView.fullScroll(View.FOCUS_UP);
    }

    private void navigateBack() {
        Intent intent;
        String callingActivity = getIntent().getStringExtra("activityName");
        String searchQuery = getIntent().getStringExtra("searchQuery");
        String sortBy = getIntent().getStringExtra("sortBy");
        String value = getIntent().getStringExtra("value");
        String brandName = getIntent().getStringExtra("brandName");

        if (callingActivity == null) {
            intent = new Intent(getApplicationContext(), home.class);
        } else {
            switch (callingActivity) {
                case "searchProduct":
                    intent = new Intent(getApplicationContext(), search_product.class);
                    intent.putExtra("activityName", "openProduct");
                    intent.putExtra("searchQuery", searchQuery);
                    break;
                case "addToCartProduct":
                    intent = new Intent(getApplicationContext(), add_to_cart_product.class);
                    break;
                case "openCategoryProduct":
                    intent = new Intent(getApplicationContext(), open_category_product.class);
                    intent.putExtra("sortBy", sortBy);
                    intent.putExtra("value", value);
                    break;
                case "openBrandProduct":
                    intent = new Intent(getApplicationContext(), open_brand_product.class);
                    intent.putExtra("brandName", brandName);
                    break;
                case "notification":
                    intent = new Intent(getApplicationContext(), notification.class);
                    break;
                case "ratingProducts":
                    intent = new Intent(getApplicationContext(), rating_products.class);
                    break;
                case "wishlistProduct":
                    intent = new Intent(getApplicationContext(), wishlist_poduct.class);
                    break;
                case "accountUser":
                    intent = new Intent(getApplicationContext(), account_user.class);
                    break;
                default:
                    intent = new Intent(getApplicationContext(), home.class);
                    break;
            }
        }
        startActivity(intent);
        finish();
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        addProductInWishList();
        navigateBack();
        finish();
    }
}