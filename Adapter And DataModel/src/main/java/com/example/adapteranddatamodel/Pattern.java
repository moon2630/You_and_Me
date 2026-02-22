package com.example.adapteranddatamodel;

public class Pattern {
    public static boolean isValidMobileNumber(String mobileNumber){
        return mobileNumber.length()==10;
    }

    public static boolean isValidName(String name){
        // the pattern for alphabetical characters and spaces
        String namePattern = "^[a-zA-Z\\s]+$";

        // if the name matches the pattern
        return name.matches(namePattern);
    }

    public static boolean isValidPanCard(String panCardNo){
        return panCardNo.matches("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    }
    public static boolean isValidAadharNumber(String aadharNumber) {
        // Define the Aadhar card pattern (12 numeric characters)
        String aadharPattern = "^[0-9]{12}$";

        // Check if the Aadhar number matches the pattern
        return aadharNumber.matches(aadharPattern);
    }
    public static boolean isValidAccountNumber(String accountNumber){
        return accountNumber.length()>=10 && accountNumber.length()<=18;
    }
}
