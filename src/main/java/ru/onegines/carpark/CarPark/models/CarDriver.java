package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import java.util.UUID;

/**
 * @author onegines
 * @date 02.11.2024
 */
@Entity
@Table(name = "car_drivers")
public class CarDriver {
    @Id
    @Column(name = "car_id")
    private UUID car_id;

    @Column(name = "driver_id")
    private UUID driver_id;

    public UUID getCar_id() {
        return car_id;
    }

    public void setCar_id(UUID car_id) {
        this.car_id = car_id;
    }

    public UUID getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(UUID driver_id) {
        this.driver_id = driver_id;
    }
}
