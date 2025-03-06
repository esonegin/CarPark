package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.RoutePoint;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    // Вы можете добавить методы для кастомных запросов, если необходимо
    List<RoutePoint> findAllByCarIdOrderByTimestampUtc(UUID carId);

    List<RoutePoint> findAllByRouteIdOrderByTimestampUtc(UUID routeId);

    List<RoutePoint> findAllByRouteIdInOrderByTimestampUtc(List<UUID> routeIds);

    // Находит первую точку маршрута с валидным адресом, отсортированную по времени в порядке возрастания
    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByTimestampUtcAsc(UUID routeId, List<String> invalidAddresses);

    // Находит последнюю точку маршрута с валидным адресом, отсортированную по времени в порядке убывания
    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByTimestampUtcDesc(UUID routeId, List<String> invalidAddresses);

    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByIdAsc(UUID routeId, List<String> excludedAddresses);

    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByIdDesc(UUID routeId, List<String> excludedAddresses);

    Optional<RoutePoint> findFirstByCarIdAndTimestampUtcBetweenOrderByTimestampUtcAsc(UUID carId, ZonedDateTime start, ZonedDateTime end);

    Optional<RoutePoint> findLastByCarIdAndTimestampUtcBetweenOrderByTimestampUtcDesc(UUID carId, ZonedDateTime start, ZonedDateTime end);

    List<RoutePoint> findByCarIdAndTimestampUtcBetween(UUID carId, ZonedDateTime start, ZonedDateTime end);
}