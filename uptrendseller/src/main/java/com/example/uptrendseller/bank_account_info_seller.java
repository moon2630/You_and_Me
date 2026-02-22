package com.example.uptrendseller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adapteranddatamodel.Pattern;
import com.example.uptrendseller.Api.BankDetailsFetcher;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DataModel.AdminBankInformation;

public class bank_account_info_seller extends AppCompatActivity implements BankDetailsFetcher.OnBankDetailsFetchedListener{

    ProgressBar progressBar5;
    int valueProgress = 0;

    LinearLayout linearLayout;

    AppCompatButton btn_account,btn_shine4;
    Timer timer;
    loadingDialog loading;

    TextView txt_percentage_account,txt_bank_name,txt_branch_name,txt_address,txt_state_bank,txt_city_bank,verify_txt,verify,verify1,verify2;
    EditText txtAccountHolderName,txtAccountNumber,txtIfscCode,txtPanCardNumber,txtAdharCardNo;
    private FirebaseAuth auth;
    private DatabaseReference  databaseReference;
    AdminBankInformation adminBankInformation;
    MaterialAutoCompleteTextView accountType;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account_info_seller);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }



        progressBar5 = findViewById(R.id.progressbar_account);
        txt_percentage_account = findViewById(R.id.text_percentage_account);
        btn_account= findViewById(R.id.btn_account);

        txtAccountHolderName=findViewById(R.id.account_holder_name);
        txtAccountNumber=findViewById(R.id.account_number);
        txtIfscCode=findViewById(R.id.account_ifsc_code);
        txtPanCardNumber=findViewById(R.id.account_panCard_no);

        accountType=findViewById(R.id.accountType);
        txtAdharCardNo=findViewById(R.id.adhar_no);


        //initialization for bank details verify
        linearLayout = findViewById(R.id.layout_hide_show);
        txt_bank_name = findViewById(R.id.bank_name);
        txt_branch_name = findViewById(R.id.branch_name);
        txt_address = findViewById(R.id.address_bank);
        txt_state_bank = findViewById(R.id.state_bank);
        txt_city_bank = findViewById(R.id.city_bank);


        verify_txt = findViewById(R.id.verify_txt);
        verify = findViewById(R.id.verify_msg);
        verify1 = findViewById(R.id.verify_msg1);
        verify2 = findViewById(R.id.verify_msg2);


        loading=new loadingDialog(this);


        // In onCreate, after initializing accountType
        ArrayAdapter<CharSequence> accountTypeAdapter = ArrayAdapter.createFromResource(
                getApplicationContext(),
                R.array.account_type_spinner,
                R.layout.spinner_dropdown_item
        );
        accountTypeAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        accountType.setAdapter(accountTypeAdapter);



        txtIfscCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    verify_txt.setVisibility(View.VISIBLE);
                    verify_txt.setText("click to verify");
                    verify_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            ContextCompat.getDrawable(getApplicationContext(),R.drawable.verify_vector),
                            null
                    );

                }
            }
        });
//

        txtAccountNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    verify.setText("Verify");

                }
            }
        });



        txtPanCardNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    verify1.setText("Verify");

                }
            }
        });

        txtAdharCardNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    verify2.setText("Verify");

                }
            }
        });





        adminBankInformation=new AdminBankInformation();


        auth=FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("AdminBankInformation");

        btn_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminBankInformation.setAdminId(auth.getCurrentUser().getUid());
                adminBankInformation.setAccountHolderName(txtAccountHolderName.getText().toString().trim());
                adminBankInformation.setAccountNumber(txtAccountNumber.getText().toString().trim());
                adminBankInformation.setIfscCode(txtIfscCode.getText().toString().trim());
                adminBankInformation.setAccountType(accountType.getText().toString());
                adminBankInformation.setPanCardNumber(txtPanCardNumber.getText().toString().trim());
                adminBankInformation.setAdharCard(txtAdharCardNo.getText().toString().trim());
                if(validInput(adminBankInformation)){
                        addBankInformation(adminBankInformation);
                }

