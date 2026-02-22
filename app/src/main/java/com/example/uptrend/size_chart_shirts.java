package com.example.uptrend;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class size_chart_shirts extends AppCompatActivity {


    TextView txtBack;

    LinearLayout size_shirt_layout, shirt_img, size_shirt_layout_W, shirt_img_W, layoutMen, layoutWomen;
    String chart,productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_chart_shirts);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }



        size_shirt_layout = findViewById(R.id.size_layout_shirt);
        shirt_img = findViewById(R.id.size_image);
        size_shirt_layout_W = findViewById(R.id.size_layout_shirt1);
        shirt_img_W = findViewById(R.id.size_image1);

        layoutMen = findViewById(R.id.layoutMan);
        layoutWomen = findViewById(R.id.layoutWoman);

        txtBack=findViewById(R.id.btnBack);

        chart = getIntent().getStringExtra("chart");
        productId=getIntent().getStringExtra("productId");
        if (chart.equals("Men's(Top)")) {
            layoutMen.setVisibility(View.VISIBLE);
            layoutWomen.setVisibility(View.GONE);
        } else if (chart.equals("Women's(Top)")) {
            layoutWomen.setVisibility(View.VISIBLE);
            layoutMen.setVisibility(View.GONE);
        }


        size_shirt_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (shirt_img.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(shirt_img, new AutoTransition());

                shirt_img.setVisibility(var);
            }
        });


        size_shirt_layout_W.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (shirt_img_W.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(shirt_img_W, new AutoTransition());

                shirt_img_W.setVisibility(var);
            }
        });


        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),open_product.class);
                i.putExtra("productId",productId);
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