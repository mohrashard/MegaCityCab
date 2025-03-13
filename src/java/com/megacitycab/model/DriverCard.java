package com.megacitycab.model;

public class DriverCard {
    private int cardId;
    private int driverId;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String cardholderName;
    private boolean isDefault;
    

    public DriverCard() {}
    
    public DriverCard(int cardId, int driverId, String cardNumber, String expiryDate, 
                     String cvv, String cardholderName, boolean isDefault) {
        this.cardId = cardId;
        this.driverId = driverId;
        this.cardNumber = cardNumber;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
        this.cardholderName = cardholderName;
        this.isDefault = isDefault;
    }
    

    public int getCardId() {
        return cardId;
    }
    
    public void setCardId(int cardId) {
        this.cardId = cardId;
    }
    
    public int getDriverId() {
        return driverId;
    }
    
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
    
    public String getCardNumber() {
        return cardNumber;
    }
    
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
    
    public String getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public String getCvv() {
        return cvv;
    }
    
    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
    
    public String getCardholderName() {
        return cardholderName;
    }
    
    public void setCardholderName(String cardholderName) {
        this.cardholderName = cardholderName;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    

    public String getMaskedCardNumber() {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
    

    public String getCardProvider() {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return "UNKNOWN";
        }
        
        String firstDigit = cardNumber.substring(0, 1);
        switch (firstDigit) {
            case "4":
                return "VISA";
            case "5":
                return "MASTERCARD";
            case "3":
                return "AMEX";
            case "6":
                return "DISCOVER";
            default:
                return "CARD";
        }
    }
}