package com.bus.reservation.common.dto;

import com.bus.reservation.common.model.Location;
import java.math.BigDecimal;

public record ReservationRequest(
    Location origin,
    Location destination,
    int passengers,
    BigDecimal paymentAmount
) {}
