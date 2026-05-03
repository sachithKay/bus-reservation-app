package com.bus.reservation.server.config;

public class AppConfig {
    public static final int PRICE_PER_SEGMENT = 50;
    public static final String TICK_PREFIX = "TICK-";

    public static void initLogging() {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "[%1$tF %1$tT] [%4$-7s] %5$s %n");
    }
}
