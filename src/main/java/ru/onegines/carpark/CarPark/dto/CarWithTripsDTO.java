package ru.onegines.carpark.CarPark.dto;

/**
 * @author onegines
 * @date 01.03.2025
 */
import lombok.Data;

import java.util.List;

@Data
public class CarWithTripsDTO {
    private CarDTO car;
    private List<RouteDTO> trips;

    public CarDTO getCar() {
        return car;
    }

    public void setCar(CarDTO car) {
        this.car = car;
    }

    public List<RouteDTO> getTrips() {
        return trips;
    }

    public void setTrips(List<RouteDTO> trips) {
        this.trips = trips;
    }
}