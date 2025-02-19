package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.RoutePoint;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    // Вы можете добавить методы для кастомных запросов, если необходимо
    List<RoutePoint> findAllByCarIdOrderByTimestampUtc(Long carId);

    List<RoutePoint> findAllByRouteIdOrderByTimestampUtc(Long routeId);

    List<RoutePoint> findAllByRouteIdInOrderByTimestampUtc(List<Long> routeIds);

    // Находит первую точку маршрута с валидным адресом, отсортированную по времени в порядке возрастания
    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByTimestampUtcAsc(Long routeId, List<String> invalidAddresses);

    // Находит последнюю точку маршрута с валидным адресом, отсортированную по времени в порядке убывания
    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByTimestampUtcDesc(Long routeId, List<String> invalidAddresses);

    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByIdAsc(Long routeId, List<String> excludedAddresses);

    Optional<RoutePoint> findFirstByRouteIdAndAddressNotInOrderByIdDesc(Long routeId, List<String> excludedAddresses);

    Optional<RoutePoint> findFirstByCarIdAndTimestampUtcBetweenOrderByTimestampUtcAsc(Long carId, ZonedDateTime start, ZonedDateTime end);

    Optional<RoutePoint> findLastByCarIdAndTimestampUtcBetweenOrderByTimestampUtcDesc(Long carId, ZonedDateTime start, ZonedDateTime end);
}
