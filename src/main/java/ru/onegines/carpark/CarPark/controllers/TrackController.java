package ru.onegines.carpark.CarPark.controllers;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import ru.onegines.carpark.CarPark.services.OpenRouteServiceClient;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Контроллер для работы с маршрутами транспортных средств.
 * Предоставляет REST API для получения маршрутов в формате GeoJSON.
 *
 * @author onegines
 * @date 08.01.2025
 */
@RestController
@RequestMapping("/tracks")
public class TrackController {

    private final OpenRouteServiceClient openRouteServiceClient;

    public TrackController(OpenRouteServiceClient openRouteServiceClient) {
        this.openRouteServiceClient = openRouteServiceClient;
    }

    @GetMapping("/visualization/{carId}")
    public ResponseEntity<?> getTrackVis(@PathVariable Long carId) {
        try {
            // Получение данных маршрута через OpenRouteServiceClient
            String routeGeoJson = openRouteServiceClient.getRouteGeoJsonForCar(carId);
            return ResponseEntity.ok(routeGeoJson);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обработке запроса для carId: " + carId + " - " + ex.getMessage());
        }
    }
}