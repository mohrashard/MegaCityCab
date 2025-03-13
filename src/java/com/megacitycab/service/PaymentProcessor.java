// PaymentProcessor.java
package com.megacitycab.service;

import com.megacitycab.model.Transaction;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

public interface PaymentProcessor {
    void processPayment(int passengerId, int bookingId, BigDecimal amount, String description) 
        throws SQLException, PaymentException;
}