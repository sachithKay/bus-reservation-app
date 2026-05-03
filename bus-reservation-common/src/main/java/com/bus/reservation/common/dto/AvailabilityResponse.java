package com.bus.reservation.common.dto;

import com.bus.reservation.common.model.Location;
import java.math.BigDecimal;

/**
 * Data Transfer Object for availability check responses.
 */
public record AvailabilityResponse(
        Location origin,
        Location destination,
        int passengers,
        BigDecimal price,
        boolean available,
        String quoteId
) {}
