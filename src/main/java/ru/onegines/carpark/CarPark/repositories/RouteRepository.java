package ru.onegines.carpark.CarPark.repositories;

import io.micrometer.common.KeyValues;
import org.springframework.beans.PropertyValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Route;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;


@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findAllByCarIdAndStartTimeUtcGreaterThanEqualAndEndTimeUtcLessThanEqual(
            Long carId,
            ZonedDateTime startTime,
            ZonedDateTime endTime
    );

    //List<Route> findByCarIdAndEnterpriseIdAndDateBetween(Long carId, Long enterpriseId, ZonedDateTime atStartOfDay, ZonedDateTime atStartOfDay1);

    //List<Route> findByCarIdAndEnterpriseId(Long carId, Long enterpriseId);

    List<Route> findByCarIdAndStartTimeUtcBetween(Long carId, ZonedDateTime start, ZonedDateTime end);

    List<Route> findByCarId(Long carId);
}