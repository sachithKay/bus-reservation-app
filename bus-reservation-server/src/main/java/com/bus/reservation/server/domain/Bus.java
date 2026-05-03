package com.bus.reservation.server.domain;

import com.bus.reservation.common.exception.SeatConflictException;
import com.bus.reservation.common.model.Location;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Bus {
    private final String name;

    // we break seat into 3 segments. A ->B, B -> C, C -> D
    // eg: 111 -> Booked from A -> D, 100 -> Booked from A -> B
    // alternative would have been to use a 3 element bool array. not a fan
    private final Map<String, BitSet> forwardSeats = new HashMap<>();
    private final Map<String, BitSet> returnSeats = new HashMap<>();
    private final List<String> seatLabels = new ArrayList<>();

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Bus(String name, int rows) {
        this.name = name;
        // Assuming 4 seats per row (A, B, C, D)

        for (int i = 1; i <= rows; i++) {
            for (char c : new char[] { 'A', 'B', 'C', 'D' }) {
                // Create "1A", "1B", etc.
                String label = i + String.valueOf(c);
                seatLabels.add(label);
                forwardSeats.put(label, new BitSet(3));
                returnSeats.put(label, new BitSet(3));
            }
        }
    }

    public List<String> allocateSeats(Location origin, Location destination, int count, boolean isReturn) {
        lock.writeLock().lock();
        try {
            Map<String, BitSet> targetGrid = isReturn ? returnSeats : forwardSeats;
            int startSeg = getStartSegment(origin, isReturn);
            int endSeg = getEndSegment(destination, isReturn);

            if (startSeg > endSeg) {
                throw new IllegalArgumentException("Invalid journey: origin must come before destination");
            }

            List<String> assigned = new ArrayList<>();
            for (String label : seatLabels) {
                if (isAvailable(targetGrid.get(label), startSeg, endSeg)) {
                    assigned.add(label);
                    if (assigned.size() == count)
                        break;
                }
            }

            if (assigned.size() < count) {
                throw new SeatConflictException("Requested seats are no longer available");
            }

            for (String label : assigned) {
                markOccupied(targetGrid.get(label), startSeg, endSeg);
            }
            return assigned;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public int getAvailableCount(Location origin, Location destination, boolean isReturn) {
        lock.readLock().lock();
        try {
            Map<String, BitSet> targetGrid = isReturn ? returnSeats : forwardSeats;
            int startSeg = getStartSegment(origin, isReturn);
            int endSeg = getEndSegment(destination, isReturn);

            int available = 0;
            for (String label : seatLabels) {
                if (isAvailable(targetGrid.get(label), startSeg, endSeg)) {
                    available++;
                }
            }
            return available;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean isAvailable(BitSet seat, int startSegment, int endSegment) {
        // A - B trip -> segment 0 should be free
        // A - C trip -> segment 0 AND 1 should be free
        // means for A -> B start AND end == 0
        // means for A -> C start == 0 AND end == 1
        for (int i = startSegment; i <= endSegment; i++) {
            if (seat.get(i))
                return false;
        }
        return true;
    }

    private void markOccupied(BitSet seat, int start, int end) {
        for (int i = start; i <= end; i++) {
            seat.set(i);
        }
    }

    private int getStartSegment(Location origin, boolean isReturn) {
        return isReturn ? (3 - origin.ordinal()) : origin.ordinal();
    }

    private int getEndSegment(Location destination, boolean isReturn) {
        //
        return isReturn ? (3 - destination.ordinal() - 1) : (destination.ordinal() - 1);
    }

    public String getName() {
        return name;
    }
}
