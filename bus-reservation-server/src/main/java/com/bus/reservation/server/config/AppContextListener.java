package com.bus.reservation.server.config;

import com.bus.reservation.server.domain.Bus;
import com.bus.reservation.server.repository.BusRepository;
import com.bus.reservation.server.service.PricingService;
import com.bus.reservation.server.service.SeatingService;
import com.bus.reservation.server.service.TicketService;
import com.bus.reservation.server.service.TicketServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        AppConfig.initLogging();

        // 0. Infrastructure
        ObjectMapper objectMapper = new ObjectMapper();
        sce.getServletContext().setAttribute("objectMapper", objectMapper);

        // 1. Domain/Repository Layer
        BusRepository busRepository = new BusRepository();
        // add a default bus. this should have 40 seats
        busRepository.save(new Bus("EXPRESS-01", 10));

        // 2. Service Layer
        PricingService pricingService = new PricingService();
        SeatingService seatingService = new SeatingService(busRepository);

        // 3. Application Service Layer (Orchestration)
        TicketService ticketService = new TicketServiceImpl(seatingService, pricingService);

        // Store in context for servlets to access
        sce.getServletContext().setAttribute("ticketService", ticketService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
