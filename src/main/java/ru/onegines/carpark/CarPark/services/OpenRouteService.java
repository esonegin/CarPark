package ru.onegines.carpark.CarPark.services;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;


/**
 * @author onegines
 * @date 08.01.2025
 */
@Service
public class OpenRouteService {

    private static final String API_URL = "https://api.openrouteservice.org/v2/directions/driving-car";

    @Value("${openrouteservice.api.key}")
    private String API_KEY;

    private final WebClient webClient;

    public OpenRouteService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.openrouteservice.org")
                .defaultHeader("Authorization", API_KEY)
                .build();
    }

    public List<List<Double>> generateRoute(List<Double> start, List<Double> end) {
        // Формируем тело запроса
        Map<String, Object> requestBody = Map.of(
                "coordinates", List.of(start, end),
                "instructions", false
        );

        // Выполняем запрос к API
        Map<String, Object> response = webClient.post()
                .uri("/v2/directions/driving-car")
                .header("Authorization", API_KEY)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .block(); // Для простоты, но в асинхронных приложениях блокировку лучше избегать.

        // Извлекаем координаты маршрута из ответа
        List<Map<String, Object>> routes = (List<Map<String, Object>>) response.get("routes");
        Map<String, Object> firstRoute = routes.get(0); // Первый маршрут
        Map<String, Object> geometry = (Map<String, Object>) firstRoute.get("geometry");
        return (List<List<Double>>) geometry.get("coordinates");
    }
}
