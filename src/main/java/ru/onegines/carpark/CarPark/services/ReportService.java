package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.onegines.carpark.CarPark.dto.ReportDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.DriverRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.*;
import java.time.temporal.ChronoUnit;
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
    private final EnterpriseRepository enterpriseRepository;
    private final DriverRepository driverRepository;
    private final RestTemplate restTemplate;
    private static final String ORS_API_URL = "https://api.openrouteservice.org/v2/directions/driving-car/json";
    @Value("${openrouteservice.api.key}")
    private String apiKey;
    private static final Logger log = LoggerFactory.getLogger(ReportService.class);


    public ReportService(RoutePointRepository routePointRepository, CarRepository carRepository, EnterpriseRepository enterpriseRepository, DriverRepository driverRepository, RestTemplate restTemplate) {
        this.routePointRepository = routePointRepository;
        this.carRepository = carRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.driverRepository = driverRepository;
        this.restTemplate = restTemplate;
    }


    public ReportDTO calculateMileage(UUID carId, UUID id, LocalDateTime start, LocalDateTime end, String period) {
        // 1. Получаем машину и её предприятие
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Машина не найдена: " + carId));
        Enterprise enterprise = car.getEnterprise();
        if (enterprise == null) {
            throw new IllegalArgumentException("Предприятие не найдено для машины: " + carId);
        }

        // 2. Определяем таймзону предприятия
        ZoneId enterpriseZone = ZoneId.of(enterprise.getTimeZone());

        // 3. Приводим start и end в нужную таймзону
        ZonedDateTime startUtc = start.atZone(enterpriseZone).withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endUtc = end.atZone(enterpriseZone).with(LocalTime.MAX).withZoneSameInstant(ZoneOffset.UTC);

        // 4. Получаем точки маршрута
        log.debug("Запрос в БД: carId={}, startUtc={}, endUtc={}", carId, startUtc, endUtc);
        List<RoutePoint> points = routePointRepository.findByCarIdAndTimestampUtcBetween(carId, startUtc, endUtc);

        if (points.isEmpty()) {
            throw new IllegalArgumentException("Нет данных за этот период");
        }

        // 5. Формируем список координат
        List<List<Double>> coordinates = points.stream()
                .sorted(Comparator.comparing(RoutePoint::getTimestampUtc))
                .map(p -> List.of(p.getPoint().getX(), p.getPoint().getY()))  // Долгота, широта
                .collect(Collectors.toList());

        // 6. Получаем пробег через OpenRouteService
        Double totalDistance = getDistanceFromORS(coordinates);

        // 7. Группируем пробег по периоду
        Map<LocalDate, Double> mileageByPeriod = groupByPeriod(points, totalDistance, period, enterpriseZone);

        // 8. Вычисляем общий пробег
        double totalMileage = mileageByPeriod.values().stream().mapToDouble(Double::doubleValue).sum();

        // 9. Формируем DTO
        return new ReportDTO("Пробег за период", period, start, end, mileageByPeriod, totalMileage);
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

    public ReportDTO calculateSalary(UUID enterpriseId, LocalDateTime start, LocalDateTime end, String period) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new IllegalArgumentException("Предприятие не найдено: " + enterpriseId));

        List<Driver> drivers = driverRepository.findByEnterpriseId(enterpriseId);

        if (drivers.isEmpty()) {
            throw new IllegalArgumentException("Водители не найдены для предприятия: " + enterpriseId);
        }

        // Получаем количество месяцев в выбранном диапазоне
        long monthsInPeriod = ChronoUnit.MONTHS.between(
                start.withDayOfMonth(1),
                end.withDayOfMonth(1)
        ) + 1;

        // Распределяем зарплату водителей по месяцам
        Map<LocalDate, Double> salaryByPeriod = new TreeMap<>();
        double totalSalary = 0.0;

        for (Driver driver : drivers) {
            double monthlySalary = driver.getSalary();
            totalSalary += monthlySalary * monthsInPeriod;

            LocalDate current = start.toLocalDate().withDayOfMonth(1);
            for (int i = 0; i < monthsInPeriod; i++) {
                salaryByPeriod.put(current, salaryByPeriod.getOrDefault(current, 0.0) + monthlySalary);
                current = current.plusMonths(1);
            }
        }

        return new ReportDTO("Зарплата водителей", period, start, end, salaryByPeriod, totalSalary);
    }

}

