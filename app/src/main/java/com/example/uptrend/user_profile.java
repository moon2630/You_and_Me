package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import DataModel.User;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.muddz.styleabletoast.StyleableToast;

public class user_profile extends AppCompatActivity {
    private FirebaseUser firebaseUser;
    private DatabaseReference userRef;
    private Query userQuery;
    private User user;
    private CircleImageView selectImageView;
    private ImageView avatarMan1, avatarMan2, avatarMan3, avatarMan4, avatarMan5;
    private ImageView avatarWoman1, avatarWoman2, avatarWoman3, avatarWoman4, avatarWoman5;
    private CardView avatarSelectionCard;
    private AppCompatButton btnCloseAvatar;
    private String selectedAvatar = "vector_profile"; // Default avatar
    private String selectedGender = null;

    EditText txtUserName, txtMobileNumber;
    TextView txtEmail, txtSelectImage;
    AppCompatButton btnUpdate;
    RadioGroup radioGroupGender;
    RadioButton radioButtonMale, radioButtonFemale;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }

        // Initialize views
        btnUpdate = findViewById(R.id.btnUpdate);
        selectImageView = findViewById(R.id.selectImage);
        txtEmail = findViewById(R.id.userEmail);
        txtUserName = findViewById(R.id.userName);
        txtMobileNumber = findViewById(R.id.userMobileNo);
        txtSelectImage = findViewById(R.id.txtSelectImage);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        radioButtonMale = findViewById(R.id.radioButtonMale);
        radioButtonFemale = findViewById(R.id.radioButtonFemale);
        avatarSelectionCard = findViewById(R.id.avatarSelectionCard);
        btnCloseAvatar = findViewById(R.id.btnCloseAvatar);

        // Avatar image views
        avatarMan1 = findViewById(R.id.avatarMan1);
        avatarMan2 = findViewById(R.id.avatarMan2);
        avatarMan3 = findViewById(R.id.avatarMan3);
        avatarMan4 = findViewById(R.id.avatarMan4);
        avatarMan5 = findViewById(R.id.avatarMan5);
        avatarWoman1 = findViewById(R.id.avatarWoman1);
        avatarWoman2 = findViewById(R.id.avatarWoman2);
        avatarWoman3 = findViewById(R.id.avatarWoman3);
        avatarWoman4 = findViewById(R.id.avatarWoman4);
        avatarWoman5 = findViewById(R.id.avatarWoman5);

        // Getting Current User
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        user = new User();

        displayUserDetails();

        txtSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toggle visibility of avatar selection layout
                if (avatarSelectionCard.getVisibility() == View.VISIBLE) {
                    avatarSelectionCard.setVisibility(View.GONE);
                } else {
                    avatarSelectionCard.setVisibility(View.VISIBLE);
                    resetAllAvatarBackgrounds();
                    highlightSelectedAvatar();
                }
            }
        });
        btnCloseAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide avatar selection layout when close button is clicked
                avatarSelectionCard.setVisibility(View.GONE);
            }
        });

        // Set avatar selection listeners
        setAvatarClickListeners();

        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.radioButtonMale) {
                    selectedGender = "Male";
                } else if (id == R.id.radioButtonFemale) {
                    selectedGender = "Female";
                }
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user.setUserId(firebaseUser.getUid());
                user.setUserName(txtUserName.getText().toString().trim());
                user.setUserMobileNumber(txtMobileNumber.getText().toString().trim());
                user.setUserEmail(txtEmail.getText().toString());
                user.setUserGender(selectedGender);
                user.setAvatarImage(selectedAvatar);

                // No need to set profileImage anymore

                profileUpdate(user);
            }
        });

        TextView back_btn33 = findViewById(R.id.back_btn33);
        back_btn33.setOnClickListener(v -> {
            Intent intent = new Intent(user_profile.this, account_user.class);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            startActivity(intent);
            finish();
        });
    }

    private void setAvatarClickListeners() {
        View.OnClickListener avatarClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Reset all avatar backgrounds
                resetAllAvatarBackgrounds();

                // Set selected background
                view.setSelected(true);

                // Get the selected avatar resource
                int resourceId = 0;
                String newAvatarName = "vector_profile";

                switch (view.getId()) {
                    case R.id.avatarMan1:
                        resourceId = R.drawable.vector_man1;
                        newAvatarName = "vector_man1";
                        break;
                    case R.id.avatarMan2:
                        resourceId = R.drawable.vector_man2;
                        newAvatarName = "vector_man2";
                        break;
                    case R.id.avatarMan3:
                        resourceId = R.drawable.vector_man3;
                        newAvatarName = "vector_man3";
                        break;
                    case R.id.avatarMan4:
                        resourceId = R.drawable.vector_man4;
                        newAvatarName = "vector_man4";
                        break;
                    case R.id.avatarMan5:
                        resourceId = R.drawable.vector_man5;
                        newAvatarName = "vector_man5";
                        break;
                    case R.id.avatarWoman1:
                        resourceId = R.drawable.vector_women1;
                        newAvatarName = "vector_women1";
                        break;
                    case R.id.avatarWoman2:
                        resourceId = R.drawable.vector_women2;
                        newAvatarName = "vector_women2";
                        break;
                    case R.id.avatarWoman3:
                        resourceId = R.drawable.vector_women3;
                        newAvatarName = "vector_women3";
                        break;
                    case R.id.avatarWoman4:
                        resourceId = R.drawable.vector_women4;
                        newAvatarName = "vector_women4";
                        break;
                    case R.id.avatarWoman5:
                        resourceId = R.drawable.vector_women5;
                        newAvatarName = "vector_women5";
                        break;
                }

                if (resourceId != 0) {
                    // Set the background of selectImageView
                    selectImageView.setBackgroundResource(resourceId);
                    selectedAvatar = newAvatarName;

                    // DO NOT close avatar selection - keep it visible
                    // avatarSelectionCard.setVisibility(View.GONE);
                }
            }
        };

        // Set listeners for all avatars
        avatarMan1.setOnClickListener(avatarClickListener);
        avatarMan2.setOnClickListener(avatarClickListener);
        avatarMan3.setOnClickListener(avatarClickListener);
        avatarMan4.setOnClickListener(avatarClickListener);
        avatarMan5.setOnClickListener(avatarClickListener);
        avatarWoman1.setOnClickListener(avatarClickListener);
        avatarWoman2.setOnClickListener(avatarClickListener);
        avatarWoman3.setOnClickListener(avatarClickListener);
        avatarWoman4.setOnClickListener(avatarClickListener);
        avatarWoman5.setOnClickListener(avatarClickListener);
    }
    private void resetAllAvatarBackgrounds() {
        avatarMan1.setSelected(false);
        avatarMan2.setSelected(false);
        avatarMan3.setSelected(false);
        avatarMan4.setSelected(false);
        avatarMan5.setSelected(false);
        avatarWoman1.setSelected(false);
        avatarWoman2.setSelected(false);
        avatarWoman3.setSelected(false);
        avatarWoman4.setSelected(false);
        avatarWoman5.setSelected(false);
    }

    private void highlightSelectedAvatar() {
        // Highlight the currently selected avatar
        switch (selectedAvatar) {
            case "vector_man1":
                avatarMan1.setSelected(true);
                break;
            case "vector_man2":
                avatarMan2.setSelected(true);
                break;
            case "vector_man3":
                avatarMan3.setSelected(true);
                break;
            case "vector_man4":
                avatarMan4.setSelected(true);
                break;
            case "vector_man5":
                avatarMan5.setSelected(true);
                break;
            case "vector_women1":
                avatarWoman1.setSelected(true);
                break;
            case "vector_women2":
                avatarWoman2.setSelected(true);
                break;
            case "vector_women3":
                avatarWoman3.setSelected(true);
                break;
            case "vector_women4":
                avatarWoman4.setSelected(true);
                break;
            case "vector_women5":
                avatarWoman5.setSelected(true);
                break;
        }
    }

    public void profileUpdate(User user) {
        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("userName", user.getUserName());
        userMap.put("userMobileNumber", user.getUserMobileNumber());
        userMap.put("userId", user.getUserId());
        userMap.put("userEmail", user.getUserEmail());
        userMap.put("userGender", user.getUserGender());
        userMap.put("avatarImage", user.getAvatarImage());

        // No need to set profileImage for backward compatibility anymore

        userRef = FirebaseDatabase.getInstance().getReference("User");
        userQuery = userRef.orderByChild("userId").equalTo(user.getUserId());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                DatabaseReference updateRef = userSnapshot.getRef();
                updateRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            StyleableToast.makeText(getApplicationContext(), "Profile Updated Successfully", R.style.UptrendToast).show();
                            finish();
                        } else {
                            StyleableToast.makeText(getApplicationContext(), "Update Failed", R.style.UptrendToast).show();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserProfile", "Error updating profile: " + error.getMessage());
                StyleableToast.makeText(getApplicationContext(), "Update Failed", R.style.UptrendToast).show();
            }
        });
    }
    public void displayUserDetails() {
        userRef = FirebaseDatabase.getInstance().getReference("User");
        userQuery = userRef.orderByChild("userId").equalTo(firebaseUser.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    user = userSnapshot.getValue(User.class);
                    txtUserName.setText(user.getUserName());
                    txtEmail.setText(user.getUserEmail());
                    txtMobileNumber.setText(user.getUserMobileNumber());

                    // Set selected gender
                    if (user.getUserGender() != null) {
                        selectedGender = user.getUserGender();
                        if (user.getUserGender().equals("Male")) {
                            radioButtonMale.setChecked(true);
                        } else if (user.getUserGender().equals("Female")) {
                            radioButtonFemale.setChecked(true);
                        }
                    }

                    // Set avatar background image
                    if (user.getAvatarImage() != null && !user.getAvatarImage().isEmpty()) {
                        selectedAvatar = user.getAvatarImage();
                        setAvatarBackground(selectedAvatar);
                    } else {
                        // Default to vector_profile
                        selectedAvatar = "vector_profile";
                        selectImageView.setBackgroundResource(R.drawable.vector_profile);
                    }
                } else {
                    // No user data found, use default
                    selectedAvatar = "vector_profile";
                    selectImageView.setBackgroundResource(R.drawable.vector_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserProfile", "Error loading user details: " + error.getMessage());
                // Use default on error
                selectedAvatar = "vector_profile";
                selectImageView.setBackgroundResource(R.drawable.vector_profile);
            }
        });
    }
    private void setAvatarBackground(String avatarName) {
        int resourceId = getResourceIdFromName(avatarName);
        if (resourceId != 0) {
            selectImageView.setBackgroundResource(resourceId);
        } else {
            // Fallback to default
            selectImageView.setBackgroundResource(R.drawable.vector_profile);
        }
    }

    private int getResourceIdFromName(String avatarName) {
        switch (avatarName) {
            case "vector_man1":
                return R.drawable.vector_man1;
            case "vector_man2":
                return R.drawable.vector_man2;
            case "vector_man3":
                return R.drawable.vector_man3;
            case "vector_man4":
                return R.drawable.vector_man4;
            case "vector_man5":
                return R.drawable.vector_man5;
            case "vector_women1":
                return R.drawable.vector_women1;
            case "vector_women2":
                return R.drawable.vector_women2;
            case "vector_women3":
                return R.drawable.vector_women3;
            case "vector_women4":
                return R.drawable.vector_women4;
            case "vector_women5":
                return R.drawable.vector_women5;
            case "vector_profile":
                return R.drawable.vector_profile;
            default:
                // Default to vector_profile for any unknown avatar name
                return R.drawable.vector_profile;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Hide avatar selection when returning to activity
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), account_user.class);
        startActivity(intent);
        finish();
    }
}