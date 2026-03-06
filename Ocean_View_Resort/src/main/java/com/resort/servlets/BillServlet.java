package com.resort.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Servlet implementation class BillServlet
 */
@WebServlet("/api/bills/*")
public class BillServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public BillServlet() {
        super();
    }

    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC",
                    "root",
                    ""
            );
        } catch (Exception e) {
            throw new ServletException("Database Connection Failed!", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        String bookingNo = request.getParameter("bookingNo");
        String roomNo = request.getParameter("roomNo");

        // Validate inputs
        if (bookingNo == null || bookingNo.isEmpty() || roomNo == null || roomNo.isEmpty()) {
            JSONObject obj = new JSONObject();
            obj.put("status", "error");
            obj.put("code", 400);
            obj.put("message", "Please enter Booking No and Room No!");
            out.print(obj.toString());
            return;
        }

        try {
            // Query reservation
            String sql = "SELECT * FROM reservation WHERE booking_no = ? AND room_number = ?";
            pst = con.prepareStatement(sql);
            pst.setInt(1, Integer.parseInt(bookingNo));
            pst.setString(2, roomNo);
            rs = pst.executeQuery();

            if (!rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("status", "error");
                obj.put("code", 404);
                obj.put("message", "Booking not found!");
                out.print(obj.toString());
                return;
            }

            // Get reservation data
            String guestName = rs.getString("guest_name");
            String roomType = rs.getString("room_type");
            Date checkIn = rs.getDate("check_in");
            Date checkOut = rs.getDate("check_out");

            JSONObject obj = new JSONObject();
            obj.put("status", "success");
            obj.put("code", 200);
            obj.put("guest_name", guestName);
            obj.put("room_type", roomType);
            obj.put("check_in", checkIn.toString());
            obj.put("check_out", checkOut.toString());

            // If calculate action, compute bill
            if ("calculate".equals(action)) {
                // Room rates
                int roomRate;
                switch (roomType) {
                    case "Single":
                        roomRate = 5000;
                        break;
                    case "Double":
                        roomRate = 8000;
                        break;
                    case "Deluxe":
                        roomRate = 12000;
                        break;
                    default:
                        roomRate = 0;
                }

                // Calculate nights
                long diffInMillies = checkOut.getTime() - checkIn.getTime();
                long nights = diffInMillies / (1000 * 60 * 60 * 24);
                if (nights <= 0) {
                    nights = 1; // minimum 1 night
                }

                long totalPrice = nights * roomRate;

                obj.put("room_rate", roomRate);
                obj.put("nights", nights);
                obj.put("total_price", totalPrice);
            }

            out.print(obj.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("status", "error");
            obj.put("code", 500);
            obj.put("message", "Database error: " + e.getMessage());
            out.print(obj.toString());
        } catch (NumberFormatException e) {
            JSONObject obj = new JSONObject();
            obj.put("status", "error");
            obj.put("code", 400);
            obj.put("message", "Invalid Booking Number format!");
            out.print(obj.toString());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }

    public void destroy() {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}