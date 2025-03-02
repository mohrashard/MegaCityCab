package com.megacitycab.servlet;

import com.megacitycab.service.RideService;
import com.megacitycab.service.RideServiceImpl;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet("/driver/cancel-ride")
public class CancelRideServlet extends HttpServlet {

    private final RideService rideService = new RideServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("user");
        String userType = (String) session.getAttribute("userType");


        if (email == null || userType == null || !userType.equals("driver")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        Integer driverId = (Integer) session.getAttribute("userId");

        if (driverId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String bookingIdStr = request.getParameter("bookingId");
        String reason = request.getParameter("reason");

        if (bookingIdStr == null || bookingIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int bookingId = Integer.parseInt(bookingIdStr);
        boolean success = rideService.cancelRide(bookingId, reason);

        JSONObject result = new JSONObject();
        result.put("success", success);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write(result.toString());
        out.flush();
    }
}
