package com.megacitycab.service;
import com.megacitycab.dao.AdminTransactionDAO;
import com.megacitycab.dao.AdminTransactionDAOImpl;
import com.megacitycab.model.AdminTransaction;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AdminTransactionServiceImpl implements AdminTransactionService {
    private final AdminTransactionDAO admintransactionDAO;
    private final AdminWalletService adminwalletService;
    private static final BigDecimal DAILY_WITHDRAWAL_LIMIT = new BigDecimal("100000.00");
    
    public AdminTransactionServiceImpl() {
        this.admintransactionDAO = new AdminTransactionDAOImpl();
        this.adminwalletService = new AdminWalletServiceImpl();
    }

    @Override
    public List<AdminTransaction> getAllTransactionsByAdminId(int adminId) throws SQLException {
        return admintransactionDAO.getAllTransactionsByAdminId(adminId);
    }

    @Override
    public List<AdminTransaction> getRecentTransactionsByAdminId(int adminId, int limit) throws SQLException {
        return admintransactionDAO.getRecentTransactionsByAdminId(adminId, limit);
    }

    @Override
    public boolean addTransaction(AdminTransaction transaction) throws SQLException {
        return admintransactionDAO.addTransaction(transaction);
    }

    @Override
    public boolean processWithdrawal(int adminId, BigDecimal amount, String description) throws SQLException {
        if (!canWithdraw(adminId, amount)) {
            return false;
        }

        boolean balanceUpdated = adminwalletService.updateBalance(adminId, amount, false);
        if (!balanceUpdated) {
            return false;
        }
        

        AdminTransaction transaction = new AdminTransaction(adminId, amount, "WITHDRAWAL", description);
        return addTransaction(transaction);
    }

    @Override
    public boolean canWithdraw(int adminId, BigDecimal amount) throws SQLException {
        LocalDate today = LocalDate.now();
        BigDecimal totalWithdrawalsToday = admintransactionDAO.getTotalWithdrawalForDay(adminId, today);
        
        if (totalWithdrawalsToday.add(amount).compareTo(DAILY_WITHDRAWAL_LIMIT) > 0) {
            return false;
        }
        
     
        return true;
    }
}
