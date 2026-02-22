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

import DataModel.Order;
import DataModel.Product;
import DataModel.UserAddress;

public class OrderRequestAdapter extends RecyclerView.Adapter<OrderRequestAdapter.OrderRequestViewHolder> {
    private Context context;
    private ArrayList<Order> orderArrayList;
    private RequestOnClick requestOnClick;

    public OrderRequestAdapter(Context context, ArrayList<Order> orderArrayList, RequestOnClick requestOnClick) {
        this.context = context;
        this.orderArrayList = orderArrayList;
        this.requestOnClick = requestOnClick;
    }

    @NonNull
    @Override
    public OrderRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_product_rv, parent, false);
        return new OrderRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderRequestViewHolder holder, int position) {
        Order order = orderArrayList.get(position);

        DatabaseReference userAddressRef = FirebaseDatabase.getInstance().getReference("UserAddress");
        Query userQuery = userAddressRef.orderByChild("userId").equalTo(order.getUserId());

        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    UserAddress userAddress = userSnapshot.getValue(UserAddress.class);
                    holder.txtUserName.setText("order by " + userAddress.getFullName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(order.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    holder.txtProductName.setText(product.getProductName());
                    Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);


                    int qty = Integer.parseInt(order.getProductQty());
                    int price = Integer.parseInt(order.getProductSellingPrice());
                    int totalAmount = qty * price;

                    holder.txtProductPrice.setText("Successfully payment ₹" + totalAmount);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.txtQuntity.setText("Qty : " + order.getProductQty());
        holder.orderDate.setText(order.getOrderDate());
        if (order.getOrderStatus().equals("new")) {
            holder.productStatus.setText("Status: Processing");

        } else if (order.getOrderStatus().equals("shiping")) {
            holder.productStatus.setText("Status: Shipping");

        } else if (order.getOrderStatus().equals("delivered")) {
            holder.deliveredIcon.setVisibility(View.VISIBLE);
            holder.productStatus.setText("Status: Delivered");

        }
        holder.cardViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestOnClick.RequestOnClickListener(
                        order.getNodeId(),
                        order.getProductId(),
                        order.getUserId(),
                        order.getProductQty(),
                        order.getProductSize()
                );
            }
        });

        updateTrackingDate(order.getOrderDate(),order.getNodeId(),order.getShipingDate(),order.getDelliveryDate(),order.getNodeId());


    }

    @Override
    public int getItemCount() {
        return orderArrayList.size();
    }

    public class OrderRequestViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtProductName, txtProductPrice, txtUserName, txtQuntity, productStatus, orderDate, deliveredIcon;
        CardView cardViewContainer;

        public OrderRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image_RV);
            txtProductName = itemView.findViewById(R.id.product_name_RV);
            txtProductPrice = itemView.findViewById(R.id.productTotalPrice);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtQuntity = itemView.findViewById(R.id.product_qty);
            cardViewContainer = itemView.findViewById(R.id.cardViewContainer);
            productStatus = itemView.findViewById(R.id.productStatus);
            orderDate = itemView.findViewById(R.id.orderDate);
            deliveredIcon = itemView.findViewById(R.id.deliveredIcon);
        }
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

    public void updateTrackingDate(String orderDate, String orderID, String date, String date2, String nodeId) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            try {
                LocalDate parsedDate = LocalDate.parse(orderDate, formatter);
                LocalDate shipingDate = parsedDate.plusDays(2);
                LocalDate deliveryDate = parsedDate.plusDays(4);
                String shipingDay = shipingDate.format(formatter);
                String deliveryDay = deliveryDate.format(formatter);

                DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference("Order").child(orderID);

                // Get current order status first
                orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String currentStatus = snapshot.child("orderStatus").getValue(String.class);

                            // Always update shipping and delivery dates
                            orderRef.child("shipingDate").setValue(shipingDay);
                            orderRef.child("delliveryDate").setValue(deliveryDay);

                            LocalDate currentDate = LocalDate.now();
                            String currentDateStr = currentDate.format(formatter);
                            String newStatus = currentStatus;

                            // Only auto-update status if it's not already delivered, cancelled, or returned
                            if (currentStatus == null ||
                                    (!"delivered".equals(currentStatus) &&
                                            !"cancelled".equals(currentStatus) &&
                                            !"canceled".equals(currentStatus) &&
                                            !"returned".equals(currentStatus))) {

                                if (currentDateStr.equals(orderDate)) {
                                    newStatus = "new";
                                } else if (currentDateStr.equals(shipingDay)) {
                                    newStatus = "shiping";
                                } else if (currentDateStr.equals(deliveryDay)) {
                                    newStatus = "delivered";
                                } else if (currentDate.isAfter(deliveryDate)) {
                                    newStatus = "delivered";
                                } else if (currentDate.isAfter(shipingDate)) {
                                    newStatus = "shiping";
                                }

                                // Update status only if it has changed
                                if (newStatus != null && !newStatus.equals(currentStatus)) {
                                    orderRef.child("orderStatus").setValue(newStatus);
                                    Log.d("OrderRequestAdapter", "Updated order " + orderID + " status from " +
                                            currentStatus + " to: " + newStatus);
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("OrderRequestAdapter", "Error getting order status: " + error.getMessage());
                    }
                });

            } catch (Exception e) {
                Log.e("OrderRequestAdapter", "Error updating tracking date: " + e.getMessage());
            }
        }
    }
}
