package com.bus.reservation.server.service;

import com.bus.reservation.common.dto.AvailabilityResponse;
import com.bus.reservation.common.dto.ReservationRequest;
import com.bus.reservation.common.dto.ReservationResponse;

/**
 * Service interface that defines the primary entry point for
 * bus ticket reservation workflow.
 * 
 */
public interface TicketService {
    AvailabilityResponse checkAvailability(String origin, String destination, int passengers);

    ReservationResponse reserveTicket(ReservationRequest request);
}
