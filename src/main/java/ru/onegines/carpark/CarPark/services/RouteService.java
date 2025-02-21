package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Route;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

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

        // Преобразуем строки в ZonedDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(zoneId);
        ZonedDateTime zonedStartTime = ZonedDateTime.parse(start, formatter);
        ZonedDateTime zonedEndTime = ZonedDateTime.parse(end, formatter);

        HashMap<String, ZonedDateTime> zoneTime = new HashMap<>();
        zoneTime.put("start", zonedStartTime);
        zoneTime.put("end", zonedEndTime);
        return zoneTime;
    }

    //Получение координат маршрутов
    public List<double[]> getPointsForDataFilter(Long carId, String start, String end) {
        // Фильтрация маршрутов
        HashMap<String, ZonedDateTime> formattedDates = getZoneStartAnEndTime(carId, start, end);
        List<Route> routes = routeRepository.findAllByCarIdAndStartTimeUtcGreaterThanEqualAndEndTimeUtcLessThanEqual(
                carId, formattedDates.get("start"), formattedDates.get("end"));

        // Логирование для отладки
        System.out.println("Диапазон дат: " + formattedDates.get("start") + " - " + formattedDates.get("end"));
        System.out.println("Найденные маршруты: " + routes);
        // Выводим только id маршрутов
        List<Long> routeIds = routes.stream()
                .map(Route::getId)  // Преобразуем каждый Route в его id
                .collect(Collectors.toList());  // Собираем в список
        System.out.println("Найденные маршруты (ID): " + routeIds);

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


    public List<RouteDTO> getTrips(Long carId, String start, String end) {
        // Получаем отформатированные даты
        HashMap<String, ZonedDateTime> formattedDates = getZoneStartAnEndTime(carId, start, end);

        // Получаем список маршрутов в заданном диапазоне
        List<Route> routes = routeRepository.findByCarIdAndStartTimeUtcBetween(
                carId, formattedDates.get("start"), formattedDates.get("end")
        );

        // Преобразуем маршруты в RouteDTO
        return routes.stream()
                .map(route -> {
                    try {
                        // Извлекаем названия улиц для начальной и конечной точек
                        String startAddress = findFirstValidAddressByRouteId(route.getId());
                        String endAddress = findLastValidAddressByRouteId(route.getId());

                        // Создаем RouteDTO
                        return new RouteDTO(
                                route.getId(),
                                startAddress,  // Получаем начальный адрес
                                endAddress,    // Получаем конечный адрес
                                route.getStartTimeUtc(),
                                route.getEndTimeUtc());
                    } catch (Exception e) {
                        // Логируем ошибки
                        logger.error("Ошибка при обработке маршрута ID {}: {}", route.getId(), e.getMessage());
                        throw new RuntimeException("Ошибка при обработке маршрута", e);
                    }
                })
                .toList();
    }


    private String findFirstValidAddressByRouteId(Long routeId) {
        // Находим первую точку маршрута с валидным адресом
        Optional<RoutePoint> firstValidPoint = routePointRepository.findFirstByRouteIdAndAddressNotInOrderByTimestampUtcAsc(
                routeId,
                List.of("Адрес не найден", "-")
        );

        // Возвращаем адрес или дефолтное значение
        return firstValidPoint.map(RoutePoint::getAddress).orElse("Адрес не определен");
    }

    private String findLastValidAddressByRouteId(Long routeId) {
        // Находим последнюю точку маршрута с валидным адресом
        Optional<RoutePoint> lastValidPoint = routePointRepository.findFirstByRouteIdAndAddressNotInOrderByTimestampUtcDesc(
                routeId,
                List.of("Адрес не найден", "-")
        );

        // Возвращаем адрес или дефолтное значение
        return lastValidPoint.map(RoutePoint::getAddress).orElse("Адрес не определен");
    }

    private HashMap<String, String> extractStreetName(Map<String, Object> orsResponse) {
        HashMap<String, String> startAndEnd = new HashMap<>();
        try {
            // Предполагаем, что orsResponse содержит JSON-ответ от OpenRouteService в виде строки
            String orsResponseString = (String) orsResponse.get("orsResponse");

            // Преобразуем строку в JsonNode
            JsonNode rootNode = objectMapper.readTree(orsResponseString);
            JsonNode routesNode = rootNode.path("routes");
            ArrayList streets = new ArrayList<>();
            if (routesNode.isArray() && routesNode.size() > 0) {
                JsonNode firstRoute = routesNode.get(0);
                JsonNode segmentsNode = firstRoute.path("segments");

                for (JsonNode segment : segmentsNode) {
                    if (!segment.get("steps").get(0).get("name").asText().equals("-")) {
                        streets.add(segment.get("steps").get(0).get("name"));
                    }
                }
                startAndEnd.put("start_point", streets.get(0).toString());
                startAndEnd.put("end_point", streets.get(streets.size() - 1).toString());
                return startAndEnd;
            }
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Map<String, Object> requestFromOpenRouteService(Long carId, ZonedDateTime start, ZonedDateTime end) {
        try {
            // Находим первую и последнюю точки маршрута
            Optional<RoutePoint> startPointOptional = routePointRepository.findFirstByCarIdAndTimestampUtcBetweenOrderByTimestampUtcAsc(carId, start, end);
            Optional<RoutePoint> endPointOptional = routePointRepository.findLastByCarIdAndTimestampUtcBetweenOrderByTimestampUtcDesc(carId, start, end);

            if (startPointOptional.isEmpty() || endPointOptional.isEmpty()) {
                throw new IllegalArgumentException("Точки маршрута не найдены.");
            }

            RoutePoint startPoint = startPointOptional.get();
            RoutePoint endPoint = endPointOptional.get();

            // Формируем URL для OpenRouteService API
            String openRouteServiceUrl = UriComponentsBuilder.fromHttpUrl("https://api.openrouteservice.org/v2/directions/driving-car")
                    .queryParam("api_key", "YOUR_API_KEY") // Замените YOUR_API_KEY на ваш ключ
                    .queryParam("start", startPoint.getPoint().getX() + "," + startPoint.getPoint().getY())
                    .queryParam("end", endPoint.getPoint().getX() + "," + endPoint.getPoint().getY())
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(openRouteServiceUrl, String.class);

            // Разбор JSON-ответа
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.getBody());

            Map<String, Object> result = new HashMap<>();
            result.put("features", rootNode.path("features")); // Сохраняем массив маршрутов
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при запросе к OpenRouteService: " + e.getMessage(), e);
        }
    }
}

