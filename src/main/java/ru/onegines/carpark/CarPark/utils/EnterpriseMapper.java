package ru.onegines.carpark.CarPark.utils;

import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 18.11.2024
 */
public class EnterpriseMapper {
    public static EnterpriseDTO toDTO(Enterprise enterprise) {
        List<Long> allDriversId = enterprise.getDrivers().stream()
                .map(Driver::getId) // Предполагается, что у Driver есть метод getId()
                .collect(Collectors.toList());

        List<UUID> allCarsId = enterprise.getCars().stream()
                .map(Car::getCarId) // Предполагается, что у Car есть метод getId()
                .collect(Collectors.toList());

        return new EnterpriseDTO(
                enterprise.getId(),
                enterprise.getName(),
                enterprise.getCity(),
                allDriversId,
                allCarsId,
                enterprise.getTimeZone());
    }
}
