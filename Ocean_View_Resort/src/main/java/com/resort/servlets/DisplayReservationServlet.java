package com.resort.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject; // Add JSON library (e.g., org.json) to classpath

/**
 * Servlet implementation class DisplayReservationServlet
 */
@WebServlet("/DisplayReservationServlet")
public class DisplayReservationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    public DisplayReservationServlet() {
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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");
        JSONArray jsonArray = new JSONArray();

        try {
            if ("search".equals(action)) {
                String bookingNo = request.getParameter("bookingNo");
                if (bookingNo == null || bookingNo.isEmpty()) {
                    out.print(jsonArray.toString());
                    return;
                }
                pst = con.prepareStatement("SELECT * FROM reservation WHERE booking_no = ?");
                pst.setString(1, bookingNo);
            } else {
                // Load all
                pst = con.prepareStatement("SELECT * FROM reservation");
            }

            rs = pst.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("booking_no", rs.getInt("booking_no"));
                obj.put("guest_name", rs.getString("guest_name"));
                obj.put("address", rs.getString("address"));
                obj.put("contact_number", rs.getString("contact_number"));
                obj.put("room_type", rs.getString("room_type"));
                obj.put("room_number", rs.getString("room_number"));
                obj.put("check_in", rs.getDate("check_in"));
                obj.put("check_out", rs.getDate("check_out"));
                jsonArray.put(obj);
            }
            out.print(jsonArray.toString());

        } catch (SQLException e) {
            e.printStackTrace();
            out.print("[]");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            String bookingNo = request.getParameter("bookingNo");
            if (bookingNo == null || bookingNo.isEmpty()) {
                out.print("Invalid Booking Number!");
                return;
            }

            try {
                pst = con.prepareStatement("DELETE FROM reservation WHERE booking_no = ?");
                pst.setString(1, bookingNo);
                int rows = pst.executeUpdate();
                if (rows > 0) {
                    out.print("Deleted Successfully!");
                } else {
                    out.print("No Record Found!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                out.print("Error deleting reservation!");
            }
        }
    }
}