package com.example.uptrendseller;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class edit_product extends AppCompatActivity {
    ArrayList<SlideModel> slideModelsArrayList;
    AppCompatButton btnClearImages, btnAddImage;
    loadingDialog dialog;
    private int count = 0, total = 0;
    private StorageReference storageReference;
    private ArrayList<Uri> imagesUri = new ArrayList<>();
    private EditText pack_of, ram_mobile, storage_mobile, processor_mobile, rear_camera_mobile, front_camera_mobile, battery_mobile;
    private AutoCompleteTextView weight_product;
    private MaterialAutoCompleteTextView fabricType, selectionType, fabric_careType, idealType;
    private LinearLayout mobileDetailLayout, fabric_gone, selection_gone, fabric_care_gone, colour_gone,cardView;
    private RadioGroup colourRadioGroup;
    private TextView get_color_name, get_color, instruction_click;

    private TextView txtSavedDate, txtEditDate;

    private TextView edit_txt_minus_amount, edit_txt_discount,edit_product_id_txt;
    private String savedAmount = "0", discountPercent = "0";
    private LinearLayout search_word_linear1, search_word_linear2, search_word_linear3,
            search_word_linear4, search_word_linear5, otherCategoryLayout, shirtLayout, jeansLayout, footWareLayout;

    private RelativeLayout search_gone2, search_gone3, search_gone4, search_gone5;
    private EditText search_wordET1, search_wordET2, search_wordET3, search_wordET4, search_wordET5, txtBrandName, txtProductName, txtSellingPrice, txtOriginalPrice;
    private TextView plus_click1, plus_click2, plus_click3, plus_click4, txtValueOtherCategory, txtTotalStockOtherCategory,
            txtS, txtM, txtL, txtXL, txtXXL, txtTotalStockShirt, txt28, txt30, txt32, txt34, txt36, txt38, txt40, txtTotalStockJeans,
            txt6, txt7, txt8, txt9, txt10, txtTotalStockFootWare, btnUpdate, close_btn;
    private String productId;
    private DatabaseReference productRef;
    private Product product;
    private ImageSlider productImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.green_meee));
        }


        search_wordET1 = findViewById(R.id.search_keyword11);
        search_wordET2 = findViewById(R.id.search_keyword22);
        search_wordET3 = findViewById(R.id.search_keyword33);
        search_wordET4 = findViewById(R.id.search_keyword44);
        search_wordET5 = findViewById(R.id.search_keyword55);

        plus_click1 = findViewById(R.id.plus_click11);
        plus_click2 = findViewById(R.id.plus_click22);
        plus_click3 = findViewById(R.id.plus_click33);
        plus_click4 = findViewById(R.id.plus_click44);

        search_gone2 = findViewById(R.id.search_key_hide_gone22);
        search_gone3 = findViewById(R.id.search_key_hide_gone33);
        search_gone4 = findViewById(R.id.search_key_hide_gone44);
        search_gone5 = findViewById(R.id.search_key_hide_gone55);

        txtBrandName = findViewById(R.id.txtBrandName);
        txtProductName = findViewById(R.id.txtProductName);
        txtSellingPrice = findViewById(R.id.txtSellingPrice);
        txtOriginalPrice = findViewById(R.id.txtOriginalPrice);
        productImage = findViewById(R.id.imageSlider);

        otherCategoryLayout = findViewById(R.id.other_categoryED);
        shirtLayout = findViewById(R.id.layoutShirtED);
        jeansLayout = findViewById(R.id.layoutPantED);
        footWareLayout = findViewById(R.id.layoutFootWareED);

        txtValueOtherCategory = findViewById(R.id.value55);
        txtTotalStockOtherCategory = findViewById(R.id.totalStockOtherCategory);

        txtS = findViewById(R.id.value);
        txtM = findViewById(R.id.value2);
        txtL = findViewById(R.id.value3);
        txtXL = findViewById(R.id.value4);
        txtXXL = findViewById(R.id.value5);
        txtTotalStockShirt = findViewById(R.id.stock_shirt);

        txt28 = findViewById(R.id.value6);
        txt30 = findViewById(R.id.value7);
        txt32 = findViewById(R.id.value8);
        txt34 = findViewById(R.id.value9);
        txt36 = findViewById(R.id.value10);
        txt38 = findViewById(R.id.value11);
        txt40 = findViewById(R.id.value12);
        txtTotalStockJeans = findViewById(R.id.stock_pant);

        txt6 = findViewById(R.id.value13);
        txt7 = findViewById(R.id.value14);
        txt8 = findViewById(R.id.value15);
        txt9 = findViewById(R.id.value16);
        txt10 = findViewById(R.id.value17);
        txtTotalStockFootWare = findViewById(R.id.stock_shoes);

        btnUpdate = findViewById(R.id.btnUpdate);
        btnClearImages = findViewById(R.id.btnClearImages);
        btnAddImage = findViewById(R.id.btnAddImage);
        close_btn = findViewById(R.id.close_btn);


        // Add after existing findViewById calls
        pack_of = findViewById(R.id.pack_of);
        weight_product = findViewById(R.id.weight_product);
        idealType = findViewById(R.id.idealType);
        fabricType = findViewById(R.id.fabricType);
        selectionType = findViewById(R.id.selectionType);
        fabric_careType = findViewById(R.id.fabric_careType);

        get_color_name = findViewById(R.id.get_color_name);
        get_color = findViewById(R.id.get_color);
        instruction_click = findViewById(R.id.instruction_click);
        colourRadioGroup = findViewById(R.id.colourRadioGroup);
        colour_gone = findViewById(R.id.instruction_click_layout);
        cardView = findViewById(R.id.cardView); // Make sure this is defined

       // Mobile detail fields
        ram_mobile = findViewById(R.id.ram_mobile);
        storage_mobile = findViewById(R.id.storage_mobile);
        processor_mobile = findViewById(R.id.processor_mobile);
        rear_camera_mobile = findViewById(R.id.rear_camera_mobile);
        front_camera_mobile = findViewById(R.id.front_camera_mobile);
        battery_mobile = findViewById(R.id.battery_mobile);

       // Layouts
        mobileDetailLayout = findViewById(R.id.mobileDetailLayout);
        fabric_gone = findViewById(R.id.fabric_gone);
        selection_gone = findViewById(R.id.selection_gone);
        fabric_care_gone = findViewById(R.id.fabric_care_gone);

        edit_txt_minus_amount = findViewById(R.id.edit_txt_minus_amount);
        edit_txt_discount = findViewById(R.id.edit_txt_discount);
        edit_product_id_txt = findViewById(R.id.edit_product_id_txt);  // ← ADD THIS LINE

        txtSavedDate = findViewById(R.id.get_product_saved_date);
        txtEditDate = findViewById(R.id.get_product_edit_date);


        //Getting Data From Previous Activity And Displaying ProductData.
        productId = getIntent().getStringExtra("productId");
        displayProductDetails(productId);
        highlightLowQuantitySizes();

        // In onCreate, after initializing views
        close_btn.setOnClickListener(v -> handleBackPress());


        // 3. Click listeners (perfect & clean)
        plus_click1.setOnClickListener(v -> {
            search_gone2.setVisibility(View.VISIBLE);
            plus_click1.setVisibility(View.GONE); // Hide plus button after clicking
        });

        plus_click2.setOnClickListener(v -> {
            search_gone3.setVisibility(View.VISIBLE);
            plus_click2.setVisibility(View.GONE); // Hide plus button after clicking
        });

        plus_click3.setOnClickListener(v -> {
            search_gone4.setVisibility(View.VISIBLE);
            plus_click3.setVisibility(View.GONE); // Hide plus button after clicking
        });

        plus_click4.setOnClickListener(v -> {
            search_gone5.setVisibility(View.VISIBLE);
            plus_click4.setVisibility(View.GONE); // Hide plus button after clicking
        });


        instruction_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (cardView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(colour_gone, new AutoTransition());
                cardView.setVisibility(var);
            }
        });

        colourRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                RadioButton radioButton = radioGroup.findViewById(id);
                get_color_name.setText(radioButton.getText().toString());
                int colorResId = getColorResourceId(radioButton.getText().toString());
                setTextViewBackgroundTint(get_color, colorResId);
                cardView.setVisibility(View.GONE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Validate product data before proceeding
                if (!validateProductData()) {
                    return; // Stop execution if validation fails
                }

                dialog = new loadingDialog(edit_product.this);
                dialog.show();

                // Calculate discount
                calculateDiscount();

                // Prepare map with discount data
                Map<String, Object> updates = new HashMap<>();
                updates.put("savedAmount", savedAmount);
                updates.put("discountPercent", discountPercent + "%");

                // Update discount data first
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Product").child(productId);
                ref.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Now update all product details
                        updateDataForBilling(productId);
                    } else {
                        dialog.cancel();
                        StyleableToast.makeText(getApplicationContext(), "Update failed: " + task.getException().getMessage(), R.style.UptrendToast).show();
                    }
                }).addOnFailureListener(e -> {
                    dialog.cancel();
                    StyleableToast.makeText(getApplicationContext(), "Update failed: " + e.getMessage(), R.style.UptrendToast).show();
                });
            }
        });


        btnClearImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                product.getProductImages().clear();
                imagesUri.clear();
                productImage.setImageList(new ArrayList<>(), ScaleTypes.FIT);
            }
        });

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                }
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "select picture"), 1);
            }
        });


        txtOriginalPrice.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateDiscount(); }
        });

        txtSellingPrice.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateDiscount(); }
        });


    }


    public void updateDataForBilling(String productId) {
        ArrayList<String> searchKeyWord = new ArrayList<>();
        ArrayList<String> productSizes = new ArrayList<>();
        searchKeyWord.add(search_wordET1.getText().toString());
        searchKeyWord.add(search_wordET2.getText().toString());
        searchKeyWord.add(search_wordET3.getText().toString());
        searchKeyWord.add(search_wordET4.getText().toString());
        searchKeyWord.add(search_wordET5.getText().toString());

        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);

        // Create update map
        Map<String, Object> updates = new HashMap<>();

        // CRITICAL: Update timestamp with current time in milliseconds
        updates.put("timestamp", System.currentTimeMillis());

        // Also update date (for display)
        updates.put("productLastUpdatedDate", DateHelper.getCurrentDate());

        // Add time to the date if DateHelper supports it
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        String dateWithTime = sdf.format(new Date());
        updates.put("productLastUpdatedDateTime", dateWithTime);
        // Basic product info
        updates.put("productName", txtProductName.getText().toString());
        updates.put("productBrandName", txtBrandName.getText().toString());
        updates.put("sellingPrice", txtSellingPrice.getText().toString());
        updates.put("originalPrice", txtOriginalPrice.getText().toString());
        updates.put("searchKeyWord", searchKeyWord);
        updates.put("productPacking", pack_of.getText().toString());
        updates.put("productWeight", weight_product.getText().toString());
        updates.put("productSuitFor", idealType.getText().toString());
        updates.put("productColour", get_color_name.getText().toString());
        updates.put("productLastUpdatedDate", DateHelper.getCurrentDate());

        // Category-specific updates
        if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
            updates.put("productFabric", fabricType.getText().toString());
            updates.put("productOccasion", selectionType.getText().toString());
            updates.put("productWashcare", fabric_careType.getText().toString());

            productSizes.add(txtS.getText().toString());
            productSizes.add(txtM.getText().toString());
            productSizes.add(txtL.getText().toString());
            productSizes.add(txtXL.getText().toString());
            productSizes.add(txtXXL.getText().toString());
            updates.put("productSizes", productSizes);
            updates.put("totalStock", txtTotalStockShirt.getText().toString());

        } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
            updates.put("productFabric", fabricType.getText().toString());
            updates.put("productOccasion", selectionType.getText().toString());
            updates.put("productWashcare", fabric_careType.getText().toString());

            productSizes.add(txt28.getText().toString());
            productSizes.add(txt30.getText().toString());
            productSizes.add(txt32.getText().toString());
            productSizes.add(txt34.getText().toString());
            productSizes.add(txt36.getText().toString());
            productSizes.add(txt38.getText().toString());
            productSizes.add(txt40.getText().toString());
            updates.put("productSizes", productSizes);
            updates.put("totalStock", txtTotalStockJeans.getText().toString());

        } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
            productSizes.add(txt6.getText().toString());
            productSizes.add(txt7.getText().toString());
            productSizes.add(txt8.getText().toString());
            productSizes.add(txt9.getText().toString());
            productSizes.add(txt10.getText().toString());
            updates.put("productSizes", productSizes);
            updates.put("totalStock", txtTotalStockFootWare.getText().toString());

        } else if (product.getProductSubCategory() != null && product.getProductSubCategory().equals("Smartphones")) {
            updates.put("ram", ram_mobile.getText().toString());
            updates.put("storage", storage_mobile.getText().toString());
            updates.put("processor", processor_mobile.getText().toString());
            updates.put("rearCamera", rear_camera_mobile.getText().toString());
            updates.put("frontCamera", front_camera_mobile.getText().toString());
            updates.put("battery", battery_mobile.getText().toString());
            updates.put("totalStock", txtTotalStockOtherCategory.getText().toString());

        } else {
            updates.put("totalStock", txtTotalStockOtherCategory.getText().toString());
        }

        // Update all data at once
        productRef.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.cancel();

                if (task.isSuccessful()) {
                    // Navigate to billing activity instead of inventory
                    Intent billingIntent = new Intent(edit_product.this, edit_product_billing.class);
                    billingIntent.putExtra("productId", productId);
                    startActivity(billingIntent);
                    finish();
                } else {
                    StyleableToast.makeText(getApplicationContext(),
                            "Failed to update product: " + task.getException().getMessage(),
                            R.style.UptrendToast).show();
                }
            }
        });
    }

    private void calculateDiscount() {
        String originalStr = txtOriginalPrice.getText().toString().trim();
        String sellingStr = txtSellingPrice.getText().toString().trim();

        if (originalStr.isEmpty() || sellingStr.isEmpty()) {
            edit_txt_minus_amount.setText("You Save: ₹0");
            edit_txt_discount.setText("0% OFF");
            savedAmount = "0";
            discountPercent = "0";
            return;
        }

        try {
            double original = Double.parseDouble(originalStr);
            double selling = Double.parseDouble(sellingStr);

            if (original <= 0) {
                edit_txt_minus_amount.setText("Invalid Price");
                edit_txt_discount.setText("0% OFF");
                return;
            }

            double saved = original - selling;
            double discount = (saved / original) * 100;

            if (saved >= 0) {
                edit_txt_minus_amount.setText("You Save: ₹" + String.format("%.0f", saved));
            } else {
                edit_txt_minus_amount.setText("Extra Charge: ₹" + String.format("%.0f", -saved));
            }

            String discountSign = discount >= 0 ? "" : "-";
            edit_txt_discount.setText(discountSign + String.format("%.0f", Math.abs(discount)) + "% OFF");

            savedAmount = String.valueOf((int) saved);
            discountPercent = String.valueOf((int) discount);

        } catch (Exception e) {
            edit_txt_minus_amount.setText("Invalid Input");
            edit_txt_discount.setText("0% OFF");
            savedAmount = "0";
            discountPercent = "0";
        }
    }

    private boolean validateProductData() {
        // 1. Brand Name validation
        if (TextUtils.isEmpty(txtBrandName.getText().toString().trim())) {
            ChangeColour.errorColour(getApplicationContext(), txtBrandName, "Brand name is required");
            StyleableToast.makeText(this, "Brand name is required", R.style.UptrendToast).show();
            return false;
        }

        // 2. Product Name validation
        if (TextUtils.isEmpty(txtProductName.getText().toString().trim())) {
            ChangeColour.errorColour(getApplicationContext(), txtProductName, "Product name is required");
            StyleableToast.makeText(this, "Product name is required", R.style.UptrendToast).show();
            return false;
        }

        // 3. Original Price validation
        String originalPriceText = txtOriginalPrice.getText().toString().trim();
        if (TextUtils.isEmpty(originalPriceText)) {
            ChangeColour.errorColour(getApplicationContext(), txtOriginalPrice, "Original price is required");
            StyleableToast.makeText(this, "Original price is required", R.style.UptrendToast).show();
            return false;
        } else {
            try {
                double originalPrice = Double.parseDouble(originalPriceText);
                if (originalPrice <= 0) {
                    ChangeColour.errorColour(getApplicationContext(), txtOriginalPrice, "Price must be greater than 0");
                    StyleableToast.makeText(this, "Original price must be greater than 0", R.style.UptrendToast).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                ChangeColour.errorColour(getApplicationContext(), txtOriginalPrice, "Enter valid price");
                StyleableToast.makeText(this, "Enter valid original price", R.style.UptrendToast).show();
                return false;
            }
        }

        // 4. Selling Price validation
        String sellingPriceText = txtSellingPrice.getText().toString().trim();
        if (TextUtils.isEmpty(sellingPriceText)) {
            ChangeColour.errorColour(getApplicationContext(), txtSellingPrice, "Selling price is required");
            StyleableToast.makeText(this, "Selling price is required", R.style.UptrendToast).show();
            return false;
        } else {
            try {
                double sellingPrice = Double.parseDouble(sellingPriceText);
                if (sellingPrice <= 0) {
                    ChangeColour.errorColour(getApplicationContext(), txtSellingPrice, "Price must be greater than 0");
                    StyleableToast.makeText(this, "Selling price must be greater than 0", R.style.UptrendToast).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                ChangeColour.errorColour(getApplicationContext(), txtSellingPrice, "Enter valid price");
                StyleableToast.makeText(this, "Enter valid selling price", R.style.UptrendToast).show();
                return false;
            }
        }

        // 5. Product Packing validation
        if (TextUtils.isEmpty(pack_of.getText().toString().trim())) {
            ChangeColour.errorColour(getApplicationContext(), pack_of, "Pack quantity is required");
            StyleableToast.makeText(this, "Pack quantity is required", R.style.UptrendToast).show();
            return false;
        } else {
            try {
                int packQty = Integer.parseInt(pack_of.getText().toString().trim());
                if (packQty <= 0) {
                    ChangeColour.errorColour(getApplicationContext(), pack_of, "Quantity must be greater than 0");
                    StyleableToast.makeText(this, "Pack quantity must be greater than 0", R.style.UptrendToast).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                ChangeColour.errorColour(getApplicationContext(), pack_of, "Enter valid quantity");
                StyleableToast.makeText(this, "Enter valid pack quantity", R.style.UptrendToast).show();
                return false;
            }
        }

        // 6. Fabric validation (only for Top/Bottom categories)
        if (product != null && (product.getProductCategory().equals("Men's(Top)") ||
                product.getProductCategory().equals("Women's(Top)") ||
                product.getProductCategory().equals("Men's(Bottom)") ||
                product.getProductCategory().equals("Women's(Bottom)"))) {

            if (TextUtils.isEmpty(fabricType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select fabric type", R.style.UptrendToast).show();
                return false;
            }

            if (TextUtils.isEmpty(selectionType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select occasion type", R.style.UptrendToast).show();
                return false;
            }

            if (TextUtils.isEmpty(fabric_careType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select fabric care", R.style.UptrendToast).show();
                return false;
            }
        }

        // 7. Ideal For validation
        if (TextUtils.isEmpty(idealType.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please select ideal for", R.style.UptrendToast).show();
            return false;
        }

        // 8. Selection For validation (only for Top/Bottom categories)
        if (product != null && (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)") ||
                product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)"))) {
            if (TextUtils.isEmpty(selectionType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select occasion type", R.style.UptrendToast).show();
                return false;
            }
        }

        // 9. Fabric Care validation (only for Top/Bottom categories)
        if (product != null && (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)") ||
                product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)"))) {
            if (TextUtils.isEmpty(fabric_careType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select fabric care", R.style.UptrendToast).show();
                return false;
            }
        }

        // 10. Weight validation
        if (TextUtils.isEmpty(weight_product.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please select product weight", R.style.UptrendToast).show();
            return false;
        }

        // 11. Search Keyword validation
        if (TextUtils.isEmpty(search_wordET1.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET2.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET3.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET4.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET5.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please enter at least one search keyword", R.style.UptrendToast).show();
            return false;
        }

        // 12. Product Colour validation
        if (TextUtils.isEmpty(get_color_name.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please select product colour", R.style.UptrendToast).show();
            return false;
        }

        // 13. Mobile fields validation (only for Smartphones)
        if (product != null && product.getProductSubCategory() != null && product.getProductSubCategory().equals("Smartphones")) {
            if (TextUtils.isEmpty(ram_mobile.getText().toString().trim())) {
                ChangeColour.errorColour(getApplicationContext(), ram_mobile, "RAM is required");
                StyleableToast.makeText(this, "RAM is required", R.style.UptrendToast).show();
                return false;
            }
            if (TextUtils.isEmpty(storage_mobile.getText().toString().trim())) {
                ChangeColour.errorColour(getApplicationContext(), storage_mobile, "Storage is required");
                StyleableToast.makeText(this, "Storage is required", R.style.UptrendToast).show();
                return false;
            }
            if (TextUtils.isEmpty(processor_mobile.getText().toString().trim())) {
                ChangeColour.errorColour(getApplicationContext(), processor_mobile, "Processor is required");
                StyleableToast.makeText(this, "Processor is required", R.style.UptrendToast).show();
                return false;
            }
            if (TextUtils.isEmpty(rear_camera_mobile.getText().toString().trim())) {
                ChangeColour.errorColour(getApplicationContext(), rear_camera_mobile, "Rear camera is required");
                StyleableToast.makeText(this, "Rear camera is required", R.style.UptrendToast).show();
                return false;
            }
            if (TextUtils.isEmpty(front_camera_mobile.getText().toString().trim())) {
                ChangeColour.errorColour(getApplicationContext(), front_camera_mobile, "Front camera is required");
                StyleableToast.makeText(this, "Front camera is required", R.style.UptrendToast).show();
                return false;
            }
            if (TextUtils.isEmpty(battery_mobile.getText().toString().trim())) {
                ChangeColour.errorColour(getApplicationContext(), battery_mobile, "Battery is required");
                StyleableToast.makeText(this, "Battery is required", R.style.UptrendToast).show();
                return false;
            }
        }

        return true;
    }
    public void updateData(String productId) {
        ArrayList<String> searchKeyWord = new ArrayList<>();
        ArrayList<String> productSizes = new ArrayList<>();
        searchKeyWord.add(search_wordET1.getText().toString());
        searchKeyWord.add(search_wordET2.getText().toString());
        searchKeyWord.add(search_wordET3.getText().toString());
        searchKeyWord.add(search_wordET4.getText().toString());
        searchKeyWord.add(search_wordET5.getText().toString());

        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        DatabaseReference productNameRef = productRef.child("productName");
        DatabaseReference brandNameRef = productRef.child("productBrandName");
        DatabaseReference sellingPriceRef = productRef.child("sellingPrice");
        DatabaseReference originalPriceRef = productRef.child("originalPrice");
        DatabaseReference totalStockRef = productRef.child("totalStock");
        DatabaseReference productSizesRef = productRef.child("productSizes");
        DatabaseReference searchKeyWordRef = productRef.child("searchKeyWord");
        DatabaseReference productImageRef = productRef.child("productImages");
        DatabaseReference productUpdatedDateRef = productRef.child("productLastUpdatedDate");
        productUpdatedDateRef.setValue(DateHelper.getCurrentDate());

        // NEW CODE ADDED HERE - Common fields for all products
        DatabaseReference productPackingRef = productRef.child("productPacking");
        DatabaseReference productWeightRef = productRef.child("productWeight");
        DatabaseReference productSuitForRef = productRef.child("productSuitFor");
        DatabaseReference productColourRef = productRef.child("productColour");

        // Common fields update for ALL products
        productNameRef.setValue(txtProductName.getText().toString());
        brandNameRef.setValue(txtBrandName.getText().toString());
        sellingPriceRef.setValue(txtSellingPrice.getText().toString());
        originalPriceRef.setValue(txtOriginalPrice.getText().toString());
        searchKeyWordRef.setValue(searchKeyWord);
        productPackingRef.setValue(pack_of.getText().toString());
        productWeightRef.setValue(weight_product.getText().toString());
        productSuitForRef.setValue(idealType.getText().toString());
        productColourRef.setValue(get_color_name.getText().toString());

        // Category-specific fields for clothing
        if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
            DatabaseReference productFabricRef = productRef.child("productFabric");
            DatabaseReference productOccasionRef = productRef.child("productOccasion");
            DatabaseReference productWashcareRef = productRef.child("productWashcare");

            productFabricRef.setValue(fabricType.getText().toString());
            productOccasionRef.setValue(selectionType.getText().toString());
            productWashcareRef.setValue(fabric_careType.getText().toString());

            productSizes.add(txtS.getText().toString());
            productSizes.add(txtM.getText().toString());
            productSizes.add(txtL.getText().toString());
            productSizes.add(txtXL.getText().toString());
            productSizes.add(txtXXL.getText().toString());
            productSizesRef.setValue(productSizes);
            totalStockRef.setValue(txtTotalStockShirt.getText().toString());

        } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
            DatabaseReference productFabricRef = productRef.child("productFabric");
            DatabaseReference productOccasionRef = productRef.child("productOccasion");
            DatabaseReference productWashcareRef = productRef.child("productWashcare");

            productFabricRef.setValue(fabricType.getText().toString());
            productOccasionRef.setValue(selectionType.getText().toString());
            productWashcareRef.setValue(fabric_careType.getText().toString());

            productSizes.add(txt28.getText().toString());
            productSizes.add(txt30.getText().toString());
            productSizes.add(txt32.getText().toString());
            productSizes.add(txt34.getText().toString());
            productSizes.add(txt36.getText().toString());
            productSizes.add(txt38.getText().toString());
            productSizes.add(txt40.getText().toString());

            productSizesRef.setValue(productSizes);
            totalStockRef.setValue(txtTotalStockJeans.getText().toString());

        } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {

            productSizes.add(txt6.getText().toString());
            productSizes.add(txt7.getText().toString());
            productSizes.add(txt8.getText().toString());
            productSizes.add(txt9.getText().toString());
            productSizes.add(txt10.getText().toString());
            productSizesRef.setValue(productSizes);
            totalStockRef.setValue(txtTotalStockFootWare.getText().toString());

        } else if (product.getProductSubCategory() != null && product.getProductSubCategory().equals("Smartphones")) {
            // Add mobile fields update for smartphones
            DatabaseReference ramRef = productRef.child("ram");
            DatabaseReference storageRef = productRef.child("storage");
            DatabaseReference processorRef = productRef.child("processor");
            DatabaseReference rearCameraRef = productRef.child("rearCamera");
            DatabaseReference frontCameraRef = productRef.child("frontCamera");
            DatabaseReference batteryRef = productRef.child("battery");

            ramRef.setValue(ram_mobile.getText().toString());
            storageRef.setValue(storage_mobile.getText().toString());
            processorRef.setValue(processor_mobile.getText().toString());
            rearCameraRef.setValue(rear_camera_mobile.getText().toString());
            frontCameraRef.setValue(front_camera_mobile.getText().toString());
            batteryRef.setValue(battery_mobile.getText().toString());

            totalStockRef.setValue(txtTotalStockOtherCategory.getText().toString());

        } else {
            // Other categories
            totalStockRef.setValue(txtTotalStockOtherCategory.getText().toString());
        }

        // Continue with existing image update logic...
        if (imagesUri.size() != 0) {
            Log.d("imagetest", "images are selected");
            updateMultipleImages(imagesUri, productImageRef);
        } else {
            Log.d("imagetest", "images are not selected");
            dialog.cancel();
            StyleableToast.makeText(getApplicationContext(), "Product Details Updated Successfully", R.style.UptrendToast).show();
            startActivity(new Intent(getApplicationContext(), inventory_product.class));
            finish();
        }
    }
    public void updateMultipleImages(ArrayList<Uri> imagesUri, DatabaseReference productImagesRef) {
        ArrayList<String> list = new ArrayList<>(product.getProductImages());
        storageReference = FirebaseStorage.getInstance().getReference();

        AtomicInteger uploadCount = new AtomicInteger(0);
        int totalUploads = imagesUri.size();

        for (Uri imageUri : imagesUri) {
            StorageReference imageRef = storageReference.child("Product Images/images" + UUID.randomUUID().toString());
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            list.add(uri.toString());
                            int count = uploadCount.incrementAndGet();
                            if (count == totalUploads) {
                                productImagesRef.setValue(list);
                                dialog.cancel();
                                StyleableToast.makeText(getApplicationContext(), "Product Details Updated Successfully", R.style.UptrendToast).show();
                                startActivity(new Intent(getApplicationContext(), inventory_product.class));
                                finish();
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle upload failure
                    });
        }
    }

    //This Method Will Display The Product Details.
    public void displayProductDetails(String productId) {
        slideModelsArrayList = new ArrayList<>();
        productRef = FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    product = snapshot.getValue(Product.class);
                    txtBrandName.setText(product.getProductBrandName());
                    txtProductName.setText(product.getProductName());
                    txtSellingPrice.setText(product.getSellingPrice());
                    txtOriginalPrice.setText(product.getOriginalPrice());


                    if (product.getProductCreatedDate() != null && !product.getProductCreatedDate().isEmpty()) {
                        txtSavedDate.setText("Added on " + product.getProductCreatedDate());
                    } else {
                        txtSavedDate.setText("Old product");
                    }

                    if (product.getProductLastUpdatedDate() != null && !product.getProductLastUpdatedDate().isEmpty()) {
                        txtEditDate.setVisibility(View.VISIBLE);
                        txtEditDate.setText("Updated on " + product.getProductLastUpdatedDate());
                    } else {
                        txtEditDate.setVisibility(View.GONE);
                    }

                    for (int i = 0; i < product.getProductImages().size(); i++) {
                        slideModelsArrayList.add(new SlideModel(product.getProductImages().get(i), ScaleTypes.FIT));
                    }
                    productImage.setImageList(slideModelsArrayList, ScaleTypes.FIT);
                    search_wordET1.setText(product.getSearchKeyWord().get(0));
                    searchLayoutVisible(product.getSearchKeyWord().get(1), search_wordET2, search_gone2);
                    searchLayoutVisible(product.getSearchKeyWord().get(2), search_wordET3, search_gone3);
                    searchLayoutVisible(product.getSearchKeyWord().get(3), search_wordET4, search_gone4);
                    searchLayoutVisible(product.getSearchKeyWord().get(4), search_wordET5, search_gone5);

                    // Inside the onDataChange method of displayProductDetails, add this after loading other data:


                    // Display product ID
                    String displayId = product.getProductDisplayId();
                    if (displayId != null && edit_product_id_txt != null) {
                        edit_product_id_txt.setText(displayId);
                    }

                    // Load packing
                    String packing = product.getProductPacking();
                    if (packing != null && pack_of != null) {
                        pack_of.setText(packing);
                    }

                    // Load color
                    String color = product.getProductColour();
                    if (color != null && get_color_name != null) {
                        get_color_name.setText(color);
                        setTextViewBackgroundTint(get_color, getColorResourceId(color));
                    }

                    // Load weight
                    String weight = product.getProductWeight();
                    if (weight != null && weight_product != null) {
                        weight_product.setText(weight);
                    }

                    // Load ideal for
                    String idealFor = product.getProductSuitFor();
                    if (idealFor != null && idealType != null) {
                        idealType.setText(idealFor);
                    }

                    // Load fabric type
                    String fabric = product.getProductFabric();
                    if (fabric != null && fabricType != null) {
                        fabricType.setText(fabric);
                    }

                    // Load occasion
                    String occasion = product.getProductOccasion();
                    if (occasion != null && selectionType != null) {
                        selectionType.setText(occasion);
                    }

                    // Load wash care
                    String washCare = product.getProductWashcare();
                    if (washCare != null && fabric_careType != null) {
                        fabric_careType.setText(washCare);
                    }

                    // Load mobile details
                    String ramValue = product.getRam();
                    if (ramValue != null && ram_mobile != null) {
                        ram_mobile.setText(ramValue);
                    }

                    String storageValue = product.getStorage();
                    if (storageValue != null && storage_mobile != null) {
                        storage_mobile.setText(storageValue);
                    }

                    String processorValue = product.getProcessor();
                    if (processorValue != null && processor_mobile != null) {
                        processor_mobile.setText(processorValue);
                    }

                    String rearCameraValue = product.getRearCamera();
                    if (rearCameraValue != null && rear_camera_mobile != null) {
                        rear_camera_mobile.setText(rearCameraValue);
                    }

                    String frontCameraValue = product.getFrontCamera();
                    if (frontCameraValue != null && front_camera_mobile != null) {
                        front_camera_mobile.setText(frontCameraValue);
                    }

                    String batteryValue = product.getBattery();
                    if (batteryValue != null && battery_mobile != null) {
                        battery_mobile.setText(batteryValue);
                    }


                    setupLayoutVisibility();


                    /*
                            Display stock Of product According To Product And
                            Also Visible The Size Layout according to the product
                     */
                    if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
                        shirtLayout.setVisibility(View.VISIBLE);
                        txtS.setText(product.getProductSizes().get(0));
                        txtM.setText(product.getProductSizes().get(1));
                        txtL.setText(product.getProductSizes().get(2));
                        txtXL.setText(product.getProductSizes().get(3));
                        txtXXL.setText(product.getProductSizes().get(4));
                        txtTotalStockShirt.setText(product.getTotalStock());
                    } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
                        jeansLayout.setVisibility(View.VISIBLE);
                        txt28.setText(product.getProductSizes().get(0));
                        txt30.setText(product.getProductSizes().get(1));
                        txt32.setText(product.getProductSizes().get(2));
                        txt34.setText(product.getProductSizes().get(3));
                        txt36.setText(product.getProductSizes().get(4));
                        txt38.setText(product.getProductSizes().get(5));
                        txt40.setText(product.getProductSizes().get(6));
                        txtTotalStockJeans.setText(product.getTotalStock());
                    } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
                        footWareLayout.setVisibility(View.VISIBLE);
                        txt6.setText(product.getProductSizes().get(0));
                        txt7.setText(product.getProductSizes().get(1));
                        txt8.setText(product.getProductSizes().get(2));
                        txt9.setText(product.getProductSizes().get(3));
                        txt10.setText(product.getProductSizes().get(4));
                        txtTotalStockFootWare.setText(product.getTotalStock());
                    } else {
                        otherCategoryLayout.setVisibility(View.VISIBLE);
                        txtValueOtherCategory.setText(product.getTotalStock());
                        txtTotalStockOtherCategory.setText(product.getTotalStock());
                    }

                    try {
                        int stockValue = Integer.parseInt(product.getTotalStock());
                        if (stockValue > 0 && stockValue <= 3) {
                            txtValueOtherCategory.setTextColor(Color.RED);
                            // txtTotalStockOtherCategory remains black (default)
                        }
                    } catch (NumberFormatException e) {
                        // Handle parsing error if needed
                    }

                    highlightLowQuantitySizes();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void searchLayoutVisible(String search, EditText editText, RelativeLayout layout) {
        if (search.length() != 0) {
            layout.setVisibility(View.VISIBLE);
            editText.setText(search);
        }
    }


    private void setupLayoutVisibility() {
        if (product == null) return;

        // FIRST: Hide all category-specific layouts
        if (shirtLayout != null) shirtLayout.setVisibility(View.GONE);
        if (jeansLayout != null) jeansLayout.setVisibility(View.GONE);
        if (footWareLayout != null) footWareLayout.setVisibility(View.GONE);
        if (otherCategoryLayout != null) otherCategoryLayout.setVisibility(View.GONE);

        // Hide fabric-related layouts
        if (fabric_gone != null) fabric_gone.setVisibility(View.GONE);
        if (selection_gone != null) selection_gone.setVisibility(View.GONE);
        if (fabric_care_gone != null) fabric_care_gone.setVisibility(View.GONE);

        // Hide mobile layout
        if (mobileDetailLayout != null) mobileDetailLayout.setVisibility(View.GONE);

        // SECOND: Show specific layout based on category
        String category = product.getProductCategory();
        String subCategory = product.getProductSubCategory();

        if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
            // Show shirt layout and fabric fields
            if (shirtLayout != null) shirtLayout.setVisibility(View.VISIBLE);
            if (fabric_gone != null) fabric_gone.setVisibility(View.VISIBLE);
            if (selection_gone != null) selection_gone.setVisibility(View.VISIBLE);
            if (fabric_care_gone != null) fabric_care_gone.setVisibility(View.VISIBLE);

        } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
            // Show jeans layout and fabric fields
            if (jeansLayout != null) jeansLayout.setVisibility(View.VISIBLE);
            if (fabric_gone != null) fabric_gone.setVisibility(View.VISIBLE);
            if (selection_gone != null) selection_gone.setVisibility(View.VISIBLE);
            if (fabric_care_gone != null) fabric_care_gone.setVisibility(View.VISIBLE);

        } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
            // Show footwear layout, fabric fields REMAIN GONE
            if (footWareLayout != null) footWareLayout.setVisibility(View.VISIBLE);

        } else if (subCategory != null && subCategory.equals("Smartphones")) {
            // Show mobile layout, fabric fields REMAIN GONE
            if (mobileDetailLayout != null) mobileDetailLayout.setVisibility(View.VISIBLE);

        } else {
            // Other categories
            if (otherCategoryLayout != null) otherCategoryLayout.setVisibility(View.VISIBLE);
            // Fabric fields REMAIN GONE
        }
    }

    public void increment(View view) {
        int total = 0;
        int count = 0;
        switch (view.getId()) {
            // Shirt sizes
            case R.id.txtPlus1:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtS.getText().toString());
                count++;
                total++;
                txtS.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus2:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtM.getText().toString());
                count++;
                total++;
                txtM.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus3:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtL.getText().toString());
                count++;
                total++;
                txtL.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus4:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtXL.getText().toString());
                count++;
                total++;
                txtXL.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus5:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtXXL.getText().toString());
                count++;
                total++;
                txtXXL.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            // Jeans sizes
            case R.id.txtPlus6:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt28.getText().toString());
                count++;
                total++;
                txt28.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtPlus7:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt30.getText().toString());
                count++;
                total++;
                txt30.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtPlus8:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt32.getText().toString());
                count++;
                total++;
                txt32.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtPlus9:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt34.getText().toString());
                count++;
                total++;
                txt34.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtPlus10:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt36.getText().toString());
                count++;
                total++;
                txt36.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtPlus11:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt38.getText().toString());
                count++;
                total++;
                txt38.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtPlus12:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt40.getText().toString());
                count++;
                total++;
                txt40.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            // Shoes sizes
            case R.id.txtPlus13:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt6.getText().toString());
                count++;
                total++;
                txt6.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtPlus14:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt7.getText().toString());
                count++;
                total++;
                txt7.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtPlus15:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt8.getText().toString());
                count++;
                total++;
                txt8.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtPlus16:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt9.getText().toString());
                count++;
                total++;
                txt9.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtPlus17:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt10.getText().toString());
                count++;
                total++;
                txt10.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            // Other category
            case R.id.txtPlus55:
                total = Integer.parseInt(txtTotalStockOtherCategory.getText().toString());
                count = Integer.parseInt(txtValueOtherCategory.getText().toString());
                count++;
                total++;
                txtValueOtherCategory.setText(String.valueOf(count));
                txtTotalStockOtherCategory.setText(String.valueOf(total));

                // ONLY CHANGE COLOR FOR txtValueOtherCategory (value55)
                // NOT for txtTotalStockOtherCategory (totalStockOtherCategory)
                int qty55plus = Integer.parseInt(txtValueOtherCategory.getText().toString());
                if (qty55plus > 0 && qty55plus <= 3) {
                    txtValueOtherCategory.setTextColor(Color.RED);
                    // DON'T change totalStockOtherCategory color
                } else {
                    txtValueOtherCategory.setTextColor(ContextCompat.getColor(edit_product.this, android.R.color.black));
                    // DON'T change totalStockOtherCategory color
                }
                break;
        }
        highlightLowQuantitySizes();
    }

    public void decrement(View view) {
        int total = 0;
        int count = 0;
        switch (view.getId()) {
            // Shirt sizes
            case R.id.txtMinus1:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtS.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txtS.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus2:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtM.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txtM.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus3:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtL.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txtL.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus4:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtXL.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txtXL.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus5:
                total = Integer.parseInt(txtTotalStockShirt.getText().toString());
                count = Integer.parseInt(txtXXL.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txtXXL.setText(String.valueOf(count));
                txtTotalStockShirt.setText(String.valueOf(total));
                break;
            // Jeans sizes
            case R.id.txtMinus6:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt28.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt28.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtMinus7:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt30.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt30.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtMinus8:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt32.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt32.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtMinus9:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt34.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt34.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtMinus10:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt36.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt36.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtMinus11:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt38.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt38.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            case R.id.txtMinus12:
                total = Integer.parseInt(txtTotalStockJeans.getText().toString());
                count = Integer.parseInt(txt40.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt40.setText(String.valueOf(count));
                txtTotalStockJeans.setText(String.valueOf(total));
                break;
            // Shoes sizes
            case R.id.txtMinus13:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt6.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt6.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtMinus14:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt7.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt7.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtMinus15:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt8.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt8.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtMinus16:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt9.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt9.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            case R.id.txtMinus17:
                total = Integer.parseInt(txtTotalStockFootWare.getText().toString());
                count = Integer.parseInt(txt10.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txt10.setText(String.valueOf(count));
                txtTotalStockFootWare.setText(String.valueOf(total));
                break;
            // Other category
            case R.id.txtMinus55:
                total = Integer.parseInt(txtTotalStockOtherCategory.getText().toString());
                count = Integer.parseInt(txtValueOtherCategory.getText().toString());
                if (count > 0) {
                    count--;
                    total--;
                }
                txtValueOtherCategory.setText(String.valueOf(count));
                txtTotalStockOtherCategory.setText(String.valueOf(total));

                // ONLY CHANGE COLOR FOR txtValueOtherCategory (value55)
                int qty55 = Integer.parseInt(txtValueOtherCategory.getText().toString());
                if (qty55 > 0 && qty55 <= 3) {
                    txtValueOtherCategory.setTextColor(Color.RED);
                    // DON'T change totalStockOtherCategory color
                } else {
                    txtValueOtherCategory.setTextColor(ContextCompat.getColor(edit_product.this, android.R.color.black));
                    // DON'T change totalStockOtherCategory color
                }
                break;
        }
        highlightLowQuantitySizes();
    }

    private int getColorResourceId(String colorName) {
        switch (colorName) {
            case "Aquamarine": return R.color.Aquamarine;
            case "Azure": return R.color.Azure;
            case "Black": return R.color.black;
            case "Brown": return R.color.Brown;
            case "Coral": return R.color.Coral;
            case "Crimson": return R.color.Crimson;
            case "Cyan": return R.color.Cyan;
            case "Golden": return R.color.Golden;
            case "Gray": return R.color.Gray;
            case "Green": return R.color.Green;
            case "Hot Pink": return R.color.Hot_Pink;
            case "Lime": return R.color.Lime;
            case "Magenta": return R.color.Magent;
            case "Maroon": return R.color.Maroon;
            case "Navy Blue": return R.color.Navy_Blue;
            case "Olive": return R.color.Olive;
            case "Orange": return R.color.Orange;
            case "Purple": return R.color.Purple;
            case "Red": return R.color.red;
            case "Royal Blue": return R.color.Royal_Blue;
            case "Silver": return R.color.Silver;
            case "Teal": return R.color.Teal;
            case "Wheat": return R.color.Wheat;
            case "White": return R.color.white;
            case "Yellow": return R.color.yellow;
            default: return R.color.transparent;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data.getClipData() != null) {
                int x = data.getClipData().getItemCount();

                for (int i = 0; i < x; i++) {
                    imagesUri.add(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                String imageURL = data.getData().getPath();
                imagesUri.add(Uri.parse(imageURL));
            }
            updateImageSlider();
        }
    }

    private void handleBackPress() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Cancel Edit");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to cancel editing this product?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {
                    Intent intent = new Intent(edit_product.this, inventory_product.class);

                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            Typeface customFont = ResourcesCompat.getFont(this, R.font.caudex);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(customFont, Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont, Typeface.BOLD);
        }
    }

    public void updateImageSlider() {
        slideModelsArrayList.clear();
        for (Uri uri : imagesUri) {
            slideModelsArrayList.add(new SlideModel(uri.toString(), ScaleTypes.FIT));
        }
        productImage.setImageList(slideModelsArrayList, ScaleTypes.FIT);
    }

    private void highlightLowQuantitySizes() {
        // Shirt sizes
        TextView[] shirtSizeTextViews = {findViewById(R.id.shirt_size1), findViewById(R.id.shirt_size2), findViewById(R.id.shirt_size3), findViewById(R.id.shirt_size4), findViewById(R.id.shirt_size5)};
        TextView[] shirtQuantityTextViews = {txtS, txtM, txtL, txtXL, txtXXL};

        // Jeans sizes
        TextView[] jeansSizeTextViews = {findViewById(R.id.jeans_size1), findViewById(R.id.jeans_size2), findViewById(R.id.jeans_size3), findViewById(R.id.jeans_size4), findViewById(R.id.jeans_size5), findViewById(R.id.jeans_size6), findViewById(R.id.jeans_size7)};
        TextView[] jeansQuantityTextViews = {txt28, txt30, txt32, txt34, txt36, txt38, txt40};

        // Shoes sizes
        TextView[] shoesSizeTextViews = {findViewById(R.id.shoes_size1), findViewById(R.id.shoes_size2), findViewById(R.id.shoes_size3), findViewById(R.id.shoes_size4), findViewById(R.id.shoes_size5)};
        TextView[] shoesQuantityTextViews = {txt6, txt7, txt8, txt9, txt10};

        // Highlight shirt sizes
        for (int i = 0; i < shirtSizeTextViews.length; i++) {
            TextView sizeView = shirtSizeTextViews[i];
            TextView quantityView = shirtQuantityTextViews[i];
            try {
                int quantity = Integer.parseInt(quantityView.getText().toString());
                if (quantity <= 3 && quantity > 0) {
                    sizeView.setBackgroundResource(R.drawable.square_size1);
                    sizeView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    quantityView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    sizeView.setBackgroundResource(R.drawable.square_size2);
                    sizeView.setTextColor(getResources().getColor(android.R.color.black));
                    quantityView.setTextColor(getResources().getColor(android.R.color.black));
                }
            } catch (NumberFormatException e) {
                sizeView.setBackgroundResource(R.drawable.square_size2);
                sizeView.setTextColor(getResources().getColor(android.R.color.black));
                quantityView.setTextColor(getResources().getColor(android.R.color.black));
            }
        }

        // Highlight jeans sizes
        for (int i = 0; i < jeansSizeTextViews.length; i++) {
            TextView sizeView = jeansSizeTextViews[i];
            TextView quantityView = jeansQuantityTextViews[i];
            try {
                int quantity = Integer.parseInt(quantityView.getText().toString());
                if (quantity <= 3 && quantity > 0) {
                    sizeView.setBackgroundResource(R.drawable.square_size1);
                    sizeView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    quantityView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    sizeView.setBackgroundResource(R.drawable.square_size2);
                    sizeView.setTextColor(getResources().getColor(android.R.color.black));
                    quantityView.setTextColor(getResources().getColor(android.R.color.black));
                }
            } catch (NumberFormatException e) {
                sizeView.setBackgroundResource(R.drawable.square_size2);
                sizeView.setTextColor(getResources().getColor(android.R.color.black));
                quantityView.setTextColor(getResources().getColor(android.R.color.black));
            }
        }

        // Highlight shoes sizes
        for (int i = 0; i < shoesSizeTextViews.length; i++) {
            TextView sizeView = shoesSizeTextViews[i];
            TextView quantityView = shoesQuantityTextViews[i];
            try {
                int quantity = Integer.parseInt(quantityView.getText().toString());
                if (quantity <= 3 && quantity > 0) {
                    sizeView.setBackgroundResource(R.drawable.square_size1);
                    sizeView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    quantityView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                } else {
                    sizeView.setBackgroundResource(R.drawable.square_size2);
                    sizeView.setTextColor(getResources().getColor(android.R.color.black));
                    quantityView.setTextColor(getResources().getColor(android.R.color.black));
                }
            } catch (NumberFormatException e) {
                sizeView.setBackgroundResource(R.drawable.square_size2);
                sizeView.setTextColor(getResources().getColor(android.R.color.black));
                quantityView.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }


    @SuppressLint("RestrictedApi")
    private void setTextViewBackgroundTint(TextView textView, int colorResId) {
        if (textView instanceof AppCompatTextView) {
            ((AppCompatTextView) textView).setSupportBackgroundTintList(
                    ContextCompat.getColorStateList(this, colorResId)
            );
        } else {
            textView.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, colorResId)
            );
        }
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        handleBackPress();
    }
}


