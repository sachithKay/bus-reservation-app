package com.bus.reservation.server.service;

import com.bus.reservation.common.model.Location;
import com.bus.reservation.server.domain.Bus;
import com.bus.reservation.server.repository.BusRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SeatingServiceTest {

    @Mock
    private BusRepository repository;
    private SeatingService seatingService;

    @BeforeEach
    public void setUp() {
        seatingService = new SeatingService(repository);
    }

    @Test
    public void testGetAvailableCount() {
        Bus bus = new Bus("EXPRESS-01", 10);
        when(repository.findByName("EXPRESS-01")).thenReturn(bus);
        
        // Initial state: 40 seats
        int available = seatingService.getAvailableCount(Location.A, Location.D, false);
        assertEquals(40, available);

        // Fill one seat
        seatingService.reserveSeats(Location.A, Location.D, 1, false);
        
        int availableAfter = seatingService.getAvailableCount(Location.A, Location.D, false);
        assertEquals(39, availableAfter);
    }
}
