package com.example.uptrend.Adapter;

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
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import DataModel.Product;
import DataModel.Return;
import io.github.muddz.styleabletoast.StyleableToast;

public class ReturnProductAdapter extends RecyclerView.Adapter<ReturnProductAdapter.ReturnProductHolder> {
    private Context context;
    private ArrayList<Return> returnArrayList;
    private ReturnOnClick returnOnClick;

    public ReturnProductAdapter(Context context, ArrayList<Return> returnArrayList, ReturnOnClick returnOnClick) {
        this.context = context.getApplicationContext();
        this.returnArrayList = returnArrayList;
        this.returnOnClick = returnOnClick;
    }

    @NonNull
    @Override
    public ReturnProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_product, parent, false);
        return new ReturnProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReturnProductHolder holder, int position) {
        Return returnProduct = returnArrayList.get(position);
        if (returnProduct == null || returnProduct.getProductId() == null) {
            StyleableToast.makeText(context, "Invalid return data", R.style.UptrendToast).show();
            return;
        }

        updateTrackingStatus(returnProduct.getNodeId(), returnProduct.getReturnDate(), () -> {
            DatabaseReference returnRef = FirebaseDatabase.getInstance().getReference("Return").child(returnProduct.getNodeId());
            returnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Return updatedReturn = snapshot.getValue(Return.class);
                        if (updatedReturn != null) {
                            String statusMessage;
                            if (updatedReturn.getReturnStatus().equals("refund")) {
                                statusMessage = "Refunded on " + DateAndTime.convertDateFormatOrder(updatedReturn.getRefundDate());
                            } else if (updatedReturn.getReturnStatus().equals("pickup")) {
                                statusMessage = "Picked up on " + DateAndTime.convertDateFormatOrder(updatedReturn.getPickupDate());
                            } else {
                                statusMessage = "Return on " + DateAndTime.convertDateFormatOrder(updatedReturn.getReturnDate());
                            }
                            holder.txtStatus.setText(statusMessage);

                            // Hide the separate date TextView
                            holder.txtReturnDate.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ReturnProductAdapter", "Error fetching updated return: " + error.getMessage());
                }
            });
        });

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(returnProduct.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
                            Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
                        } else {
                            holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                        }
                        holder.txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "N/A");
                        holder.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
                    } else {
                        StyleableToast.makeText(context, "Product data is null", R.style.UptrendToast).show();
                        holder.txtBrandName.setText("N/A");
                        holder.txtProductName.setText("N/A");
                        holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                    }
                } else {
                    StyleableToast.makeText(context, "Product not found", R.style.UptrendToast).show();
                    holder.txtBrandName.setText("N/A");
                    holder.txtProductName.setText("N/A");
                    holder.productImage.setImageResource(R.drawable.ic_launcher_background);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                StyleableToast.makeText(context, "Error fetching product: " + error.getMessage(), R.style.UptrendToast).show();
                holder.txtBrandName.setText("N/A");
                holder.txtProductName.setText("N/A");
                holder.productImage.setImageResource(R.drawable.ic_launcher_background);
            }
        });

        holder.cardView.setOnClickListener(view -> returnOnClick.ReturnOnClickItem(returnProduct.getNodeId()));
    }

    @Override
    public int getItemCount() {
        return returnArrayList.size();
    }

    public class ReturnProductHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtBrandName, txtProductName, txtReturnDate, txtStatus;
        CardView cardView;

        public ReturnProductHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.historyProductImage);
            txtBrandName = itemView.findViewById(R.id.orderBrandName);
            txtProductName = itemView.findViewById(R.id.productNAme);
            txtReturnDate = itemView.findViewById(R.id.orderDate);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            cardView = itemView.findViewById(R.id.cardViewHistoryContainerHI);

            txtReturnDate.setVisibility(View.GONE);

        }
    }

    public interface OnTrackingUpdateListener {
        void onTrackingUpdated();
    }

    public void updateTrackingStatus(String nodeId, String returnDate, OnTrackingUpdateListener listener) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate parsedReturnDate = LocalDate.parse(returnDate, formatter);
                LocalDate pickupDate = parsedReturnDate.plusDays(2);
                LocalDate refundDate = parsedReturnDate.plusDays(4);
                String pickupDay = pickupDate.format(formatter);
                String refundDay = refundDate.format(formatter);
                LocalDate currentDate = LocalDate.now(); // Current date: 23-07-2025
                String newStatus;
                if (currentDate.isBefore(pickupDate)) {
                    newStatus = "return";
                } else if (currentDate.isBefore(refundDate)) {
                    newStatus = "pickup";
                } else {
                    newStatus = "refund";
                }
                DatabaseReference returnRef = FirebaseDatabase.getInstance().getReference("Return").child(nodeId);
                returnRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Return returnProduct = snapshot.getValue(Return.class);
                            if (returnProduct == null) {
                                Log.e("ReturnProductAdapter", "Return data is null for nodeID: " + nodeId);
                                listener.onTrackingUpdated();
                                return;
                            }
                            String currentStatus = snapshot.child("returnStatus").getValue(String.class);
                            String currentPickupDate = snapshot.child("pickupDate").getValue(String.class);
                            String currentRefundDate = snapshot.child("refundDate").getValue(String.class);
                            Log.d("ReturnProductAdapter", "NodeID: " + nodeId + ", Current returnStatus: " + currentStatus + ", pickupDate: " + currentPickupDate + ", refundDate: " + currentRefundDate);
                            // Update pickupDate and refundDate if not set or incorrect
                            if (currentPickupDate == null || currentPickupDate.isEmpty() || !currentPickupDate.equals(pickupDay)) {
                                returnRef.child("pickupDate").setValue(pickupDay, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("ReturnProductAdapter", "Failed to set pickupDate for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("ReturnProductAdapter", "Set pickupDate for nodeID: " + nodeId + " to " + pickupDay);
                                    }
                                });
                            }
                            if (currentRefundDate == null || currentRefundDate.isEmpty() || !currentRefundDate.equals(refundDay)) {
                                returnRef.child("refundDate").setValue(refundDay, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("ReturnProductAdapter", "Failed to set refundDate for nodeID: " + nodeId + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("ReturnProductAdapter", "Set refundDate for nodeID: " + nodeId + " to " + refundDay);
                                    }
                                });
                            }
                            // Update returnStatus and product quantity if necessary
                            if (currentStatus == null || !currentStatus.equals(newStatus)) {
                                returnRef.child("returnStatus").setValue(newStatus, (error, ref) -> {
                                    if (error != null) {
                                        Log.e("ReturnProductAdapter", "Failed to set returnStatus for nodeID: " + nodeId + " to " + newStatus + ", Error: " + error.getMessage());
                                    } else {
                                        Log.d("ReturnProductAdapter", "Set returnStatus for nodeID: " + nodeId + " to " + newStatus);
                                        // Update product quantity only when status is "refund"
                                        if (newStatus.equals("refund")) {
                                            DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(returnProduct.getProductId());
                                            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot productSnapshot) {
                                                    if (productSnapshot.exists()) {
                                                        Product product = productSnapshot.getValue(Product.class);
                                                        if (product == null) {
                                                            Log.e("ReturnProductAdapter", "Product is null for productId: " + returnProduct.getProductId());
                                                            listener.onTrackingUpdated();
                                                            return;
                                                        }
                                                        try {
                                                            int qty = Integer.parseInt(returnProduct.getProductQty());
                                                            if (returnProduct.getProductSize() != null && !returnProduct.getProductSize().isEmpty()) {
                                                                int size = Integer.parseInt(returnProduct.getProductSize());
                                                                if (product.getProductSizes() != null) {
                                                                    DatabaseReference qtyRef = productSnapshot.getRef().child("productSizes");
                                                                    DatabaseReference totalStockRef = productSnapshot.getRef().child("totalStock");
                                                                    int currentQty = Integer.parseInt(product.getProductSizes().get(size));
                                                                    int currentTotal = Integer.parseInt(product.getTotalStock());
                                                                    currentQty += qty;
                                                                    product.getProductSizes().set(size, String.valueOf(currentQty));
                                                                    currentTotal += qty;
                                                                    int finalCurrentQty = currentQty;
                                                                    qtyRef.setValue(product.getProductSizes(), (error, ref) -> {
                                                                        if (error != null) {
                                                                            Log.e("ReturnProductAdapter", "Failed to update productSizes for productId: " + returnProduct.getProductId() + ", Error: " + error.getMessage());
                                                                        } else {
                                                                            Log.d("ReturnProductAdapter", "Updated productSizes for productId: " + returnProduct.getProductId() + ", size: " + size + ", newQty: " + finalCurrentQty);
                                                                        }
                                                                    });
                                                                    int finalCurrentTotal = currentTotal;
                                                                    totalStockRef.setValue(String.valueOf(currentTotal), (error, ref) -> {
                                                                        if (error != null) {
                                                                            Log.e("ReturnProductAdapter", "Failed to update totalStock for productId: " + returnProduct.getProductId() + ", Error: " + error.getMessage());
                                                                        } else {
                                                                            Log.d("ReturnProductAdapter", "Updated totalStock for productId: " + returnProduct.getProductId() + ", newTotal: " + finalCurrentTotal);
                                                                        }
                                                                    });
                                                                }
                                                            } else {
                                                                DatabaseReference totalStockRef = productSnapshot.getRef().child("totalStock");
                                                                int currentTotal = Integer.parseInt(product.getTotalStock());
                                                                currentTotal += qty;
                                                                int finalCurrentTotal = currentTotal;
                                                                totalStockRef.setValue(String.valueOf(currentTotal), (error, ref) -> {
                                                                    if (error != null) {
                                                                        Log.e("ReturnProductAdapter", "Failed to update totalStock for productId: " + returnProduct.getProductId() + ", Error: " + error.getMessage());
                                                                    } else {
                                                                        Log.d("ReturnProductAdapter", "Updated totalStock for productId: " + returnProduct.getProductId() + ", newTotal: " + finalCurrentTotal);
                                                                    }
                                                                });
                                                            }
                                                        } catch (NumberFormatException e) {
                                                            Log.e("ReturnProductAdapter", "Error processing product quantity/size for productId: " + returnProduct.getProductId() + ", Error: " + e.getMessage());
                                                        }
                                                    } else {
                                                        Log.e("ReturnProductAdapter", "Product not found for productId: " + returnProduct.getProductId());
                                                    }
                                                    listener.onTrackingUpdated();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e("ReturnProductAdapter", "Product fetch error for productId: " + returnProduct.getProductId() + ", Error: " + error.getMessage());
                                                    listener.onTrackingUpdated();
                                                }
                                            });
                                        } else {
                                            listener.onTrackingUpdated();
                                        }
                                    }
                                });
                            } else {
                                Log.d("ReturnProductAdapter", "returnStatus not updated for nodeID: " + nodeId + ", already set to: " + currentStatus);
                                listener.onTrackingUpdated();
                            }
                        } else {
                            Log.e("ReturnProductAdapter", "Return snapshot does not exist for nodeID: " + nodeId);
                            listener.onTrackingUpdated();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        StyleableToast.makeText(context, "Error checking return status: " + error.getMessage(), R.style.UptrendToast).show();
                        Log.e("ReturnProductAdapter", "Return status check error for nodeID: " + nodeId + ", Error: " + error.getMessage());
                        listener.onTrackingUpdated();
                    }
                });
            } catch (Exception e) {
                StyleableToast.makeText(context, "Error updating return tracking: " + e.getMessage(), R.style.UptrendToast).show();
                Log.e("ReturnProductAdapter", "Return tracking update error for nodeID: " + nodeId + ", Error: " + e.getMessage());
                listener.onTrackingUpdated();
            }
        } else {
            listener.onTrackingUpdated();
        }
    }

}