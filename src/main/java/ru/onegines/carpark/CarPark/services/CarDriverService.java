package ru.onegines.carpark.CarPark.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.repositories.CarDriversRepository;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 02.11.2024
 */
@Service
@Transactional(readOnly = true)
public class CarDriverService {
    private final CarDriversRepository carDriversRepository;

    public CarDriverService(CarDriversRepository carDriversRepository) {
        this.carDriversRepository = carDriversRepository;
    }

    public Set<UUID> findAllCarDriversByCarId(UUID id) {
        return carDriversRepository.findAll()
                .stream()
                .filter(carDriver -> carDriver.getCar_id() == id)
                .map(carDriver -> carDriver.getDriver_id()) // извлечение car_id
                .collect(Collectors.toSet());
    }
}
