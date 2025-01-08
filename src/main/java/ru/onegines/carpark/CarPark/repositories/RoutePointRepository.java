package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.onegines.carpark.CarPark.models.RoutePoint;

import java.time.ZonedDateTime;
import java.util.List;

public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
   /* List<RoutePoint> findByCarIdAndTimestampUtcBetween(Long carId, ZonedDateTime start, ZonedDateTime end);

    @Query("SELECT rp FROM RoutePoint rp WHERE rp.carId = :carId AND rp.timestampUtc BETWEEN :start AND :end")
    List<RoutePoint> findAllByCarIdAndTimestampRange(
            @Param("carId") Long carId,
            @Param("start") ZonedDateTime start,
            @Param("end") ZonedDateTime end
    );*/

    @Query("SELECT rp FROM RoutePoint rp WHERE rp.carId = :carId AND rp.timestampUtc BETWEEN :start AND :end")
    List<RoutePoint> findByCarIdAndTimestampUtcBetween(Long carId, ZonedDateTime start, ZonedDateTime end);


}
