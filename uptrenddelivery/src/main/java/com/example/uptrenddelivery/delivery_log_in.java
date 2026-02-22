package com.example.uptrenddelivery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class delivery_log_in extends AppCompatActivity {

    TextInputEditText txtEmail, txtPassword;
    Button btnLogin;
    TextView txtSignUp;
    FirebaseAuth mAuth;
    DatabaseReference deliveryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_log_in);

        // Initialize views
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignUp = findViewById(R.id.txtSignUp);

        mAuth = FirebaseAuth.getInstance();
        deliveryRef = FirebaseDatabase.getInstance().getReference("DeliveryBoys");

        // Login button click
        btnLogin.setOnClickListener(v -> {
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (validateLogin(email, password)) {
                loginDeliveryBoy(email, password);
            }
        });

        // Sign up link
        txtSignUp.setOnClickListener(v -> {
            startActivity(new Intent(delivery_log_in.this, delivery_create_account.class));
            finish();
        });
    }

    private boolean validateLogin(String email, String password) {
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Email is required");
            txtEmail.requestFocus();
            return false;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("Invalid email");
            txtEmail.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError("Password is required");
            txtPassword.requestFocus();
            return false;
        }
        if (password.length() < 6) {
            txtPassword.setError("Password too short");
            txtPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loginDeliveryBoy(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkIfDeliveryBoy(user.getUid());
                        }
                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkIfDeliveryBoy(String uid) {
        deliveryRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Login Successful
                    Toast.makeText(delivery_log_in.this, "Login Successful!", Toast.LENGTH_SHORT).show();

                    // Save login state using SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("DeliveryPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // Go to dashboard
                    startActivity(new Intent(delivery_log_in.this, dashboard_delivery.class));
                    finish();

                } else {
                    mAuth.signOut();
                    Toast.makeText(delivery_log_in.this, "Account not registered as Delivery Boy", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(delivery_log_in.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}