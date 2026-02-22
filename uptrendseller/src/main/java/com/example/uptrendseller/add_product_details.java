package com.example.uptrendseller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class add_product_details extends AppCompatActivity {

    private EditText brandName, productName, original_price, selling_price, pack_of_product, search_wordET1, search_wordET2, search_wordET3, search_wordET4, search_wordET5, ram_mobile,
            storage_mobile, processor_mobile, rear_camera_mobile, front_camera_mobile, battery_mobile;

    private AutoCompleteTextView autoCompleteWeight;

    private MaterialAutoCompleteTextView idealType, fabricType, selectionType, fabric_careType;
    private LinearLayout colour_gone, fabric_Gone, selection_Gone, fabric_care_Gone, search_word_linear1, search_word_linear2, search_word_linear3,
            search_word_linear4, search_word_linear5, cardView;

    private RelativeLayout search_key_hide_gone1, search_key_hide_gone2, search_key_hide_gone3,
            search_key_hide_gone4, search_key_hide_gone5;
    private RelativeLayout search_gone2, search_gone3, search_gone4, search_gone5;


    private TextView value1, value2, value3, value4, value5, stock_shirt, value6, value7, value8,
            value9, value10, value11, value12, stock_pant, value13, value14, value15, value16, value17,
            value55, totalStockOtherCategory, stock_shoes,
            click_info, get_color, get_color_name, plus_click1, plus_click2,
            plus_click3, plus_click4, save_details_btn, clothes_hading_txt, backButton,
            txt_minus_amount,txt_discount,productIdTextView,txtCurrentDate;
    private int count = 0, total = 0;
    private LinearLayout layoutShirt, layoutPant, layoutFootWare, layoutOtherCategory, mobileDetailLayout;
    private String category, key, SubCategory;
    private RadioGroup colourRadioGroup;
    private DatabaseReference databaseReference;

    private loadingDialog2 loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_details);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        Intent intent = getIntent();
        if (intent != null) {
            category = intent.getStringExtra("Category");
            key = intent.getStringExtra("Key");
            SubCategory = intent.getStringExtra("SubCategory");
        }
        // FindView by Id of all Widget.
        value1 = findViewById(R.id.value);
        value2 = findViewById(R.id.value2);
        value3 = findViewById(R.id.value3);
        value4 = findViewById(R.id.value4);
        value5 = findViewById(R.id.value5);
        stock_shirt = findViewById(R.id.stock_shirt);
        value6 = findViewById(R.id.value6);
        value7 = findViewById(R.id.value7);
        value8 = findViewById(R.id.value8);
        value9 = findViewById(R.id.value9);
        value10 = findViewById(R.id.value10);
        value11 = findViewById(R.id.value11);
        value12 = findViewById(R.id.value12);
        stock_pant = findViewById(R.id.stock_pant);
        stock_shoes = findViewById(R.id.stock_shoes);
        value13 = findViewById(R.id.value13);
        value14 = findViewById(R.id.value14);
        value15 = findViewById(R.id.value15);
        value16 = findViewById(R.id.value16);
        value17 = findViewById(R.id.value17);
        value55 = findViewById(R.id.value55);
        totalStockOtherCategory = findViewById(R.id.totalStockOtherCategory);
        clothes_hading_txt = findViewById(R.id.clothes_hading_txt);

        layoutShirt = findViewById(R.id.layoutShirt);
        layoutPant = findViewById(R.id.layoutPant);
        layoutFootWare = findViewById(R.id.layoutFootWare);
        layoutOtherCategory = findViewById(R.id.other_category);

        brandName = findViewById(R.id.brand_name);
        productName = findViewById(R.id.product_name);
        original_price = findViewById(R.id.original_price);
        selling_price = findViewById(R.id.selling_price);
        pack_of_product = findViewById(R.id.pack_of);


        autoCompleteWeight = findViewById(R.id.weight_product);
        idealType = findViewById(R.id.idealType);
        fabricType = findViewById(R.id.fabricType);
        selectionType = findViewById(R.id.selectionType);
        fabric_careType = findViewById(R.id.fabric_careType);

        //get colour
        get_color_name = findViewById(R.id.get_color_name);
        get_color = findViewById(R.id.get_color);


        //gone
        fabric_Gone = findViewById(R.id.fabric_gone);
        selection_Gone = findViewById(R.id.selection_gone);
        fabric_care_Gone = findViewById(R.id.fabric_care_gone);


        search_wordET1 = findViewById(R.id.search_keyword1);
        search_wordET2 = findViewById(R.id.search_keyword2);
        search_wordET3 = findViewById(R.id.search_keyword3);
        search_wordET4 = findViewById(R.id.search_keyword4);
        search_wordET5 = findViewById(R.id.search_keyword5);

        plus_click1 = findViewById(R.id.plus_click1);
        plus_click2 = findViewById(R.id.plus_click2);
        plus_click3 = findViewById(R.id.plus_click3);
        plus_click4 = findViewById(R.id.plus_click4);

        search_gone2 = findViewById(R.id.search_key_hide_gone2);
        search_gone3 = findViewById(R.id.search_key_hide_gone3);
        search_gone4 = findViewById(R.id.search_key_hide_gone4);
        search_gone5 = findViewById(R.id.search_key_hide_gone5);


        colourRadioGroup = findViewById(R.id.colourRadioGroup);
        mobileDetailLayout = findViewById(R.id.mobileDetailLayout);


        //Initialization for instruction effect
        click_info = findViewById(R.id.instruction_click);
        colour_gone = findViewById(R.id.instruction_click_layout);
        cardView = findViewById(R.id.cardView);
        //save btn
        save_details_btn = findViewById(R.id.save_details_txt);
        //Mobile
        ram_mobile = findViewById(R.id.ram_mobile);
        storage_mobile = findViewById(R.id.storage_mobile);
        processor_mobile = findViewById(R.id.processor_mobile);
        rear_camera_mobile = findViewById(R.id.rear_camera_mobile);
        front_camera_mobile = findViewById(R.id.front_camera_mobile);
        battery_mobile = findViewById(R.id.battery_mobile);

        txt_minus_amount  = findViewById(R.id.txt_minus_amount);
        txt_discount  = findViewById(R.id.txt_discount);
        backButton = findViewById(R.id.back_add_product_details);
        backButton.setOnClickListener(v -> onBackPressed());


        productIdTextView = findViewById(R.id.product_id_txt);


        txtCurrentDate = findViewById(R.id.txtCurrentDate);
        txtCurrentDate.setText("Date: " + DateHelper.getCurrentDate());


        // Initialize databaseReference FIRST
        if (key != null && !key.isEmpty()) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(key);
        }

        // Handle saved instance state
        if (savedInstanceState != null) {
            category = savedInstanceState.getString("category");
            key = savedInstanceState.getString("key");
            SubCategory = savedInstanceState.getString("SubCategory");

            // Re-initialize databaseReference if key was restored
            if (key != null && !key.isEmpty() && databaseReference == null) {
                databaseReference = FirebaseDatabase.getInstance().getReference("Product").child(key);
            }
        }

        // Call visibleLayoutSize ONCE to set the correct layout visibility
        if (category != null) {
            visibleLayoutSize(category);
        }

        // Handle product ID display - MOVE THIS TO BOTTOM
        // Check if we have a key (editing existing product)
        if (key != null && !key.isEmpty()) {
            // This is an existing product - load details including existing ID
            loadExistingProductDetails(key);
        } else if (category != null) {
            // This is a NEW product - generate fresh ID
            String generatedProductId = generateProductId(category);
            productIdTextView.setText(generatedProductId);

            // Also save it temporarily in case user goes back and comes again
            if (savedInstanceState == null) {
                // Only save to instance state if not restoring
                Bundle savedState = new Bundle();
                savedState.putString("generatedProductId", generatedProductId);
                onSaveInstanceState(savedState);
            }
        }

        //AutoCompleteTextview
        String[] weightArray = getResources().getStringArray(R.array.net_quantity);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(add_product_details.this, android.R.layout.simple_list_item_1, weightArray);
        autoCompleteWeight.setAdapter(arrayAdapter);


        // Ideal For (Men, Women, Boy, Girl, etc.)
        ArrayAdapter<CharSequence> idealAdapter = ArrayAdapter.createFromResource(this,
                R.array.idel_type_spinner, R.layout.spinner_dropdown_item);
        idealType.setAdapter(idealAdapter);

        // Fabric Type
        ArrayAdapter<CharSequence> fabricAdapter = ArrayAdapter.createFromResource(this,
                R.array.fabric_type_spinner, R.layout.spinner_dropdown_item);
        fabricType.setAdapter(fabricAdapter);

        // Occasion / Selection Type
        ArrayAdapter<CharSequence> selectionAdapter = ArrayAdapter.createFromResource(this,
                R.array.selection_type_spinner, R.layout.spinner_dropdown_item);
        selectionType.setAdapter(selectionAdapter);

        // Fabric Care
        ArrayAdapter<CharSequence> careAdapter = ArrayAdapter.createFromResource(this,
                R.array.fabric_care_type_spinner, R.layout.spinner_dropdown_item);
        fabric_careType.setAdapter(careAdapter);

        click_info.setOnClickListener(new View.OnClickListener() {
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

                //This Method Will Change The Textview BackgroundTint According which Colour is Selected.
                setTextViewBackgroundTint(get_color, colorResId);
                cardView.setVisibility(View.GONE);


            }
        });


        original_price.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateDiscount(); }
        });

        selling_price.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { calculateDiscount(); }
        });


        search_key_hide_gone2 = findViewById(R.id.search_key_hide_gone2);
        search_key_hide_gone3 = findViewById(R.id.search_key_hide_gone3);
        search_key_hide_gone4 = findViewById(R.id.search_key_hide_gone4);
        search_key_hide_gone5 = findViewById(R.id.search_key_hide_gone5);

        // 3. Click listeners (perfect & clean)
        plus_click1.setOnClickListener(v -> {
            search_key_hide_gone2.setVisibility(View.VISIBLE);
            plus_click1.setVisibility(View.GONE); // Hide plus button after clicking
        });

        plus_click2.setOnClickListener(v -> {
            search_key_hide_gone3.setVisibility(View.VISIBLE);
            plus_click2.setVisibility(View.GONE); // Hide plus button after clicking
        });

        plus_click3.setOnClickListener(v -> {
            search_key_hide_gone4.setVisibility(View.VISIBLE);
            plus_click3.setVisibility(View.GONE); // Hide plus button after clicking
        });

        plus_click4.setOnClickListener(v -> {
            search_key_hide_gone5.setVisibility(View.VISIBLE);
            plus_click4.setVisibility(View.GONE); // Hide plus button after clicking
        });

        loading = new loadingDialog2(add_product_details.this);


        save_details_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 1. First validate ALL inputs
                if (!validInput()) {
                    return; // Don't proceed if validation fails
                }

                // 2. Check for category
                if (category == null) {
                    Toast.makeText(add_product_details.this, "Category not found", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 3. Ensure we have a product ID (generate if not)
                String productIdText = productIdTextView.getText().toString().trim();
                if (TextUtils.isEmpty(productIdText)) {
                    productIdText = generateProductId(category);
                    productIdTextView.setText(productIdText);
                }

                // 4. Check if databaseReference is initialized
                if (databaseReference == null) {
                    Toast.makeText(add_product_details.this, "Database error. Please try again.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 5. Only AFTER all checks pass, show loading
                loading.show();

                ArrayList<String> searchKeyWord = new ArrayList<>();
                ArrayList<String> productSizes = new ArrayList<>();

                HashMap<String, Object> product = new HashMap<>();

                // 6. Add product display ID to the data being saved
                product.put("productDisplayId", productIdText);

                // 7. Add discount calculation
                try {
                    double original = Double.parseDouble(getSafeText(original_price));
                    double selling = Double.parseDouble(getSafeText(selling_price));
                    double saved = original - selling;
                    double discountPercent = (saved / original) * 100;

                    product.put("savedAmount", String.format("%.0f", saved));
                    product.put("discountPercent", String.format("%.0f", Math.abs(discountPercent)) + "%");
                } catch (Exception e) {
                    product.put("savedAmount", "0");
                    product.put("discountPercent", "0%");
                }

                // NULL SAFE TEXT GETTERS
                product.put("productBrandName", getSafeText(brandName));
                product.put("productName", getSafeText(productName));
                product.put("originalPrice", getSafeText(original_price));
                product.put("sellingPrice", getSafeText(selling_price));
                product.put("productPacking", getSafeText(pack_of_product));
                product.put("productColour", getSafeText(get_color_name));
                product.put("productSuitFor", getSafeAutoCompleteText(idealType));
                product.put("productWeight", getSafeAutoCompleteText(autoCompleteWeight));

                // Add search keywords with null checks
                searchKeyWord.add(getSafeText(search_wordET1));
                searchKeyWord.add(getSafeText(search_wordET2));
                searchKeyWord.add(getSafeText(search_wordET3));
                searchKeyWord.add(getSafeText(search_wordET4));
                searchKeyWord.add(getSafeText(search_wordET5));
                product.put("searchKeyWord", searchKeyWord);

                product.put("productWeight", autoCompleteWeight.getText().toString());

                if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
                    productSizes.add(value1.getText().toString());
                    productSizes.add(value2.getText().toString());
                    productSizes.add(value3.getText().toString());
                    productSizes.add(value4.getText().toString());
                    productSizes.add(value5.getText().toString());
                    product.put("productSizes", productSizes);
                    product.put("totalStock", stock_shirt.getText().toString());
                    product.put("productFabric", fabricType.getText().toString());
                    product.put("productOccasion", selectionType.getText().toString());
                    product.put("productWashcare", fabric_careType.getText().toString());

                    databaseReference.updateChildren(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.cancel();
                                        Intent i = new Intent(getApplicationContext(), add_product_manufacture_details.class);
                                        i.putExtra("Key", key);
                                        i.putExtra("Category", category);
                                        if (SubCategory != null) i.putExtra("SubCategory", SubCategory);

                                        // Pass price data explicitly
                                        i.putExtra("originalPrice", getSafeText(original_price));
                                        i.putExtra("sellingPrice", getSafeText(selling_price));

                                        startActivity(i);
                                        Toast.makeText(add_product_details.this, "Done", Toast.LENGTH_SHORT).show();
                                    }
                                }, 2000);

                            }
                        }
                    });

                } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {

                    productSizes.add(value6.getText().toString());
                    productSizes.add(value7.getText().toString());
                    productSizes.add(value8.getText().toString());
                    productSizes.add(value9.getText().toString());
                    productSizes.add(value10.getText().toString());
                    productSizes.add(value11.getText().toString());
                    productSizes.add(value12.getText().toString());
                    product.put("productSizes", productSizes);
                    product.put("totalStock", stock_pant.getText().toString());
                    product.put("productFabric", fabricType.getText().toString());
                    product.put("productOccasion", selectionType.getText().toString());
                    product.put("productWashcare", fabric_careType.getText().toString());
                    databaseReference.updateChildren(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading.cancel();
                                        Intent i = new Intent(getApplicationContext(), add_product_manufacture_details.class);
                                        i.putExtra("Key", key);
                                        i.putExtra("Category", category); // ADD THIS
                                        if (SubCategory != null)
                                            i.putExtra("SubCategory", SubCategory); // ADD THIS
                                        startActivity(i);
                                        StyleableToast.makeText(getApplicationContext(), "Successfully details added", R.style.UptrendToast).show();
                                    }
                                }, 2000);

                            }
                        }
                    });

                } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {

                    productSizes.add(value13.getText().toString());
                    productSizes.add(value14.getText().toString());
                    productSizes.add(value15.getText().toString());
                    productSizes.add(value16.getText().toString());
                    productSizes.add(value17.getText().toString());
                    product.put("productSizes", productSizes);
                    product.put("totalStock", stock_shoes.getText().toString());
                    databaseReference.updateChildren(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                loading.cancel();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getApplicationContext(), add_product_manufacture_details.class);
                                        i.putExtra("Key", key);
                                        i.putExtra("Category", category); // ADD THIS
                                        if (SubCategory != null)
                                            i.putExtra("SubCategory", SubCategory); // ADD THIS
                                        startActivity(i);
                                        StyleableToast.makeText(getApplicationContext(), "Successfully details added", R.style.UptrendToast).show();
                                    }
                                }, 2000);
                            }
                        }
                    });
                } else if (SubCategory != null && SubCategory.equals("Smartphones")) {
                    // Add null check for SubCategory
                    product.put("ram", ram_mobile.getText().toString().trim());
                    product.put("storage", storage_mobile.getText().toString().trim());
                    product.put("processor", processor_mobile.getText().toString().trim());
                    product.put("rearCamera", rear_camera_mobile.getText().toString().trim());
                    product.put("frontCamera", front_camera_mobile.getText().toString().trim());
                    product.put("battery", battery_mobile.getText().toString().trim());
                    product.put("totalStock", totalStockOtherCategory.getText().toString());
                    databaseReference.updateChildren(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                loading.cancel();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getApplicationContext(), add_product_manufacture_details.class);
                                        i.putExtra("Key", key);
                                        i.putExtra("Category", category); // ADD THIS
                                        i.putExtra("SubCategory", SubCategory); // ADD THIS (Smartphones needs this)
                                        startActivity(i);
                                        StyleableToast.makeText(getApplicationContext(), "Successfully details added", R.style.UptrendToast).show();
                                    }
                                }, 2000);
                            }
                        }
                    });

                } else {

                    product.put("totalStock", totalStockOtherCategory.getText().toString());
                    databaseReference.updateChildren(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isComplete()) {
                                loading.cancel();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent i = new Intent(getApplicationContext(), add_product_manufacture_details.class);
                                        i.putExtra("Key", key);
                                        i.putExtra("Category", category); // ADD THIS
                                        if (SubCategory != null)
                                            i.putExtra("SubCategory", SubCategory); // ADD THIS
                                        startActivity(i);
                                        Toast.makeText(add_product_details.this, "Details Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }, 1000);
                            }
                        }
                    });
                }
                //startActivity(new Intent(getApplicationContext(), add_product_manufacture_details.class));

            }

        });

        ChangeColour.changeColour(getApplicationContext(), brandName);
        ChangeColour.changeColour(getApplicationContext(), productName);
        ChangeColour.changeColour(getApplicationContext(), original_price);
        ChangeColour.changeColour(getApplicationContext(), selling_price);
        ChangeColour.changeColour(getApplicationContext(), pack_of_product);

    }

    private String generateProductId(String category) {
        // Log the category for debugging
        Log.d("ProductID", "Category received: " + category);

        String prefix = getCategoryPrefix(category);

        // Log the prefix for debugging
        Log.d("ProductID", "Prefix generated: " + prefix);

        // Generate random 6-digit number
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000; // Generates 100000-999999

        String productId = prefix + "-" + randomNumber;
        Log.d("ProductID", "Final Product ID: " + productId);

        return productId;
    }

    private String getCategoryPrefix(String category) {
        if (category == null) return "PR";

        // Convert to lowercase for case-insensitive comparison
        String lowerCategory = category.trim().toLowerCase();

        // Use contains() for flexible matching
        if (lowerCategory.contains("art")) return "AR";
        else if (lowerCategory.contains("baby product")) return "BP";
        else if (lowerCategory.contains("beauty")) return "BE";
        else if (lowerCategory.contains("books")) return "BK";
        else if (lowerCategory.contains("camera")) return "CM";
        else if (lowerCategory.contains("cell phone") || lowerCategory.contains("cellphone")) return "CP";
        else if (lowerCategory.contains("men's(top)") || lowerCategory.contains("men(top)")) return "MT";
        else if (lowerCategory.contains("men's(bottom)") || lowerCategory.contains("men(bottom)")) return "MB";
        else if (lowerCategory.contains("chocolate")) return "CH";
        else if (lowerCategory.contains("electronics")) return "EL";
        else if (lowerCategory.contains("eyewear")) return "EW";
        else if (lowerCategory.contains("footware(men)") || lowerCategory.contains("footwear(men)")) return "FM";
        else if (lowerCategory.contains("footware(women)") || lowerCategory.contains("footwear(women)")) return "FW";
        else if (lowerCategory.contains("jewelry")) return "JW";
        else if (lowerCategory.contains("grocery") || lowerCategory.contains("gourmet")) return "GF";
        else if (lowerCategory.contains("health") || lowerCategory.contains("personal care")) return "HP";
        else if (lowerCategory.contains("home") || lowerCategory.contains("garden")) return "HG";
        else if (lowerCategory.contains("musical instrument")) return "MI";
        else if (lowerCategory.contains("office product")) return "OP";
        else if (lowerCategory.contains("personal computer") || lowerCategory.contains("pc")) return "PC";
        else if (lowerCategory.contains("sports")) return "SP";
        else if (lowerCategory.contains("toys") || lowerCategory.contains("games")) return "TG";
        else if (lowerCategory.contains("watches")) return "WA";
        else if (lowerCategory.contains("women's(top)") || lowerCategory.contains("women(top)")) return "WT";
        else if (lowerCategory.contains("women's(bottom)") || lowerCategory.contains("women(bottom)")) return "WB";
        else return "PR";
    }


    private void calculateDiscount() {
        String originalStr = original_price.getText().toString().trim();
        String sellingStr = selling_price.getText().toString().trim();

        if (originalStr.isEmpty() || sellingStr.isEmpty()) {
            txt_minus_amount.setText("Saved: ₹0");
            txt_discount.setText("0% off");
            return;
        }

        try {
            double original = Double.parseDouble(originalStr);
            double selling = Double.parseDouble(sellingStr);

            if (original <= 0) {
                txt_minus_amount.setText("Invalid Price");
                txt_discount.setText("0% off");
                return;
            }

            double saved = original - selling;
            double discount = (saved / original) * 100;

            if (saved >= 0) {
                txt_minus_amount.setText("Saved: ₹" + String.format("%.0f", saved));
            } else {
                txt_minus_amount.setText("Extra: ₹" + String.format("%.0f", -saved));
            }

            String discountSign = discount >= 0 ? "" : "-";
            txt_discount.setText(discountSign + String.format("%.0f", Math.abs(discount)) + "% off");

        } catch (Exception e) {
            txt_minus_amount.setText("Invalid Input");
            txt_discount.setText("0% off");
        }
    }

    //This Method Will Check the Input Data is Valid or Not.

    private String getSafeText(EditText editText) {
        if (editText == null || editText.getText() == null) {
            return "";
        }
        return editText.getText().toString().trim();
    }

    private String getSafeAutoCompleteText(AutoCompleteTextView autoCompleteTextView) {
        if (autoCompleteTextView == null || autoCompleteTextView.getText() == null) {
            return "";
        }
        return autoCompleteTextView.getText().toString().trim();
    }

    private String getSafeText(TextView textView) {
        if (textView == null || textView.getText() == null) {
            return "";
        }
        return textView.getText().toString().trim();
    }

    public boolean validInput() {
        // 1. Brand Name validation
        if (TextUtils.isEmpty(brandName.getText().toString().trim())) {
            ChangeColour.errorColour(getApplicationContext(), brandName, "Brand name is required");
            StyleableToast.makeText(this, "Brand name is required", R.style.UptrendToast).show();
            return false;
        }

        // 2. Product Name validation
        if (TextUtils.isEmpty(productName.getText().toString().trim())) {
            ChangeColour.errorColour(getApplicationContext(), productName, "Product name is required");
            StyleableToast.makeText(this, "Product name is required", R.style.UptrendToast).show();
            return false;
        }

        // 3. Original Price validation
        String originalPriceText = original_price.getText().toString().trim();
        if (TextUtils.isEmpty(originalPriceText)) {
            ChangeColour.errorColour(getApplicationContext(), original_price, "Original price is required");
            StyleableToast.makeText(this, "Original price is required", R.style.UptrendToast).show();
            return false;
        } else {
            try {
                double originalPrice = Double.parseDouble(originalPriceText);
                if (originalPrice <= 0) {
                    ChangeColour.errorColour(getApplicationContext(), original_price, "Price must be greater than 0");
                    StyleableToast.makeText(this, "Original price must be greater than 0", R.style.UptrendToast).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                ChangeColour.errorColour(getApplicationContext(), original_price, "Enter valid price");
                StyleableToast.makeText(this, "Enter valid original price", R.style.UptrendToast).show();
                return false;
            }
        }

        // 4. Selling Price validation
        String sellingPriceText = selling_price.getText().toString().trim();
        if (TextUtils.isEmpty(sellingPriceText)) {
            ChangeColour.errorColour(getApplicationContext(), selling_price, "Selling price is required");
            StyleableToast.makeText(this, "Selling price is required", R.style.UptrendToast).show();
            return false;
        } else {
            try {
                double sellingPrice = Double.parseDouble(sellingPriceText);
                if (sellingPrice <= 0) {
                    ChangeColour.errorColour(getApplicationContext(), selling_price, "Price must be greater than 0");
                    StyleableToast.makeText(this, "Selling price must be greater than 0", R.style.UptrendToast).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                ChangeColour.errorColour(getApplicationContext(), selling_price, "Enter valid price");
                StyleableToast.makeText(this, "Enter valid selling price", R.style.UptrendToast).show();
                return false;
            }
        }

        // 5. Total Stock validation (based on category)
        if (category != null) {
            if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
                try {
                    int totalStock = Integer.parseInt(stock_shirt.getText().toString());
                    if (totalStock < 10) {
                        StyleableToast.makeText(this, "Total stock must be at least 10", R.style.UptrendToast).show();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    StyleableToast.makeText(this, "Invalid stock value", R.style.UptrendToast).show();
                    return false;
                }
            } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
                try {
                    int totalStock = Integer.parseInt(stock_pant.getText().toString());
                    if (totalStock < 10) {
                        StyleableToast.makeText(this, "Total stock must be at least 10", R.style.UptrendToast).show();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    StyleableToast.makeText(this, "Invalid stock value", R.style.UptrendToast).show();
                    return false;
                }
            } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
                try {
                    int totalStock = Integer.parseInt(stock_shoes.getText().toString());
                    if (totalStock < 10) {
                        StyleableToast.makeText(this, "Total stock must be at least 10", R.style.UptrendToast).show();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    StyleableToast.makeText(this, "Invalid stock value", R.style.UptrendToast).show();
                    return false;
                }
            } else if (SubCategory != null && SubCategory.equals("Smartphones")) {
                try {
                    int totalStock = Integer.parseInt(totalStockOtherCategory.getText().toString());
                    if (totalStock < 10) {
                        StyleableToast.makeText(this, "Total stock must be at least 10", R.style.UptrendToast).show();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    StyleableToast.makeText(this, "Invalid stock value", R.style.UptrendToast).show();
                    return false;
                }
            } else {
                try {
                    int totalStock = Integer.parseInt(totalStockOtherCategory.getText().toString());
                    if (totalStock < 10) {
                        StyleableToast.makeText(this, "Total stock must be at least 10", R.style.UptrendToast).show();
                        return false;
                    }
                } catch (NumberFormatException e) {
                    StyleableToast.makeText(this, "Invalid stock value", R.style.UptrendToast).show();
                    return false;
                }
            }
        }

        // 6. Product Packing validation
        if (TextUtils.isEmpty(pack_of_product.getText().toString().trim())) {
            ChangeColour.errorColour(getApplicationContext(), pack_of_product, "Pack quantity is required");
            StyleableToast.makeText(this, "Pack quantity is required", R.style.UptrendToast).show();
            return false;
        } else {
            try {
                int packQty = Integer.parseInt(pack_of_product.getText().toString().trim());
                if (packQty <= 0) {
                    ChangeColour.errorColour(getApplicationContext(), pack_of_product, "Quantity must be greater than 0");
                    StyleableToast.makeText(this, "Pack quantity must be greater than 0", R.style.UptrendToast).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                ChangeColour.errorColour(getApplicationContext(), pack_of_product, "Enter valid quantity");
                StyleableToast.makeText(this, "Enter valid pack quantity", R.style.UptrendToast).show();
                return false;
            }
        }

        // 7. Fabric validation (only for Top/Bottom categories)
        if (category != null && (category.equals("Men's(Top)") || category.equals("Women's(Top)") ||
                category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)"))) {
            if (TextUtils.isEmpty(fabricType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select fabric type", R.style.UptrendToast).show();
                return false;
            }
        }

        // 8. Ideal For validation
        if (TextUtils.isEmpty(idealType.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please select ideal for", R.style.UptrendToast).show();
            return false;
        }

        // 9. Selection For validation (only for Top/Bottom categories)
        if (category != null && (category.equals("Men's(Top)") || category.equals("Women's(Top)") ||
                category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)"))) {
            if (TextUtils.isEmpty(selectionType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select occasion type", R.style.UptrendToast).show();
                return false;
            }
        }

        // 10. Fabric Care validation (only for Top/Bottom categories)
        if (category != null && (category.equals("Men's(Top)") || category.equals("Women's(Top)") ||
                category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)"))) {
            if (TextUtils.isEmpty(fabric_careType.getText().toString().trim())) {
                StyleableToast.makeText(this, "Please select fabric care", R.style.UptrendToast).show();
                return false;
            }
        }

        // 11. Weight validation
        if (TextUtils.isEmpty(autoCompleteWeight.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please select product weight", R.style.UptrendToast).show();
            return false;
        }

        // 12. Search Keyword validation
        if (TextUtils.isEmpty(search_wordET1.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET2.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET3.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET4.getText().toString().trim()) &&
                TextUtils.isEmpty(search_wordET5.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please enter at least one search keyword", R.style.UptrendToast).show();
            return false;
        }

        // 13. Product Colour validation
        if (TextUtils.isEmpty(get_color_name.getText().toString().trim())) {
            StyleableToast.makeText(this, "Please select product colour", R.style.UptrendToast).show();
            return false;
        }

        // 14. Mobile fields validation (only for Smartphones)
        if (SubCategory != null && SubCategory.equals("Smartphones")) {
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

    public void visibleLayoutSize(String category) {
        if (category == null) {
            return; // Exit if category is null
        }

        // DEBUG LOG
        Log.d("visibleLayoutSize", "Category: " + category + ", SubCategory: " + SubCategory);

        // FIRST: HIDE ALL LAYOUTS AND FIELDS
        if (layoutShirt != null) layoutShirt.setVisibility(View.GONE);
        if (layoutPant != null) layoutPant.setVisibility(View.GONE);
        if (layoutFootWare != null) layoutFootWare.setVisibility(View.GONE);
        if (layoutOtherCategory != null) layoutOtherCategory.setVisibility(View.GONE);
        if (mobileDetailLayout != null) mobileDetailLayout.setVisibility(View.GONE);
        if (clothes_hading_txt != null) clothes_hading_txt.setVisibility(View.GONE);

        // HIDE FABRIC FIELDS
        if (fabric_Gone != null) fabric_Gone.setVisibility(View.GONE);
        if (selection_Gone != null) selection_Gone.setVisibility(View.GONE);
        if (fabric_care_Gone != null) fabric_care_Gone.setVisibility(View.GONE);

        // SECOND: SHOW SPECIFIC LAYOUT BASED ON CATEGORY/SUBCATEGORY
        if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
            // Show shirt layout and fabric fields
            if (layoutShirt != null) layoutShirt.setVisibility(View.VISIBLE);
            if (clothes_hading_txt != null) clothes_hading_txt.setVisibility(View.VISIBLE);
            if (fabric_Gone != null) fabric_Gone.setVisibility(View.VISIBLE);
            if (selection_Gone != null) selection_Gone.setVisibility(View.VISIBLE);
            if (fabric_care_Gone != null) fabric_care_Gone.setVisibility(View.VISIBLE);

        } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
            // Show jeans layout and fabric fields
            if (layoutPant != null) layoutPant.setVisibility(View.VISIBLE);
            if (clothes_hading_txt != null) clothes_hading_txt.setVisibility(View.VISIBLE);
            if (fabric_Gone != null) fabric_Gone.setVisibility(View.VISIBLE);
            if (selection_Gone != null) selection_Gone.setVisibility(View.VISIBLE);
            if (fabric_care_Gone != null) fabric_care_Gone.setVisibility(View.VISIBLE);

        } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
            // Show footwear layout ONLY
            if (layoutFootWare != null) layoutFootWare.setVisibility(View.VISIBLE);
            // Fabric fields remain GONE

        } else if (SubCategory != null && SubCategory.equals("Smartphones")) {
            // Show mobile layout AND layoutOtherCategory for stock control
            if (mobileDetailLayout != null) mobileDetailLayout.setVisibility(View.VISIBLE);
            if (layoutOtherCategory != null) layoutOtherCategory.setVisibility(View.VISIBLE);
            // Fabric fields remain GONE

        } else {
            // For ALL OTHER categories (NOT smartphones, clothing, or footwear)
            if (layoutOtherCategory != null) layoutOtherCategory.setVisibility(View.VISIBLE);
            // Fabric fields remain GONE
        }
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

    public void increment(View v) {
        switch (v.getId()) {
            case R.id.txtPlus1:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value1.getText().toString());
                count++;
                total++;
                value1.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus2:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value2.getText().toString());
                count++;
                total++;
                value2.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus3:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value3.getText().toString());
                count++;
                total++;
                value3.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus4:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value4.getText().toString());
                count++;
                total++;
                value4.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus5:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value5.getText().toString());
                count++;
                total++;
                value5.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtPlus6:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value6.getText().toString());
                count++;
                total++;
                value6.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus7:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value7.getText().toString());
                count++;
                total++;
                value7.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus8:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value8.getText().toString());
                count++;
                total++;
                value8.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus9:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value9.getText().toString());
                count++;
                total++;
                value9.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus10:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value10.getText().toString());
                count++;
                total++;
                value10.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus11:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value11.getText().toString());
                count++;
                total++;
                value11.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus12:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value12.getText().toString());
                count++;
                total++;
                value12.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtPlus13:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value13.getText().toString());
                count++;
                total++;
                value13.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtPlus14:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value14.getText().toString());
                count++;
                total++;
                value14.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtPlus15:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value15.getText().toString());
                count++;
                total++;
                value15.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtPlus16:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value16.getText().toString());
                count++;
                total++;
                value16.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtPlus17:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value17.getText().toString());
                count++;
                total++;
                value17.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtPlus55:
                total = Integer.parseInt(totalStockOtherCategory.getText().toString());
                count = Integer.parseInt(value55.getText().toString());
                count++;
                total++;
                value55.setText(String.valueOf(count));
                totalStockOtherCategory.setText(String.valueOf(total));
                break;
        }
    }

    public void decrement(View v) {
        switch (v.getId()) {
            case R.id.txtMinus1:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value1.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value1.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus2:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value2.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value2.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus3:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value3.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value3.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus4:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value4.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value4.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus5:
                total = Integer.parseInt(stock_shirt.getText().toString());
                count = Integer.parseInt(value5.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value5.setText(String.valueOf(count));
                stock_shirt.setText(String.valueOf(total));
                break;
            case R.id.txtMinus6:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value6.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value6.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus7:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value7.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value7.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus8:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value8.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value8.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus9:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value9.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value9.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus10:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value10.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value10.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus11:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value11.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value11.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus12:
                total = Integer.parseInt(stock_pant.getText().toString());
                count = Integer.parseInt(value12.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value12.setText(String.valueOf(count));
                stock_pant.setText(String.valueOf(total));
                break;
            case R.id.txtMinus13:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value13.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value13.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtMinus14:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value14.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value14.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtMinus15:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value15.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value15.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtMinus16:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value16.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value16.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtMinus17:
                total = Integer.parseInt(stock_shoes.getText().toString());
                count = Integer.parseInt(value17.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value17.setText(String.valueOf(count));
                stock_shoes.setText(String.valueOf(total));
                break;
            case R.id.txtMinus55:
                total = Integer.parseInt(totalStockOtherCategory.getText().toString());
                count = Integer.parseInt(value55.getText().toString());
                if (count <= 0) count = 0;
                else if (total <= 0) total = 0;
                else {
                    count--;
                    total--;
                }
                value55.setText(String.valueOf(count));
                totalStockOtherCategory.setText(String.valueOf(total));
                break;
        }

    }

    private void loadExistingProductDetails(String productKey) {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(productKey);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {


                    String displayId = snapshot.child("productDisplayId").getValue(String.class);
                    if (displayId != null && productIdTextView != null) {
                        productIdTextView.setText(displayId);
                    } else if (category != null && productIdTextView != null) {
                        // If no display ID exists, generate one
                        String generatedProductId = generateProductId(category);
                        productIdTextView.setText(generatedProductId);
                    }

                    // Load brand name
                    String brand = snapshot.child("productBrandName").getValue(String.class);
                    if (brand != null && brandName != null) {
                        brandName.setText(brand);
                    }

                    // Load product name
                    String productNameValue = snapshot.child("productName").getValue(String.class);
                    if (productNameValue != null && productName != null) {
                        productName.setText(productNameValue);
                    }

                    // Load original price
                    String originalPrice = snapshot.child("originalPrice").getValue(String.class);
                    if (originalPrice != null && original_price != null) {
                        original_price.setText(originalPrice);
                    }

                    // Load selling price
                    String sellingPrice = snapshot.child("sellingPrice").getValue(String.class);
                    if (sellingPrice != null && selling_price != null) {
                        selling_price.setText(sellingPrice);
                    }

                    // Load packing
                    String packing = snapshot.child("productPacking").getValue(String.class);
                    if (packing != null && pack_of_product != null) {
                        pack_of_product.setText(packing);
                    }

                    // Load color
                    String color = snapshot.child("productColour").getValue(String.class);
                    if (color != null && get_color_name != null) {
                        get_color_name.setText(color);
                        setTextViewBackgroundTint(get_color, getColorResourceId(color));
                    }

                    // Load weight
                    String weight = snapshot.child("productWeight").getValue(String.class);
                    if (weight != null && autoCompleteWeight != null) {
                        autoCompleteWeight.setText(weight);
                    }

                    // Load ideal for
                    String idealFor = snapshot.child("productSuitFor").getValue(String.class);
                    if (idealFor != null && idealType != null) {
                        idealType.setText(idealFor);
                    }

                    // Load fabric type
                    String fabric = snapshot.child("productFabric").getValue(String.class);
                    if (fabric != null && fabricType != null) {
                        fabricType.setText(fabric);
                    }

                    // Load occasion
                    String occasion = snapshot.child("productOccasion").getValue(String.class);
                    if (occasion != null && selectionType != null) {
                        selectionType.setText(occasion);
                    }

                    // Load wash care
                    String washCare = snapshot.child("productWashcare").getValue(String.class);
                    if (washCare != null && fabric_careType != null) {
                        fabric_careType.setText(washCare);
                    }

                    // Load mobile details if they exist
                    String ramValue = snapshot.child("ram").getValue(String.class);
                    if (ramValue != null && ram_mobile != null) {
                        ram_mobile.setText(ramValue);
                    }

                    String storageValue = snapshot.child("storage").getValue(String.class);
                    if (storageValue != null && storage_mobile != null) {
                        storage_mobile.setText(storageValue);
                    }

                    String processorValue = snapshot.child("processor").getValue(String.class);
                    if (processorValue != null && processor_mobile != null) {
                        processor_mobile.setText(processorValue);
                    }

                    String rearCameraValue = snapshot.child("rearCamera").getValue(String.class);
                    if (rearCameraValue != null && rear_camera_mobile != null) {
                        rear_camera_mobile.setText(rearCameraValue);
                    }

                    String frontCameraValue = snapshot.child("frontCamera").getValue(String.class);
                    if (frontCameraValue != null && front_camera_mobile != null) {
                        front_camera_mobile.setText(frontCameraValue);
                    }

                    String batteryValue = snapshot.child("battery").getValue(String.class);
                    if (batteryValue != null && battery_mobile != null) {
                        battery_mobile.setText(batteryValue);
                    }

                    // Load saved amount and discount
                    String savedAmount = snapshot.child("savedAmount").getValue(String.class);
                    String discountPercent = snapshot.child("discountPercent").getValue(String.class);
                    if (savedAmount != null && txt_minus_amount != null) {
                        txt_minus_amount.setText("Saved: ₹" + savedAmount);
                    }
                    if (discountPercent != null && txt_discount != null) {
                        txt_discount.setText(discountPercent + " off");
                    }

                    // Load description
                    String description = snapshot.child("productDescription").getValue(String.class);
                    // You'll need an EditText for description if you want to display it

                    // Load search keywords
                    if (snapshot.hasChild("searchKeyWord")) {
                        ArrayList<String> keywords = (ArrayList<String>) snapshot.child("searchKeyWord").getValue();
                        if (keywords != null && keywords.size() >= 5) {
                            if (search_wordET1 != null) search_wordET1.setText(keywords.get(0));
                            if (search_wordET2 != null) search_wordET2.setText(keywords.get(1));
                            if (search_wordET3 != null) search_wordET3.setText(keywords.get(2));
                            if (search_wordET4 != null) search_wordET4.setText(keywords.get(3));
                            if (search_wordET5 != null) search_wordET5.setText(keywords.get(4));

                            // ADD THE VISIBILITY CODE HERE
                            // Show/hide search layouts based on filled keywords
                            if (!keywords.get(1).isEmpty()) {
                                search_key_hide_gone2.setVisibility(View.VISIBLE);
                                plus_click1.setVisibility(View.GONE);
                            }
                            if (!keywords.get(2).isEmpty()) {
                                search_key_hide_gone3.setVisibility(View.VISIBLE);
                                plus_click2.setVisibility(View.GONE);
                            }
                            if (!keywords.get(3).isEmpty()) {
                                search_key_hide_gone4.setVisibility(View.VISIBLE);
                                plus_click3.setVisibility(View.GONE);
                            }
                            if (!keywords.get(4).isEmpty()) {
                                search_key_hide_gone5.setVisibility(View.VISIBLE);
                                plus_click4.setVisibility(View.GONE);
                            }
                        }
                    }

                    // Load sizes and stock based on category - IMPORTANT FIX HERE
                    if (category != null) {
                        visibleLayoutSize(category);

                        // Now load stock values based on visible layout
                        if (snapshot.hasChild("totalStock")) {
                            String totalStockValue = snapshot.child("totalStock").getValue(String.class);

                            if (totalStockValue != null) {
                                if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
                                    // Load shirt sizes
                                    if (snapshot.hasChild("productSizes")) {
                                        ArrayList<String> sizes = (ArrayList<String>) snapshot.child("productSizes").getValue();
                                        if (sizes != null && sizes.size() >= 5) {
                                            if (value1 != null) value1.setText(sizes.get(0));
                                            if (value2 != null) value2.setText(sizes.get(1));
                                            if (value3 != null) value3.setText(sizes.get(2));
                                            if (value4 != null) value4.setText(sizes.get(3));
                                            if (value5 != null) value5.setText(sizes.get(4));
                                        }
                                    }
                                    if (stock_shirt != null) stock_shirt.setText(totalStockValue);

                                } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
                                    // Load jeans sizes
                                    if (snapshot.hasChild("productSizes")) {
                                        ArrayList<String> sizes = (ArrayList<String>) snapshot.child("productSizes").getValue();
                                        if (sizes != null && sizes.size() >= 7) {
                                            if (value6 != null) value6.setText(sizes.get(0));
                                            if (value7 != null) value7.setText(sizes.get(1));
                                            if (value8 != null) value8.setText(sizes.get(2));
                                            if (value9 != null) value9.setText(sizes.get(3));
                                            if (value10 != null) value10.setText(sizes.get(4));
                                            if (value11 != null) value11.setText(sizes.get(5));
                                            if (value12 != null) value12.setText(sizes.get(6));
                                        }
                                    }
                                    if (stock_pant != null) stock_pant.setText(totalStockValue);

                                } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
                                    // Load shoes sizes
                                    if (snapshot.hasChild("productSizes")) {
                                        ArrayList<String> sizes = (ArrayList<String>) snapshot.child("productSizes").getValue();
                                        if (sizes != null && sizes.size() >= 5) {
                                            if (value13 != null) value13.setText(sizes.get(0));
                                            if (value14 != null) value14.setText(sizes.get(1));
                                            if (value15 != null) value15.setText(sizes.get(2));
                                            if (value16 != null) value16.setText(sizes.get(3));
                                            if (value17 != null) value17.setText(sizes.get(4));
                                        }
                                    }
                                    if (stock_shoes != null) stock_shoes.setText(totalStockValue);

                                } else {
                                    // For ALL OTHER categories INCLUDING smartphones
                                    // Use layoutOtherCategory for stock control
                                    if (value55 != null) value55.setText(totalStockValue);
                                    if (totalStockOtherCategory != null) {
                                        totalStockOtherCategory.setText(totalStockValue);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("LoadDetails", "Failed to load details: " + error.getMessage());
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        // Reset validation colors using custom method
        resetEditTextColour(brandName);
        resetEditTextColour(productName);
        resetEditTextColour(original_price);
        resetEditTextColour(selling_price);
        resetEditTextColour(pack_of_product);

        if (key != null) {
            Intent intent = new Intent(add_product_details.this, add_product_image.class);
            intent.putExtra("Key", key);
            if (category != null) intent.putExtra("Category", category);
            if (SubCategory != null) intent.putExtra("SubCategory", SubCategory);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    private void resetEditTextColour(EditText editText) {
        if (editText != null) {
            editText.setBackgroundResource(R.drawable.edittext_touch_effect);
            editText.setTextColor(getResources().getColor(R.color.black));
            // If you have a hint color in colors.xml
            // editText.setHintTextColor(getResources().getColor(R.color.hint_color));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("category", category);
        outState.putString("key", key);
        outState.putString("SubCategory", SubCategory);

        // Save generated product ID if we have one
        if (productIdTextView != null) {
            String productId = productIdTextView.getText().toString().trim();
            if (!TextUtils.isEmpty(productId)) {
                outState.putString("generatedProductId", productId);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        category = savedInstanceState.getString("category");
        key = savedInstanceState.getString("key");
        SubCategory = savedInstanceState.getString("SubCategory");

        // Restore generated product ID if we have one
        String savedProductId = savedInstanceState.getString("generatedProductId");
        if (savedProductId != null && productIdTextView != null) {
            productIdTextView.setText(savedProductId);
        }
    }
}