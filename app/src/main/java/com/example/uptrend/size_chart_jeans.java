package com.example.uptrend;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class size_chart_jeans extends AppCompatActivity {

    LinearLayout jeans_shirt_layout, jeans_img, size_jeans_layout_W, jeans_img_W, layoutMenBottom, layoutWomenBottom;
    String chart, productId;
    TextView txtBack;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_chart_jeans);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        // Initialize layouts
        jeans_shirt_layout = findViewById(R.id.size_layout_jeans);
        jeans_img = findViewById(R.id.size_image_jeans);
        size_jeans_layout_W = findViewById(R.id.size_layout_jeans_W);
        jeans_img_W = findViewById(R.id.size_image_jeans_W);
        layoutMenBottom = findViewById(R.id.layoutMenBottom);
        layoutWomenBottom = findViewById(R.id.layoutWomenBottom);
        txtBack = findViewById(R.id.btnBack);

        // Get Intent extras
        chart = getIntent().getStringExtra("chart");
        productId = getIntent().getStringExtra("productId");

        // Log the chart value for debugging
        Log.d("SizeChartJeans", "Received chart value: " + chart);

        // Set layout visibility based on chart value
        if ("Men's(Bottom)".equals(chart)) {
            layoutMenBottom.setVisibility(View.VISIBLE);
            layoutWomenBottom.setVisibility(View.GONE);
        } else if ("Women's(Bottom)".equals(chart)) {
            layoutWomenBottom.setVisibility(View.VISIBLE);
            layoutMenBottom.setVisibility(View.GONE);
        } else {
            // Default case: log error and set a fallback (e.g., Men's as default)
            Log.e("SizeChartJeans", "Invalid chart value: " + chart);
            layoutMenBottom.setVisibility(View.VISIBLE);
            layoutWomenBottom.setVisibility(View.GONE);
        }

        // Click listener for men's jeans size chart toggle
        jeans_shirt_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (jeans_img.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(jeans_img, new AutoTransition());
                jeans_img.setVisibility(var);
            }
        });

        // Click listener for women's jeans size chart toggle
        size_jeans_layout_W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (jeans_img_W.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(jeans_img_W, new AutoTransition());
                jeans_img_W.setVisibility(var);
            }
        });

        // Back button to return to open_product activity
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), open_product.class);
                i.putExtra("productId", productId);
                startActivity(i);
                finish();
            }
        });
    }
    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent i = new Intent(getApplicationContext(), open_product.class);
        i.putExtra("productId", productId);
        startActivity(i);
        finish();
    }
}