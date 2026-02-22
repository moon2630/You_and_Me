package com.example.uptrend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class select_language extends AppCompatActivity {

    private FirebaseUser user;
    private loadingDialog loadingDialog;
    private Timer timer;

    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        String status = sharedPreferences.getString("status", "");
        String code = sharedPreferences.getString("code", "");
        if (user != null) {
            setLanguage(code);
            startActivity(new Intent(getApplicationContext(), home.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        loadingDialog = new loadingDialog(this);
        timer = new Timer();

        // Find language TextViews
        RadioButton languageEnglish = findViewById(R.id.english);
        RadioButton languageHindi = findViewById(R.id.hindi);
        RadioButton languageTelugu = findViewById(R.id.telugu);
        RadioButton languagePunjabi = findViewById(R.id.punjabi);
        RadioButton languageMarathi = findViewById(R.id.marathi);
        RadioButton languageGujarati = findViewById(R.id.gujarati);

        // Set click listeners for each language TextView
        languageEnglish.setOnClickListener(v -> changeLanguage("en"));
        languageHindi.setOnClickListener(v -> changeLanguage("hi"));
        languagePunjabi.setOnClickListener(v -> changeLanguage("pa"));
        languageTelugu.setOnClickListener(v -> changeLanguage("te"));
        languageMarathi.setOnClickListener(v -> changeLanguage("mr"));
        languageGujarati.setOnClickListener(v -> changeLanguage("gu"));
    }

    public void setLanguage(String language_code) {
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        Locale locale = new Locale(language_code);
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    public void changeLanguage(String languageCode) {
        loadingDialog.show();
        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("status", "login");
        editor.putString("code", languageCode);
        editor.apply();
        loading(languageCode);
    }

    public void loading(String languageCode) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                setLanguage(languageCode);
                startActivity(new Intent(getApplicationContext(), skip_signin.class));
                finish();
            }
        }, 3000);
    }
}