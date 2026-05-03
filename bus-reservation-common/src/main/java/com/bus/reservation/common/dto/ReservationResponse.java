package com.bus.reservation.common.dto;

import java.math.BigDecimal;
import java.util.List;

public record ReservationResponse(
    String ticketNumber,
    List<String> bookedSeats,
    String origin,
    String destination,
    String departureTime,
    String arrivalTime,
    BigDecimal totalPrice
) {}
