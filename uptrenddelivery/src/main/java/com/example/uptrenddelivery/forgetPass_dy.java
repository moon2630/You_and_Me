package com.example.uptrenddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.github.muddz.styleabletoast.StyleableToast;

public class forgetPass_dy extends AppCompatActivity {

    EditText email_forget_dy;
    AppCompatButton btn_send_dy,btn_shine3;

    TextView click_info, back;
    LinearLayout linearLayout;
    CardView cardView;
    Animation top;
    RelativeLayout rl_anime3;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass_dy);



        email_forget_dy = findViewById(R.id.email_forget);
        btn_send_dy = findViewById(R.id.btn_forget_delivery);

        click_info = findViewById(R.id.instruction_click);
        linearLayout = findViewById(R.id.linearLayout);
        cardView = findViewById(R.id.cardView);

        back = findViewById(R.id.back_To_login);

        rl_anime3 = findViewById(R.id.top_anim3);



        top = AnimationUtils.loadAnimation(this, R.anim.top);
        rl_anime3.setAnimation(top);


        btn_shine3 = findViewById(R.id.shine_btn_Sign3);
        auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("DeliveryBoy");


        ScheduledExecutorService scheduledExecutorService =
                Executors.newSingleThreadScheduledExecutor();


        scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread((new Runnable() {
                    @Override
                    public void run() {
                        shineStart();
                    }
                }));
            }
        }, 1, 2, TimeUnit.SECONDS);

        btn_send_dy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=email_forget_dy.getText().toString();
                if(validInput(email)){
                    FirebaseUtils.checkUserEmailInDatabase(databaseReference, email, new FirebaseUtils.EmailCheckListener() {
                        @Override
                        public void onEmailCheckResult(boolean emailExists) {
                            if(emailExists){
                                sendResetEmail(email);
                            }else{
                                ChangeColour.errorColour(getApplicationContext(),
                                        email_forget_dy,
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
                startActivity(new Intent(getApplicationContext(),delivery_log_in.class));
            }
        });
        //edittext border and vector changed colour method
        ChangeColour.changeColour(getApplicationContext(),
                email_forget_dy,
                R.drawable.edittext_touch_effect
                ,R.drawable.email_vector_effect
        );
    }
    private void shineStart() {
        Animation animation = new TranslateAnimation(
                0, btn_send_dy.getWidth() + btn_shine3.getWidth(), 0, 0);

        animation.setDuration(600);
        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());

        btn_shine3.startAnimation(animation);

    }
    //validInput Method will check the input is valid or not
    private boolean validInput(String email){
        if (TextUtils.isEmpty(email)) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_forget_dy,
                    "Email is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );

            return false;
        } else if (!isValidEmail(email) ){
            ChangeColour.errorColour(getApplicationContext(),
                    email_forget_dy,
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

    private void sendResetEmail(String email) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(forgot_password.this, "Check your Email", Toast.LENGTH_SHORT).show();
                    StyleableToast.makeText(forgetPass_dy.this, "Check your Email",R.style.UptrendToast).show();
                }
            }
        });
    }
}
