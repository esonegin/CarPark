package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.exceptions.DuplicateActiveDriverException;
import ru.onegines.carpark.CarPark.exceptions.ResourceNotFoundException;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.DriverRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 29.10.2024
 */
@Service
@Transactional(readOnly = true)
public class CarService {
    private final CarRepository carRepository;
    private final DriverRepository driverRepository;
    private final DriverService driverService;

    @Autowired
    public CarService(CarRepository carRepository, DriverRepository driverRepository, DriverService driverService) {

        this.carRepository = carRepository;
        this.driverRepository = driverRepository;
        this.driverService = driverService;
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public List<CarDTO> getAllCars() {
        return carRepository.findAll()
                .stream()
                .map(car -> new CarDTO(car.getCarId(),
                        car.getBrand().getId(),
                        car.getMileage(),
                        car.getГод_выпуска(),
                        car.getReserve(),
                        car.getNumber(),
                        car.getActiveDriver() != null ? car.getActiveDriver().getId() : null,
                        getAllDriversId(car.getCarId()),
                        car.getEnterprise().getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(Car car) {
        carRepository.save(car);
    }

    @Transactional
    public void update(Long id, Car updatedCar) {
        updatedCar.setCarId(id);
        carRepository.save(updatedCar);
    }

    public Car findById(long id) {
        Optional<Car> car = carRepository.findById(id);
        return car.orElse(null);
    }

    @Transactional
    public void delete(long id) {
        carRepository.deleteById(id);
    }

    public List<Car> getAvailableCars() {
        return findAll().stream()
                .filter(car -> car.getEnterprise() == null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignDriver(Long carId, int driverId) {
        Car car = findById(carId);
        Driver driver = driverService.findById(driverId);
        car.getDrivers().add(driver);
        carRepository.save(car);
    }

    public void assignActiveDriver(Long carId, Long driverId)  {
        Car car = carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("Car not found"));

        // Деактивируем предыдущего активного водителя
        if (car.getActiveDriver() != null) {
            car.getActiveDriver().setActive(false);
            driverRepository.save(car.getActiveDriver());
        }
        // Назначаем нового активного водителя
        Driver driver = driverRepository.findById(driverId).orElseThrow(() -> new ResourceNotFoundException("Driver not found"));
        car.setActiveDriver(driver);
        driver.setActive(true); // Устанавливаем статус активного водителя
        carRepository.save(car); // Сохраняем изменения
        driverRepository.save(driver); // Сохраняем изменения в
    }

    public List<Long> getAllDriversId(Long car_id) {
        return findById(car_id).getDrivers()
                .stream()
                .map(Driver::getId) // Извлекаем id каждого водителя
                .collect(Collectors.toList());
    }

  /*  public List<Car> getCarsByVisibleEnterprises(Set<Long> enterprise_ids) {
        return carRepository.findByEnterpriseIn(enterprise_ids);
    }*/
}
