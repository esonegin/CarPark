package ru.onegines.carpark.CarPark.dto;

import java.time.ZonedDateTime;

/**
 * @author onegines
 * @date 29.01.2025
 */



public class RouteDTO {
    private Long id;
    private String startAddress;
    private String endAddress;
    private String startTimeUtc;
    private String endTimeUtc;

    // Полный конструктор
    public RouteDTO(Long id, String startAddress, String endAddress, String startTimeUtc, String endTimeUtc) {
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

    public String getStartTimeUtc() {
        return startTimeUtc;
    }

    public String getEndTimeUtc() {
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

    public void setStartTimeUtc(String startTimeUtc) {
        this.startTimeUtc = startTimeUtc;
    }

    public void setEndTimeUtc(String endTimeUtc) {
        this.endTimeUtc = endTimeUtc;
    }
}


