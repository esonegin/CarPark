package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.models.Vehicle;
import ru.onegines.carpark.CarPark.repositories.CarRepository;

import java.util.List;

/**
 * @author onegines
 * @date 29.10.2024
 */
@Service
@Transactional(readOnly = true)
public class CarService {
    private final CarRepository carRepository;

    @Autowired
    public CarService(CarRepository carRepository) {

        this.carRepository = carRepository;
    }

    public List<Vehicle> findAll() {
        return carRepository.findAll();
    }
}
