package com.bus.reservation.common.exception;

/**
 * Thrown when the user's expected price does not match the server's calculation.
 * Maps to HTTP 402 Payment Required.
 */
public class PriceMismatchException extends BusReservationException {
    public PriceMismatchException(String message) {
        super(message, 402);
    }
}
