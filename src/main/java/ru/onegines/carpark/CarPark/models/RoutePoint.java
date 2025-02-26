package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import org.locationtech.jts.geom.Point;

import java.awt.*;
import java.time.ZonedDateTime;
import java.util.UUID;


/**
 * @author onegines
 * @date 26.12.2024
 */
@Entity
@Table(name = "route_points")
public class RoutePoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "car_id")
    private UUID carId;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "point", columnDefinition = "geometry(Point,4326)")
    private Point point;

    @Column(name = "timestamp", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private ZonedDateTime timestampUtc;

    @Column(name = "adress") // Новое поле для текстового представления адреса
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

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

    public ZonedDateTime getTimestampUtc() {
        return timestampUtc;
    }

    public void setTimestampUtc(ZonedDateTime timestampUtc) {
        this.timestampUtc = timestampUtc;
    }


    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

}
