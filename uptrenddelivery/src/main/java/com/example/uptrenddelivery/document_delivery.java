package com.example.uptrenddelivery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class document_delivery extends AppCompatActivity {

    ImageView imgAadhar, imgLicense, imgRC;
    Button btnUploadAadhar, btnUploadLicense, btnUploadRC, btnSubmitDocuments;
    loadingDialog loadingDialog;
    DatabaseReference tempRef;
    StorageReference storageRef;
    String uid;
    Uri aadharUri, licenseUri, rcUri;

    private int currentUpload = 0;

    ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    if (currentUpload == 1) {
                        aadharUri = uri;
                        Glide.with(this).load(uri).into(imgAadhar);
                    } else if (currentUpload == 2) {
                        licenseUri = uri;
                        Glide.with(this).load(uri).into(imgLicense);
                    } else if (currentUpload == 3) {
                        rcUri = uri;
                        Glide.with(this).load(uri).into(imgRC);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_delivery);

        imgAadhar = findViewById(R.id.imgAadhar);
        imgLicense = findViewById(R.id.imgLicense);
        imgRC = findViewById(R.id.imgRC);
        btnUploadAadhar = findViewById(R.id.btnUploadAadhar);
        btnUploadLicense = findViewById(R.id.btnUploadLicense);
        btnUploadRC = findViewById(R.id.btnUploadRC);
        btnSubmitDocuments = findViewById(R.id.btnSubmitDocuments);

        loadingDialog = new loadingDialog(this);
        uid = getIntent().getStringExtra("uid");
        tempRef = FirebaseDatabase.getInstance().getReference("TempDeliveryRegistration").child(uid);
        storageRef = FirebaseStorage.getInstance().getReference("DeliveryDocuments");

        btnUploadAadhar.setOnClickListener(v -> { currentUpload = 1; pickImage.launch("image/*"); });
        btnUploadLicense.setOnClickListener(v -> { currentUpload = 2; pickImage.launch("image/*"); });
        btnUploadRC.setOnClickListener(v -> { currentUpload = 3; pickImage.launch("image/*"); });

        btnSubmitDocuments.setOnClickListener(v -> uploadDocuments());
    }

    private void uploadDocuments() {
        if (aadharUri == null || licenseUri == null || rcUri == null) {
            Toast.makeText(this, "Upload all documents", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingDialog.show();

        uploadFile(aadharUri, "aadhar", url1 -> {
            uploadFile(licenseUri, "license", url2 -> {
                uploadFile(rcUri, "rc", url3 -> {
                    HashMap<String, Object> docs = new HashMap<>();
                    docs.put("aadharUrl", url1);
                    docs.put("licenseUrl", url2);
                    docs.put("rcUrl", url3);

                    tempRef.updateChildren(docs).addOnCompleteListener(task -> {
                        loadingDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(this, agreement_delivery.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Failed to save documents", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            });
        });
    }

    private void uploadFile(Uri uri, String type, UploadCallback callback) {
        StorageReference fileRef = storageRef.child(uid + "_" + type + ".jpg");
        fileRef.putFile(uri)
                .addOnSuccessListener(task -> fileRef.getDownloadUrl()
                        .addOnSuccessListener(downloadUri -> callback.onSuccess(downloadUri.toString())))
                .addOnFailureListener(e -> {
                    loadingDialog.dismiss();
                    Toast.makeText(this, "Upload failed: " + type, Toast.LENGTH_SHORT).show();
                });
    }

    interface UploadCallback {
        void onSuccess(String url);
    }
}