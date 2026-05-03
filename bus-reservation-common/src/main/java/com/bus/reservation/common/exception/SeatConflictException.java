package com.bus.reservation.common.exception;

/**
 * Thrown when requested seats are no longer available.
 * Maps to HTTP 409 Conflict.
 */
public class SeatConflictException extends BusReservationException {
    public SeatConflictException(String message) {
        super(message, 409);
    }
}
