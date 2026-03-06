package com.resort.servlets;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AddReservationServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private AddReservationServlet servlet;
    private StringWriter writer;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        when(response.getWriter()).thenReturn(printWriter);
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
        when(response.getContentType()).thenReturn("application/json");
        servlet = new AddReservationServlet();
    }

    @Test
    public void testDoPost_AddReservation_Success() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("roomNumber")).thenReturn("101");
        when(request.getParameter("guestName")).thenReturn("John Doe");
        when(request.getParameter("address")).thenReturn("123 Main St");
        when(request.getParameter("contactNumber")).thenReturn("555-0199");
        when(request.getParameter("roomType")).thenReturn("Single");
        when(request.getParameter("checkIn")).thenReturn("2023-12-01");
        when(request.getParameter("checkOut")).thenReturn("2023-12-05");

        servlet.doPost(request, response);

        String output = writer.toString();
        assertTrue(output.contains("status"));
        assertTrue(output.contains("success"));
        assertTrue(output.contains("Reservation added successfully!"));
    }

    @Test
    public void testDoPost_AddReservation_Fail_MissingFields() throws Exception {
        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("roomNumber")).thenReturn("");
        when(request.getParameter("guestName")).thenReturn("John");

        servlet.doPost(request, response);

        String output = writer.toString();
        assertTrue(output.contains("error"));
        assertTrue(output.contains("Please fill all fields!"));
    }
}