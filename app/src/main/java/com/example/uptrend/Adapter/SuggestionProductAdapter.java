package com.example.uptrend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import DataModel.Product;
import DataModel.Review;

public class SuggestionProductAdapter extends RecyclerView.Adapter<SuggestionProductAdapter.SuggestionProductViewHolder>{

    private Context context;
    int totalReview=0;
    float rating,total;
    private ArrayList<Product> products;
    private Onclick onclick;

    // Array to store the review count for each product



    public SuggestionProductAdapter(Context context, ArrayList<Product> products,Onclick onclick) {
        this.context = context;
        this.products = products;
        this.onclick=onclick;
    }

    @NonNull
    @Override
    public SuggestionProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.similiar_product_design,parent,false);
        return new SuggestionProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionProductViewHolder holder, int position) {
        Product product=products.get(position);
        Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
        holder.txtBrandName.setText(product.getProductBrandName());
        holder.txtProductName.setText(product.getProductName());
        holder.txtOriginalPrice.setText(product.getOriginalPrice());
        holder.txtSellingPrice.setText(product.getSellingPrice());
        Double discount = calculateDiscountPercentage(Double.parseDouble(product.getOriginalPrice()),
                Double.parseDouble(product.getSellingPrice()));
        DecimalFormat df = new DecimalFormat("#.##");
        String formattedDiscountPercentage = df.format(discount);
        holder.txtDiscount.setText(formattedDiscountPercentage);
        holder.layoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclick.ItemOnClickListener(product.getProductId());
            }
        });

        DatabaseReference reviewRef= FirebaseDatabase.getInstance().getReference("Review");
        Query reviewQuery=reviewRef.orderByChild("productId").equalTo(product.getProductId());
        reviewQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                total=0;
                rating=0;
                totalReview=0;
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    totalReview++;
                    Review review=dataSnapshot.getValue(Review.class);
                    rating= Float.parseFloat(review.getProductStar());
                    total+=rating;
                }
                if(totalReview==0){
                    holder.txtRatingCount.setText(String.valueOf(0));
                    holder.txtRating.setText("0.0");
                }else{
                    holder.txtRatingCount.setText(String.valueOf(totalReview));
                    holder.txtRating.setText(String.valueOf(total/totalReview));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class SuggestionProductViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView productImage;
        TextView txtBrandName,txtProductName,txtOriginalPrice,txtSellingPrice,txtDiscount
                ,txtRating,txtRatingCount;
        CardView layoutContainer;

        public SuggestionProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.productImage);
            txtBrandName=itemView.findViewById(R.id.txtBrandName);
            txtProductName=itemView.findViewById(R.id.txtProductName);
            txtOriginalPrice=itemView.findViewById(R.id.text_product_original_price_PD);
            txtSellingPrice=itemView.findViewById(R.id.txt_product_selling_price_PD);
            txtDiscount=itemView.findViewById(R.id.txtDiscount_PD);
            txtRating=itemView.findViewById(R.id.txt_rating_PD);
            txtRatingCount=itemView.findViewById(R.id.txt_rating_count_PD);
            layoutContainer=itemView.findViewById(R.id.layoutContainer);
        }

    }
    public double calculateDiscountPercentage(double originalPrice, double sellingPrice) {
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }
}
