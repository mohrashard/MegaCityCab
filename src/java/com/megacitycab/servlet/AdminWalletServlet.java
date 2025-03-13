package com.megacitycab.servlet;

import com.megacitycab.model.AdminWallet;
import com.megacitycab.service.AdminWalletService;
import com.megacitycab.service.AdminWalletServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.SQLException;

@WebServlet("/admin/wallet")
public class AdminWalletServlet extends HttpServlet {
    private final AdminWalletService adminWalletService = new AdminWalletServiceImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    HttpSession session = request.getSession();
    Integer adminId = (Integer) session.getAttribute("adminId");
    
    if (adminId == null) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print("{\"success\": false, \"message\": \"Unauthorized\"}");
        return;
    }
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        try {

            adminWalletService.createWalletIfNotExists(adminId);
            

            AdminWallet wallet = adminWalletService.getWalletByAdminId(adminId);
            
            if (wallet != null) {
                String jsonResponse = "{" +
                    "\"success\": true," +
                    "\"walletId\": " + wallet.getWalletId() + "," +
                    "\"adminId\": " + wallet.getAdminId() + "," +
                    "\"balance\": \"" + wallet.getBalance() + "\"," +
                    "\"createdAt\": \"" + wallet.getCreatedAt() + "\"," +
                    "\"updatedAt\": \"" + wallet.getUpdatedAt() + "\"" +
                "}";
                
                out.print(jsonResponse);
            } else {
                out.print("{\"success\": false, \"message\": \"Failed to retrieve wallet information.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.print("{\"success\": false, \"message\": \"An error occurred: " + e.getMessage() + "\"}");
        }
    }
    

private int getAdminIdFromSession(HttpSession session) {
        Object adminIdObj = session.getAttribute("adminId");
        if (adminIdObj instanceof Integer) {
            return (Integer) adminIdObj;
        } else {
            throw new RuntimeException("Admin not logged in");
        }
    }
}