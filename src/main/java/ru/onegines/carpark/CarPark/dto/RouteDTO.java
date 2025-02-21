package ru.onegines.carpark.CarPark.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

/**
 * @author onegines
 * @date 29.01.2025
 */



public class RouteDTO {
    private Long id;
    private String startAddress;
    private String endAddress;
    private ZonedDateTime startTimeUtc;
    private ZonedDateTime endTimeUtc;

    // Полный конструктор
    public RouteDTO(Long id, String startAddress, String endAddress, ZonedDateTime startTimeUtc, ZonedDateTime endTimeUtc) {
        this.id = id;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startTimeUtc = startTimeUtc;
        this.endTimeUtc = endTimeUtc;
    }

    // Сеттеры и геттеры
    public Long getId() {
        return id;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public ZonedDateTime getStartTimeUtc() {
        return startTimeUtc;
    }

    public ZonedDateTime getEndTimeUtc() {
        return endTimeUtc;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public void setStartTimeUtc(ZonedDateTime startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public void setEndTimeUtc(ZonedDateTime endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }
}


