package DataModel;

import java.util.ArrayList;

public class Product {

    private String adminId, productId, productCategory, productSubCategory, productBrandName,
            productName, originalPrice, sellingPrice, totalStock, productPacking, productColour,
            productWeight, productFabric, productOccasion, productWashcare, productSuitFor,
            productGenericName, productManufactureDetails, productPackerDetail, ram, storage,
            processor, rearCamera, frontCamera, battery, productDescription;

    private String savedAmount, discountPercent;


    private String productStatus;  // Add this field

    private ArrayList<String> productImages, searchKeyWord, productSizes;

    private long timestamp;

    private String productDisplayId; // Add this field

    private String productCreatedDate; // Format: "26/06/2025"
    private String productLastUpdatedDate; // Format: "28/06/2025"
     private int salesCount;

    public Product() {}

    public int getSalesCount() { return salesCount; }
    public void setSalesCount(int salesCount) { this.salesCount = salesCount; }


    public String getProductCreatedDate() {
        return productCreatedDate;
    }

    public void setProductCreatedDate(String productCreatedDate) {
        this.productCreatedDate = productCreatedDate;
    }

    public String getProductLastUpdatedDate() {
        return productLastUpdatedDate;
    }

    public void setProductLastUpdatedDate(String productLastUpdatedDate) {
        this.productLastUpdatedDate = productLastUpdatedDate;
    }
    public String getProductDisplayId() {
        return productDisplayId;
    }

    public void setProductDisplayId(String productDisplayId) {
        this.productDisplayId = productDisplayId;
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



    public String getProductStatus() { return productStatus; }
    public void setProductStatus(String productStatus) { this.productStatus = productStatus; }


    public String getSavedAmount() { return savedAmount; }
    public void setSavedAmount(String savedAmount) { this.savedAmount = savedAmount; }

    public String getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(String discountPercent) { this.discountPercent = discountPercent; }
    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getRam() {
        return ram;
    }

    public void setRam(String ram) {
        this.ram = ram;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getRearCamera() {
        return rearCamera;
    }

    public void setRearCamera(String rearCamera) {
        this.rearCamera = rearCamera;
    }

    public String getFrontCamera() {
        return frontCamera;
    }

    public void setFrontCamera(String frontCamera) {
        this.frontCamera = frontCamera;
    }

    public String getBattery() {
        return battery;
    }

    public void setBattery(String battery) {
        this.battery = battery;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    public String getProductSubCategory() {
        return productSubCategory;
    }

    public void setProductSubCategory(String productSubCategory) {
        this.productSubCategory = productSubCategory;
    }

    public String getProductBrandName() {
        return productBrandName;
    }

    public void setProductBrandName(String productBrandName) {
        this.productBrandName = productBrandName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(String originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(String totalStock) {
        this.totalStock = totalStock;
    }

    public String getProductPacking() {
        return productPacking;
    }

    public void setProductPacking(String productPacking) {
        this.productPacking = productPacking;
    }

    public String getProductColour() {
        return productColour;
    }

    public void setProductColour(String productColour) {
        this.productColour = productColour;
    }

    public String getProductWeight() {
        return productWeight;
    }

    public void setProductWeight(String productWeight) {
        this.productWeight = productWeight;
    }

    public String getProductFabric() {
        return productFabric;
    }

    public void setProductFabric(String productFabric) {
        this.productFabric = productFabric;
    }

    public String getProductOccasion() {
        return productOccasion;
    }

    public void setProductOccasion(String productOccasion) {
        this.productOccasion = productOccasion;
    }

    public String getProductWashcare() {
        return productWashcare;
    }

    public void setProductWashcare(String productWashcare) {
        this.productWashcare = productWashcare;
    }

    public String getProductSuitFor() {
        return productSuitFor;
    }

    public void setProductSuitFor(String productSuitFor) {
        this.productSuitFor = productSuitFor;
    }

    public String getProductGenericName() {
        return productGenericName;
    }

    public void setProductGenericName(String productGenericName) {
        this.productGenericName = productGenericName;
    }

    public String getProductManufactureDetails() {
        return productManufactureDetails;
    }

    public void setProductManufactureDetails(String productManufactureDetails) {
        this.productManufactureDetails = productManufactureDetails;
    }

    public String getProductPackerDetail() {
        return productPackerDetail;
    }

    public void setProductPackerDetail(String productPackerDetail) {
        this.productPackerDetail = productPackerDetail;
    }

    public ArrayList<String> getProductSizes() {
        return productSizes;
    }

    public void setProductSizes(ArrayList<String> productSizes) {
        this.productSizes = productSizes;
    }

    public ArrayList<String> getProductImages() {
        return productImages;
    }

    public void setProductImages(ArrayList<String> productImages) {
        this.productImages = productImages;
    }

    public ArrayList<String> getSearchKeyWord() {
        return searchKeyWord;
    }

    public void setSearchKeyWord(ArrayList<String> searchKeyWord) {
        this.searchKeyWord = searchKeyWord;
    }
}