package ru.onegines.carpark.CarPark.services;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author onegines
 * @date 28.01.2025
 */
@Service
public class OpenRouteService {

    private final RestTemplate restTemplate;

    public OpenRouteService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String fetchRoute(String apiKey, List<double[]> coordinates) {
        try {
            // Формирование тела запроса
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("coordinates", coordinates);
            requestBody.put("profile", "driving-car");
            requestBody.put("format", "json");

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Выполнение POST-запроса
            String url = "https://api.openrouteservice.org/v2/directions/driving-car";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new RuntimeException("Ошибка при запросе к OpenRouteService: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запросе к OpenRouteService: " + e.getMessage());
        }
    }
/*
    public String fetchRoute(String apiKey, List<double[]> coordinates) {
        try {
            String url = "https://api.openrouteservice.org/v2/directions/driving-car";
            // Пример: Подготовка запроса
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Тело запроса
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("coordinates", coordinates);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            // Отправка запроса
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Ошибка при вызове ORS API: " + e.getMessage());
            return null;
        }
    }*/

}
