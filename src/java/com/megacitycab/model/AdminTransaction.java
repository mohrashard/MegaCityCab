package com.megacitycab.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdminTransaction {
    private int transactionId;
    private int adminId;
    private BigDecimal amount;
    private String transactionType; 
    private String description;
    private String status; 
    private LocalDateTime createdAt;
    
 
    public AdminTransaction() {}
    
    public AdminTransaction(int adminId, BigDecimal amount, String transactionType, String description) {
        this.adminId = adminId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.status = "COMPLETED";
        this.createdAt = LocalDateTime.now();
    }
    
    public int getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }
    
    public int getAdminId() {
        return adminId;
    }
    
    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}