//                addBankInformation(adminBankInformation);

            }
        });

        verify_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BankDetailsFetcher bankDetailsFetcher = new BankDetailsFetcher(bank_account_info_seller.this);
                bankDetailsFetcher.execute(txtIfscCode.getText().toString().trim());
                DetailsVisible();

            }
        });

        // In onCreate, after initializing verify_txt
        verify_txt.setOnClickListener(v -> {
            String ifscCode = txtIfscCode.getText().toString().trim();
            if (!TextUtils.isEmpty(ifscCode)) {
                new BankDetailsFetcher(bank_account_info_seller.this).execute(ifscCode);
            } else {
                ChangeColour.errorColour(getApplicationContext(), txtIfscCode, "IFSC Code is Required");
            }
        });
        ChangeColour.changeColour(getApplicationContext(),txtAccountHolderName);
        ChangeColour.changeColour(getApplicationContext(),txtAccountNumber);
        ChangeColour.changeColour(getApplicationContext(),accountType);
        ChangeColour.changeColour(getApplicationContext(),txtIfscCode);
        ChangeColour.changeColour(getApplicationContext(),txtPanCardNumber);
        ChangeColour.changeColour(getApplicationContext(),txtAdharCardNo);

        accountType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                accountType.setError(null);
            }
        });

        /*
                TextError Massage Code.
         */
        txtAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Pattern.isValidAccountNumber(editable.toString())){
                    verify.setTextColor(getColor(R.color.green));
                } else if (editable.length()==0) {
                    verify.setText("");
                }else{
                    verify.setText("Verify");
                    verify.setTextColor(getColor(R.color.red));
                }

            }
        });
        txtPanCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                    if(Pattern.isValidPanCard(editable.toString())){
                        verify1.setText("Verify");
                        verify1.setTextColor(getColor(R.color.green));
                    } else if (editable.length() == 0) {
                        verify1.setText("");
                    }else {
                        verify1.setText("Verify");
                        verify1.setTextColor(getColor(R.color.red));
                    }
            }
        });

        txtAdharCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(Pattern.isValidAadharNumber(editable.toString())){
                    verify2.setText("Verify");
                    verify2.setTextColor(getColor(R.color.green));
                } else if (editable.length() == 0) {
                    verify2.setText("");
                }else {
                    verify2.setText("Verify");
                    verify2.setTextColor(getColor(R.color.red));
                }
            }
        });



    }

    private void addBankInformation(AdminBankInformation adminBankInformation) {
        loading.show();
        databaseReference.push().setValue(adminBankInformation);
       SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPreferences.edit();
       valueProgress = sharedPreferences.getInt("process", 0);
       valueProgress += 25;
       editor.putInt("process", valueProgress);
       editor.apply();
       updateProgress(valueProgress);
       timer=new Timer();
       timer.schedule(new TimerTask() {
           @Override
           public void run() {
               startActivity(new Intent(getApplicationContext(),agreement_seller.class));
               finish();
               loading.cancel();
           }
       },2500);

    }

    @Override
    protected void onStart () {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        valueProgress = sharedPreferences.getInt("process", 0);
        if (valueProgress >= 0) {
            progressBar5.setProgress(valueProgress);
            txt_percentage_account.setText(valueProgress + "%");
        }
    }

    private void updateProgress ( int value)
    {
        progressBar5.setProgress(value);
        txt_percentage_account.setText(valueProgress + "%");
    }
    public void DetailsVisible(){
        int isVisible= linearLayout.getVisibility();
        if (isVisible== View.VISIBLE){
            linearLayout.setVisibility(View.GONE);
        }else {
            linearLayout.setVisibility(View.VISIBLE);
        }
    }

    public boolean validInput(AdminBankInformation adminBankInformation){
        if(TextUtils.isEmpty(adminBankInformation.getAccountHolderName())){
            ChangeColour.errorColour(getApplicationContext(),
                    txtAccountHolderName,
                    "This Filed Is Required"
            );
            return false;
        }else if (!Pattern.isValidName(adminBankInformation.getAccountHolderName())) {
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtAccountHolderName,
                    "This Filed Contain Only Character"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminBankInformation.getAccountNumber())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtAccountNumber,
                    "This Filed Is Required"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminBankInformation.getAccountType())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    accountType,
                    "Please Select Account Type"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminBankInformation.getIfscCode())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtIfscCode,
                    "This Filed Is Required"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminBankInformation.getPanCardNumber())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtPanCardNumber,
                    "This Filed Is Required"
            );
            return false;
        } else if (!Pattern.isValidPanCard(adminBankInformation.getPanCardNumber())) {
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtPanCardNumber,
                    "Pan Card Is Invalid"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminBankInformation.getAdharCard())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtAdharCardNo,
                    "This Filed Is Required"
            );
            return false;
        }else if (!Pattern.isValidAadharNumber(adminBankInformation.getAdharCard())) {
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtAdharCardNo,
                    "Adhar Card Is Invalid"
            );
            return false;
        }
        return true;
    }

    @Override
    public void onSuccess(String bankName, String branch, String address, String contact, String city, String state) {
        txt_bank_name.setText(bankName);
        txt_branch_name.setText(branch);
        txt_address.setText(address);
        txt_city_bank.setText(city);
        txt_state_bank.setText(state);
        verify_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.verify_vector_green),
                null
        );
        verify_txt.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
    }

    @Override
    public void onFailure(String errorMessage) {
        verify_txt.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                ContextCompat.getDrawable(getApplicationContext(),R.drawable.verify_vector_red),
                null
        );

    }
}
