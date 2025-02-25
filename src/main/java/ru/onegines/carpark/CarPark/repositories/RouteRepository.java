package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Route;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findAllByCarIdAndStartTimeUtcGreaterThanEqualAndEndTimeUtcLessThanEqual(
            UUID carId,
            ZonedDateTime startTime,
            ZonedDateTime endTime
    );

    //List<Route> findByCarIdAndEnterpriseIdAndDateBetween(Long carId, Long enterpriseId, ZonedDateTime atStartOfDay, ZonedDateTime atStartOfDay1);

    //List<Route> findByCarIdAndEnterpriseId(Long carId, Long enterpriseId);

    List<Route> findByCarIdAndStartTimeUtcBetween(UUID carId, ZonedDateTime start, ZonedDateTime end);

    List<Route> findByCarId(UUID carId);
}