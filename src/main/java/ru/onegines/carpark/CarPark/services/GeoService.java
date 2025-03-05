package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class GeoService {

    @Value("${openrouteservice.api.key}")
    private String apiKey;
    private static final String GEOCODE_URL = "https://api.openrouteservice.org/geocode/search";
    private static final String REVERSE_GEOCODE_URL = "https://api.openrouteservice.org/geocode/reverse";

    private final RestTemplate restTemplate;

    public GeoService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> getCoordinatesByAddress(String address) {
        String url = UriComponentsBuilder.fromHttpUrl(GEOCODE_URL)
                .queryParam("api_key", apiKey)
                .queryParam("text", address)
                .queryParam("size", 1)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }

    public Map<String, Object> getAddressByCoordinates(double latitude, double longitude) {
        String url = UriComponentsBuilder.fromHttpUrl(REVERSE_GEOCODE_URL)
                .queryParam("api_key", apiKey)
                .queryParam("point.lat", latitude)
                .queryParam("point.lon", longitude)
                .queryParam("size", 1)
                .toUriString();

        return restTemplate.getForObject(url, Map.class);
    }
}
