package com.example.uptrenddelivery;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class delivery_boy_info extends AppCompatActivity {

    EditText txtName, txtAddress, txtVehicleNo;
    TextView txtAge;
    MaterialAutoCompleteTextView spinnerState, spinnerCity;
    Button btnNext;
    loadingDialog loadingDialog;
    DatabaseReference tempRef;
    String uid, email, mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_boy_info);

        txtName = findViewById(R.id.txtName);
        txtAddress = findViewById(R.id.txtAddress);
        txtVehicleNo = findViewById(R.id.txtVehicleNo);
        txtAge = findViewById(R.id.txtAge);
        spinnerState = findViewById(R.id.spinnerState);
        spinnerCity = findViewById(R.id.spinnerCity);
        btnNext = findViewById(R.id.btnNext);

        loadingDialog = new loadingDialog(this);
        uid = getIntent().getStringExtra("uid");
        email = getIntent().getStringExtra("email");
        mobile = getIntent().getStringExtra("mobile");

        tempRef = FirebaseDatabase.getInstance().getReference("TempDeliveryRegistration").child(uid);

        // Pre-fill name from previous step
        String name = getIntent().getStringExtra("name");
        if (name != null) txtName.setText(name);

        txtAge.setOnClickListener(v -> showDatePicker());

        btnNext.setOnClickListener(v -> savePersonalInfo());
    }

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            int age = Calendar.getInstance().get(Calendar.YEAR) - year;
            txtAge.setText(String.valueOf(age));
        }, cal.get(Calendar.YEAR) - 18, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void savePersonalInfo() {
        String name = txtName.getText().toString().trim();
        String address = txtAddress.getText().toString().trim();
        String vehicleNo = txtVehicleNo.getText().toString().trim();
        String age = txtAge.getText().toString().trim();
        String state = spinnerState.getText().toString().trim();
        String city = spinnerCity.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(address) || TextUtils.isEmpty(vehicleNo) ||
                TextUtils.isEmpty(age) || TextUtils.isEmpty(state) || TextUtils.isEmpty(city)) {
            Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("email", email);
        data.put("mobile", mobile);
        data.put("address", address);
        data.put("vehicleNo", vehicleNo);
        data.put("age", age);
        data.put("state", state);
        data.put("city", city);

        tempRef.setValue(data).addOnCompleteListener(task -> {
            loadingDialog.dismiss();
            if (task.isSuccessful()) {
                Intent intent = new Intent(delivery_boy_info.this, document_delivery.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Failed to save info", Toast.LENGTH_SHORT).show();
            }
        });
    }
}