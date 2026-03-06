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
 * Servlet implementation class AddReservationServlet
 */
@WebServlet("/api/reservations/*")  
public class AddReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection con;
    PreparedStatement pst;

    public AddReservationServlet() {
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
        String bookingNoStr = request.getParameter("bookingNo");

        if ("find".equals(action)) {
            if (bookingNoStr == null || bookingNoStr.isEmpty()) {
                JSONObject obj = new JSONObject();
                obj.put("status", "error");
                obj.put("code", 400);
                obj.put("message", "Please enter a booking number!");
                out.print(obj.toString());
                return;
            }

            try {
                int bookingNo = Integer.parseInt(bookingNoStr);
                pst = con.prepareStatement("SELECT * FROM reservation WHERE booking_no = ?");
                pst.setInt(1, bookingNo);
                ResultSet rs = pst.executeQuery();

                if (rs.next()) {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "success");
                    obj.put("code", 200);
                    obj.put("data", new JSONObject()
                        .put("booking_no", rs.getInt("booking_no"))
                        .put("guest_name", rs.getString("guest_name"))
                        .put("address", rs.getString("address"))
                        .put("contact_number", rs.getString("contact_number"))
                        .put("room_type", rs.getString("room_type"))
                        .put("room_number", rs.getString("room_number"))
                        .put("check_in", rs.getDate("check_in").toString())
                        .put("check_out", rs.getDate("check_out").toString())
                    );
                    out.print(obj.toString());
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "error");
                    obj.put("code", 404);
                    obj.put("message", "Reservation not found!");
                    out.print(obj.toString());
                }

            } catch (NumberFormatException e) {
                JSONObject obj = new JSONObject();
                obj.put("status", "error");
                obj.put("code", 400);
                obj.put("message", "Invalid booking number format!");
                out.print(obj.toString());
            } catch (SQLException e) {
                e.printStackTrace();
                JSONObject obj = new JSONObject();
                obj.put("status", "error");
                obj.put("code", 500);
                obj.put("message", "Database error!");
                out.print(obj.toString());
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");  
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        String roomNumber = request.getParameter("roomNumber");
        String guestName = request.getParameter("guestName");
        String address = request.getParameter("address");
        String contactNumber = request.getParameter("contactNumber");
        String roomType = request.getParameter("roomType");
        String checkInStr = request.getParameter("checkIn");
        String checkOutStr = request.getParameter("checkOut");

        // Validate required fields
        if (roomNumber == null || roomNumber.isEmpty() || guestName == null || guestName.isEmpty() ||
            address == null || address.isEmpty() || contactNumber == null || contactNumber.isEmpty() ||
            roomType == null || roomType.isEmpty() || checkInStr == null || checkInStr.isEmpty() ||
            checkOutStr == null || checkOutStr.isEmpty()) {
            JSONObject obj = new JSONObject();
            obj.put("status", "error");
            obj.put("code", 400);
            obj.put("message", "Please fill all fields!");
            out.print(obj.toString());
            return;
        }

        try {
            java.sql.Date checkIn = java.sql.Date.valueOf(checkInStr);
            java.sql.Date checkOut = java.sql.Date.valueOf(checkOutStr);

            if ("add".equals(action)) {
                // INSERT new reservation
                String sql = "INSERT INTO reservation "
                        + "(room_number, guest_name, address, contact_number, room_type, check_in, check_out) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pst.setString(1, roomNumber);
                pst.setString(2, guestName);
                pst.setString(3, address);
                pst.setString(4, contactNumber);
                pst.setString(5, roomType);
                pst.setDate(6, checkIn);
                pst.setDate(7, checkOut);
                
                int rowsAffected = pst.executeUpdate();
                
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = pst.getGeneratedKeys();
                    int generatedBookingNo = 0;
                    if (generatedKeys.next()) {
                        generatedBookingNo = generatedKeys.getInt(1);
                    }
                    JSONObject obj = new JSONObject();
                    obj.put("status", "success");
                    obj.put("code", 201);
                    obj.put("message", "Reservation added successfully!");
                    obj.put("booking_number", generatedBookingNo);
                    out.print(obj.toString());
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "error");
                    obj.put("code", 500);
                    obj.put("message", "Failed to add reservation!");
                    out.print(obj.toString());
                }
                
            } else if ("update".equals(action)) {
                // UPDATE existing reservation
                String bookingNoStr = request.getParameter("bookingNo");
                
                if (bookingNoStr == null || bookingNoStr.isEmpty()) {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "error");
                    obj.put("code", 400);
                    obj.put("message", "Please find a reservation first using the search box!");
                    out.print(obj.toString());
                    return;
                }
                
                int bookingNo = Integer.parseInt(bookingNoStr);
                
                String sql = "UPDATE reservation SET "
                        + "room_number = ?, guest_name = ?, address = ?, contact_number = ?, "
                        + "room_type = ?, check_in = ?, check_out = ? "
                        + "WHERE booking_no = ?";
                pst = con.prepareStatement(sql);
                pst.setString(1, roomNumber);
                pst.setString(2, guestName);
                pst.setString(3, address);
                pst.setString(4, contactNumber);
                pst.setString(5, roomType);
                pst.setDate(6, checkIn);
                pst.setDate(7, checkOut);
                pst.setInt(8, bookingNo);
                
                int rowsAffected = pst.executeUpdate();
                
                if (rowsAffected > 0) {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "success");
                    obj.put("code", 200);
                    obj.put("message", "Reservation updated successfully!");
                    obj.put("booking_number", bookingNo);
                    out.print(obj.toString());
                } else {
                    JSONObject obj = new JSONObject();
                    obj.put("status", "error");
                    obj.put("code", 404);
                    obj.put("message", "Reservation not found or update failed!");
                    out.print(obj.toString());
                }
            } else {
                JSONObject obj = new JSONObject();
                obj.put("status", "error");
                obj.put("code", 400);
                obj.put("message", "Invalid action!");
                out.print(obj.toString());
            }
            
        } catch (IllegalArgumentException e) {
            JSONObject obj = new JSONObject();
            obj.put("status", "error");
            obj.put("code", 400);
            obj.put("message", "Invalid date format! Please use YYYY-MM-DD format.");
            out.print(obj.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject obj = new JSONObject();
            obj.put("status", "error");
            obj.put("code", 500);
            obj.put("message", "Database error: " + e.getMessage());
            out.print(obj.toString());
        }
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