package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author onegines
 * @date 26.01.2025
 */
@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "car_id", nullable = false)
    private UUID carId;

    @Column(name = "start_time_utc", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime startTimeUtc;

    @Column(name = "end_time_utc", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime endTimeUtc;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

    public ZonedDateTime getStartTimeUtc() {
        return startTimeUtc;
    }

    public void setStartTimeUtc(ZonedDateTime startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public ZonedDateTime getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setEndTimeUtc(ZonedDateTime endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }
}
