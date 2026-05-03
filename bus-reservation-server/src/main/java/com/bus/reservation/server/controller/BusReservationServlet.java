package com.bus.reservation.server.controller;

import com.bus.reservation.common.dto.AvailabilityResponse;
import com.bus.reservation.common.dto.ReservationRequest;
import com.bus.reservation.common.dto.ReservationResponse;
import com.bus.reservation.server.service.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/api/v1/tickets/*")
public class BusReservationServlet extends HttpServlet {
    private TicketService ticketService;
    private ObjectMapper objectMapper;

    // For testing
    BusReservationServlet(TicketService ticketService, ObjectMapper objectMapper) {
        this.ticketService = ticketService;
        this.objectMapper = objectMapper;
    }

    public BusReservationServlet() {
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        this.ticketService = (TicketService) config.getServletContext().getAttribute("ticketService");
        this.objectMapper = (ObjectMapper) config.getServletContext().getAttribute("objectMapper");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/availability".equals(pathInfo)) {
            String originParam = req.getParameter("origin");
            String destParam = req.getParameter("destination");
            String passParam = req.getParameter("passengers");

            if (originParam == null || destParam == null || passParam == null) {
                throw new IllegalArgumentException("Missing required parameters: origin, destination, passengers");
            }

            int passengers = Integer.parseInt(passParam);
            AvailabilityResponse result = ticketService.checkAvailability(originParam, destParam, passengers);
            sendJsonResponse(resp, result);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/reservations".equals(pathInfo)) {
            ReservationRequest reservationReq = objectMapper.readValue(req.getReader(), ReservationRequest.class);
            ReservationResponse response = ticketService.reserveTicket(reservationReq);
            sendJsonResponse(resp, response);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void sendJsonResponse(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        objectMapper.writeValue(resp.getWriter(), data);
    }
}
