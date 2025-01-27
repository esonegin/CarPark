package ru.onegines.carpark.CarPark.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import ru.onegines.carpark.CarPark.services.RouteGenerationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 23.01.2025
 */
@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteGenerationService routeGenerationService;
    private final RoutePointRepository routePointRepository;
    private final RestTemplate restTemplate;

    public RouteController(RouteGenerationService routeGenerationService, RoutePointRepository routePointRepository, RestTemplate restTemplate) {
        this.routeGenerationService = routeGenerationService;
        this.routePointRepository = routePointRepository;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startRoute(
            @RequestParam Long carId,
            @RequestParam double radius,
            @RequestParam double trackLength,
            @RequestParam int pointStep
    ) {
        routeGenerationService.startRouteGeneration(carId, radius, trackLength, pointStep);
        return ResponseEntity.ok("Маршрутная генерация начата.");
    }


    @GetMapping("/fetch/{carId}")
    public ResponseEntity<?> fetchRoutePoints(@PathVariable Long carId) {
        try {
            // Извлечение точек маршрута из базы данных
            List<RoutePoint> routePoints = routePointRepository.findAllByCarIdOrderByTimestampUtc(carId);
            if (routePoints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Маршрут не найден для данного автомобиля");
            }

            // Формирование координат в формате [[x1, y1], [x2, y2], ...]
            List<double[]> coordinates = routePoints.stream()
                    .map(point -> new double[]{point.getPoint().getX(), point.getPoint().getY()})
                    .collect(Collectors.toList());

            // Возврат координат
            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка получения маршрута: " + e.getMessage());
        }
    }

}