package com.bus.reservation.server.service;

import com.bus.reservation.common.dto.ReservationRequest;
import com.bus.reservation.common.dto.ReservationResponse;
import com.bus.reservation.common.exception.SeatConflictException;
import com.bus.reservation.common.model.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock
    private PricingService pricingService;
    @Mock
    private SeatingService seatingService;

    private TicketService ticketService;

    @BeforeEach
    public void setUp() {
        ticketService = new TicketServiceImpl(seatingService, pricingService);
    }

    @Test
    public void testSuccessfulReservation() {
        when(pricingService.calculatePrice(any(), any(), anyInt())).thenReturn(BigDecimal.valueOf(150));
        when(seatingService.reserveSeats(any(), any(), anyInt(), anyBoolean()))
                .thenReturn(Collections.singletonList("1A"));

        ReservationRequest request = new ReservationRequest(Location.A, Location.D, 1, BigDecimal.valueOf(150));
        ReservationResponse response = ticketService.reserveTicket(request);

        assertNotNull(response);
        assertEquals(1, response.bookedSeats().size());
        assertEquals("1A", response.bookedSeats().get(0));
        verify(seatingService).reserveSeats(any(), any(), anyInt(), anyBoolean());
    }

    @Test
    public void testPriceMismatch() {
        when(pricingService.calculatePrice(any(), any(), anyInt())).thenReturn(BigDecimal.valueOf(200));

        ReservationRequest request = new ReservationRequest(Location.A, Location.D, 1, BigDecimal.valueOf(150));

        assertThrows(RuntimeException.class, () -> {
            ticketService.reserveTicket(request);
        });
    }

    @Test
    public void testSeatingFailure() {
        when(pricingService.calculatePrice(any(), any(), anyInt())).thenReturn(BigDecimal.valueOf(150));
        when(seatingService.reserveSeats(any(), any(), anyInt(), anyBoolean()))
                .thenThrow(new SeatConflictException("Full"));

        ReservationRequest request = new ReservationRequest(Location.A, Location.D, 1, BigDecimal.valueOf(150));

        assertThrows(SeatConflictException.class, () -> {
            ticketService.reserveTicket(request);
        });
    }
}
