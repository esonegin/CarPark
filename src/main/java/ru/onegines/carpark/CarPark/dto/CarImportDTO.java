package ru.onegines.carpark.CarPark.dto;

import lombok.Data;

import java.util.List;

@Data
public
class CarImportDTO {
    private CarDTO car;
    private List<RouteDTO> trips; // Используем существующий RouteDTO
}
