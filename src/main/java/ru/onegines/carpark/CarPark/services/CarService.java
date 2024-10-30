package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.repositories.CarRepository;

import java.util.List;
import java.util.Optional;

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

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Transactional
    public void save(Car car) {
        carRepository.save(car);
    }

    @Transactional
    public void update(int id, Car updatedCar){
        updatedCar.setCar_id(id);

        carRepository.save(updatedCar);
    }

    public Car findById(long id) {
        Optional<Car> person = carRepository.findById(id);
        return person.orElse(null);
    }

    @Transactional
    public void delete(long id){
        carRepository.deleteById(id);
    }
}
