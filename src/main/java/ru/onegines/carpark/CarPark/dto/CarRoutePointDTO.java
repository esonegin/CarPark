package ru.onegines.carpark.CarPark.dto;

import java.time.LocalDateTime;

/**
 * @author onegines
 * @date 18.12.2024
 */
public class CarRoutePointDTO {
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;

    public CarRoutePointDTO(double latitude, double longitude, LocalDateTime timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
