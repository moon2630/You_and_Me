package com.example.uptrend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

import java.util.ArrayList;
import DataModel.Cart;
import DataModel.Product;

public class CartAdapterPayment extends RecyclerView.Adapter<CartAdapterPayment.ViewHolder> {

    private Context context;
    private ArrayList<Cart> cartItems;
    private ArrayList<Product> productList;

    public CartAdapterPayment(Context context, ArrayList<Cart> cartItems, ArrayList<Product> productList) {
        this.context = context;
        this.cartItems = cartItems;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cart cartItem = cartItems.get(position);
        Product product = productList.get(position);

        // Set product name and brand name with null checks
        holder.txtProductName.setText(product.getProductName() != null ? product.getProductName() : "N/A");
        holder.txtBrandName.setText(product.getProductBrandName() != null ? product.getProductBrandName() : "N/A");

        // Map size index to user-friendly size based on product category
        String sizeText = "N/A";
        if (cartItem.getProductSize() != null && !cartItem.getProductSize().equals("0") && product.getProductSizes() != null && !product.getProductSizes().isEmpty()) {
            try {
                int sizeIndex = Integer.parseInt(cartItem.getProductSize());
                if (product.getProductCategory() != null) {
                    if (product.getProductCategory().equals("Men's(Top)") || product.getProductCategory().equals("Women's(Top)")) {
                        String[] shirtSizes = {"S", "M", "L", "XL", "XXL"};
                        if (sizeIndex >= 0 && sizeIndex < shirtSizes.length) {
                            sizeText = shirtSizes[sizeIndex];
                        }
                    } else if (product.getProductCategory().equals("Men's(Bottom)") || product.getProductCategory().equals("Women's(Bottom)")) {
                        String[] jeansSizes = {"28", "30", "32", "34", "36","38","40"};
                        if (sizeIndex >= 0 && sizeIndex < jeansSizes.length) {
                            sizeText = jeansSizes[sizeIndex];
                        }
                    } else if (product.getProductCategory().equals("Footware(Men)") || product.getProductCategory().equals("Footware(Women)")) {
                        String[] shoeSizes = {"6", "7", "8", "9", "10"};
                        if (sizeIndex >= 0 && sizeIndex < shoeSizes.length) {
                            sizeText = shoeSizes[sizeIndex];
                        }
                    }
                }
            } catch (NumberFormatException e) {
                sizeText = "N/A"; // Fallback in case of invalid size index
            }
        }
        holder.txtSize.setText("Size: " + sizeText);

        // Set quantity with null check
        holder.txtQuantity.setText("Quantity: " + (cartItem.getQty() != null ? cartItem.getQty() : "1"));

        // Set prices with null checks
        holder.txtDiscountPrice.setText("₹" + (product.getSellingPrice() != null ? product.getSellingPrice() : "0"));
        holder.txtOriginalPrice.setText("₹" + (product.getOriginalPrice() != null ? product.getOriginalPrice() : product.getSellingPrice() != null ? product.getSellingPrice() : "0"));

        // Calculate savings if applicable
        try {
            int sellingPrice = Integer.parseInt(product.getSellingPrice());
            int originalPrice = Integer.parseInt(product.getOriginalPrice());
            if (originalPrice > sellingPrice) {
                int savings = originalPrice - sellingPrice;
                holder.txtSavings.setText("Saving ₹" + savings);
            } else {
                holder.txtSavings.setText("");
            }
        } catch (NumberFormatException e) {
            holder.txtSavings.setText("");
        }

        // Load product image with null check
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            Glide.with(context).load(product.getProductImages().get(0)).into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.ic_launcher_background); // Fallback image
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView imgProduct;
        TextView txtProductName, txtBrandName, txtSize, txtQuantity, txtDiscountPrice, txtOriginalPrice, txtSavings;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtBrandName = itemView.findViewById(R.id.txt_brand_name);
            txtSize = itemView.findViewById(R.id.txt_size);
            txtQuantity = itemView.findViewById(R.id.txt_quantity);
            txtDiscountPrice = itemView.findViewById(R.id.txt_discount_price);
            txtOriginalPrice = itemView.findViewById(R.id.txt_original_price);
            txtSavings = itemView.findViewById(R.id.txt_savings);
        }
    }
}