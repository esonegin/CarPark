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
    private String startTime;
    private String endTime;

    public RouteDTO(Long id, String startAddress, String endAddress, ZonedDateTime startTime, ZonedDateTime endTime) {
        this.id = id;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startTime = startTime.toString();
        this.endTime = endTime.toString();
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

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Long getId() {
        return id;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}

