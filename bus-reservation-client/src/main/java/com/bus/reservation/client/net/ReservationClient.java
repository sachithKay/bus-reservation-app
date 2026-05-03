package com.bus.reservation.client.net;

import com.bus.reservation.common.dto.AvailabilityResponse;
import com.bus.reservation.common.dto.ReservationRequest;
import com.bus.reservation.common.dto.ReservationResponse;
import com.bus.reservation.common.model.Location;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ReservationClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ReservationClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public AvailabilityResponse checkAvailability(Location origin, Location destination, int passengers)
            throws Exception {
        String url = String.format("%s/availability?origin=%s&destination=%s&passengers=%d",
                baseUrl, origin, destination, passengers);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), AvailabilityResponse.class);
    }

    public ReservationResponse reserveTicket(ReservationRequest requestDTO) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/reservations"))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(requestDTO)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            // throw something ad-hoc here
            throw new RuntimeException("HTTP " + response.statusCode() + ": " + response.body());
        }
        return objectMapper.readValue(response.body(), ReservationResponse.class);
    }
}
