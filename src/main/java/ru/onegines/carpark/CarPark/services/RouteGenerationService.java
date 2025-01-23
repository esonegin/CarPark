package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import org.locationtech.jts.geom.*;


@Service
public class RouteGenerationService {

    private final RoutePointRepository routePointRepository;

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

    public RouteGenerationService(RoutePointRepository routePointRepository) {
        this.routePointRepository = routePointRepository;
    }

    @Transactional
    public void startRouteGeneration(Long carId, double radius, double trackLength, int pointStep, double startLat, double startLon, double endLat, double endLon) {
        this.carId = carId;
        this.radius = radius;
        this.trackLength = trackLength;
        this.pointStep = pointStep;
        this.startLat = startLat;
        this.startLon = startLon;
        this.endLat = endLat;
        this.endLon = endLon;

        // Инициализируем начальные значения
        this.currentDistance = 0;
        this.pointsGenerated = 0;
    }

    // Метод для запуска генерации точек маршрута каждые 10 секунд
    @Scheduled(fixedRate = 10000) // Каждые 10 секунд
    public void generateRoute() {
        if (currentDistance >= trackLength) {
            System.out.println("Маршрут полностью сгенерирован.");
            return; // Завершаем, если маршрут достиг нужной длины
        }

        // URL для получения маршрута от OpenRouteService
        String openRouteServiceUrl = "https://api.openrouteservice.org/v2/directions/driving-car";
        String apiKey = "5b3ce3597851110001cf62483be0355aa52f408eae421fd99091b903";  // Замените на ваш API-ключ

        // Формирование URL запроса
        String url = UriComponentsBuilder.fromHttpUrl(openRouteServiceUrl)
                .queryParam("api_key", apiKey)
                .queryParam("start", startLon + "," + startLat)
                .queryParam("end", endLon + "," + endLat)
                .toUriString();

        // Используем RestTemplate для отправки GET-запроса
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Получаем маршрут из ответа API
        String responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("Не удалось получить данные маршрута от OpenRouteService");
        }

        // Парсим ответ (можно использовать библиотеку для парсинга JSON)
        List<Coordinate> routeCoordinates = parseRouteCoordinates(responseBody);

        // Генерация следующей точки маршрута
        if (pointsGenerated < routeCoordinates.size()) {
            Coordinate coordinate = routeCoordinates.get(pointsGenerated);
            double lat = coordinate.getY();
            double lon = coordinate.getX();

            // Проверяем, что точка в пределах радиуса
            if (calculateDistance(startLat, startLon, lat, lon) <= radius) {
                Point point = createPoint(lon, lat);
                RoutePoint routePoint = new RoutePoint();
                routePoint.setCarId(carId);
                routePoint.setPoint(point);
                routePoint.setTimestampUtc(ZonedDateTime.now());
                routePoint.setCreated_at(ZonedDateTime.now());

                // Сохраняем точку маршрута
                try {
                    routePointRepository.save(routePoint);
                    System.out.println("RoutePoint сохранен: " + routePoint);
                } catch (Exception e) {
                    System.out.println("Ошибка при сохранении точки маршрута: " + e.getMessage());
                }

                currentDistance += pointStep; // увеличиваем длину маршрута
                pointsGenerated++;
            }
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
            JsonNode featuresNode = rootNode.path("features");

            if (featuresNode.isArray()) {
                for (JsonNode featureNode : featuresNode) {
                    JsonNode propertiesNode = featureNode.path("properties");
                    JsonNode segmentsNode = propertiesNode.path("segments");

                    if (segmentsNode.isArray()) {
                        for (JsonNode segmentNode : segmentsNode) {
                            JsonNode stepsNode = segmentNode.path("steps");

                            if (stepsNode.isArray()) {
                                for (JsonNode stepNode : stepsNode) {
                                    JsonNode wayPointsNode = stepNode.path("way_points");

                                    if (wayPointsNode.isArray() && wayPointsNode.size() > 0) {
                                        int startWayPoint = wayPointsNode.get(0).asInt();
                                        int endWayPoint = wayPointsNode.get(1).asInt();

                                        JsonNode coordinatesNode = rootNode.path("features").get(0).path("geometry").path("coordinates");
                                        double startLon = coordinatesNode.get(startWayPoint).get(0).asDouble();
                                        double startLat = coordinatesNode.get(startWayPoint).get(1).asDouble();
                                        coordinates.add(new Coordinate(startLon, startLat));

                                        double endLon = coordinatesNode.get(endWayPoint).get(0).asDouble();
                                        double endLat = coordinatesNode.get(endWayPoint).get(1).asDouble();
                                        coordinates.add(new Coordinate(endLon, endLat));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Ошибка при разборе координат маршрута: " + e.getMessage());
        }
        return coordinates;
    }
}

