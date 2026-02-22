package com.example.adapteranddatamodel;

import java.io.Serializable;

public class PriceRange implements Serializable {
    private String minPrice,maxPrice;

    public String getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(String minPrice) {
        this.minPrice = minPrice;
    }

    public String getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice) {
        this.maxPrice = maxPrice;
    }

    public PriceRange(String minPrice, String maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }
}
