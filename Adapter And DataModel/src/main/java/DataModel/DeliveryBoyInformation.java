package DataModel;

public class DeliveryBoyInformation {
    private String deliveryBoyId,deliveryBoyName,deliveryBoyAddress,deliveryBoyVehicleNumber,deliveryBoyMobileNumber,state,city,vehicleType;
    public DeliveryBoyInformation(){}

    public DeliveryBoyInformation(String deliveryBoyId, String deliveryBoyName, String deliveryBoyAddress, String deliveryBoyVehicleNumber, String deliveryBoyMobileNumber, String state, String city, String vehicleType) {
        this.deliveryBoyId = deliveryBoyId;
        this.deliveryBoyName = deliveryBoyName;
        this.deliveryBoyAddress = deliveryBoyAddress;
        this.deliveryBoyVehicleNumber = deliveryBoyVehicleNumber;
        this.deliveryBoyMobileNumber = deliveryBoyMobileNumber;
        this.state = state;
        this.city = city;
        this.vehicleType = vehicleType;
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

    public String getDeliveryBoyAddress() {
        return deliveryBoyAddress;
    }

    public void setDeliveryBoyAddress(String deliveryBoyAddress) {
        this.deliveryBoyAddress = deliveryBoyAddress;
    }

    public String getDeliveryBoyVehicleNumber() {
        return deliveryBoyVehicleNumber;
    }

    public void setDeliveryBoyVehicleNumber(String deliveryBoyVehicleNumber) {
        this.deliveryBoyVehicleNumber = deliveryBoyVehicleNumber;
    }

    public String getDeliveryBoyMobileNumber() {
        return deliveryBoyMobileNumber;
    }

    public void setDeliveryBoyMobileNumber(String deliveryBoyMobileNumber) {
        this.deliveryBoyMobileNumber = deliveryBoyMobileNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
