package com.bus.reservation.server.service;

import com.bus.reservation.common.model.Location;
import com.bus.reservation.server.config.AppConfig;

import java.math.BigDecimal;

import java.math.RoundingMode;

/**
 * Domain Service for calculation of ticket prices.
 */
public class PricingService {
    public BigDecimal calculatePrice(Location origin, Location destination, int passengers) {
        int segments = Math.abs(origin.ordinal() - destination.ordinal());
        int total = segments * AppConfig.PRICE_PER_SEGMENT * passengers;
        
        return BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);
    }
}
