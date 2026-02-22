package com.example.uptrend;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class splash_return_pd extends AppCompatActivity {


    CardView keep_shopping,view_all_orders;

    TextView back_btn,txtCancel,txtReturn;
    String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_return_pd);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }




        keep_shopping = findViewById(R.id.keep_shopping);
        view_all_orders= findViewById(R.id.view_all_orders);
        back_btn = findViewById(R.id.back_btn55);
        txtCancel=findViewById(R.id.txtCancel);
        txtReturn=findViewById(R.id.txtReturn);

        status=getIntent().getStringExtra("status");
        if(status.equals("cancel")){
            txtReturn.setVisibility(View.GONE);
            txtCancel.setVisibility(View.VISIBLE);
        } else if (status.equals("return")) {
            txtCancel.setVisibility(View.GONE);
            txtReturn.setVisibility(View.VISIBLE);
        }


        keep_shopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), home.class));
                finish();
            }
        });

        view_all_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), complete_order.class));
                finish();

            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), complete_order.class));
                finish();
            }
        });
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), complete_order.class);
        startActivity(intent);
        finish();
    }
}