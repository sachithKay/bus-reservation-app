package com.bus.reservation.server.infrastructure;

import com.bus.reservation.common.exception.InvalidRequestException;
import com.bus.reservation.common.exception.PriceMismatchException;
import com.bus.reservation.common.exception.SeatConflictException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionFilterTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain chain;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private GlobalExceptionFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new GlobalExceptionFilter(objectMapper);
    }

    @Test
    public void testFilterHandlesSeatConflictException() throws Exception {
        doThrow(new SeatConflictException("Taken")).when(chain).doFilter(any(), any());

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        assertTrue(stringWriter.toString().contains("Taken"));
        assertTrue(stringWriter.toString().contains("409"));
    }

    @Test
    public void testFilterHandlesInvalidRequestException() throws Exception {
        doThrow(new InvalidRequestException("Invalid data")).when(chain).doFilter(any(), any());

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("Invalid data"));
        assertTrue(stringWriter.toString().contains("400"));
    }

    @Test
    public void testFilterHandlesPriceMismatchException() throws Exception {
        doThrow(new PriceMismatchException("Price changed")).when(chain).doFilter(any(), any());

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_PAYMENT_REQUIRED);
        assertTrue(stringWriter.toString().contains("Price changed"));
        assertTrue(stringWriter.toString().contains("402"));
    }

    @Test
    public void testFilterHandlesGenericIllegalArgumentException() throws Exception {
        doThrow(new IllegalArgumentException("System error")).when(chain).doFilter(any(), any());

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(stringWriter.toString().contains("System error"));
    }

    @Test
    public void testFilterHandlesUnknownException() throws Exception {
        doThrow(new RuntimeException("Fatal")).when(chain).doFilter(any(), any());

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));

        filter.doFilter(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        assertTrue(stringWriter.toString().contains("An unexpected error occurred"));
    }
}
