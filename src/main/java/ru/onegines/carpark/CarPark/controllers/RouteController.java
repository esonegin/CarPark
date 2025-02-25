package ru.onegines.carpark.CarPark.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;
import ru.onegines.carpark.CarPark.services.OpenRouteService;
import ru.onegines.carpark.CarPark.services.RouteGenerationService;
import ru.onegines.carpark.CarPark.services.RouteService;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RouteController(RouteGenerationService routeGenerationService, RoutePointRepository routePointRepository, RouteRepository routeRepository, CarRepository carRepository, EnterpriseRepository enterpriseRepository, RouteService routeService, OpenRouteService openRouteService) {
        this.routeGenerationService = routeGenerationService;
        this.routePointRepository = routePointRepository;
        this.routeRepository = routeRepository;
        this.routeService = routeService;
        this.openRouteService = openRouteService;
    }

    @PostMapping("/mass-generate")
    public ResponseEntity<?> generateRoutes(
            @RequestParam UUID carId,
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
            @RequestParam UUID carId,
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
            @RequestParam("carId") UUID carId, // Указываем имя параметра
            @RequestParam("start") String start, // Указываем имя параметра
            @RequestParam("end") String end // Указываем имя параметра
    ) {
        try {
            // Получаем данные от OpenRouteService
            Map<String, Object> orsResponse = routeService.requestFromOpenRouteService(carId, start, end);

            // Проверяем, есть ли данные в ответе
            if (orsResponse.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Маршруты не найдены в заданном диапазоне.");
            }

            // Возвращаем ответ
            return ResponseEntity.ok(orsResponse.get("orsResponse"));
        } catch (IllegalArgumentException e) {
            // Обработка ошибки "Нет маршрутов в заданном диапазоне"
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (Exception e) {
            // Обработка других ошибок
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    @GetMapping("/api")
    public ResponseEntity<?> getTrips(
            @RequestParam UUID carId,
            @RequestParam String start,
            @RequestParam String end
    ) {
        try {
            List<RouteDTO> trips = routeService.getTrips(carId, start, end);

            ///Сейчас выдает начальные и конечные точки совокупности поездок, нужно по каждой поездке отдельно
            return ResponseEntity.ok(trips);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Некорректный формат даты. Используйте формат: yyyy-MM-dd'T'HH:mm");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка обработки маршрута: " + e.getMessage());
        }
    }
}