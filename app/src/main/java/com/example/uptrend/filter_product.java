package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.TextViewCompat;

import com.example.adapteranddatamodel.DiscountRange;
import com.example.adapteranddatamodel.PriceRange;

import java.util.ArrayList;

public class filter_product extends AppCompatActivity {
    private AppCompatButton btnReset, btnApply;
    private CheckBox[] colorCheckBoxes;
    private CheckBox[] priceCheckBoxes;
    private CheckBox[] discountCheckBoxes;
    private RadioGroup genderRG, brandRG;
    private TextView gender_txt, color_txt, brand_txt, price_txt, discount_txt;
    private LinearLayout gender_layout, color_layout, brand_layout, price_layout, discount_layout;
    private ArrayList<String> colour;
    private ArrayList<PriceRange> price;
    private ArrayList<DiscountRange> discount;
    private String gender;
    private String brand;
    private String searchQuery;
    private TextView selectedTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_product);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }


        // Initialize views
        gender_txt = findViewById(R.id.gender_click);
        gender_layout = findViewById(R.id.gender_layout);
        color_txt = findViewById(R.id.colour_click);
        color_layout = findViewById(R.id.color_layout);
        brand_txt = findViewById(R.id.brand_click);
        brand_layout = findViewById(R.id.brand_layout);
        price_txt = findViewById(R.id.price_click);
        price_layout = findViewById(R.id.price_layout);
        discount_txt = findViewById(R.id.discount_click);
        discount_layout = findViewById(R.id.discount_layout);

        // Initialize RadioGroups
        genderRG = findViewById(R.id.genderRadioGroup);
        brandRG = findViewById(R.id.brandRadioGroup);

        // Initialize CheckBoxes
        colorCheckBoxes = new CheckBox[]{
                findViewById(R.id.checkbox_color1), findViewById(R.id.checkbox_color2),
                findViewById(R.id.checkbox_color3), findViewById(R.id.checkbox_color4),
                findViewById(R.id.checkbox_color5), findViewById(R.id.checkbox_color6),
                findViewById(R.id.checkbox_color7), findViewById(R.id.checkbox_color8),
                findViewById(R.id.checkbox_color9), findViewById(R.id.checkbox_color10),
                findViewById(R.id.checkbox_color11), findViewById(R.id.checkbox_color12),
                findViewById(R.id.checkbox_color13), findViewById(R.id.checkbox_color14),
                findViewById(R.id.checkbox_color15), findViewById(R.id.checkbox_color16),
                findViewById(R.id.checkbox_color17), findViewById(R.id.checkbox_color18),
                findViewById(R.id.checkbox_color19), findViewById(R.id.checkbox_color20),
                findViewById(R.id.checkbox_color21), findViewById(R.id.checkbox_color22),
                findViewById(R.id.checkbox_color23), findViewById(R.id.checkbox_color24),
                findViewById(R.id.checkbox_color25)
        };

        priceCheckBoxes = new CheckBox[]{
                findViewById(R.id.checkbox_brand1), findViewById(R.id.checkbox_brand2),
                findViewById(R.id.checkbox_brand3), findViewById(R.id.checkbox_brand4),
                findViewById(R.id.checkbox_brand5), findViewById(R.id.checkbox_brand6),
                findViewById(R.id.checkbox_brand7), findViewById(R.id.checkbox_brand8),
                findViewById(R.id.checkbox_brand9)
        };

        discountCheckBoxes = new CheckBox[]{
                findViewById(R.id.checkbox_discount1), findViewById(R.id.checkbox_discount2),
                findViewById(R.id.checkbox_discount3), findViewById(R.id.checkbox_discount4),
                findViewById(R.id.checkbox_discount5), findViewById(R.id.checkbox_discount6)
        };

        // Initialize buttons
        btnReset = findViewById(R.id.btnReset);
        btnApply = findViewById(R.id.btnApply);

        // Initialize data structures
        colour = new ArrayList<>();
        price = new ArrayList<>();
        discount = new ArrayList<>();

        // Get search query from intent
        searchQuery = getIntent().getStringExtra("searchQuery");

        selectedTextView = null;

        // Update click listeners with correct parameters and syntax
        gender_txt.setOnClickListener(v -> showLayout(gender_layout, gender_txt));
        color_txt.setOnClickListener(v -> showLayout(color_layout, color_txt));
        brand_txt.setOnClickListener(v -> showLayout(brand_layout, brand_txt));
        price_txt.setOnClickListener(v -> showLayout(price_layout, price_txt));
        discount_txt.setOnClickListener(v -> showLayout(discount_layout, discount_txt));


        // Update close_btn_filter click listener to pass searchQuery
        TextView close_btn_filter = findViewById(R.id.close_btn_filter);
        // Update close_btn_filter click listener
        close_btn_filter.setOnClickListener(v -> {
            Intent intent = new Intent(filter_product.this, search_product.class);
            intent.putExtra("activityName", "filterActivityNoApply");
            intent.putExtra("searchQuery", searchQuery);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });
        // Gender selection
        genderRG.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonMale) {
                gender = "male";
            } else if (checkedId == R.id.radioButtonFemale) {
                gender = "female";
            } else {
                gender = null;
            }
        });

        // Brand selection
        brandRG.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.Apple: brand = "Apple"; break;
                case R.id.Biba: brand = "Biba"; break;
                case R.id.Cadbury: brand = "Cadbury"; break;
                case R.id.Cartier: brand = "Cartier"; break;
                case R.id.Charlie_Bear: brand = "Charlie Bear"; break;
                case R.id.Doms: brand = "Doms"; break;
                case R.id.Gucci: brand = "Gucci"; break;
                case R.id.H_M: brand = "H & M"; break;
                case R.id.Jordan: brand = "Jordan"; break;
                case R.id.Lee: brand = "Lee"; break;
                case R.id.Lenskart: brand = "Lenskart"; break;
                case R.id.Levis: brand = "Levi's"; break;
                case R.id.Loreal: brand = "Loreal"; break;
                case R.id.Manish_Malhotra: brand = "Manish Malhotra"; break;
                case R.id.Marvel: brand = "Marvel"; break;
                case R.id.Mi: brand = "Mi"; break;
                case R.id.Motorola: brand = "Motorola"; break;
                case R.id.Nike: brand = "Nike"; break;
                case R.id.Oppo: brand = "Oppo"; break;
                case R.id.Puma: brand = "Puma"; break;
                case R.id.Redmi: brand = "Redmi"; break;
                case R.id.Realme: brand = "Realme"; break;
                case R.id.Rolex: brand = "Rolex"; break;
                case R.id.Samsung: brand = "Samsung"; break;
                case R.id.Sugar_Cosmetics: brand = "Sugar Cosmetics"; break;
                case R.id.Zara: brand = "Zara"; break;
                default: brand = null;
            }
        });


        // Reset button
        btnReset.setOnClickListener(v -> {
            genderRG.clearCheck();
            brandRG.clearCheck();
            for (CheckBox cb : colorCheckBoxes) cb.setChecked(false);
            for (CheckBox cb : priceCheckBoxes) cb.setChecked(false);
            for (CheckBox cb : discountCheckBoxes) cb.setChecked(false);
            colour.clear();
            price.clear();
            discount.clear();
            gender = null;
            brand = null;
        });

        // Apply button
        btnApply.setOnClickListener(v -> {
            colour.clear();
            price.clear();
            discount.clear();

            // Collect colors
            String[] colors = {"Aquamarine", "Azure", "Black", "Brown", "Coral", "Crimson", "Cyan", "Golden",
                    "Gray", "Green", "Hot Pink", "Lime", "Magent", "Maroon", "Navy Blue", "Olive", "Orange",
                    "Purple", "Red", "Royal Blue", "Silver", "Teal", "Wheat", "White", "Yellow"};
            for (int i = 0; i < colorCheckBoxes.length; i++) {
                if (colorCheckBoxes[i].isChecked()) {
                    colour.add(colors[i]);
                }
            }

            // Collect price ranges
            String[][] priceRanges = {
                    {"0", "500"}, {"501", "1000"}, {"1001", "1500"}, {"1501", "2000"},
                    {"2001", "2500"}, {"2501", "5000"}, {"5001", "10000"}, {"10001", "50000"},
                    {"50001", String.valueOf(Long.MAX_VALUE)}
            };
            for (int i = 0; i < priceCheckBoxes.length; i++) {
                if (priceCheckBoxes[i].isChecked()) {
                    price.add(new PriceRange(priceRanges[i][0], priceRanges[i][1]));
                }
            }
            // Collect discount ranges
            String[][] discountRanges = {
                    {"0.00", "20.99"}, {"21.00", "30.99"}, {"31.00", "40.99"},
                    {"41.00", "50.99"}, {"51.00", "80.99"}, {"81.00", "99.99"}
            };
            for (int i = 0; i < discountCheckBoxes.length; i++) {
                if (discountCheckBoxes[i].isChecked()) {
                    discount.add(new DiscountRange(discountRanges[i][0], discountRanges[i][1]));
                }
            }
            // Navigate to search_product with filter data
            Intent intent = new Intent(this, search_product.class);
            intent.putExtra("activityName", "filterActivity");
            intent.putExtra("searchQuery", searchQuery);
            intent.putExtra("colour", colour);
            intent.putExtra("gender", brand);
            intent.putExtra("price", price);
            intent.putExtra("discount", discount);
            startActivity(intent);
            finish();
        });
    }

    private void toggleVisibility(LinearLayout layout) {
        int visibility = layout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        TransitionManager.beginDelayedTransition(layout, new AutoTransition());
        layout.setVisibility(visibility);
    }

    // Then update the method
    private void showLayout(LinearLayout selectedLayout, TextView clickedText) {
        // Collapse all layouts except selectedLayout
        LinearLayout[] layouts = {gender_layout, color_layout, brand_layout, price_layout, discount_layout};
        for (LinearLayout layout : layouts) {
            if (layout != selectedLayout && layout.getVisibility() == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(layout, new AutoTransition());
                layout.setVisibility(View.GONE);
            }
        }

        // Toggle selected layout
        int visibility = selectedLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        TransitionManager.beginDelayedTransition(selectedLayout, new AutoTransition());
        selectedLayout.setVisibility(View.VISIBLE);

        // Get colors from resources
        int defaultColor = ContextCompat.getColor(this, R.color.black); // Your default text color
        int selectedColor = ContextCompat.getColor(this, R.color.greenmeee); // Use your green color

        // Reset previous TextView style
        if (selectedTextView != null && selectedTextView != clickedText) {
            selectedTextView.setTextColor(defaultColor);
            TextViewCompat.setCompoundDrawableTintList(selectedTextView, ColorStateList.valueOf(defaultColor));
        }

        // Style clicked TextView
        clickedText.setTextColor(selectedColor);
        TextViewCompat.setCompoundDrawableTintList(clickedText, ColorStateList.valueOf(selectedColor));
        selectedTextView = clickedText; // Update selected TextView
    }
    // Add onBackPressed to handle back navigation
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, search_product.class);
        intent.putExtra("activityName", "filterActivityNoApply");
        intent.putExtra("searchQuery", searchQuery);
        startActivity(intent);
        finish();
    }
}