package com.bus.reservation.server.service;

import com.bus.reservation.common.model.Location;
import com.bus.reservation.server.domain.Bus;
import com.bus.reservation.server.repository.BusRepository;
import java.util.List;

/**
 * Domain Service for managing seat allocation logic across
 * different bus journeys.
 */
public class SeatingService {
    private final BusRepository busRepository;
    private static final String DEFAULT_BUS = "EXPRESS-01";

    public SeatingService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    public List<String> reserveSeats(Location origin, Location destination, int count, boolean isReturn) {
        Bus bus = busRepository.findByName(DEFAULT_BUS);
        if (bus == null) {
            throw new IllegalStateException("Bus not found");
        }
        return bus.allocateSeats(origin, destination, count, isReturn);
    }

    public int getAvailableCount(Location origin, Location destination, boolean isReturn) {
        Bus bus = busRepository.findByName(DEFAULT_BUS);
        if (bus == null) {
            return 0;
        }
        return bus.getAvailableCount(origin, destination, isReturn);
    }
}
