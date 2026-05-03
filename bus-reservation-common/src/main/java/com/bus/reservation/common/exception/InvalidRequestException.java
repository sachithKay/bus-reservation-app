package com.bus.reservation.common.exception;

/**
 * Thrown when the request parameters or format are invalid.
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidRequestException extends BusReservationException {
    public InvalidRequestException(String message) {
        super(message, 400);
    }
}
