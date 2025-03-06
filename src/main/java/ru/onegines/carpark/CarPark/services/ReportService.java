package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.onegines.carpark.CarPark.dto.ReportDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 05.03.2025
 */
@Service
public class ReportService {
    private final RoutePointRepository routePointRepository;
    private final CarRepository carRepository;
    private final RestTemplate restTemplate;
    private static final String ORS_API_URL = "https://api.openrouteservice.org/v2/directions/driving-car/json";
    @Value("${openrouteservice.api.key}")
    private String apiKey;
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);


    public ReportService(RoutePointRepository routePointRepository, CarRepository carRepository, RestTemplate restTemplate) {
        this.routePointRepository = routePointRepository;
        this.carRepository = carRepository;
        this.restTemplate = restTemplate;
    }


    public ReportDTO calculateMileage(UUID carId, LocalDateTime start, LocalDateTime end, String period) {
        // 1. Получаем машину и её предприятие
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Машина не найдена: " + carId));
        Enterprise enterprise = car.getEnterprise();
        if (enterprise == null) {
            throw new IllegalArgumentException("Предприятие не найдено для машины: " + carId);
        }

        // 2. Получаем таймзону предприятия
        ZoneId enterpriseZone = ZoneId.of(enterprise.getTimeZone());  // Например, "Europe/Moscow"

        // 3. Переводим start и end в таймзону предприятия
        ZonedDateTime startEnterpriseTime = start.atZone(enterpriseZone);  // 2024-02-01T00:00:00+03:00
        ZonedDateTime endEnterpriseTime = end.atZone(enterpriseZone).with(LocalTime.MAX); // 2024-02-28T23:59:59+03:00

        // 4. Конвертируем в UTC для запроса в БД
        ZonedDateTime startUtc = startEnterpriseTime.withZoneSameInstant(ZoneOffset.UTC); // 2024-01-31T21:00:00+00:00
        ZonedDateTime endUtc = endEnterpriseTime.withZoneSameInstant(ZoneOffset.UTC);     // 2024-02-28T20:59:59+00:00


        // 5. Запрашиваем точки маршрута с корректным UTC-интервалом
        log.debug("Запрос в БД: carId={}, startUtc={}, endUtc={}", carId, startUtc, endUtc);
        List<RoutePoint> points = routePointRepository.findByCarIdAndTimestampUtcBetween(
                carId,
                startUtc.withZoneSameInstant(ZoneOffset.UTC), // ZonedDateTime в UTC
                endUtc.withZoneSameInstant(ZoneOffset.UTC)
        );

        if (points.isEmpty()) {
            throw new IllegalArgumentException("Нет данных за этот период");
        }

        // 6. Формируем список координат
        List<List<Double>> coordinates = points.stream()
                .sorted(Comparator.comparing(RoutePoint::getTimestampUtc))
                .map(p -> List.of(p.getPoint().getX(), p.getPoint().getY()))  // Долгота, широта
                .collect(Collectors.toList());

        // 7. Запрос в OpenRouteService
        Double totalDistance = getDistanceFromORS(coordinates);

        // 8. Группируем пробег в таймзоне предприятия
        Map<LocalDate, Double> mileageByPeriod = groupByPeriod(points, totalDistance, period, enterpriseZone);

        // 9. Возвращаем результат
        return new ReportDTO("Пробег за период", period, start, end, mileageByPeriod);
    }


    private Map<LocalDate, Double> groupByPeriod(List<RoutePoint> points, Double totalDistance, String period, ZoneId timezone) {
        Map<LocalDate, Double> mileageByPeriod = new HashMap<>();
        double distancePerPoint = totalDistance / (points.size() - 1); // Средний пробег между точками

        for (RoutePoint point : points) {
            LocalDate date = point.getTimestampUtc()
                    .withZoneSameInstant(timezone)
                    .toLocalDate(); // Конвертируем в дату

            mileageByPeriod.put(date, mileageByPeriod.getOrDefault(date, 0.0) + distancePerPoint);
        }

        return mileageByPeriod;
    }


    private Double getDistanceFromORS(List<List<Double>> coordinates) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of("coordinates", coordinates);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            log.debug("Отправка запроса в ORS: {}", body);
            ResponseEntity<Map> response = restTemplate.exchange(ORS_API_URL, HttpMethod.POST, request, Map.class);
            log.debug("Ответ ORS: {}", response.getBody());
            Map<String, Object> summary = (Map<String, Object>) ((List<Map<String, Object>>) response.getBody().get("routes")).get(0).get("summary");

            return ((Number) summary.get("distance")).doubleValue() / 1000; // в км
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при запросе маршрута в OpenRouteService", e);
        }
    }

    private Map<LocalDate, Double> groupByPeriod(List<RoutePoint> points, Double totalDistance, String period) {
        Map<LocalDate, Double> grouped = new TreeMap<>();
        double distancePerPoint = totalDistance / (points.size() - 1); // Средний пробег между точками

        for (int i = 1; i < points.size(); i++) {
            LocalDate dateKey = switch (period) {
                case "day" -> points.get(i).getTimestampUtc().toLocalDate();
                case "month" -> points.get(i).getTimestampUtc().toLocalDate().withDayOfMonth(1);
                default -> throw new IllegalArgumentException("Неподдерживаемый период");
            };
            grouped.put(dateKey, grouped.getOrDefault(dateKey, 0.0) + distancePerPoint);
        }
        return grouped;
    }
}

