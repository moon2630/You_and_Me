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

public class size_chart_shoes extends AppCompatActivity {
    TextView txtBack;

    LinearLayout shoes_shirt_layout,shoes_img,size_shirt_layout_W,shirt_img_W,layoutMenShoes,layoutWomenShoes;
    String chart,productId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_size_chart_shoes);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        shoes_shirt_layout = findViewById(R.id.size_layout_shoes);
        shoes_img = findViewById(R.id.size_image_shoes);
        size_shirt_layout_W = findViewById(R.id.size_layout_shoes2);
        shirt_img_W = findViewById(R.id.size_image2);
        layoutMenShoes=findViewById(R.id.layoutMenShoes);
        layoutWomenShoes=findViewById(R.id.layoutWomenShoes);
        txtBack=findViewById(R.id.btnBack);

        chart=getIntent().getStringExtra("chart");
        productId=getIntent().getStringExtra("productId");
        if(chart.equals("Footware(Men)")){
            layoutMenShoes.setVisibility(View.VISIBLE);
            layoutWomenShoes.setVisibility(View.GONE);

        } else if (chart.equals("Footware(Women)")) {
            layoutWomenShoes.setVisibility(View.VISIBLE);
            layoutWomenShoes.setVisibility(View.GONE);
        }


        shoes_shirt_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (shoes_img.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(shoes_img, new AutoTransition());

                shoes_img.setVisibility(var);
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