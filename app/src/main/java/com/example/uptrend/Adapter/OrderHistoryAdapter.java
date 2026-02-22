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

import DataModel.Order;
import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderHistoryViewHolder> {
    private Context context;
    private ArrayList<Order> orderArrayList;
    private OrderOnClick onclick;

    public OrderHistoryAdapter(Context context, ArrayList<Order> orderArrayList, OrderOnClick onclick) {
        // Use application context to prevent Glide crashes
        this.context = context.getApplicationContext();
        this.orderArrayList = orderArrayList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_product, parent, false);
        return new OrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, int position) {
        Order order = orderArrayList.get(position);
        if (order == null || order.getProductId() == null) {
            StyleableToast.makeText(context, "Invalid order data", R.style.UptrendToast).show();
            return;
        }

        // Update tracking date and refresh UI
        updateTrackingDate(order.getOrderDate(), order.getNodeId(), () -> {
            DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(order.getNodeId());
            orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Order updatedOrder = snapshot.getValue(Order.class);
                        if (updatedOrder != null) {
                            holder.txtOrderDate.setText(DateAndTime.convertDateFormatOrder(updatedOrder.getOrderDate()));
                            String statusMessage;
                            if (updatedOrder.getOrderStatus().equals("delivered")) {
                                statusMessage = "Successfully Delivered on " + DateAndTime.convertDateFormatOrder(updatedOrder.getDelliveryDate());
                            } else if (updatedOrder.getOrderStatus().equals("shiping")) {
                                statusMessage = "In Transit, Expected by " + DateAndTime.convertDateFormatOrder(updatedOrder.getDelliveryDate());
                            } else {
                                statusMessage = "Order Placed on " + DateAndTime.convertDateFormatOrder(updatedOrder.getOrderDate());
                            }
                            holder.txtDeliveryStatus.setText(statusMessage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("OrderHistoryAdapter", "Error fetching updated order: " + error.getMessage());
                }
            });
        });

        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(order.getProductId());
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

        holder.cardView.setOnClickListener(view -> onclick.onClickItem(order.getNodeId()));
    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtBrandName, txtProductName, txtOrderDate, txtDeliveryStatus;
        CardView cardView;

        public OrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.historyProductImage);
            txtBrandName = itemView.findViewById(R.id.orderBrandName);
            txtProductName = itemView.findViewById(R.id.productNAme);
            txtOrderDate = itemView.findViewById(R.id.orderDate);
            cardView = itemView.findViewById(R.id.cardViewHistoryContainerHI);
            txtDeliveryStatus = itemView.findViewById(R.id.txtDeliveryStatus);
        }
    }

    public interface OnTrackingUpdateListener {
        void onTrackingUpdated();
    }

    // Replace the updateTrackingDate method in OrderHistoryAdapter.java with this:
    public void updateTrackingDate(String orderDate, String orderID, OnTrackingUpdateListener listener) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate parsedOrderDate = LocalDate.parse(orderDate, formatter);
                LocalDate shippingDate = parsedOrderDate.plusDays(2);
                LocalDate deliveryDate = parsedOrderDate.plusDays(4);
                String shippingDay = shippingDate.format(formatter);
                String deliveryDay = deliveryDate.format(formatter);

                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderID);
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String currentStatus = snapshot.child("orderStatus").getValue(String.class);

                            // Always update shipping and delivery dates if not set
                            orderRef.child("shipingDate").setValue(shippingDay);
                            orderRef.child("delliveryDate").setValue(deliveryDay);

                            // Don't auto-change status if already delivered, cancelled, or returned
                            if (!"delivered".equals(currentStatus) &&
                                    !"cancelled".equals(currentStatus) &&
                                    !"canceled".equals(currentStatus) &&
                                    !"returned".equals(currentStatus)) {

                                LocalDate currentDate = LocalDate.now();
                                String newStatus;
                                if (currentDate.isBefore(shippingDate)) {
                                    newStatus = "new";
                                } else if (currentDate.isBefore(deliveryDate)) {
                                    newStatus = "shiping";
                                } else {
                                    newStatus = "delivered";
                                }

                                if (!newStatus.equals(currentStatus)) {
                                    orderRef.child("orderStatus").setValue(newStatus);
                                }
                            }

                            listener.onTrackingUpdated();
                        } else {
                            listener.onTrackingUpdated();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        listener.onTrackingUpdated();
                    }
                });
            } catch (Exception e) {
                listener.onTrackingUpdated();
            }
        } else {
            listener.onTrackingUpdated();
        }
    }

}