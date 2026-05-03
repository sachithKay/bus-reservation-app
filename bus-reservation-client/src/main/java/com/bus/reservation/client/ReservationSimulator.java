package com.bus.reservation.client;

import com.bus.reservation.client.model.SimulationReport;
import com.bus.reservation.client.net.ReservationClient;
import com.bus.reservation.client.service.SimulationService;
import com.bus.reservation.common.model.Location;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * CLI Entry point for the Bus Reservation Simulation.
 * Handles user input and displays final results.
 */
public class ReservationSimulator {
    private static final Logger logger = Logger.getLogger(ReservationSimulator.class.getName());
    private static final String API_URL = "http://localhost:8080/bus-reservation-server/api/v1/tickets";

    public static void main(String[] args) throws InterruptedException {
        // Set up professional logging format
        System.setProperty("java.util.logging.SimpleFormatter.format", "[%1$tF %1$tT] [%4$-7s] %5$s %n");

        int userCount = parseUserCount(args);

        logger.info("Initializing contention simulation for " + userCount + " users...");

        // Wire up the application (Manual DI)
        ReservationClient api = new ReservationClient(API_URL);
        SimulationService simulation = new SimulationService(api);

        // Every user will attempt to book 2 tickets
        // from A -> D
        // if everything goes well, 20 users should succeed and everyone else should
        // fail.
        SimulationReport report = simulation.run(userCount, Location.A, Location.D, 2);

        // Output Final Summary
        printSummary(report);
    }

    private static int parseUserCount(String[] args) {
        if (args.length > 0) {
            return Integer.parseInt(args[0]);
        }
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter number of concurrent users to simulate: ");
            while (!scanner.hasNextInt()) {
                System.out.print("Please enter a valid number: ");
                scanner.next();
            }
            return scanner.nextInt();
        }
    }

    private static void printSummary(SimulationReport report) {
        logger.info("\n--- Simulation Complete ---");
        logger.info(String.format("Total Users:   %d", report.totalUsers()));
        logger.info(String.format("Successes:     %d", report.successCount()));
        logger.info(String.format("Failures:      %d", report.failureCount()));
        logger.info(String.format("Success Rate:  %.2f%%", report.getSuccessRate()));
        logger.info(String.format("Duration:      %dms", report.durationMs()));
        logger.info("--------------------------");
    }
}
