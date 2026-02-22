package DataModel;

public class AdminBankInformation {
    private String adminId,accountHolderName,accountType,accountNumber,ifscCode,panCardNumber,adharCard;

    public String getAdharCard() {
        return adharCard;
    }

    public void setAdharCard(String adharCard) {
        this.adharCard = adharCard;
    }

    public AdminBankInformation(){}

    public AdminBankInformation(String adminId, String accountHolderName, String accountType, String accountNumber, String ifscCode, String panCardNumber) {
        this.adminId = adminId;
        this.accountHolderName = accountHolderName;
        this.accountType = accountType;
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.panCardNumber = panCardNumber;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getPanCardNumber() {
        return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }
}
