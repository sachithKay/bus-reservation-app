package com.bus.reservation.server.repository;

import com.bus.reservation.server.domain.Bus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

public class BusRepositoryTest {

    private BusRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new BusRepository();
    }

    @Test
    public void testSaveAndFind() {
        Bus bus = new Bus("BUS-1", 10);
        repository.save(bus);

        Bus found = repository.findByName("BUS-1");
        assertNotNull(found);
        assertEquals("BUS-1", found.getName());
    }

    @Test
    public void testFindAll() {
        repository.save(new Bus("BUS-1", 10));
        repository.save(new Bus("BUS-2", 10));

        Collection<Bus> all = repository.findAll();
        assertEquals(2, all.size());
    }

    @Test
    public void testFindNonExistent() {
        Bus found = repository.findByName("GHOST-BUS");
        assertNull(found);
    }
}
