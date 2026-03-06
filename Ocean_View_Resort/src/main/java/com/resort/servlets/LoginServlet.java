package com.resort.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Hardcoded credentials (replace with database validation in production)
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";
       
    public LoginServlet() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validate credentials
        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            // Create session
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("loggedIn", true);
            
            // Redirect to main page
            response.sendRedirect("main.html");
        } else {
            // Invalid credentials - redirect back to login with error
            response.setContentType("text/html");
            response.getWriter().write(
                "<script>" +
                "alert('Invalid username or password!');" +
                "window.location.href = 'Login.html';" +
                "</script>"
            );
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.sendRedirect("Login.html");
    }
}