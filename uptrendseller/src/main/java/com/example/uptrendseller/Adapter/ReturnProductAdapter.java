package com.example.uptrendseller.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adapteranddatamodel.DateAndTime;
import com.example.uptrendseller.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import DataModel.Product;
import DataModel.Return;
import DataModel.UserAddress;

public class ReturnProductAdapter extends RecyclerView.Adapter<ReturnProductAdapter.ReturnProductViewHolder> {
    private Context context;
    private ArrayList<Return> returnArrayList;
    private RequestOnClick requestOnClick;

    public ReturnProductAdapter(Context context, ArrayList<Return> returnArrayList, RequestOnClick requestOnClick) {
        this.context = context;
        this.returnArrayList = returnArrayList;
        this.requestOnClick = requestOnClick;
    }

    @NonNull
    @Override
    public ReturnProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.cancel_and_return,parent,false);
        return new ReturnProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnProductViewHolder holder, int position) {
        Return returnProduct = returnArrayList.get(position);
        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        Query userQuery = userAddressRef.orderByChild("userId").equalTo(returnProduct.getUserId());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    UserAddress userAddress = userSnapshot.getValue(UserAddress.class);
                    holder.txtUserName.setText("Order returned by " + userAddress.getFullName());
                } else {
                    holder.txtUserName.setText("Order returned by N/A");
                    Log.e("ReturnProductAdapter", "User address not found for userId: " + returnProduct.getUserId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.txtUserName.setText("Order returned by N/A");
                Log.e("ReturnProductAdapter", "Error fetching user address for userId: " + returnProduct.getUserId() + ", Error: " + error.getMessage());
            }
        });

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(returnProduct.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        holder.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                            Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
                        } else {
                            holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                        }
                        try {
                            int qty = Integer.parseInt(returnProduct.getProductQty());
                            long price = Long.parseLong(product.getSellingPrice());
                            long totalPrice = qty * price;
                            holder.txtPrice.setText("Payment ₹" + totalPrice);
                        } catch (NumberFormatException e) {
                            holder.txtPrice.setText("Payment ₹0");
                            Log.e("ReturnProductAdapter", "Price calculation error for productId: " + returnProduct.getProductId() + ", Error: " + e.getMessage());
                        }
                    } else {
                        holder.txtProductName.setText("N/A");
                        holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                        holder.txtPrice.setText("Payment ₹0");
                        Log.e("ReturnProductAdapter", "Product is null for productId: " + returnProduct.getProductId());
                    }
                } else {
                    holder.txtProductName.setText("N/A");
                    holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                    holder.txtPrice.setText("Payment ₹0");
                    Log.e("ReturnProductAdapter", "Product not found for productId: " + returnProduct.getProductId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                holder.txtProductName.setText("N/A");
                holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                holder.txtPrice.setText("Payment ₹0");
                Log.e("ReturnProductAdapter", "Error fetching product for productId: " + returnProduct.getProductId() + ", Error: " + error.getMessage());
            }
        });

        holder.txtQty.setText("Qty : " + returnProduct.getProductQty());
        holder.txtDate.setText(DateAndTime.convertDateFormat(returnProduct.getReturnDate()));
        String paymentStatus = returnProduct.getPaymentStatus() != null ? returnProduct.getPaymentStatus() : "pending";

        // Updated status text
        if (paymentStatus.equals("completed")) {
            holder.txtStatus.setText("Status : Refunded");
            holder.itemView.findViewById(R.id.product_payment_successful).setVisibility(View.VISIBLE);
        } else {
            String statusText = "";
            String returnStatus = returnProduct.getReturnStatus();
            if (returnStatus.equals("return")) {
                statusText = "Status : Return";
            } else if (returnStatus.equals("pickup")) {
                statusText = "Status : Picked Up";
            } else {
                statusText = "Status : Refund Pending";
            }
            holder.txtStatus.setText(statusText);
            holder.itemView.findViewById(R.id.product_payment_successful).setVisibility(View.GONE);
        }

        updateTrackingStatus(returnProduct.getNodeId(), returnProduct.getReturnDate(), returnProduct.getPickupDate(), returnProduct.getRefundDate());

        holder.cardViewContainerCancel.setOnClickListener(v -> {
            requestOnClick.RequestOnClickListener(
                    returnProduct.getNodeId(),
                    returnProduct.getProductId(),
                    returnProduct.getUserId(),
                    returnProduct.getProductQty(),
                    returnProduct.getProductSize()
            );
        });
    }

    @Override
    public int getItemCount() {
        return returnArrayList.size();
    }

    public class ReturnProductViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        CardView cardViewContainerCancel;
        TextView txtProductName, txtQty, txtPrice, txtUserName, txtStatus, txtDate;

        public ReturnProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.product_image_CR);
            txtProductName = itemView.findViewById(R.id.product_name_CR);
            txtQty = itemView.findViewById(R.id.product_qty_CR);
            txtPrice = itemView.findViewById(R.id.productTotalPriceCR);
            txtUserName = itemView.findViewById(R.id.txtUserNameCR);
            txtStatus = itemView.findViewById(R.id.productStatusCR);
            txtDate = itemView.findViewById(R.id.orderDateCR);
            cardViewContainerCancel = itemView.findViewById(R.id.cardViewContainerCancel);
        }
    }

    public void updateTrackingStatus(String nodeId, String returnDate, String pickUpDate, String refundDay) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate currentDate = LocalDate.now();

                DatabaseReference returnRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId);

                // Get current return status first
                returnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Return returnProduct = snapshot.getValue(Return.class);
                            if (returnProduct != null) {
                                String currentStatus = returnProduct.getReturnStatus();

                                // Don't auto-update if already refunded
                                if ("refund".equals(currentStatus) || "completed".equals(returnProduct.getPaymentStatus())) {
                                    Log.d("ReturnProductAdapter", "Return already refunded, skipping auto-update for nodeId: " + nodeId);
                                    return;
                                }

                                // Parse dates
                                LocalDate parsedReturnDate = LocalDate.parse(returnDate, formatter);
                                LocalDate parsedPickupDate = parsedReturnDate.plusDays(2);
                                LocalDate parsedRefundDate = parsedReturnDate.plusDays(4);

                                // Always update pickup and refund dates if not set
                                if (pickUpDate == null || pickUpDate.isEmpty()) {
                                    returnRef.child("pickupDate").setValue(parsedPickupDate.format(formatter));
                                }
                                if (refundDay == null || refundDay.isEmpty()) {
                                    returnRef.child("refundDate").setValue(parsedRefundDate.format(formatter));
                                }

                                // Determine new status based on current date
                                String newStatus = currentStatus;

                                if (currentDate.isAfter(parsedRefundDate) || currentDate.isEqual(parsedRefundDate)) {
                                    newStatus = "refund";
                                } else if (currentDate.isAfter(parsedPickupDate) || currentDate.isEqual(parsedPickupDate)) {
                                    newStatus = "pickup";
                                } else if (currentDate.isAfter(parsedReturnDate) || currentDate.isEqual(parsedReturnDate)) {
                                    newStatus = "return";
                                }

                                // Update status only if changed and not already in final state
                                if (newStatus != null && !newStatus.equals(currentStatus) &&
                                        !"refund".equals(currentStatus)) {
                                    returnRef.child("returnStatus").setValue(newStatus);
                                    Log.d("ReturnProductAdapter", "Updated return " + nodeId +
                                            " status from " + currentStatus + " to: " + newStatus);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("ReturnProductAdapter", "Error getting return status: " + error.getMessage());
                    }
                });

            } catch (Exception e) {
                Log.e("ReturnProductAdapter", "Error updating tracking status: " + e.getMessage());
            }
        } else {
            // Fallback for older Android versions
            DatabaseReference returnStatusRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId).child("returnStatus");
            if (returnDate.equals(DateAndTime.getDate())) {
                returnStatusRef.setValue("return");
            } else if (pickUpDate.equals(DateAndTime.getDate())) {
                returnStatusRef.setValue("pickup");
            } else if (refundDay.equals(DateAndTime.getDate())) {
                returnStatusRef.setValue("refund");
            }
        }
    }
}