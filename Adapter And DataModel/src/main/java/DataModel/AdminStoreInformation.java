package DataModel;

public class AdminStoreInformation {
   private String adminId,sellerName,storeName,storePincode,storeAddress1,storeAddress2,storeCity,storeState;

   public AdminStoreInformation(){}

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public AdminStoreInformation(String adminId, String storeName, String storePincode, String storeAddress1, String storeAddress2, String storeCity, String storeState) {
        this.adminId = adminId;
        this.storeName = storeName;
        this.storePincode = storePincode;
        this.storeAddress1 = storeAddress1;
        this.storeAddress2 = storeAddress2;
        this.storeCity = storeCity;
        this.storeState = storeState;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }


    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getStoreAddress1() {
        return storeAddress1;
    }

    public void setStoreAddress1(String storeAddress1) {
        this.storeAddress1 = storeAddress1;
    }

    public String getStoreAddress2() {
        return storeAddress2;
    }

    public void setStoreAddress2(String storeAddress2) {
        this.storeAddress2 = storeAddress2;
    }

    public String getStoreCity() {
        return storeCity;
    }

    public void setStoreCity(String storeCity) {
        this.storeCity = storeCity;
    }

    public String getStoreState() {
        return storeState;
    }

    public void setStoreState(String storeState) {
        this.storeState = storeState;
    }
}
