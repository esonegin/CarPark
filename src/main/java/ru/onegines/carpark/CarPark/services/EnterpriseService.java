package ru.onegines.carpark.CarPark.services;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author onegines
 * @date 02.11.2024
 */
@Service
@Transactional(readOnly = true)
public class EnterpriseService {
    private final EnterpriseRepository enterpriseRepository;
    private final CarRepository carRepository;
    private final DriverService driverService;
    private final Random random = new Random();
    private final AtomicLong enterpriseIdGenerator = new AtomicLong(1);
    private final AtomicLong carIdGenerator = new AtomicLong(1);
    private final AtomicLong driverIdGenerator = new AtomicLong(1);
    private Set<Car> generatedCars = new HashSet<>();

    public EnterpriseService(EnterpriseRepository enterpriseRepository, CarRepository carRepository, DriverService driverService) {
        this.enterpriseRepository = enterpriseRepository;
        this.carRepository = carRepository;
        this.driverService = driverService;
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

    public List<EnterpriseDTO> getAllEnterprises() {
        return enterpriseRepository.findAll()
                .stream()
                .map(enterprise -> new EnterpriseDTO(enterprise.getId(),
                        enterprise.getName(),
                        enterprise.getCity(),
                        getAllDriversId(enterprise.getId()),
                        getAllCarsId(enterprise.getId()),
                        enterprise.getTimeZone()))
                .collect(Collectors.toList());
    }

    public EnterpriseDTO getEnterpriseDTO(Long id) {
        for (EnterpriseDTO enterpriseDTO : getAllEnterprises()) {
            if (enterpriseDTO.getEnterpriseId().equals(id)) {
                return enterpriseDTO;
            }
        }
        return null;
    }

    private List<Long> getAllCarsId(Long enterprise_id) {
        return findById(enterprise_id).getCars()
                .stream()
                .map(Car::getCarId) // Извлекаем id каждого водителя
                .collect(Collectors.toList());
    }

    public List<Car> getEnterpriseCarsById(Long enterprise_id) {
        //List<CarDTO> enterpriseCars= new ArrayList<>();
        List<Long> carIds = getAllCarsId(enterprise_id);
        return carRepository.findAll()
                .stream()
                .filter(car -> carIds.contains(car.getCarId()))
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

    public List<EnterpriseDTO> getEnterprisesByManagerId(Long id) {
       return getEnterprisesForManager(id)
               .stream()
               .map(enterprise -> new EnterpriseDTO(enterprise.getId(),
                       enterprise.getName(),
                       enterprise.getCity(),
                       getAllDriversId(enterprise.getId()),
                       getAllCarsId(enterprise.getId()),
                       enterprise.getTimeZone()))
               .collect(Collectors.toList());
    }

    public String getEnterpriseTimeZone(Long enterpriseId) {
        return findById(enterpriseId).getTimeZone();
    }


    /*public List<Enterprise> generateEnterprises(int enterpriseCount, int carCountPerEnterprise) {
        List<Enterprise> enterprises = new ArrayList<>();

        for (int i = 0; i < enterpriseCount; i++) {
            // Создаем предприятие
            Enterprise enterprise = new Enterprise();
            enterprise.setId(enterpriseIdGenerator.getAndIncrement());
            enterprise.setName("Enterprise " + enterprise.getId());
            enterprise.setCity(generateRandomCity());

            // Генерируем автомобили и привязываем их к предприятию
            generatedCars = generateCars(carCountPerEnterprise, enterprise);
            enterprise.setCars(generatedCars);

            // Генерируем водителей (примерно каждый 10-й автомобиль имеет активного водителя)
            List<Driver> drivers = generateDrivers(generatedCars);
            enterprise.setDrivers(drivers);

            enterprises.add(enterprise);
        }

        return enterprises;
    }

    private Set<Car> generateCars(int carCount, Enterprise enterprise  ) {
        Set<Car> cars = new HashSet<>();
        for (int i = 0; i < carCount; i++) {
            Car car = new Car();
            car.setCarId(carIdGenerator.getAndIncrement());
            //car.setBrand(random.nextLong(1, 10)); // Генерация ID бренда от 1 до 10
            car.setMileage(random.nextInt(0, 200_000));
            car.setReserve(random.nextInt(0, 100));
            car.setNumber("CAR" + car.getCarId());
            car.setEnterprise(enterprise); // Устанавливаем связь с предприятием
            cars.add(car);
        }
        return cars;
    }

    private List<Driver> generateDrivers(Set<Car> cars) {
        List<Driver> drivers = new ArrayList<>();
        for (Car car : cars) {
            if (random.nextInt(10) == 0) { // Примерно каждая 10-я машина получает водителя
                Driver driver = new Driver();
                driver.setId(driverIdGenerator.getAndIncrement());
                driver.setName("Driver " + driver.getId());
                driver.setActiveCar(car); // Привязываем водителя к машине
                car.setActiveDriver(driver); // Устанавливаем активного водителя
                drivers.add(driver);
            }
        }
        return drivers;
    }

    private String generateRandomCity() {
        String[] cities = {"Moscow", "Saint Petersburg", "Kazan", "Novosibirsk", "Yekaterinburg"};
        return cities[random.nextInt(cities.length)];
    }

    public Page<Car> getGeneratedCars(Pageable pageable) {
        List<Car> carList = new ArrayList<>(generatedCars);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), carList.size());
        if (start > carList.size()) {
            return new PageImpl<>(List.of(), pageable, carList.size());
        }
        List<Car> carsOnPage = carList.subList(start, end);

        return new PageImpl<>(carsOnPage, pageable, carList.size());
    }

    public void setGeneratedCars(Set<Car> generatedCars) {
        this.generatedCars = generatedCars;
    }
*/

}

