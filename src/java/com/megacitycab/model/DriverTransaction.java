package com.megacitycab.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DriverTransaction {
    private int transactionId;
    private int driverId;
    private String transactionType;
    private BigDecimal amount;
    private String description;
    private LocalDateTime dateTime;
    

    public DriverTransaction() {}
    
    public DriverTransaction(int transactionId, int driverId, String transactionType, 
                            BigDecimal amount, String description, LocalDateTime dateTime) {
        this.transactionId = transactionId;
        this.driverId = driverId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
    }
    

    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getDriverId() {
        return driverId;
    }
    
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
    

    public boolean isCredit() {
        return "Top Up".equals(transactionType) || "Ride Earnings".equals(transactionType);
    }
}
