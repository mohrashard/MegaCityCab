package com.megacitycab.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DriverWallet {
    private int driverId;
    private BigDecimal walletBalance;
    private BigDecimal totalEarnings;
    private BigDecimal totalExpenses;
    

    public DriverWallet() {}
    
    public DriverWallet(int driverId, BigDecimal walletBalance, BigDecimal totalEarnings, BigDecimal totalExpenses) {
        this.driverId = driverId;
        this.walletBalance = walletBalance;
        this.totalEarnings = totalEarnings;
        this.totalExpenses = totalExpenses;
    }
    

    public int getDriverId() {
        return driverId;
    }
    
    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }
    
    public BigDecimal getWalletBalance() {
        return walletBalance;
    }
    
    public void setWalletBalance(BigDecimal walletBalance) {
        this.walletBalance = walletBalance;
    }
    
    public BigDecimal getTotalEarnings() {
        return totalEarnings;
    }
    
    public void setTotalEarnings(BigDecimal totalEarnings) {
        this.totalEarnings = totalEarnings;
    }
    
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }
    
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}