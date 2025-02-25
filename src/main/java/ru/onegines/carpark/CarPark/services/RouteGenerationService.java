package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Random;
import java.util.UUID;

import org.locationtech.jts.geom.*;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;


@Service
public class RouteGenerationService {

    private final RoutePointRepository routePointRepository;
    private final RouteRepository routeRepository;

    private double currentDistance = 0;
    private int pointsGenerated = 0;

    @Value("${openrouteservice.api.key}")
    private String apiKey;

    private UUID carId;
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
    public void startRouteGeneration(UUID carId, double radius, double trackLength, int pointStep) {
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

    @Transactional
    public void generateMonthlyRoutes(UUID carId, int numRoutes, double maxRadius, double maxTrackLength, int maxStep) {
        Random random = new Random();
        // Координаты Москвы (центр города)
        double moscowLat = 55.7558;  // Широта Москвы
        double moscowLon = 37.6173;  // Долгота Москвы

        for (int i = 0; i < numRoutes; i++) {
            // Генерация случайных координат в пределах Москвы
            double startLat = moscowLat + (random.nextDouble() - 0.5) * 0.1; // Генерация случайной широты в пределах 10 км
            double startLon = moscowLon + (random.nextDouble() - 0.5) * 0.1; // Генерация случайной долготы в пределах 10 км
            // Генерация конечных координат в пределах Москвы
            double endLat = moscowLat + (random.nextDouble() - 0.5) * 0.1;
            double endLon = moscowLon + (random.nextDouble() - 0.5) * 0.1;

            // Получаем маршрут по автодорогам через OpenRouteService API
            String openRouteServiceUrl = UriComponentsBuilder.fromHttpUrl("https://api.openrouteservice.org/v2/directions/driving-car")
                    .queryParam("api_key", apiKey)
                    .queryParam("start", startLon + "," + startLat)
                    .queryParam("end", endLon + "," + endLat)
                    .toUriString();

            RestTemplate restTemplate = new RestTemplate();
            try {
                ResponseEntity<String> response = restTemplate.getForEntity(openRouteServiceUrl, String.class);
                List<Coordinate> routeCoordinates = parseRouteCoordinates(response.getBody());

                if (routeCoordinates == null || routeCoordinates.isEmpty()) {
                    System.out.println("Маршрут не найден.");
                    continue;
                }

                // Генерация маршрута
                Route route = new Route();
                route.setCarId(carId);
                route.setStartTimeUtc(ZonedDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24)));
                route.setEndTimeUtc(route.getStartTimeUtc().plusMinutes((long) (maxTrackLength / maxStep)));
                routeRepository.save(route);

                // Генерация точек маршрута
                double currentTrackLength = 0;
                ZonedDateTime currentTimestamp = route.getStartTimeUtc();
                int stepIndex = 0; // Индекс шага для получения адреса

                for (Coordinate coordinate : routeCoordinates) {
                    double lat = coordinate.getY();
                    double lon = coordinate.getX();

                    if (currentTrackLength >= maxTrackLength) {
                        break;
                    }

                    // Извлечение адреса из ответа API
                    String address = extractAddressFromResponse(response.getBody(), stepIndex);

                    // Создание точки маршрута
                    RoutePoint routePoint = new RoutePoint();
                    routePoint.setRoute(route);
                    routePoint.setCarId(carId);
                    Point point = createPoint(lon, lat); // Создаём точку маршрута
                    routePoint.setPoint(point);
                    routePoint.setTimestampUtc(currentTimestamp);
                    routePoint.setAddress(address); // Устанавливаем адрес
                    routePointRepository.save(routePoint);

                    System.out.println("Точка маршрута сохранена: " + point + ", адрес: " + address);

                    currentTrackLength += maxStep;
                    currentTimestamp = currentTimestamp.plusSeconds(60); // Точка каждые 60 секунд
                    stepIndex++; // Увеличиваем индекс шага
                }

                System.out.println("Маршрут сгенерирован: " + route.getId());
            } catch (Exception e) {
                System.out.println("Ошибка при генерации маршрута: " + e.getMessage());
            }
        }
    }




    @PreDestroy
    @Transactional
    public boolean stopRouteGeneration() {
        if (currentRoute != null) {
            currentRoute.setEndTimeUtc(ZonedDateTime.now());
            routeRepository.save(currentRoute);
            System.out.println("Маршрут завершён при остановке приложения: " + currentRoute);
        }
        return false;
    }

    // Метод для запуска генерации точек маршрута каждые 10 секунд
    @Scheduled(fixedRate = 10000)
    public void generateRoute() {
        if (currentDistance >= trackLength) {
            // Завершаем маршрут
            if (currentRoute != null) {
                currentRoute.setEndTimeUtc(ZonedDateTime.now());
                routeRepository.save(currentRoute);
                System.out.println("Маршрут завершен: " + currentRoute);
                currentRoute = null;
            }
            return;
        }

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

                // Извлекаем адрес из ответа OpenRouteService
                String address = extractAddressFromResponse(response.getBody(), pointsGenerated);

                // Создаём и сохраняем RoutePoint
                RoutePoint routePoint = new RoutePoint();
                routePoint.setCarId(carId);
                routePoint.setRoute(currentRoute);
                routePoint.setPoint(point);
                routePoint.setTimestampUtc(ZonedDateTime.now());
                routePoint.setAddress(address); // Устанавливаем адрес

                routePointRepository.save(routePoint);
                System.out.println("RoutePoint сохранен: " + routePoint);

                pointsGenerated++;
                currentDistance += pointStep;

                if (currentDistance >= trackLength) {
                    // Завершаем маршрут после добавления последней точки
                    currentRoute.setEndTimeUtc(ZonedDateTime.now());
                    routeRepository.save(currentRoute);
                    System.out.println("Маршрут завершен. Время окончания: " + currentRoute.getEndTimeUtc());
                    currentRoute = null;
                }
            } else {
                System.out.println("Точка за пределами заданного радиуса: " + lat + ", " + lon);
                pointsGenerated++; // Пропускаем точки за радиусом
            }
        } catch (Exception e) {
            System.out.println("Ошибка при генерации маршрута: " + e.getMessage());
        }
    }

    private String extractAddressFromResponse(String responseBody, int stepIndex) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);

            // Переходим к узлу "features"
            JsonNode featuresNode = rootNode.path("features");
            if (!featuresNode.isArray() || featuresNode.size() == 0) {
                System.out.println("Узел 'features' отсутствует или пуст.");
                return "Адрес не найден";
            }

            // Берём первый элемент из массива "features"
            JsonNode firstFeature = featuresNode.get(0);
            if (firstFeature == null || !firstFeature.isObject()) {
                System.out.println("Первый элемент 'features' отсутствует или имеет неверный тип.");
                return "Адрес не найден";
            }

            // Переходим к узлу "properties" внутри первого элемента "features"
            JsonNode propertiesNode = firstFeature.path("properties");
            if (propertiesNode == null || !propertiesNode.isObject()) {
                System.out.println("Узел 'properties' отсутствует или имеет неверный тип.");
                return "Адрес не найден";
            }

            // Переходим к узлу "segments" внутри "properties"
            JsonNode segmentsNode = propertiesNode.path("segments");
            if (!segmentsNode.isArray() || segmentsNode.size() == 0) {
                System.out.println("Узел 'segments' отсутствует или пуст.");
                return "Адрес не найден";
            }

            // Берём первый сегмент маршрута
            JsonNode firstSegment = segmentsNode.get(0);
            if (firstSegment == null || !firstSegment.isObject()) {
                System.out.println("Первый сегмент маршрута отсутствует.");
                return "Адрес не найден";
            }

            // Переходим к узлу "steps" внутри первого сегмента
            JsonNode stepsNode = firstSegment.path("steps");
            if (!stepsNode.isArray() || stepsNode.size() <= stepIndex) {
                System.out.println("Узел 'steps' отсутствует или индекс шага вне диапазона.");
                return "Адрес не найден";
            }

            // Берём нужный шаг маршрута
            JsonNode step = stepsNode.get(stepIndex);
            if (step == null || !step.isObject()) {
                System.out.println("Шаг маршрута с индексом " + stepIndex + " отсутствует.");
                return "Адрес не найден";
            }

            // Извлекаем значение поля "name" из текущего шага
            return step.path("name").asText();
        } catch (Exception e) {
            System.out.println("Ошибка при извлечении адреса: " + e.getMessage());
        }
        return "Адрес не найден";
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

    /*private Point createPoint(double lon, double lat) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(lon, lat);
        return geometryFactory.createPoint(coordinate);
    }
*/
    private Point createPoint(double lon, double lat) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Coordinate coordinate = new Coordinate(lon, lat);
        // Преобразуем Coordinate в массив
        Coordinate[] coordinates = new Coordinate[]{coordinate};
        // Создаем CoordinateSequence
        CoordinateSequence sequence = geometryFactory.getCoordinateSequenceFactory().create(coordinates);
        // Создаем Point
        return geometryFactory.createPoint(sequence);
    }

    public List<Coordinate> parseRouteCoordinates(String responseBody) {
        List<Coordinate> coordinates = new ArrayList<>();
        try {
            ObjectMapper jacksonObjectMapper = new ObjectMapper();
            JsonNode rootNode = jacksonObjectMapper.readTree(responseBody);
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

