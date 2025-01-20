package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import org.locationtech.jts.geom.Point;

import java.awt.*;
import java.time.ZonedDateTime;


/**
 * @author onegines
 * @date 26.12.2024
 */
@Entity
@Table(name = "route_points")
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_id")
    private Long carId;


    @Column(name = "point", columnDefinition = "geometry(Point,4326)")
    private Point point;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime timestampUtc;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public ZonedDateTime getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(ZonedDateTime timestampUtc) {
        this.timestampUtc = timestampUtc;
    }

    public ZonedDateTime getCreated_at() {
        return createdAt;
    }

    public void setCreated_at(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
