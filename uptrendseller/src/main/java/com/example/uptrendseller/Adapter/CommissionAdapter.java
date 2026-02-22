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

import com.example.uptrendseller.R;
import com.example.uptrendseller.commission_notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import DataModel.Commission;

public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.CommissionViewHolder> {

    private Context context;
    private ArrayList<Commission> commissionList;
    private String currentFilter = "all"; // Track current filter

    public CommissionAdapter(Context context, ArrayList<Commission> commissionList) {
        this.context = context;
        this.commissionList = commissionList;
        Log.d("CommissionAdapter", "Adapter created with " + commissionList.size() + " items");
    }

    // Method to update filter from activity
    public void setCurrentFilter(String filter) {
        this.currentFilter = filter;
        Log.d("CommissionAdapter", "Filter set to: " + filter);
    }

    @NonNull
    @Override
    public CommissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commission_notification_item, parent, false);
        return new CommissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommissionViewHolder holder, int position) {
        Commission commission = commissionList.get(position);
        String status = commission.getStatus();

        // Set product name
        holder.txtProductName.setText(commission.getProductName() != null ?
                commission.getProductName() : "Product");

        // Format order date for display
        if (commission.getOrderDate() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM", Locale.getDefault());
                Date date = inputFormat.parse(commission.getOrderDate());
                if (date != null) {
                    holder.txtDate.setText(outputFormat.format(date));
                } else {
                    holder.txtDate.setText("-");
                }
            } catch (ParseException e) {
                holder.txtDate.setText("-");
            }
        } else {
            holder.txtDate.setText("-");
        }

        // ============== UPDATED AMOUNT CALCULATION ==============
        String displayStatusText;
        int badgeResource;
        int textColor;
        String displayAmount;

        try {
            double commissionAmt = Double.parseDouble(commission.getCommissionAmount());
            double platformFee = Double.parseDouble(commission.getPlatformFee());
            double netAmt = Double.parseDouble(commission.getNetAmount());
            double totalCommissionAndFee = commissionAmt + platformFee;

            if (currentFilter.equals("all")) {
                // For "All" filter, always show as "CREDITED"
                displayStatusText = "CREDITED";
                badgeResource = R.drawable.badge_credited;
                textColor = context.getResources().getColor(R.color.green);
                displayAmount = String.format("₹%.0f", netAmt); // Show net amount
            }
            else if (currentFilter.equals("credited")) {
                // For "Credited" filter, always show as "CREDITED"
                displayStatusText = "CREDITED";
                badgeResource = R.drawable.badge_credited;
                textColor = context.getResources().getColor(R.color.green);
                displayAmount = String.format("₹%.0f", netAmt); // Show net amount
            }
            else if (currentFilter.equals("debited")) {
                // For "Debited" filter, always show as "DEBITED"
                displayStatusText = "DEBITED";
                badgeResource = R.drawable.badge_credited;
                textColor = context.getResources().getColor(R.color.red);
                // Show commission + platform fee
                displayAmount = String.format("₹%.0f", totalCommissionAndFee);
            }
            else if (currentFilter.equals("pending")) {
                // For "Pending" filter, always show as "PENDING"
                displayStatusText = "PENDING";
                badgeResource = R.drawable.badge_pending;
                textColor = context.getResources().getColor(R.color.orange3333);
                // Show commission + platform fee
                displayAmount = String.format("₹%.0f", totalCommissionAndFee);
            }
            else {
                // Default
                displayStatusText = "CREDITED";
                badgeResource = R.drawable.badge_credited;
                textColor = context.getResources().getColor(R.color.green);
                displayAmount = String.format("₹%.0f", netAmt);
            }

            // Set the status badge
            holder.txtStatusBadge.setText(displayStatusText);
            holder.txtStatusBadge.setBackgroundResource(badgeResource);
            holder.txtStatusBadge.setTextColor(textColor);

            // Set the amount
            holder.txtAmount.setText(displayAmount);

            Log.d("CommissionAdapter", commission.getProductName() +
                    " | Filter: " + currentFilter +
                    " | Actual Status: " + status +
                    " | Commission: ₹" + commissionAmt +
                    " | Platform Fee: ₹" + platformFee +
                    " | Total (Commission+Fee): ₹" + totalCommissionAndFee +
                    " | Net: ₹" + netAmt +
                    " | Display: " + displayAmount);

        } catch (NumberFormatException e) {
            holder.txtStatusBadge.setText("ERROR");
            holder.txtAmount.setText("₹0");
            Log.e("CommissionAdapter", "Error parsing amounts: " + e.getMessage());
        }
        // ============== END OF UPDATED AMOUNT CALCULATION ==============

        // Rest of your existing code remains the same...
        holder.txtOrderDate.setText(commission.getOrderDate() != null ? commission.getOrderDate() : "-");
        holder.txtPayoutDate.setText(commission.getPayoutDate() != null ? commission.getPayoutDate() : "-");
        holder.txtSalePrice.setText(String.format("₹%s", commission.getProductSellingPrice()));

        // Show commission with percentage
        if (commission.getCommissionPercentage() > 0) {
            holder.txtCommissionPercent.setText(String.format("₹%s (%d%%)",
                    commission.getCommissionAmount(),
                    commission.getCommissionPercentage()));
        } else {
            holder.txtCommissionPercent.setText(String.format("₹%s", commission.getCommissionAmount()));
        }

        holder.txtPlatformFee.setText(String.format("₹%s", commission.getPlatformFee()));
        holder.txtNetAmount.setText(String.format("₹%s", commission.getNetAmount()));

        // Show credited/debited dates
        if (commission.getCreditedDate() != null && !commission.getCreditedDate().isEmpty()) {
            holder.txtCreditedDate.setText("Credited on: " + commission.getCreditedDate());
            holder.txtCreditedDate.setVisibility(View.VISIBLE);
        } else {
            holder.txtCreditedDate.setVisibility(View.GONE);
        }

        if (commission.getDebitedDate() != null && !commission.getDebitedDate().isEmpty()) {
            holder.txtDebitedDate.setText("Debited on: " + commission.getDebitedDate());
            holder.txtDebitedDate.setVisibility(View.VISIBLE);
        } else {
            holder.txtDebitedDate.setVisibility(View.GONE);
        }

        // Expand/collapse functionality
        final boolean[] isExpanded = {false};

        holder.btnExpand.setOnClickListener(v -> {
            isExpanded[0] = !isExpanded[0];
            holder.detailsLayout.setVisibility(isExpanded[0] ? View.VISIBLE : View.GONE);
            holder.btnExpand.setText(isExpanded[0] ? "Hide Details" : "View Details");
        });
    }

    public void updateList(ArrayList<Commission> newList) {
        this.commissionList.clear();
        this.commissionList.addAll(newList);
        notifyDataSetChanged();
        Log.d("CommissionAdapter", "Adapter updated with " + this.commissionList.size() + " items");
    }

    @Override
    public int getItemCount() {
        return commissionList.size();
    }

    public static class CommissionViewHolder extends RecyclerView.ViewHolder {
        CardView cardCommission;
        TextView txtStatusBadge, txtProductName, txtDate, txtAmount;
        TextView txtOrderDate, txtPayoutDate, txtSalePrice, txtCommissionPercent;
        TextView txtPlatformFee, txtNetAmount, btnExpand, txtCreditedDate, txtDebitedDate;
        View detailsLayout;

        public CommissionViewHolder(@NonNull View itemView) {
            super(itemView);

            cardCommission = itemView.findViewById(R.id.cardViewCommission);
            txtStatusBadge = itemView.findViewById(R.id.txtStatusBadge);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtDate = itemView.findViewById(R.id.txtDate);
            txtAmount = itemView.findViewById(R.id.txtAmount);
            txtOrderDate = itemView.findViewById(R.id.txtOrderDate);
            txtPayoutDate = itemView.findViewById(R.id.txtPayoutDate);
            txtSalePrice = itemView.findViewById(R.id.txtSalePrice);
            txtCommissionPercent = itemView.findViewById(R.id.txtCommissionPercent);
            txtPlatformFee = itemView.findViewById(R.id.txtPlatformFee);
            txtNetAmount = itemView.findViewById(R.id.txtNetAmount);
            btnExpand = itemView.findViewById(R.id.btnExpand);
            detailsLayout = itemView.findViewById(R.id.detailsLayout);
            txtCreditedDate = itemView.findViewById(R.id.txtCreditedDate);
            txtDebitedDate = itemView.findViewById(R.id.txtDebitedDate);
        }
    }
}