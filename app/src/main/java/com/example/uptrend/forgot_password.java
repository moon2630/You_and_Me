package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.TransitionManager;


import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DataModel.User;
import io.github.muddz.styleabletoast.StyleableToast;

public class forgot_password extends AppCompatActivity {

    TextView click_info, back;
    LinearLayout linearLayout;
    CardView cardView;
    EditText txtEmail;
    AppCompatButton btnSubmit, shine;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        //Initialization forgot password
        txtEmail = findViewById(R.id.txtEmail);
        btnSubmit = findViewById(R.id.btnSubmit);

        //Initialization for instruction effect
        click_info = findViewById(R.id.instruction_click);
        linearLayout = findViewById(R.id.linearLayout);
        cardView = findViewById(R.id.cardView);

        //Initialization for back button
        back = findViewById(R.id.back_To_login);



        auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("User");



        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=txtEmail.getText().toString();
                if(validInput(email)){
                    FirebaseUtils.checkUserEmailInDatabase(databaseReference, email, new FirebaseUtils.EmailCheckListener() {
                        @Override
                        public void onEmailCheckResult(boolean emailExists) {
                            if(emailExists){
                                sendResetEmail(email);
                            }else{
                                ChangeColour.errorColour(getApplicationContext(),
                                        txtEmail,
                                        "There Is No Email With this Account",
                                        R.drawable.edittext_error_effect,
                                        R.drawable.email_vector_red_error
                                );
                            }
                        }
                    });

                }
            }
        });

        click_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int var = (cardView.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
                TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());

                cardView.setVisibility(var);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), signUp_and_logIn_page.class);
                i.putExtra("status", "SignIn");
                startActivity(i);
            }
        });

        //edittext border and vector changed colour method
        ChangeColour.changeColour(getApplicationContext(),
                txtEmail,
                R.drawable.edittext_touch_effect
                ,R.drawable.email_vector_effect
        );
    }

    //sendResetEmail method will send to the Reset password link to the User.
    private void sendResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                   // Toast.makeText(forgot_password.this, "Check your Email", Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(forgot_password.this, "Check your Email",R.style.UptrendToast).show();
                }
            }
        });
    }


    //validInput Method will check the input is valid or not
    private boolean validInput(String email){
        if (TextUtils.isEmpty(email)) {
            ChangeColour.errorColour(getApplicationContext(),
                    txtEmail,
                    "Email is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );

            return false;
        } else if (!isValidEmail(email) ){
            ChangeColour.errorColour(getApplicationContext(),
                    txtEmail,
                    "Invalid email id",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        }
        return  true;
    }

    /* isValidEmail will check the enter email is valid or not according
       to the universal email pattern.
    */

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}


