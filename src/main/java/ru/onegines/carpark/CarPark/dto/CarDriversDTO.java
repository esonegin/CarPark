package ru.onegines.carpark.CarPark.dto;

/**
 * @author onegines
 * @date 04.11.2024
 */
public class CarDriversDTO {
    private int car_id;
    private int driver_id;

    public CarDriversDTO(int car_id, int driver_id) {
        this.car_id = car_id;
        this.driver_id = driver_id;
    }

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
    }

    public int getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(int driver_id) {
        this.driver_id = driver_id;
    }
}
