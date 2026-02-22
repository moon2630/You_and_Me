package com.example.uptrendseller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.adapteranddatamodel.Pattern;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DataModel.Admin;
import io.github.muddz.styleabletoast.StyleableToast;

public class admin_create_account extends AppCompatActivity {


    private EditText name_CA_seller, mobileno_CA_seller, email_CA_seller, password_CA_seller;
    private CheckBox checkBox;

    loadingDialog loading;
    Timer timer;
    AppCompatButton btn_create_account,shine2;
    private FirebaseAuth auth;
    private DatabaseReference  databaseReference;

    ProgressBar progressBar1;
    int valueProgress = 0;

    TextView txt, txt_percentage_create_account,txt_pass_msg,txt_log;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_account);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        progressBar1 = findViewById(R.id.progressbar_create_account);
        txt_percentage_create_account = findViewById(R.id.text_percentage_create_account);

        name_CA_seller = findViewById(R.id.name_create_account);
        mobileno_CA_seller = findViewById(R.id.mobileno_create_account);
        email_CA_seller = findViewById(R.id.email_create_account);
        password_CA_seller = findViewById(R.id.password_create_account);
        checkBox = findViewById(R.id.checkbox_create_account);
        btn_create_account = findViewById(R.id.btn_create_account_admin);

        txt_log = findViewById(R.id.logIn_txt);


        txt_pass_msg = findViewById(R.id.password_msg);

        timer=new Timer();

        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("Admin");
        loading=new loadingDialog(this);


        auth = FirebaseAuth.getInstance();



        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password=password_CA_seller.getText().toString().trim();
                Admin admin=new Admin();
                admin.setAdminName(name_CA_seller.getText().toString().trim());
                admin.setAdminEmail(email_CA_seller.getText().toString().trim());
                admin.setAdminMobileNumber(mobileno_CA_seller.getText().toString().trim());
                if(validInput(admin,password)) {
                    createAdmin(password, admin);
                }
            }
        });
        password_CA_seller.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    txt_pass_msg.setText("Password must be at list 6 characters");
                }
            }
        });


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    password_CA_seller.setTransformationMethod(null);
                } else {
                    password_CA_seller.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });


        txt_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),admin_login.class));
            }
        });

        password_CA_seller.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()>=6){
                    txt_pass_msg.setTextColor(getColor(R.color.green));
                } else if (editable.length()==0) {
                    txt_pass_msg.setText("");
                } else{
                    txt_pass_msg.setTextColor(getColor(R.color.red));
                    txt_pass_msg.setText("Password must be at list 6 characters");
                }
            }
        });
        ChangeColour.changeColour(getApplicationContext(),mobileno_CA_seller,R.drawable.edittext_touch_effect,R.drawable.phone_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),name_CA_seller,R.drawable.edittext_touch_effect,R.drawable.person_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),email_CA_seller,R.drawable.edittext_touch_effect,R.drawable.email_vector_effect);
        ChangeColour.changeColour(getApplicationContext(),password_CA_seller,R.drawable.edittext_touch_effect,R.drawable.lock_vector_effect);
    }

    private boolean validInput(Admin admin,String password) {
        if (TextUtils.isEmpty(admin.getAdminEmail())) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_CA_seller,
                    "Email is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        } else if (!isValidEmail(admin.getAdminEmail())) {
            ChangeColour.errorColour(getApplicationContext(),
                    email_CA_seller,
                    "Invalid Email",
                    R.drawable.edittext_error_effect,
                    R.drawable.email_vector_red_error
            );
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            ChangeColour.errorColour(getApplicationContext(),
                    password_CA_seller,
                    "Password is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.lock_vector_red_error
            );
            return false;
        } else if (password.length()<=5) {
            ChangeColour.errorColour(getApplicationContext(),
                    password_CA_seller,
                    "Password is Too short Min 6 Char",
                    R.drawable.edittext_error_effect,
                    R.drawable.lock_vector_red_error
            );
            return false;
        }
        if(TextUtils.isEmpty(admin.getAdminMobileNumber())){
            ChangeColour.errorColour(getApplicationContext(),
                    mobileno_CA_seller,
                    "Mobile Number is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.phone_vector_red_error
            );
            return false;
        }else if (!Pattern.isValidMobileNumber(admin.getAdminMobileNumber())) {
            ChangeColour.errorColour(getApplicationContext(),
                    mobileno_CA_seller,
                    "Invalid Mobile Number",
                    R.drawable.edittext_error_effect,
                    R.drawable.phone_vector_red_error
            );
            return false;
        }
        if(TextUtils.isEmpty(admin.getAdminName())){
            ChangeColour.errorColour(getApplicationContext(),
                    name_CA_seller,
                    "Name is required",
                    R.drawable.edittext_error_effect,
                    R.drawable.person_vector_red_error
            );
            return false;
        }else if (!Pattern.isValidName(admin.getAdminName())) {
            ChangeColour.errorColour(getApplicationContext(),
                    name_CA_seller,
                    "This Filed Only Contain Character",
                    R.drawable.edittext_error_effect,
                    R.drawable.person_vector_red_error
            );
            return false;
        }
        return true;
    }
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void createAdmin(String password, Admin admin) {

        loading.show();

        auth.createUserWithEmailAndPassword(admin.getAdminEmail(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    admin.setAdminId(auth.getCurrentUser().getUid());
                    databaseReference.push().setValue(admin);


                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            Intent i=new Intent(getApplicationContext(),email_verification.class);
                            i.putExtra("email",admin.getAdminEmail());
                            startActivity(i);
                            // load2.setVisibility(View.GONE);
                        }
                    },3200);

                    SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    valueProgress = sharedPreferences.getInt("process", 0);
                    valueProgress += 25;
                    editor.putInt("process", valueProgress);
                    editor.apply();
                    updateProgress(valueProgress);

                }else{
                    loading.cancel();
                    ChangeColour.errorColour(
                            getApplicationContext(),
                            email_CA_seller,
                            "Email is existed",
                            R.drawable.edittext_error_effect,
                            R.drawable.email_vector_red_error
                    );
                    StyleableToast.makeText(getApplicationContext(),"This Email is already existed",R.style.UptrendToast).show();
                }
            }
        });

    }

    @Override
    protected void onStart () {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        valueProgress = sharedPreferences.getInt("process", 0);
        if (valueProgress >= 0) {
            progressBar1.setProgress(valueProgress);
            txt_percentage_create_account.setText(valueProgress + "%");
        }
    }

    private void updateProgress ( int value)
    {
        progressBar1.setProgress(value);
        txt_percentage_create_account.setText(valueProgress + "%");
    }

}



