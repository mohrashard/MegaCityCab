package com.megacitycab.model;

public class DriverBank {
    private int bankId;
    private int driverId;
    private String bankName;
    private String accountNumber;
    private String accountHolderName;
    private String ifscCode;
    private boolean isDefault;
    

    public DriverBank() {}
    
    public DriverBank(int bankId, int driverId, String bankName, String accountNumber, 
                     String accountHolderName, String ifscCode, boolean isDefault) {
        this.bankId = bankId;
        this.driverId = driverId;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.ifscCode = ifscCode;
        this.isDefault = isDefault;
    }
    

    public int getBankId() {
        return bankId;
    }
    
    public void setBankId(int bankId) {
        this.bankId = bankId;
    }
    
    public int getDriverId() {
        return driverId;
    }
    
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getAccountHolderName() {
        return accountHolderName;
    }
    
    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }
    
    public String getIfscCode() {
        return ifscCode;
    }
    
    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
    
    public boolean isDefault() {
        return isDefault;
    }
    
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
    

    public String getMaskedAccountNumber() {
        if (accountNumber == null || accountNumber.length() < 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}