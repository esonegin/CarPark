package ru.onegines.carpark.CarPark.controllers;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.models.Route;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;
import ru.onegines.carpark.CarPark.services.OpenRouteService;
import ru.onegines.carpark.CarPark.services.RouteGenerationService;
import ru.onegines.carpark.CarPark.services.RouteService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final RouteRepository routeRepository;
    private final RouteService routeService;
    private final OpenRouteService openRouteService;

    public RouteController(RouteGenerationService routeGenerationService, RoutePointRepository routePointRepository, RouteRepository routeRepository, CarRepository carRepository, EnterpriseRepository enterpriseRepository, RouteService routeService, OpenRouteService openRouteService) {
        this.routeGenerationService = routeGenerationService;
        this.routePointRepository = routePointRepository;
        this.routeRepository = routeRepository;
        this.routeService = routeService;
        this.openRouteService = openRouteService;
    }

    @PostMapping("/mass-generate")
    public ResponseEntity<?> generateRoutes(
            @RequestParam Long carId,
            @RequestParam int numRoutes,
            @RequestParam double maxRadius,
            @RequestParam double maxTrackLength,
            @RequestParam int maxStep
    ) {
        try {
            routeGenerationService.generateMonthlyRoutes(carId, numRoutes, maxRadius, maxTrackLength, maxStep);
            return ResponseEntity.ok("Массовая генерация маршрутов завершена.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка генерации маршрутов: " + e.getMessage());
        }
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

    @PostMapping("/stop")
    public ResponseEntity<?> stopRoute() {
        try {
            boolean stopped = routeGenerationService.stopRouteGeneration();
            if (stopped) {
                return ResponseEntity.ok("Генерация маршрута успешно остановлена.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Нет активной генерации маршрута для остановки.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка остановки маршрута: " + e.getMessage());
        }
    }


   /* @GetMapping("/fetch/{routeId}")
    public ResponseEntity<?> fetchRoutePoints(@RequestParam Long routeId) {
        try {
            // Извлечение точек маршрута из базы данных по routeId
            List<RoutePoint> routePoints = routePointRepository.findAllByRouteIdOrderByTimestampUtc(routeId);
            if (routePoints.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Точки маршрута не найдены для routeId: " + routeId);
            }

            // Формирование координат в формате [[x1, y1], [x2, y2], ...]
            List<double[]> coordinates = routePoints.stream()
                    .map(point -> new double[]{point.getPoint().getX(), point.getPoint().getY()})
                    .collect(Collectors.toList());

            return ResponseEntity.ok(coordinates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка получения точек маршрута: " + e.getMessage());
        }
    }*/

    @GetMapping("/filter")
    public ResponseEntity<?> filterRoutesAndFetchDetails(
            @RequestParam Long carId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        try {
            return ResponseEntity.ok(routeService.requestFromOpenRouteService(carId, start, end).get("orsResponse"));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Некорректный формат даты. Используйте формат: yyyy-MM-dd'T'HH:mm");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка обработки маршрута: " + e.getMessage());
        }
    }

}