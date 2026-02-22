package com.example.uptrend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import DataModel.Order;
import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.res.ResourcesCompat;

public class return_order extends AppCompatActivity {


   private AppCompatButton continue_btn;
    private ShapeableImageView productImage;
    private TextView txtProductName, txtPrice, txtQty,txtProductColour,productSize;
    private RadioGroup radioGroupReason;
    private String orderId;
    private DatabaseReference orderRef, productRef;
    EditText txtComment;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_order);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.greenmeee));
        }




        continue_btn = findViewById(R.id.continue_btn_next);
        productImage=findViewById(R.id.historyProductImageReturn);
        txtProductName=findViewById(R.id.productNameReturn);
        txtQty=findViewById(R.id.txtQty);
        txtProductColour=findViewById(R.id.productColour);
        productSize=findViewById(R.id.productSize);
        radioGroupReason=findViewById(R.id.reasonRadioGroupReturn);
        txtComment=findViewById(R.id.txtComment);
        txtPrice=findViewById(R.id.productPriceReturn);
        TextView closeBtnReturn = findViewById(R.id.close_btn_Return);


        orderId = getIntent().getStringExtra("orderId");

        closeBtnReturn.setOnClickListener(v -> handleBackPress());



        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int checkedButtonId = radioGroupReason.getCheckedRadioButtonId();
                if (checkedButtonId == -1) {
                    StyleableToast.makeText(getApplicationContext(), "Please Select Reason For Cancellation", R.style.UptrendToast).show();
                } else {
                    RadioButton radioButton=findViewById(checkedButtonId);
                    Intent i=new Intent(getApplicationContext(),return_order2.class);
                    i.putExtra("orderId",orderId);
                    i.putExtra("reason",radioButton.getText().toString());
                    i.putExtra("comment",txtComment.getText().toString());

                   startActivity(i);
                }
                //startActivity(new Intent(getApplicationContext(), return_order2.class));
            }
        });

        displayProductDetails(orderId);
    }
    public void displayProductDetails(String orderId) {
        orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderId);
        orderRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Order order = snapshot.getValue(Order.class);
                            if (order != null) {
                                txtQty.setText(order.getProductQty());
                                txtPrice.setText(
                                        String.valueOf(
                                                Integer.parseInt(order.getProductQty()) *
                                                        Integer.parseInt(order.getProductSellingPrice())
                                        )
                                );

                                productRef = FirebaseDatabase.getInstance().getReference("Product").child(order.getProductId());
                                productRef.addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.exists()) {
                                                    Product product = snapshot.getValue(Product.class);
                                                    if (product != null) {
                                                        txtProductName.setText(product.getProductName());
                                                        Glide.with(getApplicationContext()).load(product.getProductImages().get(0)).into(productImage);
                                                        txtProductColour.setText(product.getProductColour());
                                                        if(getProductSize(product.getProductCategory(), order.getProductSize()).equals("no")){
                                                            productSize.setText("");
                                                        }else{
                                                            productSize.setText(getProductSize(product.getProductCategory(), order.getProductSize())+" , ");
                                                        }
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        }
                                );
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }
        );
    }

    public String getProductSize(String category, String index) {
        String size = "";
        if (category.equals("Men's(Top)") || category.equals("Women's(Top)")) {
            if (index.equals("0")) size = "S";
            else if (index.equals("1")) size = "M";
            else if (index.equals("2")) size = "L";
            else if (index.equals("3")) size = "XL";
            else if (index.equals("4")) size = "XXL";
        } else if (category.equals("Men's(Bottom)") || category.equals("Women's(Bottom)")) {
            if (index.equals("0")) size = "28";
            else if (index.equals("1")) size = "30";
            else if (index.equals("2")) size = "32";
            else if (index.equals("3")) size = "34";
            else if (index.equals("4")) size = "36";
            else if (index.equals("5")) size = "38";
            else if (index.equals("6")) size = "40";

        } else if (category.equals("Footware(Men)") || category.equals("Footware(Women)")) {
            if (index.equals("0")) size = "6";
            else if (index.equals("1")) size = "7";
            else if (index.equals("2")) size = "8";
            else if (index.equals("3")) size = "9";
            else if (index.equals("3")) size = "10";
        } else {
            size = "no";
        }
        return size;
    }

    private void handleBackPress() {
        LinearLayout dialogLayout = new LinearLayout(this);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(this);
        title.setText("Cancel Return Process");
        title.setTypeface(ResourcesCompat.getFont(this, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(getResources().getColor(android.R.color.black));

        TextView message = new TextView(this);
        message.setText("Are you sure you want to cancel the return process?");
        message.setTypeface(ResourcesCompat.getFont(this, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogLayout)
                .setPositiveButton("OK", (d, which) -> {
                    Intent intent = new Intent(return_order.this, open_history_pd.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("status", "order");
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
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);
            Typeface customFont = ResourcesCompat.getFont(this, R.font.caudex);
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(customFont, Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(customFont, Typeface.BOLD);
        }
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        handleBackPress();
    }
}