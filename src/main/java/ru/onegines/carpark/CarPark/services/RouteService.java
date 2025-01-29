package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Route;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 28.01.2025
 */
@Service
public class RouteService {

    private final CarRepository carRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final RouteRepository routeRepository;
    private final RoutePointRepository routePointRepository;
    private final OpenRouteService openRouteService;

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    public RouteService(CarRepository carRepository, EnterpriseRepository enterpriseRepository, RouteRepository routeRepository, RoutePointRepository routePointRepository, OpenRouteService openRouteService) {
        this.carRepository = carRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
        this.openRouteService = openRouteService;
    }

    //Конвертация дат фильтра в тайм-зону предприятия
    public HashMap<String, ZonedDateTime> getZoneStartAnEndTime(Long carId, String start, String end) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Автомобиль с ID " + carId + " не найден"));

        Enterprise enterprise = enterpriseRepository.findById(car.getEnterprise().getId())
                .orElseThrow(() -> new IllegalArgumentException("Предприятие с ID " + car.getEnterprise().getId() + " не найдено"));

        String enterpriseTimeZone = enterprise.getTimeZone();
        ZoneId zoneId = (enterpriseTimeZone != null) ? ZoneId.of(enterpriseTimeZone) : ZoneId.of("UTC");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        ZonedDateTime zonedStartTime = LocalDateTime.parse(start, formatter).atZone(zoneId);
        ZonedDateTime zonedEndTime = LocalDateTime.parse(end, formatter).atZone(zoneId);

        HashMap<String, ZonedDateTime> zoneTime = new HashMap<>();
        zoneTime.put("start", zonedStartTime);
        zoneTime.put("end", zonedEndTime);
        return zoneTime;
    }

    //Получение координат маршрутов
    public List<double[]> getPointsForDataFilter(Long carId, String start, String end) {
        //Фильтрация маршрутов
        HashMap<String, ZonedDateTime> formatedDates = getZoneStartAnEndTime(carId, start, end);
        List<Route> routes = routeRepository.findAllByCarIdAndStartTimeUtcGreaterThanEqualAndEndTimeUtcLessThanEqual(
                carId, formatedDates.get("start"), formatedDates.get("end"));
        if (routes.isEmpty()) {
            throw new IllegalArgumentException("Нет маршрутов в заданном диапазоне.");
        }
        List<Long> routesId = routes.stream()
                .map(Route::getId)
                .collect(Collectors.toList());
        List<RoutePoint> routePoints = routePointRepository.findAllByRouteIdInOrderByTimestampUtc(routesId);

        return routePoints.stream()
                .map(point -> new double[]{point.getPoint().getX(), point.getPoint().getY()})
                .collect(Collectors.toList());
    }

    //Запрос к OpenRouteService
    public Map<String, Object> requestFromOpenRouteService(Long carId, String start, String end) {
        // Получаем список координат для фильтрации
        List<double[]> coordinates = getPointsForDataFilter(carId, start, end);
        String orsResponse = openRouteService.fetchRoute(apiKey, coordinates);
        /*// Создаем объект маршрута (Route)
        HashMap<String, ZonedDateTime> formatedDates = getZoneStartAnEndTime(carId, start, end);
        Route route = new Route();
        route.setCarId(carId);
        route.setStartTimeUtc(formatedDates.get("start"));
        route.setEndTimeUtc(formatedDates.get("end"));
        // Сохраняем маршрут в базе данных
        routeRepository.save(route);*/
        // Отправляем ответ от OpenRouteService
        Map<String, Object> response = new HashMap<>();
        response.put("orsResponse", orsResponse);
        return response;
    }
}

