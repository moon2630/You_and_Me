package com.example.adapteranddatamodel;

import java.io.Serializable;

public class DiscountRange implements Serializable {
    private String minDiscount,maxDiscount;

    public DiscountRange() {
    }

    public DiscountRange(String minDiscount, String maxDiscount) {
        this.minDiscount = minDiscount;
        this.maxDiscount = maxDiscount;
    }

    public String getMinDiscount() {
        return minDiscount;
    }

    public void setMinDiscount(String minDiscount) {
        this.minDiscount = minDiscount;
    }

    public String getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(String maxDiscount) {
        this.maxDiscount = maxDiscount;
    }
}
