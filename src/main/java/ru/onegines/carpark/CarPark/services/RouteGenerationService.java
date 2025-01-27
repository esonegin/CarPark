package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.annotation.PreDestroy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import ru.onegines.carpark.CarPark.models.Route;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import org.locationtech.jts.geom.*;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;


@Service
public class RouteGenerationService {

    private final RoutePointRepository routePointRepository;
    private final RouteRepository routeRepository;

    private double currentDistance = 0;
    private int pointsGenerated = 0;

    private Long carId;
    private double radius;
    private double trackLength;
    private int pointStep;
    private double startLat;
    private double startLon;
    private double endLat;
    private double endLon;
    private Route currentRoute; // Текущий маршрут

    public RouteGenerationService(RoutePointRepository routePointRepository, RouteRepository routeRepository) {
        this.routePointRepository = routePointRepository;
        this.routeRepository = routeRepository;
    }

    @Transactional
    public void startRouteGeneration(Long carId, double radius, double trackLength, int pointStep) {
        this.carId = carId;
        this.radius = radius;
        this.trackLength = trackLength;
        this.pointStep = pointStep;

        // Центральная точка (например, центр города или фиксированная точка)
        double centerLat = 55.75; // Пример: широта Москвы
        double centerLon = 37.61; // Пример: долгота Москвы

        // Генерация начальных и конечных координат
        double[] startCoordinates = generateRandomCoordinates(centerLat, centerLon, radius);
        double[] endCoordinates = generateRandomCoordinates(centerLat, centerLon, radius);

        this.startLat = startCoordinates[0];
        this.startLon = startCoordinates[1];
        this.endLat = endCoordinates[0];
        this.endLon = endCoordinates[1];

        // Создаём новый маршрут
        currentRoute = new Route();
        currentRoute.setCarId(carId);
        currentRoute.setStartTimeUtc(ZonedDateTime.now()); // Устанавливаем текущее время как начало маршрута
        routeRepository.save(currentRoute);

        System.out.println("Создан маршрут: " + currentRoute);
        System.out.println("Начальные координаты: lat=" + startLat + ", lon=" + startLon);
        System.out.println("Конечные координаты: lat=" + endLat + ", lon=" + endLon);

        // Инициализация начальных значений
        this.currentDistance = 0;
        this.pointsGenerated = 0;
    }

    @PreDestroy
    @Transactional
    public void stopRouteGeneration() {
        if (currentRoute != null) {
            currentRoute.setEndTimeUtc(ZonedDateTime.now());
            routeRepository.save(currentRoute);
            System.out.println("Маршрут завершён при остановке приложения: " + currentRoute);
        }
    }

