package DataModel;

public class LikeProduct {
    private String userId,productId,nodeId,qty,productSize,adminId,productOriginalPrice,productSellingPrice;
    public LikeProduct() {
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getProductOriginalPrice() {
        return productOriginalPrice;
    }

    public void setProductOriginalPrice(String productOriginalPrice) {
        this.productOriginalPrice = productOriginalPrice;
    }

    public String getProductSellingPrice() {
        return productSellingPrice;
    }

    public void setProductSellingPrice(String productSellingPrice) {
        this.productSellingPrice = productSellingPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }
}
