package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.dto.ManagerDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.ManagerRepository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 05.11.2024
 */
@Service
public class ManagerService {

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private EnterpriseRepository enterpriseRepository;


    public ManagerService(ManagerRepository managerRepository, EnterpriseRepository enterpriseRepository) {
        this.managerRepository = managerRepository;
        this.enterpriseRepository = enterpriseRepository;
    }

    public Manager findById(long id) {
        Optional<Manager> manager = managerRepository.findById(id);
        return manager.orElse(null);
    }

    @Transactional
    public void save(Manager manager) {
        managerRepository.save(manager);
    }

    @Transactional
    public void update(Long id, Manager updatedManager) {
        updatedManager.setId(id);
        managerRepository.save(updatedManager);
    }

    @Transactional
    public void delete(long id) {
        managerRepository.deleteById(id);
    }

    public List<ManagerDTO> getAllManagers() {
        return managerRepository.findAll()
                .stream()
                .map(manager -> new ManagerDTO(manager.getId(),
                        manager.getUsername(),
                        getAllInterprisesId(manager.getId()),
                        getAllDriversId(manager.getId()),
                        getAllCarsId(manager.getId())))
                .collect(Collectors.toList());
    }

    public List<Manager> findAll() {
        return managerRepository.findAll();
    }

    public List<Enterprise> getAvailableEnterprises() {
        return enterpriseRepository.findAll().stream()
                .filter(enterprises -> enterprises.getManagers() == null)
                .collect(Collectors.toList());
    }

    public void assignEnterpriseToManager(Long manager_id, UUID enterpriseId) {
        Manager manager = managerRepository.findById(manager_id)
                .orElseThrow(() -> new RuntimeException("Manager not found"));
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new RuntimeException("Enterprise not found"));

        manager.getEnterprises().add(enterprise);
        managerRepository.save(manager);
    }

    public List<UUID> getAllInterprisesId(Long id) {
        return findById(id).getEnterprises()
                .stream()
                .map(Enterprise::getId) // Извлекаем id каждого водителя
                .collect(Collectors.toList());
    }

    public List<Long> getAllDriversId(Long id) {
        return enterpriseRepository.findByManagers_Id(id)
                .stream()
                .flatMap(enterprise -> enterprise.getDrivers().stream()) // объединяет всех водителей от всех предприятий
                .map(Driver::getId) // получает id каждого водителя
                .collect(Collectors.toList());

    }

    public List<Long> getAllCarsId(Long id) {
        return enterpriseRepository.findByManagers_Id(id)
                .stream()
                .flatMap(enterprise -> enterprise.getCars().stream()) // объединяет всех водителей от всех предприятий
                .map(Car::getCarId) // получает id каждого водителя
                .collect(Collectors.toList());

    }

    public Optional<Manager> findByUsername(String username) {
        return findAll()
                .stream()
                .filter(manager -> manager.getUsername().equals(username))
                .findFirst();
    }

    // public ManagerDTO(Long id, String managerName, Integer managerSalary, List<Long> allEnterpiseId, List<Long> allDriversId, List<Long> allCarsId) {

    public ManagerDTO getManagerDTOById(Long id) {
        return managerRepository.findAll()
                .stream()
                .filter(manager -> manager.getId() == id)
                .map(manager -> new ManagerDTO(
                        manager.getId(),
                        manager.getUsername()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Manager not found with ID: " + id));
    }
}