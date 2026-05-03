package com.bus.reservation.server.repository;

import com.bus.reservation.server.domain.Bus;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repository component that provides an abstraction over the data source
 * for {@link Bus} entities.
 */
public class BusRepository {
    // data source (in-memory)
    private final Map<String, Bus> buses = new ConcurrentHashMap<>();

    public void save(Bus bus) {
        buses.put(bus.getName(), bus);
    }

    public Bus findByName(String name) {
        return buses.get(name);
    }

    public Collection<Bus> findAll() {
        return buses.values();
    }
}
