package com.megacitycab.model;

import java.math.BigDecimal;

public class Wallet {
    private int passengerId;
    private BigDecimal balance;

    public int getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}