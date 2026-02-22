package DataModel;

public class DeliveryBoyDocument {
    private String deliveryBoyId,deliveryBoyAdharCard,deliveryBoyRcBook,deliveryBoyLicNo,deliveryBoyPanCardNo,deliveryBoyAccountName,deliveryBoyAccountNo;


    public DeliveryBoyDocument(){}

    public DeliveryBoyDocument(String deliveryBoyId, String deliveryBoyAdharCard, String deliveryBoyRcBook, String deliveryBoyLicNo, String deliveryBoyPanCardNo, String deliveryBoyAccountName, String deliveryBoyAccountNo) {
        this.deliveryBoyId = deliveryBoyId;
        this.deliveryBoyAdharCard = deliveryBoyAdharCard;
        this.deliveryBoyRcBook = deliveryBoyRcBook;
        this.deliveryBoyLicNo = deliveryBoyLicNo;
        this.deliveryBoyPanCardNo = deliveryBoyPanCardNo;
        this.deliveryBoyAccountName = deliveryBoyAccountName;
        this.deliveryBoyAccountNo = deliveryBoyAccountNo;
    }

    public String getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(String deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
    }

    public String getDeliveryBoyAdharCard() {
        return deliveryBoyAdharCard;
    }

    public void setDeliveryBoyAdharCard(String deliveryBoyAdharCard) {
        this.deliveryBoyAdharCard = deliveryBoyAdharCard;
    }

    public String getDeliveryBoyRcBook() {
        return deliveryBoyRcBook;
    }

    public void setDeliveryBoyRcBook(String deliveryBoyRcBook) {
        this.deliveryBoyRcBook = deliveryBoyRcBook;
    }

    public String getDeliveryBoyLicNo() {
        return deliveryBoyLicNo;
    }

    public void setDeliveryBoyLicNo(String deliveryBoyLicNo) {
        this.deliveryBoyLicNo = deliveryBoyLicNo;
    }

    public String getDeliveryBoyPanCardNo() {
        return deliveryBoyPanCardNo;
    }

    public void setDeliveryBoyPanCardNo(String deliveryBoyPanCardNo) {
        this.deliveryBoyPanCardNo = deliveryBoyPanCardNo;
    }

    public String getDeliveryBoyAccountName() {
        return deliveryBoyAccountName;
    }

    public void setDeliveryBoyAccountName(String deliveryBoyAccountName) {
        this.deliveryBoyAccountName = deliveryBoyAccountName;
    }

    public String getDeliveryBoyAccountNo() {
        return deliveryBoyAccountNo;
    }

    public void setDeliveryBoyAccountNo(String deliveryBoyAccountNo) {
        this.deliveryBoyAccountNo = deliveryBoyAccountNo;
    }
}
