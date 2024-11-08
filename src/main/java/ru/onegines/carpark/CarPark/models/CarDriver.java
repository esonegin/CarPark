package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

/**
 * @author onegines
 * @date 02.11.2024
 */
@Entity
@Table(name = "car_drivers")
public class CarDriver {
    @Id
    @Column(name = "car_id")
    private int car_id;

    @Column(name = "driver_id")
    private Long driver_id;

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public Long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(Long driver_id) {
        this.driver_id = driver_id;
    }
}
