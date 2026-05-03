package com.bus.reservation.server.infrastructure;

import com.bus.reservation.common.exception.BusReservationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Exception handler for all API requests.
 * Acts similarly to @RestControllerAdvice in Spring Boot.
 */
@WebFilter("/api/*")
public class GlobalExceptionFilter implements Filter {
    private static final Logger logger = Logger.getLogger(GlobalExceptionFilter.class.getName());
    private ObjectMapper objectMapper;

    // For testing
    GlobalExceptionFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public GlobalExceptionFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.objectMapper = (ObjectMapper) filterConfig.getServletContext().getAttribute("objectMapper");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            handleException((HttpServletResponse) response, e);
        }
    }

    private void handleException(HttpServletResponse response, Exception e) throws IOException {
        int status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = "An unexpected error occurred";

        // looks like my domain exceptions are getting wrapped.
        // check and verify
        // Unwrap ServletException
        Throwable cause = e instanceof ServletException ? e.getCause() : e;

        if (cause instanceof BusReservationException) {
            BusReservationException be = (BusReservationException) cause;
            status = be.getStatus();
            message = be.getMessage();
            logger.info(String.format("Reservation FAILED: %s", message));
        } else if (cause instanceof IllegalArgumentException) {
            // Currently any number format issues or invalid location errors will fall back
            // here.
            // ideally, we should validate abd sanitize inputs and throw
            // InvalidRequestException
            status = HttpServletResponse.SC_BAD_REQUEST;
            message = cause.getMessage();
        } else {
            logger.log(Level.SEVERE, "Unhandled exception caught by Filter", e);
        }

        response.setStatus(status);
        response.setContentType("application/json");

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        errorResponse.put("status", String.valueOf(status));

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    @Override
    public void destroy() {
    }
}
