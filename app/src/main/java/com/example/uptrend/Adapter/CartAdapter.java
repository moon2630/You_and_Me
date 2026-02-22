package com.example.uptrend.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Cart;
import DataModel.Product;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private ArrayList<Cart> cartArrayList;
    private Onclick onclick;

    public CartAdapter(Context context, ArrayList<Cart> cartArrayList, Onclick onclick) {
        this.context = context;
        this.cartArrayList = cartArrayList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.add_to_cart_container, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = cartArrayList.get(position);
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference("Product").child(cart.getProductId());
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Product product = snapshot.getValue(Product.class);
                    Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
                    holder.txtBrandName.setText(product.getProductBrandName());
                    holder.txtProductName.setText(product.getProductName());
                    holder.txtSellingPrice.setText(product.getSellingPrice());
                    holder.txtOriginalPrice.setText(product.getOriginalPrice());
                    Long discountAmount = (long) (Double.parseDouble(product.getOriginalPrice())
                            - Double.parseDouble(product.getSellingPrice()));
                    holder.txtDiscountPrice.setText(String.valueOf(discountAmount));
                    holder.txtQty.setText(cart.getQty());

                    // Check stock and update visibility
                    boolean isOutOfStock = false;
                    if (product.getProductSizes() != null && cart.getProductSize() != null) {
                        int sizeIndex = Integer.parseInt(cart.getProductSize());
                        if (sizeIndex >= 0 && sizeIndex < product.getProductSizes().size()) {
                            isOutOfStock = Integer.parseInt(product.getProductSizes().get(sizeIndex)) == 0;
                        }
                    } else {
                        isOutOfStock = product.getTotalStock() != null && Integer.parseInt(product.getTotalStock()) == 0;
                    }
                    holder.txtProductNotAvailable.setVisibility(isOutOfStock ? View.VISIBLE : View.GONE);
                    holder.layoutQty.setVisibility(isOutOfStock ? View.GONE : View.VISIBLE);

                    // Display size based on product category and selected size
                    if (!isOutOfStock && product.getProductSizes() != null && cart.getProductSize() != null) {
                        int sizeIndex = Integer.parseInt(cart.getProductSize());
                        String sizeText = "N/A";
                        if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
                            String[] shirtSizes = {"S", "M", "L", "XL", "XXL"};
                            if (sizeIndex >= 0 && sizeIndex < shirtSizes.length) {
                                sizeText = shirtSizes[sizeIndex];
                            }
                        } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
                            String[] jeansSizes = {"28", "30", "32", "34", "36", "38", "40"};
                            if (sizeIndex >= 0 && sizeIndex < jeansSizes.length) {
                                sizeText = jeansSizes[sizeIndex];
                            }
                        } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
                            String[] shoeSizes = {"6", "7", "8", "9", "10"};
                            if (sizeIndex >= 0 && sizeIndex < shoeSizes.length) {
                                sizeText = shoeSizes[sizeIndex];
                            }
                        }
                        holder.txtProductSize.setText(sizeText);
                    } else {
                        holder.txtProductSize.setText("");
                    }

                    // Disable quantity controls for out-of-stock items
                    if (!isOutOfStock) {
                        DatabaseReference qtyRef = FirebaseDatabase.getInstance().getReference("Cart").child(cart.getCartId()).child("qty");
                        holder.txtPlus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int qty = Integer.parseInt(holder.txtQty.getText().toString());
                                if (product != null) {
                                    if (product.getProductSizes() != null && product.getProductSizes().size() != 0) {
                                        int productSizeIndex = Integer.parseInt(cart.getProductSize());
                                        if (productSizeIndex >= 0 && productSizeIndex < product.getProductSizes().size()) {
                                            int maxQuantity = Integer.parseInt(product.getProductSizes().get(productSizeIndex));
                                            if (qty < maxQuantity) {
                                                qty++;
                                                holder.txtQty.setText(String.valueOf(qty));
                                                qtyRef.setValue(String.valueOf(qty));
                                            }
                                        }
                                    } else {
                                        if (product.getTotalStock() != null) {
                                            int totalStock = Integer.parseInt(product.getTotalStock());
                                            if (qty < totalStock) {
                                                qty++;
                                                holder.txtQty.setText(String.valueOf(qty));
                                                qtyRef.setValue(String.valueOf(qty));
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        holder.txtMinus.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int qty = Integer.parseInt(holder.txtQty.getText().toString());
                                if (qty > 1) {
                                    qty--;
                                    holder.txtQty.setText(String.valueOf(qty));
                                    qtyRef.setValue(String.valueOf(qty));
                                }
                            }
                        });
                    } else {
                        holder.txtPlus.setOnClickListener(null);
                        holder.txtMinus.setOnClickListener(null);
                    }

                    holder.txtRemove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference qtyRef = FirebaseDatabase.getInstance().getReference("Cart").child(cart.getCartId());
                            qtyRef.removeValue();
                            notifyDataSetChanged();
                        }
                    });
                    holder.layoutContainer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            onclick.ItemOnClickListener(product.getProductId());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartArrayList.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView productImage;
        TextView txtBrandName, txtProductName, txtOriginalPrice, txtSellingPrice, txtDiscountPrice, txtQty,size_TXT,saved_TXT;
        TextView txtPlus, txtMinus, txtRemove, txtProductSize, txtProductNotAvailable;
        CardView layoutContainer1;
        LinearLayout layoutQty;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImageA2C);
            txtBrandName = itemView.findViewById(R.id.txtBrandNameA2C);
            txtProductName = itemView.findViewById(R.id.txtProductNameA2C);
            txtSellingPrice = itemView.findViewById(R.id.txt_product_selling_price_A2C);
            txtOriginalPrice = itemView.findViewById(R.id.text_product_original_price_A2C);
            txtDiscountPrice = itemView.findViewById(R.id.discount_A2C);
            txtQty = itemView.findViewById(R.id.txtQty);
            txtPlus = itemView.findViewById(R.id.txtPlus);
            txtMinus = itemView.findViewById(R.id.txtMinus);
            txtRemove = itemView.findViewById(R.id.delete_A2C);
            txtProductSize = itemView.findViewById(R.id.txt_product_size);
            txtProductNotAvailable = itemView.findViewById(R.id.txt_product_not_available);
            layoutQty = itemView.findViewById(R.id.layout_qty);
            layoutContainer1 = itemView.findViewById(R.id.layoutContainer1);
            saved_TXT = itemView.findViewById(R.id.saved_TXT);

            // Set Caudex font programmatically for all TextViews
            Typeface caudexFont = ResourcesCompat.getFont(context, R.font.caudex);
            Typeface brygadaFont = ResourcesCompat.getFont(context, R.font.brygada_1918);
            Typeface dmSerifFont = ResourcesCompat.getFont(context, R.font.dm_serif_display);
            txtBrandName.setTypeface(dmSerifFont);
            txtProductName.setTypeface(brygadaFont);
            txtSellingPrice.setTypeface(dmSerifFont);
            txtOriginalPrice.setTypeface(brygadaFont);
            txtDiscountPrice.setTypeface(dmSerifFont);
            txtQty.setTypeface(caudexFont);
            txtPlus.setTypeface(caudexFont);
            txtMinus.setTypeface(caudexFont);
            txtRemove.setTypeface(caudexFont);
            txtProductSize.setTypeface(caudexFont);
            txtProductNotAvailable.setTypeface(caudexFont);
            saved_TXT.setTypeface(brygadaFont);
        }
    }}