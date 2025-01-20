package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.RoutePoint;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {

    @Query("SELECT rp FROM RoutePoint rp WHERE rp.carId = :carId AND rp.timestampUtc BETWEEN :start AND :end")
    List<RoutePoint> findByCarIdAndTimestampUtcBetween(
            @Param("carId") Long carId,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );

    RoutePoint findFirstByCarIdOrderByTimestampUtcDesc(Long carId);


    List<RoutePoint> findByCarId(Long carId);

    List<RoutePoint> findByCarIdOrderByTimestampUtcAsc(Long carId);

    List<RoutePoint> findByCarIdOrderByTimestampUtc(Long carId);
}
