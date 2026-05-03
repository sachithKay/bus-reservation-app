package com.bus.reservation.common.exception;

/**
 * Base class for all business exceptions in the Bus Reservation System.
 */
public class BusReservationException extends RuntimeException {
    private final int status;

    public BusReservationException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
