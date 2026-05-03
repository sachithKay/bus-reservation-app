package com.bus.reservation.server.controller;

import com.bus.reservation.common.dto.AvailabilityResponse;
import com.bus.reservation.common.model.Location;
import com.bus.reservation.server.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

@ExtendWith(MockitoExtension.class)
public class BusReservationServletTest {

    @Mock
    private TicketService ticketService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private BusReservationServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @BeforeEach
    public void setUp() {
        servlet = new BusReservationServlet(ticketService, objectMapper);
    }

    @Test
    public void testDoGetAvailability() throws Exception {
        when(request.getPathInfo()).thenReturn("/availability");
        when(request.getParameter("origin")).thenReturn("A");
        when(request.getParameter("destination")).thenReturn("B");
        when(request.getParameter("passengers")).thenReturn("2");

        AvailabilityResponse result = new AvailabilityResponse(
                Location.A,
                Location.B,
                2,
                BigDecimal.valueOf(100),
                true,
                "quote-123");
        when(ticketService.checkAvailability("A", "B", 2)).thenReturn(result);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(writer);

        servlet.doGet(request, response);

        verify(response).setContentType("application/json");
        String output = stringWriter.toString();
        assertTrue(output.contains("\"available\":true"));
    }

    @Test
    public void testDoGetNotFound() throws Exception {
        when(request.getPathInfo()).thenReturn("/wrong-path");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
