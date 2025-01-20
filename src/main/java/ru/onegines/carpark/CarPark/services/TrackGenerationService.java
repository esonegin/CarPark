package ru.onegines.carpark.CarPark.services;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author onegines
 * @date 12.01.2025
 */
@Service
public class TrackGenerationService {

    private static final double MOSCOW_LAT = 55.7558;
    private static final double MOSCOW_LON = 37.6173;

    private final RoutePointRepository routePointRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final Random random = new Random();

    public TrackGenerationService(RoutePointRepository routePointRepository) {
        this.routePointRepository = routePointRepository;
    }

    public void addTrackPoint(Long carId, double radius) {
        // Получаем последний маршрут
        List<RoutePoint> routePoints = routePointRepository.findByCarIdOrderByTimestampUtcAsc(carId);
        List<Coordinate> coordinates = new ArrayList<>();

        if (routePoints.isEmpty()) {
            coordinates.add(new Coordinate(MOSCOW_LON, MOSCOW_LAT));
        } else {
            for (RoutePoint routePoint : routePoints) {
                coordinates.add(new Coordinate(routePoint.getPoint().getX(), routePoint.getPoint().getY()));
            }
        }

        // Генерация новой точки
        double currentLat = coordinates.isEmpty() ? MOSCOW_LAT : coordinates.get(coordinates.size() - 1).getY();
        double currentLon = coordinates.isEmpty() ? MOSCOW_LON : coordinates.get(coordinates.size() - 1).getX();

        // Генерация случайного смещения
        double offsetLat = (random.nextDouble() - 0.5) * 2 * radius / 111.0;
        double offsetLon = (random.nextDouble() - 0.5) * 2 * radius / (111.0 * Math.cos(Math.toRadians(currentLat)));

        double newLat = currentLat + offsetLat;
        double newLon = currentLon + offsetLon;

        Point point = geometryFactory.createPoint(new Coordinate(newLon, newLat));
        point.setSRID(4326);

        RoutePoint routePoint = new RoutePoint();
        routePoint.setCarId(carId);
        routePoint.setPoint(point);
        routePoint.setTimestampUtc(ZonedDateTime.now());

        routePointRepository.save(routePoint);
        coordinates.add(new Coordinate(newLon, newLat));

        // Формируем линию из точек
        LineString lineString = geometryFactory.createLineString(coordinates.toArray(new Coordinate[0]));

        // Вставляем или обновляем запись в базе данных для маршрута (если нужна линия в БД)
        // Для этого потребуется дополнительная модель, которая хранит линии треков
        System.out.println("Track updated for car " + carId + ": " + lineString);
    }
}
