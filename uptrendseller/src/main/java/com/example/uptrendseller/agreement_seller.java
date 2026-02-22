package com.example.uptrendseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;

public class agreement_seller extends AppCompatActivity {


    TextView txt1,txt2;
    AppCompatButton btnContinue,shine_btn;
    CheckBox checkBox1,checkBox2;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_seller);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }



        txt1 = findViewById(R.id.terms_txt);
        txt2 = findViewById(R.id.privacy_txt);
        checkBox1=findViewById(R.id.checkbox1);
        checkBox2=findViewById(R.id.checkbox2);

        btnContinue=findViewById(R.id.btnContinue);




        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),condition_use_seller.class));
            }
        });

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),privacy_policy_seller.class));
            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkBoxIsChecked()){

                }
            }
        });
    }

    private boolean checkBoxIsChecked() {
        if (!checkBox2.isChecked() || !checkBox1.isChecked()) {
            StyleableToast.makeText(getApplicationContext(),"Please You Can Select Our Terms & Conditions",R.style.UptrendToast).show();
            return false;
        }else{
            startActivity(new Intent(getApplicationContext(), sucessfull_seller.class));
            finish();
            return true;
        }
    }

}
