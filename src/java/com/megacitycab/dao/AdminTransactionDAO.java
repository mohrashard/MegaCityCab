package com.megacitycab.dao;

import com.megacitycab.model.AdminTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface AdminTransactionDAO {
    List<AdminTransaction> getAllTransactionsByAdminId(int adminId) throws SQLException;
    List<AdminTransaction> getRecentTransactionsByAdminId(int adminId, int limit) throws SQLException;
    boolean addTransaction(AdminTransaction transaction) throws SQLException;
    BigDecimal getTotalWithdrawalForDay(int adminId, LocalDate date) throws SQLException;
}