package com.bus.reservation.server.domain;

import com.bus.reservation.common.exception.SeatConflictException;
import com.bus.reservation.common.model.Location;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class BusTest {

    @Test
    public void testForwardReservation() {
        Bus bus = new Bus("TEST-BUS", 10);
        // A -> B (Segment 0)
        List<String> seats = bus.allocateSeats(Location.A, Location.B, 2, false);
        assertNotNull(seats);
        assertEquals(2, seats.size());
        
        // These same seats should be available for B -> D (Segments 1 & 2)
        List<String> reuseSeats = bus.allocateSeats(Location.B, Location.D, 2, false);
        assertNotNull(reuseSeats);
        assertEquals(seats, reuseSeats);
    }

    @Test
    public void testFullBus() {
        Bus bus = new Bus("TEST-BUS", 10);
        // Reserve all 40 seats A -> D
        List<String> seats = bus.allocateSeats(Location.A, Location.D, 40, false);
        assertNotNull(seats);
        assertEquals(40, seats.size());
        
        // Try to reserve one more - should throw exception
        assertThrows(SeatConflictException.class, () -> {
            bus.allocateSeats(Location.A, Location.B, 1, false);
        });
    }

    @Test
    public void testInvalidJourney() {
        Bus bus = new Bus("TEST-BUS", 10);
        // D -> A is invalid (backward)
        assertThrows(IllegalArgumentException.class, () -> {
            bus.allocateSeats(Location.D, Location.A, 1, false);
        });

        // A -> A is invalid (same location)
        assertThrows(IllegalArgumentException.class, () -> {
            bus.allocateSeats(Location.A, Location.A, 1, false);
        });
    }

    @Test
    public void testPartialOverlap() {
        Bus bus = new Bus("TEST-BUS", 10);
        // Reserve A -> C
        List<String> seatsAC = bus.allocateSeats(Location.A, Location.C, 40, false);
        
        // B -> D should fail because A->C overlaps with B->C
        assertThrows(SeatConflictException.class, () -> {
            bus.allocateSeats(Location.B, Location.D, 1, false);
        });

        // C -> D should succeed because it starts where A->C ends
        List<String> seatsCD = bus.allocateSeats(Location.C, Location.D, 40, false);
        assertNotNull(seatsCD);
        assertEquals(40, seatsCD.size());
    }

    @Test
    public void testReturnJourneyIndependent() {
        Bus bus = new Bus("TEST-BUS", 10);
        // Reserve A -> D on forward
        bus.allocateSeats(Location.A, Location.D, 40, false);
        
        // Return journey D -> A should still be empty
        int available = bus.getAvailableCount(Location.D, Location.A, true);
        assertEquals(40, available);
    }
}
