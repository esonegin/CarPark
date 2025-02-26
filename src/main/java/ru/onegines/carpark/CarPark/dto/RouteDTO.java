package ru.onegines.carpark.CarPark.dto;

import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * @author onegines
 * @date 29.01.2025
 */



public class RouteDTO {
    private UUID id;
    private String startAddress;
    private String endAddress;
    private ZonedDateTime startTimeUtc;
    private ZonedDateTime endTimeUtc;

    // Полный конструктор

    public RouteDTO(UUID id, String startAddress, String endAddress, ZonedDateTime startTimeUtc, ZonedDateTime endTimeUtc) {
        this.id = id;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
        this.startTimeUtc = startTimeUtc;
        this.endTimeUtc = endTimeUtc;
    }

    public RouteDTO(UUID id, ZonedDateTime startTimeUtc, ZonedDateTime endTimeUtc) {
    }

    // Сеттеры и геттеры
    public UUID getId() {
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

    public void setId(UUID id) {
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


