package DataModel;

public class Commission {
    private String nodeId;
    private String orderId;
    private String productId;
    private String productName;
    private String userId;
    private String sellerId;
    private String productSellingPrice;
    private String commissionAmount;
    private String platformFee;
    private String netAmount;
    private String status; // pending, credited, debited
    private String orderDate;
    private String payoutDate;
    private String generatedDate;
    private String creditedDate;
    private String debitedDate;
    private String category;
    private String subCategory;
    private int commissionPercentage;
    private boolean autoCredited;
    private String autoCreditedDate;
    private boolean autoDebited;
    private String autoDebitedDate;

    public Commission() {
    }

    // Getters and Setters
    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getProductSellingPrice() {
        return productSellingPrice;
    }

    public void setProductSellingPrice(String productSellingPrice) {
        this.productSellingPrice = productSellingPrice;
    }

    public String getCommissionAmount() {
        return commissionAmount;
    }

    public void setCommissionAmount(String commissionAmount) {
        this.commissionAmount = commissionAmount;
    }

    public String getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(String platformFee) {
        this.platformFee = platformFee;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPayoutDate() {
        return payoutDate;
    }

    public void setPayoutDate(String payoutDate) {
        this.payoutDate = payoutDate;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getCreditedDate() {
        return creditedDate;
    }

    public void setCreditedDate(String creditedDate) {
        this.creditedDate = creditedDate;
    }

    public String getDebitedDate() {
        return debitedDate;
    }

    public void setDebitedDate(String debitedDate) {
        this.debitedDate = debitedDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getCommissionPercentage() {
        return commissionPercentage;
    }

    public void setCommissionPercentage(int commissionPercentage) {
        this.commissionPercentage = commissionPercentage;
    }

    public boolean isAutoCredited() {
        return autoCredited;
    }

    public void setAutoCredited(boolean autoCredited) {
        this.autoCredited = autoCredited;
    }

    public String getAutoCreditedDate() {
        return autoCreditedDate;
    }

    public void setAutoCreditedDate(String autoCreditedDate) {
        this.autoCreditedDate = autoCreditedDate;
    }

    public boolean isAutoDebited() {
        return autoDebited;
    }

    public void setAutoDebited(boolean autoDebited) {
        this.autoDebited = autoDebited;
    }

    public String getAutoDebitedDate() {
        return autoDebitedDate;
    }

    public void setAutoDebitedDate(String autoDebitedDate) {
        this.autoDebitedDate = autoDebitedDate;
    }
}