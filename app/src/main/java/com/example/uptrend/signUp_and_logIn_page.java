package com.example.uptrend;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.adapteranddatamodel.Pattern;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DataModel.User;
import io.github.muddz.styleabletoast.StyleableToast;

public class signUp_and_logIn_page extends AppCompatActivity {

    SpinKitView load2;
    loadingDialog loadingDialog;
    Timer timer;
    AppCompatButton btnCreateAccount,btnLogin,AC_btn1,AC_btn2,shine1,shine2;
    private TextView txtForgotPassword,txt_privacy_ca,txt_privacy_sign,txt_terms_ca,txt_terms_sign;
    LinearLayout layoutHideShowCreateAccount,layoutHideShowSignIn;

    private EditText name_CA,mobileno_CA,email_CA,password_CA,email_SI,password_SI;
    private CheckBox checkBox,checkBox2;
    private String status;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private RadioButton radioBtnCreateAccount, radioBtnSignIn;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_and_log_in_page);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        radioBtnCreateAccount = findViewById(R.id.radioBtn_create_account);
        radioBtnSignIn = findViewById(R.id.radioBtn_signIn);
        //initialization for checkbox
        checkBox = findViewById(R.id.checkbox_create_account);
        checkBox2 = findViewById(R.id.checkbox_signin);

        //initialization for create account
        name_CA = findViewById(R.id.name_create_account);
        mobileno_CA = findViewById(R.id.mobileno_create_account);
        email_CA = findViewById(R.id.email_create_account);
        password_CA= findViewById(R.id.password_create_account);
        btnCreateAccount=findViewById(R.id.create_account);

        //initialization for sign in
        email_SI = findViewById(R.id.email_signin);
        password_SI = findViewById(R.id.password_signin);
        btnLogin=findViewById(R.id.sign_in);
        loadingDialog=new loadingDialog(this);

        //initialization for layout hide show
        layoutHideShowCreateAccount=findViewById(R.id.layout_hide_show_create_account);
        layoutHideShowSignIn=findViewById(R.id.layout_hide_show_signin);

        //initialization for forget password
        txtForgotPassword=findViewById(R.id.txtForgotPassword);

        //initialization for privacy policy and condition of use
        txt_privacy_ca = findViewById(R.id.privacy_policy_CA);
        txt_privacy_sign = findViewById(R.id.privacy_policy_signIn);
        txt_terms_ca = findViewById(R.id.terms_CA);
        txt_terms_sign = findViewById(R.id.terms_signIn);

        //load2 = findViewById(R.id.loading2);
        timer=new Timer();

        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("User");

        auth = FirebaseAuth.getInstance();



        status = getIntent().getStringExtra("status");
        if ("SignIn".equals(status)) {
            layoutHideShowSignIn.setVisibility(View.VISIBLE);
            layoutHideShowCreateAccount.setVisibility(View.GONE);
            radioBtnSignIn.setChecked(true);
            radioBtnCreateAccount.setChecked(false);
        } else if ("CreateAccount".equals(status)) {
            layoutHideShowCreateAccount.setVisibility(View.VISIBLE);
            layoutHideShowSignIn.setVisibility(View.GONE);
            radioBtnCreateAccount.setChecked(true);
            radioBtnSignIn.setChecked(false);
        }

        // Radio button listeners
        radioBtnCreateAccount.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioBtnSignIn.setChecked(false);
                layoutHideShowCreateAccount.setVisibility(View.VISIBLE);
                layoutHideShowSignIn.setVisibility(View.GONE);
            }
        });

        radioBtnSignIn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                radioBtnCreateAccount.setChecked(false);
                layoutHideShowSignIn.setVisibility(View.VISIBLE);
                layoutHideShowCreateAccount.setVisibility(View.GONE);
            }
        });


        //checkbox method
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    password_CA.setTransformationMethod(null);
                }else {
                    password_CA.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        checkBox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    password_SI.setTransformationMethod(null);
                }else {
                    password_SI.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        //button create account
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User u=new User();
                u.setUserName(name_CA.getText().toString().trim());
                u.setUserEmail(email_CA.getText().toString().trim());
                String password=password_CA.getText().toString().trim();
                u.setUserMobileNumber(mobileno_CA.getText().toString());
                if(validInput(u,password)) {
                    createUser(u, password);
                }
            }
        });

        //button login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=email_SI.getText().toString().trim();
                String password=password_SI.getText().toString().trim();
                if(validInputForLogin(email,password)) {
                    FirebaseUtils.checkUserEmailInDatabase(databaseReference, email, new FirebaseUtils.EmailCheckListener() {
                        @Override
                        public void onEmailCheckResult(boolean emailExists) {
                            if(emailExists){
                                loginUser(email,password);
                            }else{
                                ChangeColour.errorColour(getApplicationContext(),
                                        email_SI,
                                        "Wrong Email",
                                        R.drawable.edittext_error_effect,
                                        R.drawable.email_vector_red_error
                                );
                            }
                        }
                    });
                }
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), forgot_password.class));
                finish();
            }
        });
        txt_privacy_ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),privacy_policy_CA.class);
                i.putExtra("status","CreateAccount");
                startActivity(i);

            }
        });
        txt_privacy_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),privacy_policy_CA.class);
                i.putExtra("status","SignIn");
                startActivity(i);
            }
        });
        txt_terms_ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),terms_condition.class);
                i.putExtra("status","CreateAccount");
                startActivity(i);
            }
        });
        txt_terms_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getApplicationContext(),terms_condition.class);
                i.putExtra("status","SignIn");
                startActivity(i);
            }
        });

        ChangeColour.changeColour(getApplicationContext(),email_CA,R.drawable.edittext_touch_effect,R.drawable.email_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),email_SI,R.drawable.edittext_touch_effect,R.drawable.email_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),password_CA,R.drawable.edittext_touch_effect,R.drawable.lock_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),password_SI,R.drawable.edittext_touch_effect,R.drawable.key_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),mobileno_CA,R.drawable.edittext_touch_effect,R.drawable.phone_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),name_CA,R.drawable.edittext_touch_effect,R.drawable.person_vector_effect);
    }

    //method of login
    public void loginUser(String email,String password){
        //load2.setVisibility(View.VISIBLE);
        loadingDialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(),home.class));
                            finish();
                            // load2.setVisibility(View.GONE);
                        }
                    },3200);
                }else{
                    loadingDialog.cancel();
                    ChangeColour.errorColour(
                            getApplicationContext(),
                            password_SI,
                            "Wrong Password",
                            R.drawable.edittext_error_effect,
                            R.drawable.key_vector_red_error
                    );
                    StyleableToast.makeText(getApplicationContext(),"Login Failed",R.style.UptrendToast).show();
                }
            }
        });
    }

    //method of create user
    public void createUser(User u,String password){
        //load2.setVisibility(View.VISIBLE);
        loadingDialog.show();
        auth.createUserWithEmailAndPassword(u.getUserEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    u.setUserId(auth.getCurrentUser().getUid());
                    databaseReference.push().setValue(u);


                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(),home.class));
                            finish();
                            // load2.setVisibility(View.GONE);
                        }
                    },3200);

                }else{
                    loadingDialog.cancel();
                    StyleableToast.makeText(getApplicationContext(),"Failed",R.style.UptrendToast).show();
                }

            }
        });
    }

    // this method is for email is valid or not
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //this method will check if all input is valid or not

    private boolean validInput(User user, String password) {
        if (TextUtils.isEmpty(user.getUserEmail())) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_CA,
                    "Email is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        } else if (!isValidEmail(user.getUserEmail())) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_CA,
                    "Invalid Email",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ChangeColour.errorColour(getApplicationContext(),
                    password_CA,
                    "Password is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.lock_vector_red_error
            );
            return false;
        } else if (password.length()<=5) {
            ChangeColour.errorColour(getApplicationContext(),
                    password_CA,
                    "Password is Too short Min 6 Char",
                    R.drawable.edittext_error_effect,
                    R.drawable.lock_vector_red_error
            );
            return false;
        }
        if(TextUtils.isEmpty(user.getUserMobileNumber())){
            ChangeColour.errorColour(getApplicationContext(),
                    mobileno_CA,
                    "Mobile Number is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.phone_vector_red_error
            );
            return false;
        } else if (!Pattern.isValidMobileNumber(user.getUserMobileNumber())) {
            ChangeColour.errorColour(getApplicationContext(),
                    mobileno_CA,
                    "Invalid Mobile Number",
                    R.drawable.edittext_error_effect,
                    R.drawable.phone_vector_red_error
            );
            return false;
        }
        if(TextUtils.isEmpty(user.getUserName())){
            ChangeColour.errorColour(getApplicationContext(),
                    name_CA,
                    "Name is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.person_vector_red_error
            );
            return false;
        } else if (!Pattern.isValidName(user.getUserName())) {
            ChangeColour.errorColour(getApplicationContext(),
                    name_CA,
                    "This Filed Only Contain Character",
                    R.drawable.edittext_error_effect,
                    R.drawable.person_vector_red_error
            );
            return false;
        }
        return true;
    }
    private boolean validInputForLogin(String email,String password){

        if (TextUtils.isEmpty(email)) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_SI,
                    "Email is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        } else if (!isValidEmail(email)) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_SI,
                    "Invalid Email",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ChangeColour.errorColour(getApplicationContext(),
                    password_SI,
                    "Password is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.key_vector_red_error
            );
            return false;
        } else if (password.length()<=5) {
            ChangeColour.errorColour(getApplicationContext(),
                    password_SI,
                    "Password is Too short Min 6 Char",
                    R.drawable.edittext_error_effect,
                    R.drawable.key_vector_red_error
            );
            return false;
        }
        return true;
    }
}
