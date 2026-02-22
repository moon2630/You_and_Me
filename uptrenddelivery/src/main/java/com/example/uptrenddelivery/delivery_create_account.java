package com.example.uptrenddelivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class delivery_create_account extends AppCompatActivity {

    TextInputEditText txtName, txtEmail, txtMobile, txtPassword;
    Button btnCreateAccount;
    TextView txtLogin;
    FirebaseAuth mAuth;
    loadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_create_account);

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtMobile = findViewById(R.id.txtMobile);
        txtPassword = findViewById(R.id.txtPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        txtLogin = findViewById(R.id.txtLogin);

        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new loadingDialog(this);

        btnCreateAccount.setOnClickListener(v -> {
            String name = txtName.getText().toString().trim();
            String email = txtEmail.getText().toString().trim();
            String mobile = txtMobile.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if (validateInput(name, email, mobile, password)) {
                registerUser(name, email, mobile, password);
            }
        });

        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, delivery_log_in.class));
            finish();
        });
    }

    private boolean validateInput(String name, String email, String mobile, String password) {
        if (TextUtils.isEmpty(name)) {
            txtName.setError("Name required");
            return false;
        }
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            txtEmail.setError("Valid email required");
            return false;
        }
        if (TextUtils.isEmpty(mobile) || mobile.length() != 10) {
            txtMobile.setError("Valid 10-digit mobile required");
            return false;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            txtPassword.setError("Password must be 6+ characters");
            return false;
        }
        return true;
    }

    private void registerUser(String name, String email, String mobile, String password) {
        loadingDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Save basic info temporarily and go to next step
                            Intent intent = new Intent(delivery_create_account.this, delivery_boy_info.class);
                            intent.putExtra("name", name);
                            intent.putExtra("email", email);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("uid", user.getUid());
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        loadingDialog.dismiss();
                        Toast.makeText(this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}