package ru.onegines.carpark.CarPark.repositories;

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
}

