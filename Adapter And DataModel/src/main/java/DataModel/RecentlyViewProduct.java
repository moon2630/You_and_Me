package DataModel;

public class RecentlyViewProduct {
    private String userId;
    private String productId;
    private String timeStamp;
    private String userId_productId;

    public RecentlyViewProduct() {
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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUserId_productId() {
        return userId_productId;
    }

    public void setUserId_productId(String userId_productId) {
        this.userId_productId = userId_productId;
    }
}