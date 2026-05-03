package com.bus.reservation.server.service;

import com.bus.reservation.common.model.Location;
import com.bus.reservation.server.config.AppConfig;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PricingServiceTest {

    private final PricingService pricingService = new PricingService();

    @Test
    public void testCalculatePrice() {
        // A -> B is 1 segment
        BigDecimal price1 = pricingService.calculatePrice(Location.A, Location.B, 1);
        assertEquals(0, BigDecimal.valueOf(AppConfig.PRICE_PER_SEGMENT).compareTo(price1));

        // A -> D is 3 segments
        BigDecimal price3 = pricingService.calculatePrice(Location.A, Location.D, 1);
        assertEquals(0, BigDecimal.valueOf(AppConfig.PRICE_PER_SEGMENT * 3).compareTo(price3));

        // 2 passengers for A -> D
        BigDecimal price3p2 = pricingService.calculatePrice(Location.A, Location.D, 2);
        assertEquals(0, BigDecimal.valueOf(AppConfig.PRICE_PER_SEGMENT * 3 * 2).compareTo(price3p2));
    }
}
