package com.example.uptrendseller.Adapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrendseller.R;
import com.example.uptrendseller.edit_product;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import DataModel.Product;
import de.hdodenhof.circleimageview.CircleImageView;

public class InventoryProductAdapter extends RecyclerView.Adapter<InventoryProductAdapter.ProductViewHolder>{
    private Context context;
    private ArrayList<Product> productArrayList;
    private Onclick onclick;

    public InventoryProductAdapter(Context context, ArrayList<Product> productArrayList, Onclick onclick) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.raw_add_product,parent,false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productArrayList.get(position);

        // Log for debugging
        Log.d("InventoryAdapter", "Product: " + product.getProductName() +
                ", Category: " + product.getProductCategory() +
                ", Total Stock: " + product.getTotalStock());

        // SAFE IMAGE LOAD
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
        } else {
            holder.productImage.setImageResource(R.drawable.ic_launcher_background);
        }


        if (product.getProductCreatedDate() != null && !product.getProductCreatedDate().isEmpty()) {
            holder.productDate.setText("Added on " + product.getProductCreatedDate());
        } else {
            holder.productDate.setText("Old product");
        }

        holder.productName.setText(product.getProductName());
        holder.productPrice.setText(product.getSellingPrice());
        holder.productStock.setText(product.getTotalStock());

        holder.lowSizeTxt.setVisibility(View.GONE);
        holder.lowSizeTxt.setText("");

        // Get low stock message
        String lowMsg = getLowStockMessage(product);

        if (lowMsg != null && !lowMsg.isEmpty()) {
            try {
                // Set text first, then make visible
                holder.lowSizeTxt.setText(lowMsg);

                // Force redraw and make visible
                holder.lowSizeTxt.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.lowSizeTxt.setVisibility(View.VISIBLE);
                        holder.lowSizeTxt.invalidate();
                        holder.itemView.invalidate();
                    }
                });

                Log.d("InventoryAdapter", "Low stock text set: " + lowMsg);
            } catch (Exception e) {
                Log.e("InventoryAdapter", "Error setting low stock text: " + e.getMessage());
            }
        } else {
            holder.lowSizeTxt.setVisibility(View.GONE);
        }

        // Check for low quantity sizes
        int lowQuantityCount = 0;
        if (product.getProductSizes() != null) {
            for (String size : product.getProductSizes()) {
                try {
                    int quantity = Integer.parseInt(size);
                    if (quantity <= 3 && quantity > 0) {
                        lowQuantityCount++;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid size values
                }
            }
        }

        // Update low_size_txt visibility and text
        if (lowQuantityCount > 0) {
            holder.lowSizeTxt.setVisibility(View.VISIBLE);
            holder.lowSizeTxt.setText(lowQuantityCount + " size" + (lowQuantityCount > 1 ? "s" : "") + " has low quantity !");
        } else {
            holder.lowSizeTxt.setVisibility(View.GONE);
        }


        // deleteIng product

        holder.productDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog(product.getProductId());

            }
        });

        holder.productEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.ItemOnClickListener(product.getProductId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView productImage;
        TextView productName,productPrice,productStock,productEdit,productDelete,lowSizeTxt,productDate;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.product_image_RV);
            productName=itemView.findViewById(R.id.product_name_RV);
            productPrice=itemView.findViewById(R.id.product_price_RV);
            productStock=itemView.findViewById(R.id.product_stock_RV);
            productEdit=itemView.findViewById(R.id.product_edit_RV);
            productDelete=itemView.findViewById(R.id.product_delete_RV);
            lowSizeTxt = itemView.findViewById(R.id.low_size_txt);
            productDate = itemView.findViewById(R.id.product_date_RV); // Add this




            // Debug: check if lowSizeTxt is found
            if (lowSizeTxt == null) {
                Log.e("ProductViewHolder", "lowSizeTxt NOT FOUND in layout!");
            } else {
                Log.d("ProductViewHolder", "lowSizeTxt found successfully");
            }
        }
    }
    public void deleteDialog(String productId) {
        LinearLayout dialogLayout = new LinearLayout(context);
        dialogLayout.setOrientation(LinearLayout.VERTICAL);
        dialogLayout.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        dialogLayout.setPadding(50, 50, 50, 35);

        TextView title = new TextView(context);
        title.setText("Delete Product?");
        title.setTypeface(ResourcesCompat.getFont(context, R.font.caudex), Typeface.BOLD);
        title.setPadding(0, 0, 10, 20);
        title.setTextSize(22);
        title.setTextColor(context.getResources().getColor(android.R.color.black));

        TextView message = new TextView(context);
        message.setText("Are you sure you want to delete this item?");
        message.setTypeface(ResourcesCompat.getFont(context, R.font.caudex));
        message.setTextSize(16);
        message.setPadding(0, 10, 0, 0);
        message.setTextColor(context.getResources().getColor(android.R.color.black));

        dialogLayout.addView(title);
        dialogLayout.addView(message);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogLayout)
                .setPositiveButton("Yes", (d, which) -> deleteProduct(productId))
                .setNegativeButton("No", null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.blue));
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTypeface(ResourcesCompat.getFont(context, R.font.caudex), Typeface.BOLD);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.red));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTypeface(ResourcesCompat.getFont(context, R.font.caudex), Typeface.BOLD);
        }
    }

    private String getLowStockMessage(Product product) {
        if (product == null) {
            return null;
        }

        String totalStockStr = product.getTotalStock();
        if (totalStockStr == null || totalStockStr.trim().isEmpty()) {
            return null;
        }

        try {
            int totalStock = Integer.parseInt(totalStockStr.trim());

            // Check if it's a category with sizes (clothing, shoes, etc.)
            boolean hasSizes = product.getProductSizes() != null && !product.getProductSizes().isEmpty();

            if (hasSizes) {
                // For products with sizes, check individual sizes
                int lowQuantityCount = 0;
                for (String sizeQty : product.getProductSizes()) {
                    try {
                        int quantity = Integer.parseInt(sizeQty);
                        if (quantity <= 3 && quantity > 0) {
                            lowQuantityCount++;
                        }
                    } catch (NumberFormatException e) {
                        // Skip invalid size values
                    }
                }

                if (lowQuantityCount > 0) {
                    return lowQuantityCount + " size" + (lowQuantityCount > 1 ? "s" : "") + " has low quantity!";
                }
            } else {
                // For products without sizes (other category), check total stock
                if (totalStock <= 3 && totalStock > 0) {
                    return "Low quantity! Only " + totalStock + " left!";
                }
            }

            // Additional check: if total stock itself is low (even for sized products)
            if (totalStock <= 3 && totalStock > 0) {
                return "Low overall stock! Only " + totalStock + " items left!";
            }

        } catch (NumberFormatException e) {
            return null;
        }

        return null;
    }

    public void updateList(ArrayList<Product> newList) {
        this.productArrayList = newList;
        notifyDataSetChanged();
        Log.d("AdapterDebug", "Adapter updated with " + newList.size() + " items");
    }
    private void deleteProduct(String productId){
        DatabaseReference productNode= FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productNode.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                notifyDataSetChanged();
                Toast.makeText(context, "Product Deleted", Toast.LENGTH_SHORT).show();
            }
        });
    }
}




