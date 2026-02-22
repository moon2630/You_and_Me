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

import DataModel.Product;
import DataModel.Review;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>{
    private Context context;
    int totalReview=0;
    float rating,total;
    private ArrayList<Product> products;
    private Onclick onclick;

    public CategoryAdapter(Context context, ArrayList<Product> products, Onclick onclick) {
        this.context = context;
        this.products = products;
        this.onclick = onclick;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.category_product,parent,false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
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
        holder.cardViewCategory.setOnClickListener(new View.OnClickListener() {
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
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    totalReview= (int) snapshot.getChildrenCount();
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

    public class CategoryViewHolder extends RecyclerView.ViewHolder{
        ShapeableImageView productImage;
        TextView txtBrandName,txtProductName,txtOriginalPrice,txtSellingPrice,txtDiscount
                ,txtRating,txtRatingCount;
        CardView cardViewCategory;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardViewCategory=itemView.findViewById(R.id.cardViewCategory);
            productImage=itemView.findViewById(R.id.productImageCategory);
            txtBrandName=itemView.findViewById(R.id.brandName_Category);
            txtProductName=itemView.findViewById(R.id.productName_Category);
            txtOriginalPrice=itemView.findViewById(R.id.product_original_price_Category);
            txtSellingPrice=itemView.findViewById(R.id.productSellingPrice_Category);
            txtDiscount=itemView.findViewById(R.id.txtDiscount01Category);
            txtRating=itemView.findViewById(R.id.txt_rating_Category);
            txtRatingCount=itemView.findViewById(R.id.txt_rating_count_Category);
        }
    }
    public double calculateDiscountPercentage(double originalPrice, double sellingPrice) {
        double discount = originalPrice - sellingPrice;
        return (discount / originalPrice) * 100;
    }
}
