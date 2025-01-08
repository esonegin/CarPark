package ru.onegines.carpark.CarPark.services;

import org.geotools.geojson.geom.GeometryJSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.RoutePointRepository;

import java.awt.*;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 26.12.2024
 */
@Service
public class RouteService {
    @Autowired
    private RoutePointRepository routePointRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;

    @Autowired
    private EnterpriseRepository enterpriseService;

    @Autowired
    private CarService carService;

    public String getRouteGeoJson(Long carId, ZonedDateTime start, ZonedDateTime end) {
        List<RoutePoint> routePoints = routePointRepository.findByCarIdAndTimestampUtcBetween(carId, start, end);

        if (routePoints.isEmpty()) {
            throw new IllegalArgumentException("No route points found for car ID: " + carId);
        }

        return convertToGeoJson(routePoints);
    }

    public String convertToGeoJson(List<RoutePoint> routePoints) {
        GeometryJSON geometryJSON = new GeometryJSON(); // Используется для преобразования геометрий в GeoJSON

        List<String> geoJsonPoints = routePoints.stream()
                .map(routePoint -> {
                    try (StringWriter writer = new StringWriter()) {
                        // Преобразование Point в GeoJSON
                        geometryJSON.write(routePoint.getPoint(), writer);
                        return writer.toString();
                    } catch (Exception e) {
                        throw new RuntimeException("Error converting point to GeoJSON", e);
                    }
                })
                .collect(Collectors.toList());

        // Собираем все точки в GeoJSON FeatureCollection
        return "{\"type\":\"FeatureCollection\",\"features\":[" +
                geoJsonPoints.stream()
                        .map(point -> "{\"type\":\"Feature\",\"geometry\":" + point + "}")
                        .collect(Collectors.joining(",")) +
                "]}";
    }

    /**
     * Извлекает координаты из GEOMETRY(Point, 4326) в формате GeoJSON.
     *
     * @param point объект Point (GEOMETRY)
     * @return строка с координатами в формате "[longitude, latitude]"
     */
    private String extractCoordinatesFromPoint(Point point) {
        if (point == null) {
            throw new IllegalArgumentException("Point cannot be null");
        }
        return "[" + point.getX() + ", " + point.getY() + "]";
    }

    public String getAllRoutePoints(Long carId) {
        ZonedDateTime start = ZonedDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        ZonedDateTime end = ZonedDateTime.of(9999, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        return getRouteGeoJson(carId, start, end);

    }
}
