package com.example.uptrend.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uptrend.R;
import com.example.uptrend.open_product;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import DataModel.Cart;
import DataModel.LikeProduct;
import DataModel.Product;
import io.github.muddz.styleabletoast.StyleableToast;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.WishListViewHolder> {
    private Context context;
    private ArrayList<LikeProduct> likeProductArrayList;

    public WishListAdapter(Context context, ArrayList<LikeProduct> likeProductArrayList) {
        this.context = context;
        this.likeProductArrayList = likeProductArrayList;
    }

    @NonNull
    @Override
    public WishListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.wishlist_product_rv,parent,false);
        return new WishListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishListViewHolder holder, int position) {
        LikeProduct likeProduct=likeProductArrayList.get(position);
        String productId=likeProductArrayList.get(position).getProductId();
        String nodeId=likeProductArrayList.get(position).getNodeId();
        DatabaseReference productRef= FirebaseDatabase.getInstance().getReference("Product").child(productId);
        productRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Product product=snapshot.getValue(Product.class);
                    Glide.with(context).load(product.getProductImages().get(0)).into(holder.productImage);
                    holder.txtBrandName.setText(product.getProductBrandName());
                    holder.txtProductName.setText(product.getProductName());
                    holder.txtSellingPrice.setText(product.getSellingPrice());
                    holder.txtOriginalPrice.setText(product.getOriginalPrice());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference("WishListProduct").child(nodeId);
                deleteRef.removeValue();
                StyleableToast.makeText(context, "Remove from Favourites", R.style.UptrendToast).show();
                notifyDataSetChanged();
            }
        });

        holder.itemView.findViewById(R.id.cardViewWishlist).setOnClickListener(v -> {
            Intent intent = new Intent(context, open_product.class);
            intent.putExtra("productId", productId);
            intent.putExtra("activityName", "wishlistProduct");
            context.startActivity(intent);
        });

        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cart cart=new Cart();
                DatabaseReference productRef=FirebaseDatabase.getInstance().getReference("Product").child(likeProduct.getProductId());
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Product product=snapshot.getValue(Product.class);
                            if(product.getProductSizes()!=null){
                                int stock= Integer.parseInt(product.getProductSizes().get(0));
                                if(stock==0){
                                    StyleableToast.makeText(context,"Currently Product Is Out Of Stock",R.style.UptrendToast).show();
                                }else{
                                    cart.setUserId(likeProduct.getUserId());
                                    cart.setAdminId(likeProduct.getAdminId());
                                    cart.setProductId(likeProduct.getProductId());
                                    cart.setOriginalPrice(likeProduct.getProductOriginalPrice());
                                    cart.setSellingPrice(likeProduct.getProductSellingPrice());
                                    cart.setQty(likeProduct.getQty());
                                    cart.setProductSize(likeProduct.getProductSize());
                                    DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference("Cart");
                                    cartRef.push().setValue(cart);
                                    StyleableToast.makeText(context,"Product Add In The Cart",R.style.UptrendToast).show();
                                }

                            }else{
                                int stock= Integer.parseInt(product.getTotalStock());
                                if(stock==0){
                                    StyleableToast.makeText(context,"Currently Product Is Out Of Stock",R.style.UptrendToast).show();
                                }else{
                                    cart.setUserId(likeProduct.getUserId());
                                    cart.setAdminId(likeProduct.getAdminId());
                                    cart.setProductId(likeProduct.getProductId());
                                    cart.setOriginalPrice(likeProduct.getProductOriginalPrice());
                                    cart.setSellingPrice(likeProduct.getProductSellingPrice());
                                    cart.setQty(likeProduct.getQty());
                                    cart.setProductSize(likeProduct.getProductSize());
                                    DatabaseReference cartRef=FirebaseDatabase.getInstance().getReference("Cart");
                                    cartRef.push().setValue(cart);
                                    StyleableToast.makeText(context,"Product Add In The Cart",R.style.UptrendToast).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return likeProductArrayList.size();
    }

    public class WishListViewHolder extends RecyclerView.ViewHolder{
        TextView txtBrandName,txtProductName,txtDiscountPer,txtSellingPrice,txtOriginalPrice;
        TextView btnDelete;

        AppCompatButton btnAddToCart;
        ShapeableImageView productImage;

        public WishListViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage=itemView.findViewById(R.id.likeProductImage);
            txtBrandName=itemView.findViewById(R.id.likeBrandName);
            txtProductName=itemView.findViewById(R.id.likeProductName);
            txtDiscountPer=itemView.findViewById(R.id.txtDiscount02);
            txtSellingPrice=itemView.findViewById(R.id.likeProductSellingPrice);
            txtOriginalPrice=itemView.findViewById(R.id.likeProductOriginalPrice);
            btnDelete=itemView.findViewById(R.id.likeDelete);
            btnAddToCart=itemView.findViewById(R.id.likeAddToBag);
        }
    }
}
