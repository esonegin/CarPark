package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author onegines
 * @date 16.01.2025
 */
@Service
public class OpenRouteServiceClient {

    private final RestTemplate restTemplate;

    @Value("${openrouteservice.api.url}")
    private String apiUrl;

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    public OpenRouteServiceClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public String getRouteGeoJsonForCar(Long carId) {
        // Здесь должен быть вызов к базе данных или другому сервису для получения координат маршрута
        List<List<Double>> coordinates = getCoordinatesForCar(carId);

        if (coordinates == null || coordinates.isEmpty()) {
            throw new RuntimeException("No coordinates found for carId: " + carId);
        }

        // Формирование запроса к OpenRouteService
        Map<String, Object> requestBody = Map.of(
                "coordinates", coordinates,
                "format", "geojson"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            Map<?, ?> responseBody = response.getBody();

            if (responseBody == null || !responseBody.containsKey("features")) {
                throw new RuntimeException("Response does not contain any features.");
            }

            return new ObjectMapper().writeValueAsString(responseBody);
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            throw new RuntimeException("Error from OpenRouteService: " + ex.getResponseBodyAsString(), ex);
        } catch (Exception ex) {
            throw new RuntimeException("Unexpected error: " + ex.getMessage(), ex);
        }
    }

    private List<List<Double>> getCoordinatesForCar(Long carId) {
        // Пример данных: получить координаты для указанного carId
        // В реальной реализации подключиться к базе данных
        return List.of(
                List.of(37.6173, 55.7558), // Москва, пример
                List.of(30.3141, 59.9386)  // Санкт-Петербург, пример
        );
    }
}