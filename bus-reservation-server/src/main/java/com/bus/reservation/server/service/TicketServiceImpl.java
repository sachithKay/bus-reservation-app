package com.bus.reservation.server.service;

import com.bus.reservation.common.dto.AvailabilityResponse;
import com.bus.reservation.common.dto.ReservationRequest;
import com.bus.reservation.common.dto.ReservationResponse;
import com.bus.reservation.common.exception.InvalidRequestException;
import com.bus.reservation.common.exception.PriceMismatchException;
import com.bus.reservation.common.model.Location;
import com.bus.reservation.server.config.AppConfig;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Implem of {@link TicketService} that coordinates multiple domain
 * services to handle reservation requests.
 */
public class TicketServiceImpl implements TicketService {
    private static final Logger logger = Logger.getLogger(TicketServiceImpl.class.getName());

    private final SeatingService seatingService;
    private final PricingService pricingService;
    private final AtomicLong ticketCounter = new AtomicLong(1000);

    public TicketServiceImpl(SeatingService seatingService, PricingService pricingService) {
        this.seatingService = seatingService;
        this.pricingService = pricingService;
    }

    @Override
    public AvailabilityResponse checkAvailability(String originParam, String destParam, int passengers) {
        if (passengers <= 0) {
            throw new InvalidRequestException("Passenger count must be positive");
        }

        Location origin = Location.valueOf(originParam);
        Location destination = Location.valueOf(destParam);

        boolean isReturn = isReturnJourney(origin, destination);
        int availableCount = seatingService.getAvailableCount(origin, destination, isReturn);
        BigDecimal price = pricingService.calculatePrice(origin, destination, passengers);

        AvailabilityResponse response = new AvailabilityResponse(
                origin,
                destination,
                passengers,
                price,
                availableCount >= passengers,
                UUID.randomUUID().toString());

        logger.info(String.format("Availability check: %s -> %s for %d pax. Price: %s, Available: %b",
                origin, destination, passengers, price, response.available()));

        return response;
    }

    @Override
    public ReservationResponse reserveTicket(ReservationRequest request) {
        Location origin = request.origin();
        Location destination = request.destination();
        int passengers = request.passengers();

        boolean isReturn = isReturnJourney(origin, destination);
        BigDecimal expectedPrice = pricingService.calculatePrice(origin, destination, passengers);

        if (request.paymentAmount().compareTo(expectedPrice) != 0) {
            throw new PriceMismatchException("Incorrect payment amount. Expected: " + expectedPrice);
        }

        List<String> seats = seatingService.reserveSeats(origin, destination, passengers, isReturn);

        ReservationResponse response = new ReservationResponse(
                AppConfig.TICK_PREFIX + ticketCounter.getAndIncrement(),
                seats,
                origin.name(),
                destination.name(),
                "10:00 AM",
                "02:00 PM",
                expectedPrice);

        logger.info(String.format("Reservation SUCCESS: %s, Seats: %s", response.ticketNumber(), seats));
        return response;
    }

    /**
     * Return true if journey direction is D -> A
     * else false
     * 
     * @param origin
     * @param destination
     * @return boolean
     */
    private boolean isReturnJourney(Location origin, Location destination) {
        return origin.ordinal() > destination.ordinal();
    }
}
