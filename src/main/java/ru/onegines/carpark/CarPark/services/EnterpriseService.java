package ru.onegines.carpark.CarPark.services;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.ManagerRepository;
import ru.onegines.carpark.CarPark.security.ManagerDetails;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 02.11.2024
 */
@Service
@Transactional(readOnly = true)
public class EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;
    private final DriverService driverService;
    private final ManagerRepository managerRepository;

    public EnterpriseService(EnterpriseRepository enterpriseRepository, DriverService driverService, ManagerRepository managerRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.driverService = driverService;
        this.managerRepository = managerRepository;
    }

    public List<Enterprise> findAll() {
        return enterpriseRepository.findAll();
    }

    public Enterprise findById(long id) {
        Optional<Enterprise> enterprise = enterpriseRepository.findById(id);
        return enterprise.orElse(null);
    }

    @Transactional
    public void save(Enterprise enterprise) {
        enterpriseRepository.save(enterprise);
    }

    @Transactional
    public void update(Long id, Enterprise updatedEnterprise) {
        updatedEnterprise.setId(id);
        enterpriseRepository.save(updatedEnterprise);
    }

    @Transactional
    public void delete(long id) {
        enterpriseRepository.deleteById(id);
    }

    @Transactional
    public void assignDriver(Long enterprise_id, Long driverId) {
        Enterprise enterprise = findById(enterprise_id);
        Driver driver = driverService.findById(driverId);
        enterprise.getDrivers().add(driver);
        driver.setEnterprise(enterprise);
        enterpriseRepository.save(enterprise);
    }

    //  car.getActiveDriver() != null ? car.getActiveDriver().getDriver_id() : null,
    public List<EnterpriseDTO> getAllEnterprises() {
        return enterpriseRepository.findAll()
                .stream()
                .map(enterprise -> new EnterpriseDTO(enterprise.getId(),
                        enterprise.getName(),
                        enterprise.getCity(),
                        getAllDriversId(enterprise.getId()),
                        getAllCarsId(enterprise.getId())))
                .collect(Collectors.toList());
    }

    private List<Long> getAllCarsId(Long enterprise_id) {
        return findById(enterprise_id).getCars()
                .stream()
                .map(Car::getCarId) // Извлекаем id каждого водителя
                .collect(Collectors.toList());
    }

    public List<Long> getAllDriversId(Long enterprise_id) {
        return findById(enterprise_id).getDrivers()
                .stream()
                .map(Driver::getId) // Извлекаем id каждого водителя
                .collect(Collectors.toList());
    }

    public Set<Enterprise> getEnterprisesForManager(Long managerId) {
        return enterpriseRepository.findByManagers_Id(managerId);
    }

    public boolean isManagerHasAccess(Long managerId, Long enterpriseId) {
        return enterpriseRepository.findByManagers_Id(managerId)
                .stream()
                .anyMatch(enterprise -> enterprise.getId().equals(enterpriseId));
    }

}
