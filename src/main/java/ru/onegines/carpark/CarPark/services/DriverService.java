package ru.onegines.carpark.CarPark.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.dto.DriverDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.repositories.DriverRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 01.11.2024
 */
@Service
@Transactional(readOnly = true)
public class DriverService {
    private final DriverRepository driverRepository;
    private final CarDriverService carDriverService;



    public DriverService(DriverRepository driverRepository, CarDriverService carDriverService) {
        this.driverRepository = driverRepository;
        this.carDriverService = carDriverService;

    }

    public Driver findById(long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        return driver.orElse(null);
    }

    public List<Driver> findAll() {
        return driverRepository.findAll();
    }

    public List<DriverDTO> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(driver -> {
                    List<UUID> carIds = driver.getCars().stream()
                            .map(Car::getCarId)
                            .collect(Collectors.toList());

                    return new DriverDTO(
                            driver.getId(),
                            driver.getName(),
                            driver.getSalary(),
                            driver.getEnterprise() != null ? driver.getEnterprise().getId() : null, // Проверка на null
                            carIds
                      );
                })
                .collect(Collectors.toList());
    }

    public Optional<Driver> getDriverWithCars(Long driverId) {
        return driverRepository.findById(driverId);
    }

    @Transactional
    public void save(Driver driver) {
        driverRepository.save(driver);
    }

    @Transactional
    public void update(UUID id, Driver updatedDriver) {
        updatedDriver.setId(id);
        driverRepository.save(updatedDriver);
    }

    @Transactional
    public void delete(long id) {
        driverRepository.deleteById(id);
    }

    public Set<Driver> findDriversByCarId(UUID id) {
        //driver_id c нужным car_id
        Set<UUID> carDrivers = carDriverService.findAllCarDriversByCarId(id);

        //Стрим всех водителей и таблицы drivers
        Set<Driver> drivers = driverRepository.findAll().stream()
                .filter(driver -> carDrivers.contains(driver.getId()))
                .collect(Collectors.toSet());
        return drivers;
    }

 /*   public boolean isManagerHasAccess(Long managerId, Long id) {
        Optional<Driver> driver = driverRepository.findById(id);
        return enterpriseService.isManagerHasAccess(managerId, driver.get().getEnterprise().getId());
    }*/
}
