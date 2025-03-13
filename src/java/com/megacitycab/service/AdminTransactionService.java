package com.megacitycab.service;

import com.megacitycab.model.AdminTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AdminTransactionService {
    List<AdminTransaction> getAllTransactionsByAdminId(int adminId) throws SQLException;
    List<AdminTransaction> getRecentTransactionsByAdminId(int adminId, int limit) throws SQLException;
    boolean addTransaction(AdminTransaction transaction) throws SQLException;
    boolean processWithdrawal(int adminId, BigDecimal amount, String description) throws SQLException;
    boolean canWithdraw(int adminId, BigDecimal amount) throws SQLException;
}