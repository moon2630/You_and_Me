package com.example.uptrend.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uptrend.R;

import java.util.ArrayList;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.RecentSearchViewHolder> {

    private Context context;
    private ArrayList<String> recentSearches;
    private OnRecentSearchClickListener listener;

    public interface OnRecentSearchClickListener {
        void onRecentSearchClick(String searchText);
        void onRecentSearchRemove(String searchText, int position);
    }

    public RecentSearchAdapter(Context context, ArrayList<String> recentSearches, OnRecentSearchClickListener listener) {
        this.context = context;
        this.recentSearches = recentSearches;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_search, parent, false);
        return new RecentSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentSearchViewHolder holder, int position) {
        String searchText = recentSearches.get(position);
        holder.txtRecentSearch.setText(searchText);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecentSearchClick(searchText);
            }
        });

        // Replace the imgClear click listener in onBindViewHolder:
        holder.imgClear.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecentSearchRemove(searchText, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    public static class RecentSearchViewHolder extends RecyclerView.ViewHolder {
        TextView txtRecentSearch;
        ImageView imgClear;

        public RecentSearchViewHolder(@NonNull View itemView) {
            super(itemView);
            txtRecentSearch = itemView.findViewById(R.id.txtRecentSearch);
            imgClear = itemView.findViewById(R.id.imgClear);
        }
    }
}