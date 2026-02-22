package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;

public class forget_pass_seller extends AppCompatActivity {


    TextView click_info, back;
    LinearLayout linearLayout;
    CardView cardView;
    private EditText email_forget;

    AppCompatButton btn_forget,shine;;
    private FirebaseAuth  auth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_seller);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        email_forget = findViewById(R.id.email_forget);
        btn_forget= findViewById(R.id.btn_forget);


        //Initialization for instruction effect
        click_info = findViewById(R.id.instruction_click);
        linearLayout = findViewById(R.id.linearLayout);
        cardView = findViewById(R.id.cardView);

        //Initialization for back button
        back = findViewById(R.id.back_To_login);




        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("Admin");

        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=email_forget.getText().toString().trim();
                if(validInput(email)){
                    FirebaseUtils.checkUserEmailInDatabase(databaseReference, email, new FirebaseUtils.EmailCheckListener() {
                        @Override
                        public void onEmailCheckResult(boolean emailExists) {
                            if(emailExists){
                                sendForgetPasswordLink(email);
                            }else{
                                ChangeColour.errorColour(getApplicationContext(),
                                        email_forget,
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
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),admin_login.class));
            }
        });
        ChangeColour.changeColour(
                getApplicationContext(),
                email_forget,
                R.drawable.edittext_touch_effect,
                R.drawable.email_vector_effect
        );
    }


    // it will send the reset password link.
    private void sendForgetPasswordLink(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    StyleableToast.makeText(forget_pass_seller.this, "Check Your Email",R.style.UptrendToast).show();
                }
            }
        });
    }

    //validInput Method will check the input is valid or not
    private boolean validInput(String email){
        if (TextUtils.isEmpty(email)) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_forget,
                    "Email is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );

            return false;
        } else if (!isValidEmail(email) ){
            ChangeColour.errorColour(getApplicationContext(),
                    email_forget,
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

