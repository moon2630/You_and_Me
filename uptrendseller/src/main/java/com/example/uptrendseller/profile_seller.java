package com.example.uptrendseller;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import DataModel.Admin;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.muddz.styleabletoast.StyleableToast;

public class profile_seller extends AppCompatActivity {

    private CircleImageView profileIv;
    private DatabaseReference sellerNode, sellerInfoNode;

    // permission constant
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 200;
    private static final int IMAGE_FROM_GALLERY_CODE = 300;
    private static final int IMAGE_FROM_CAMERA_CODE = 400;

    // string array of permission
    private String[] cameraPermission;
    private String[] storagePermission;
    private String nodeId, infoId;

    // image uri var
    private Uri imageUri;
    private FirebaseUser user;
    private Query seller, store;
    EditText txtSellerName, txtSellerMobileNo, txtStoreName;
    TextView txtSellerEmail, txtSave, add_img_txt;
    private FirebaseStorage storage;
    loadingDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_seller);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.green_meee));
        }

        profileIv = findViewById(R.id.image);
        txtSellerName = findViewById(R.id.profile_name);
        txtSellerMobileNo = findViewById(R.id.profile_mobile_no);
        txtSellerEmail = findViewById(R.id.profile_email);
        txtStoreName = findViewById(R.id.profile_display);
        txtSave = findViewById(R.id.save_btn);
        add_img_txt = findViewById(R.id.add_img_txt);

        // init permission
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Getting The Current Login User Instance.
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Making Instance of Database.
        sellerNode = FirebaseDatabase.getInstance().getReference("Admin");
        sellerInfoNode = FirebaseDatabase.getInstance().getReference("AdminStoreInformation");

        // Getting Seller Information and displaying on Edit textview as well as textview
        seller = sellerNode.orderByChild("adminId").equalTo(user.getUid());
        loading = new loadingDialog(profile_seller.this);
        seller.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loading.show();
                DataSnapshot sellerSnapshot = snapshot.getChildren().iterator().next();
                Admin admin = sellerSnapshot.getValue(Admin.class);
                nodeId = sellerSnapshot.getKey();
                txtSellerName.setText(admin.getAdminName());
                txtSellerEmail.setText(admin.getAdminEmail());
                txtSellerMobileNo.setText(admin.getAdminMobileNumber());
                Glide.with(getApplicationContext()).load(admin.getProfileImage()).into(profileIv);
                loading.cancel();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loading.cancel();
                Toast.makeText(profile_seller.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
            }
        });

        store = sellerInfoNode.orderByChild("adminId").equalTo(user.getUid());
        store.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildren().iterator().hasNext()) {
                    DataSnapshot sellerSnapshot = snapshot.getChildren().iterator().next();
                    String storeName = sellerSnapshot.child("storeName").getValue(String.class);
                    infoId = sellerSnapshot.getKey();
                    txtStoreName.setText(storeName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(profile_seller.this, "Failed to load store data", Toast.LENGTH_SHORT).show();
            }
        });

        add_img_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });

        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading.show();
                Map<String, Object> updateValues = new HashMap<>();
                updateValues.put("adminName", txtSellerName.getText().toString().trim());
                updateValues.put("adminMobileNumber", txtSellerMobileNo.getText().toString().trim());
                String storeName = txtStoreName.getText().toString().trim();
                imageUri = getUri();
                updateData(updateValues, imageUri, storeName, nodeId, infoId);
            }
        });
    }

    public void updateData(Map<String, Object> updateValues, Uri uri, String storeName, String nodeID, String infoID) {
        storage = FirebaseStorage.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Admin").child(nodeID);
        DatabaseReference infoReference = FirebaseDatabase.getInstance().getReference("AdminStoreInformation").child(infoID);
        Map<String, Object> update = new HashMap<>();
        update.put("storeName", storeName);

        if (uri != null) {
            StorageReference profileImagesRef = storage.getReference().child("Profile Images");
            StorageReference upload = profileImagesRef.child("profileImage" + UUID.randomUUID());
            upload.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    upload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            updateValues.put("profileImage", downloadUri.toString());
                            databaseReference.updateChildren(updateValues);
                            infoReference.updateChildren(update);
                            loading.cancel();
                            StyleableToast.makeText(getApplicationContext(), "Profile Updated Successfully", R.style.UptrendToast).show();
                        }
                    }).addOnFailureListener(e -> {
                        loading.cancel();
                        Toast.makeText(profile_seller.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
                }
            }).addOnFailureListener(e -> {
                loading.cancel();
                Toast.makeText(profile_seller.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        } else {
            databaseReference.updateChildren(updateValues);
            infoReference.updateChildren(update);
            loading.cancel();
            StyleableToast.makeText(getApplicationContext(), "Profile Updated Successfully", R.style.UptrendToast).show();
        }
    }

    public Uri getUri() {
        Uri imageUri = null;
        Drawable drawable = profileIv.getDrawable();
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            FileOutputStream fileOutputStream;
            try {
                File file = new File(getCacheDir(), "image.png");
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                imageUri = Uri.fromFile(file);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(profile_seller.this, "Error processing image", Toast.LENGTH_SHORT).show();
            }
        }
        Log.d("image", "image uri in method: " + imageUri);
        return imageUri;
    }

    private void showImagePickerDialog() {
        String options[] = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromCamera();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        }).create().show();
    }

    private ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            imageUri = data.getData();
                            profileIv.setImageURI(imageUri);
                        } else {
                            profileIv.setImageURI(imageUri); // For camera, imageUri is set in pickFromCamera
                        }
                    } else {
                        Toast.makeText(profile_seller.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private void handleBackPress() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Cancel Edit");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("You have unsaved changes. Discard them and exit?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {
                    Intent intent = new Intent(profile_seller.this, dashboard_admin.class);

                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            Typeface customFont = ResourcesCompat.getFont(this, R.font.caudex);
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTypeface(customFont, Typeface.BOLD);
            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont, Typeface.BOLD);
        }
    }


    private void pickFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        activityResultLauncher.launch(galleryIntent);
    }

    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "IMAGE_TITLE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "IMAGE_DETAILS");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activityResultLauncher.launch(cameraIntent);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result && result1;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, cameraPermission, CAMERA_PERMISSION_CODE);
    }

    private boolean checkStoragePermission() {
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        return result1;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, storagePermission, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera();
                    } else {
                        Toast.makeText(getApplicationContext(), "Camera & Storage Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getApplicationContext(), "Storage Permission Needed", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }



    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        handleBackPress();
    }
}