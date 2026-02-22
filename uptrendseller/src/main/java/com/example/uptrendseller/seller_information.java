package com.example.uptrendseller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapteranddatamodel.CityDataListener;
import com.example.adapteranddatamodel.Pattern;
import com.example.adapteranddatamodel.StateDataListener;
import com.example.uptrendseller.Api.FetchCities;
import com.example.uptrendseller.Api.FetchStates;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import DataModel.Admin;
import DataModel.AdminStoreInformation;
import DataModel.StateData;
import io.github.muddz.styleabletoast.StyleableToast;

public class seller_information extends AppCompatActivity implements StateDataListener, CityDataListener {

    ProgressBar progressBar3;
    loadingDialog loading;
    MaterialAutoCompleteTextView txtCity,txtState;
    Timer timer;
    int valueProgress = 0;

    AppCompatButton btn_seller_info, btn_shine2;
    EditText txtStoreName,txtPincode, txtAddress1, txtAddress2,txtSellerName;

    TextView txt_percentage_seller_info,pinCode_txt;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private AdminStoreInformation adminStoreInformation;
    ArrayAdapter<String> cityAdapter,stateAdapter;
    ArrayList<String> stateData,cityData,isoCode;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_information);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }


        progressBar3 = findViewById(R.id.progressbar_seller_info);
        txt_percentage_seller_info = findViewById(R.id.text_percentage_seller_info);
        btn_seller_info = findViewById(R.id.btn_seller_info);


        txtCity=findViewById(R.id.txtCity);
        txtState=findViewById(R.id.txtState);

        txtStoreName = findViewById(R.id.store_name_seller);
        txtPincode = findViewById(R.id.pincode_seller);
        txtAddress1 = findViewById(R.id.address1_seller);
        txtAddress2 = findViewById(R.id.address2_seller);
        txtSellerName=findViewById(R.id.seller_name);


        pinCode_txt = findViewById(R.id.pincode_msg);
        stateData=new ArrayList<>();
        cityData=new ArrayList<>();
        isoCode=new ArrayList<>();
        loading=new loadingDialog(this);
        new FetchStates(this).execute();

        auth = FirebaseAuth.getInstance();
        adminStoreInformation = new AdminStoreInformation();
        databaseReference = FirebaseDatabase.getInstance().getReference("AdminStoreInformation");


        txtState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                fetchCitiesForState(isoCode.get(i));
            }
        });



        txtPincode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if(hasFocus){
                    pinCode_txt.setText("Enter the 6 Digit PinCode");

                }
            }
        });



        btn_seller_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminStoreInformation.setAdminId(auth.getCurrentUser().getUid());
                adminStoreInformation.setStoreName(txtStoreName.getText().toString().trim());
                adminStoreInformation.setStorePincode(txtPincode.getText().toString().trim());
                adminStoreInformation.setStoreAddress1(txtAddress1.getText().toString().trim());
                adminStoreInformation.setStoreAddress2(txtAddress2.getText().toString().trim());
                adminStoreInformation.setSellerName(txtSellerName.getText().toString().trim());
                adminStoreInformation.setStoreState(txtState.getText().toString());
                adminStoreInformation.setStoreCity(txtCity.getText().toString());
                if(validInput(adminStoreInformation)){
                    addStoreInformation(adminStoreInformation);
                }


            }
        });
        ChangeColour.changeColour(getApplicationContext(),txtSellerName);
        ChangeColour.changeColour(getApplicationContext(),txtStoreName);
        ChangeColour.changeColour(getApplicationContext(),txtPincode);
        ChangeColour.changeColour(getApplicationContext(),txtAddress1);
        ChangeColour.changeColour(getApplicationContext(),txtAddress2);
        ChangeColour.changeColour(getApplicationContext(),txtState);
        ChangeColour.changeColour(getApplicationContext(),txtCity);



        txtPincode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.length()==6){
                    pinCode_txt.setTextColor(getColor(R.color.green));
                } else if (editable.length()==0) {
                    pinCode_txt.setText("");
                }else{
                    pinCode_txt.setTextColor(getColor(R.color.red));
                    pinCode_txt.setText("Enter the 6 Digit PinCode");
                }
            }
        });

    }

    private void addStoreInformation(AdminStoreInformation adminStoreInformation) {
        loading.show();
        databaseReference.push().setValue(adminStoreInformation);
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
                startActivity(new Intent(getApplicationContext(), bank_account_info_seller.class));
                finish();
                loading.cancel();
            }
        },2500);


    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        valueProgress = sharedPreferences.getInt("process", 0);
        if (valueProgress >= 0) {
            progressBar3.setProgress(valueProgress);
            txt_percentage_seller_info.setText(valueProgress + "%");
        }
    }

    private void updateProgress(int value) {
        progressBar3.setProgress(value);
        txt_percentage_seller_info.setText(valueProgress + "%");
    }


    public boolean validInput(AdminStoreInformation adminStoreInformation){
        if(TextUtils.isEmpty(adminStoreInformation.getSellerName())){
            ChangeColour.errorColour(getApplicationContext(),txtSellerName,"This Filed Is Required");
            return false;
        } else if (!Pattern.isValidName(adminStoreInformation.getSellerName())) {
            ChangeColour.errorColour(getApplicationContext(),
                    txtSellerName,
                    "This Filed Only Contain Character"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminStoreInformation.getStoreName())){
            ChangeColour.errorColour(getApplicationContext(),
                    txtStoreName,
                    "This Filed Is Required"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminStoreInformation.getStoreAddress1())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtAddress1,
                    "This Filed Is Required"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminStoreInformation.getStoreAddress2())){
            ChangeColour.errorColour(
                    getApplicationContext(),
                    txtAddress2,
                    "This Filed Is Required"
            );
            return false;
        }
        if(TextUtils.isEmpty(adminStoreInformation.getStoreState())){
            StyleableToast.makeText(getApplicationContext(),"Please Select The State",R.style.UptrendToast).show();
            return false;
        }
        if(TextUtils.isEmpty(adminStoreInformation.getStoreCity())){
            StyleableToast.makeText(getApplicationContext(),"Please Select The City",R.style.UptrendToast).show();
            return false;
        }

        return true;
    }

    @Override
    public void onStateDataReceived(ArrayList<StateData> stateDataList) {
        Collections.sort(stateDataList, new Comparator<StateData>() {
            @Override
            public int compare(StateData stateData1, StateData stateData2) {
                return stateData1.getStateName().compareToIgnoreCase(stateData2.getStateName());
            }
        });
        for (StateData state : stateDataList) {
            stateData.add(state.getStateName());
            isoCode.add(state.getIsoCode());
        }
        stateAdapter=new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_dropdown_item, stateData);
        txtState.setAdapter(stateAdapter);
    }
    private void fetchCitiesForState(String isoCode) {
        // Use the selected state's ISO code to fetch cities
        String citiesApiUrl = "https://api.countrystatecity.in/v1/countries/IN/states/" + isoCode + "/cities";
        // Example using AsyncTask:
        new FetchCities(this).execute(citiesApiUrl);
    }

    @Override
    public void onCityDataReceived(ArrayList<String> cityData) {
        Collections.sort(cityData  );
        cityAdapter=new ArrayAdapter<>(getApplicationContext(),
                R.layout.spinner_dropdown_item, cityData
        );
       txtCity.setAdapter(cityAdapter);
    }
}
