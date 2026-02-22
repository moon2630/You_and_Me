package DataModel;

public class Order {
    private String nodeId,userId,sellerId,productId,productSize,productQty,orderStatus,orderDate,orderTime,shipingDate,delliveryDate,productSellingPrice,productOriginalPrice;

    public String getProductSellingPrice() {
        return productSellingPrice;
    }

    public void setProductSellingPrice(String productSellingPrice) {
        this.productSellingPrice = productSellingPrice;
    }

    public String getProductOriginalPrice() {
        return productOriginalPrice;
    }

    public void setProductOriginalPrice(String productOriginalPrice) {
        this.productOriginalPrice = productOriginalPrice;
    }

    public Order() {
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }


    public String getShipingDate() {
        return shipingDate;
    }

    public void setShipingDate(String shipingDate) {
        this.shipingDate = shipingDate;
    }

    public String getDelliveryDate() {
        return delliveryDate;
    }

    public void setDelliveryDate(String delliveryDate) {
        this.delliveryDate = delliveryDate;
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

    public String getProductSize() {
        return productSize;
    }

    public void setProductSize(String productSize) {
        this.productSize = productSize;
    }

    public String getProductQty() {
        return productQty;
    }

    public void setProductQty(String productQty) {
        this.productQty = productQty;
    }


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}