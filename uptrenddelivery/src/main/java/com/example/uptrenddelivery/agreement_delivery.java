package com.example.uptrenddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class agreement_delivery extends AppCompatActivity {

    CheckBox checkTerms, checkPrivacy;
    Button btnAgree;
    String uid;
    DatabaseReference tempRef, finalRef;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement_delivery);

        checkTerms = findViewById(R.id.checkTerms);
        checkPrivacy = findViewById(R.id.checkPrivacy);
        btnAgree = findViewById(R.id.btnAgree);

        uid = getIntent().getStringExtra("uid");
        tempRef = FirebaseDatabase.getInstance().getReference("TempDeliveryRegistration").child(uid);
        finalRef = FirebaseDatabase.getInstance().getReference("DeliveryBoys").child(uid);
        loadingDialog = new loadingDialog(this);

        checkTerms.setOnCheckedChangeListener((btn, checked) -> updateButton());
        checkPrivacy.setOnCheckedChangeListener((btn, checked) -> updateButton());

        btnAgree.setOnClickListener(v -> {
            if (checkTerms.isChecked() && checkPrivacy.isChecked()) {
                createFinalAccount();
            } else {
                Toast.makeText(this, "Please accept both", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateButton() {
        btnAgree.setEnabled(checkTerms.isChecked() && checkPrivacy.isChecked());
    }

    private void createFinalAccount() {
        loadingDialog.show();

        tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, Object> data = new HashMap<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        data.put(child.getKey(), child.getValue());
                    }
                    data.put("status", "offline");
                    data.put("totalEarnings", 0);
                    data.put("completedOrders", 0);

                    finalRef.setValue(data).addOnCompleteListener(task -> {
                        loadingDialog.dismiss();
                        tempRef.removeValue(); // Clean temp
                        if (task.isSuccessful()) {
                            Toast.makeText(agreement_delivery.this, "Account Created!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(agreement_delivery.this, dashboard_delivery.class));
                            finishAffinity();
                        } else {
                            Toast.makeText(agreement_delivery.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingDialog.dismiss();
                Toast.makeText(agreement_delivery.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}