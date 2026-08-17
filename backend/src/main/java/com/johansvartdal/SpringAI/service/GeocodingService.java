package com.johansvartdal.SpringAI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GeocodingService {

    private final WebClient webClient;

    public GeocodingService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://nominatim.openstreetmap.org").build();
    }

    public Mono<GeocodeResponse> geocodeAddress(String address) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", address)
                        .queryParam("format", "json")
                        .queryParam("addressdetails", "1")
                        .build())
                .retrieve()
                .bodyToMono(GeocodeResponse[].class)
                .map(responses -> responses.length > 0 ? responses[0] : null);
    }

    public static class GeocodeResponse {
        private String lat;
        private String lon;

        // getters and setters
        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }
    }
}
