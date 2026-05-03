package com.bus.reservation.client.service;

import com.bus.reservation.client.model.SimulationReport;
import com.bus.reservation.client.net.ReservationClient;
import com.bus.reservation.common.dto.AvailabilityResponse;
import com.bus.reservation.common.dto.ReservationRequest;
import com.bus.reservation.common.dto.ReservationResponse;
import com.bus.reservation.common.model.Location;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Handles concurrent user simulations and result aggregation.
 */
public class SimulationService {
    private static final Logger logger = Logger.getLogger(SimulationService.class.getName());

    private final ReservationClient api;
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);

    public SimulationService(ReservationClient api) {
        this.api = api;
    }

    public SimulationReport run(int userCount, Location origin, Location destination, int passengers)
            throws InterruptedException {
        // gonna set a reasonable max pool size here to avoid possible memory issues
        int poolSize = Math.min(userCount, 50);
        ExecutorService executor = Executors.newFixedThreadPool(poolSize);

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < userCount; i++) {
            final int userId = i + 1;
            executor.submit(() -> simulateUser(userId, origin, destination, passengers));
        }

        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.MINUTES)) {
            logger.severe("Simulation timed out!");
            executor.shutdownNow();
        }

        long duration = System.currentTimeMillis() - startTime;
        return new SimulationReport(userCount, successCount.get(), failureCount.get(), duration);
    }

    private void simulateUser(int userId, Location origin, Location destination, int passengers) {
        try {
            // All users attempt to book the SAME route simultaneously to test race
            // conditions
            // 1. Availability Check
            AvailabilityResponse availabilityResponse = api.checkAvailability(origin, destination, passengers);
            if (!availabilityResponse.available()) {
                logger.info(String.format("User %d: No seats for %s -> %s", userId, origin, destination));
                failureCount.incrementAndGet();
                return;
            }

            // 3. Reserve
            BigDecimal price = availabilityResponse.price();
            ReservationRequest request = new ReservationRequest(origin, destination, passengers, price);
            ReservationResponse reservationResponse = api.reserveTicket(request);

            logger.info(String.format("User %d SUCCESS: %s -> %s | Ticket %s | Seats %s",
                    userId, origin, destination, reservationResponse.ticketNumber(),
                    reservationResponse.bookedSeats()));
            successCount.incrementAndGet();

        } catch (Exception e) {
            logger.warning(String.format("User %d FAILED: %s", userId, e.getMessage()));
            failureCount.incrementAndGet();
        }
    }
}
