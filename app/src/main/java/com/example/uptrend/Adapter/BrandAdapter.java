package com.example.uptrend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import DataModel.Product;

public class BrandAdapter extends RecyclerView.Adapter<BrandAdapter.BrandViewHolder>{
    private Context context;
    private ArrayList<Product> productArrayList;
    private Onclick onclick;

    public BrandAdapter(Context context, ArrayList<Product> productArrayList, Onclick onclick) {
        this.context = context;
        this.productArrayList = productArrayList;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public BrandViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.brand_product,parent,false);
        return new BrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BrandViewHolder holder, int position) {
        Product product=productArrayList.get(position);
        Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
        holder.txtProductName.setText(product.getProductName());
        holder.txtSellingPrice.setText(product.getSellingPrice());
        holder.txtOriginalPrice.setText(product.getOriginalPrice());
        holder.txtBrandName.setText(product.getProductBrandName());
        Double discount = calculateDiscountPercentage(Double.parseDouble(product.getOriginalPrice()),
                Double.parseDouble(product.getSellingPrice()));
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedDiscountPercentage = df.format(discount);
        holder.txtDiscount.setText(formattedDiscountPercentage);
        holder.productCardView.setOnClickListener(new View.OnClickListener() {
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

    public class BrandViewHolder extends RecyclerView.ViewHolder{
            ShapeableImageView productImage;
            TextView txtBrandName,txtProductName,txtOriginalPrice,txtSellingPrice,txtDiscount;
            CardView productCardView;
        public BrandViewHolder(@NonNull View itemView) {
            super(itemView);
            productCardView=itemView.findViewById(R.id.productCardView);
            productImage=itemView.findViewById(R.id.productImageCategory);
            txtBrandName=itemView.findViewById(R.id.txtBrandNameBrand);
            txtProductName=itemView.findViewById(R.id.txtProductNameBrand);
            txtSellingPrice=itemView.findViewById(R.id.txt_product_selling_price_Brand);
            txtOriginalPrice=itemView.findViewById(R.id.text_product_original_price_Brand);
            txtDiscount=itemView.findViewById(R.id.txtDiscountBrand);
        }
    }
    public double calculateDiscountPercentage(double originalPrice, double sellingPrice) {
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }
}
