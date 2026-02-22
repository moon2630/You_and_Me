package DataModel;

public class DeliveryBoy {
    private String deliveryBoyId,deliveryBoyName,deliveryBoyEmail,deliveryBoyMobileNumber;
    public DeliveryBoy(){}

    public DeliveryBoy(String deliveryBoyId, String deliveryBoyName, String deliveryBoyEmail, String deliveryBoyMobileNumber) {
        this.deliveryBoyId = deliveryBoyId;
        this.deliveryBoyName = deliveryBoyName;
        this.deliveryBoyEmail = deliveryBoyEmail;
        this.deliveryBoyMobileNumber = deliveryBoyMobileNumber;
    }

    public String getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(String deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
    }

    public String getDeliveryBoyName() {
        return deliveryBoyName;
    }

    public void setDeliveryBoyName(String deliveryBoyName) {
        this.deliveryBoyName = deliveryBoyName;
    }

    public String getDeliveryBoyEmail() {
        return deliveryBoyEmail;
    }

    public void setDeliveryBoyEmail(String deliveryBoyEmail) {
        this.deliveryBoyEmail = deliveryBoyEmail;
    }

    public String getDeliveryBoyMobileNumber() {
        return deliveryBoyMobileNumber;
    }

    public void setDeliveryBoyMobileNumber(String deliveryBoyMobileNumber) {
        this.deliveryBoyMobileNumber = deliveryBoyMobileNumber;
    }
}
