package com.bus.reservation.client.model;

public record SimulationReport(
        int totalUsers,
        int successCount,
        int failureCount,
        long durationMs) {
    public double getSuccessRate() {
        return totalUsers == 0 ? 0 : (double) successCount / totalUsers * 100;
    }
}