    // Метод для запуска генерации точек маршрута каждые 10 секунд
    @Scheduled(fixedRate = 10000)
    public void generateRoute() {
        if (currentDistance >= trackLength) {
            // Завершаем маршрут
            if (currentRoute != null) {
                // Устанавливаем время завершения маршрута
                currentRoute.setEndTimeUtc(ZonedDateTime.now());
                routeRepository.save(currentRoute); // Сохраняем маршрут с обновленным временем
                System.out.println("Маршрут завершен: " + currentRoute);

                currentRoute = null; // Сбрасываем текущий маршрут
            }
            return;
        }

        String apiKey = "5b3ce3597851110001cf62483be0355aa52f408eae421fd99091b903";
        String openRouteServiceUrl = UriComponentsBuilder.fromHttpUrl("https://api.openrouteservice.org/v2/directions/driving-car")
                .queryParam("api_key", apiKey)
                .queryParam("start", startLon + "," + startLat)
                .queryParam("end", endLon + "," + endLat)
                .toUriString();

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(openRouteServiceUrl, String.class);
            List<Coordinate> routeCoordinates = parseRouteCoordinates(response.getBody());

            // Проверяем, остались ли точки для обработки
            if (pointsGenerated >= routeCoordinates.size()) {
                System.out.println("Все доступные точки маршрута обработаны.");
                return;
            }

            // Берём следующую координату
            Coordinate coordinate = routeCoordinates.get(pointsGenerated);
            double lat = coordinate.getY();
            double lon = coordinate.getX();

            if (calculateDistance(startLat, startLon, lat, lon) <= radius) {
                // Создаём объект Point
                Point point = createPoint(lon, lat);

                // Создаём и сохраняем RoutePoint
                RoutePoint routePoint = new RoutePoint();
                routePoint.setCarId(carId);
                routePoint.setRoute(currentRoute); // Устанавливаем связь с текущим маршрутом
                routePoint.setPoint(point); // Устанавливаем Point
                routePoint.setTimestampUtc(ZonedDateTime.now());

                routePointRepository.save(routePoint);
                System.out.println("RoutePoint сохранен: " + routePoint);

                pointsGenerated++;
                currentDistance += pointStep;

                if (currentDistance >= trackLength) {
                    // Завершаем маршрут после добавления последней точки
                    currentRoute.setEndTimeUtc(ZonedDateTime.now());
                    routeRepository.save(currentRoute); // Сохраняем маршрут
                    System.out.println("Маршрут завершен. Время окончания: " + currentRoute.getEndTimeUtc());
                    currentRoute = null; // Сбрасываем текущий маршрут
                }
            } else {
                System.out.println("Точка за пределами заданного радиуса: " + lat + ", " + lon);
                pointsGenerated++; // Пропускаем точки за радиусом
            }
        } catch (Exception e) {
            System.out.println("Ошибка при генерации маршрута: " + e.getMessage());
        }
    }



    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371; // Радиус Земли в километрах
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // возвращаем расстояние в километрах
    }

    private Point createPoint(double lon, double lat) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(lon, lat);
        return geometryFactory.createPoint(coordinate);
    }

    public List<Coordinate> parseRouteCoordinates(String responseBody) {
        List<Coordinate> coordinates = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            System.out.println("JSON ответ: " + responseBody);

            JsonNode featuresNode = rootNode.path("features");
            if (featuresNode.isArray() && featuresNode.size() > 0) {
                JsonNode geometryNode = featuresNode.get(0).path("geometry");
                if (geometryNode != null && "LineString".equals(geometryNode.path("type").asText())) {
                    ArrayNode coordinatesArray = (ArrayNode) geometryNode.get("coordinates");

                    for (JsonNode coordinate : coordinatesArray) {
                        if (coordinate.isArray() && coordinate.size() >= 2) {
                            double lon = coordinate.get(0).asDouble();
                            double lat = coordinate.get(1).asDouble();
                            coordinates.add(new Coordinate(lon, lat));
                        } else {
                            System.out.println("Неверный формат координаты: " + coordinate);
                        }
                    }
                } else {
                    System.out.println("Узел 'geometry' отсутствует или имеет неверный тип.");
                }
            } else {
                System.out.println("Узел 'features' отсутствует или пуст.");
            }
        } catch (Exception e) {
            System.out.println("Ошибка при разборе координат маршрута: " + e.getMessage());
        }
        return coordinates;
    }

    private double[] generateRandomCoordinates(double centerLat, double centerLon, double radius) {
        double randomAngle = Math.random() * 2 * Math.PI; // Случайный угол
        double randomRadius = Math.random() * radius; // Случайное расстояние в пределах радиуса

        // Перевод радиуса в градусы
        double deltaLat = randomRadius * Math.cos(randomAngle) / 111320; // 111320 м = 1 градус широты
        double deltaLon = randomRadius * Math.sin(randomAngle) / (111320 * Math.cos(Math.toRadians(centerLat))); // Коррекция долготы

        double randomLat = centerLat + deltaLat;
        double randomLon = centerLon + deltaLon;

        return new double[]{randomLat, randomLon};
    }
}

