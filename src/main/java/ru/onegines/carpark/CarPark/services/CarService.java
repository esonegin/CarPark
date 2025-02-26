package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.exceptions.ResourceNotFoundException;
import ru.onegines.carpark.CarPark.models.*;
import ru.onegines.carpark.CarPark.repositories.*;
import ru.onegines.carpark.CarPark.utils.DateTimeUtil;

import java.time.*;
import java.util.*;
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
    public final EnterpriseService enterpriseService;
    public final BrandService brandService;
    public final BrandRepository brandRepository;
    public final EnterpriseRepository enterpriseRepository;
    public final RouteRepository routeRepository;
    public final RoutePointRepository routePointRepository;

    @Autowired
    public CarService(CarRepository carRepository, DriverRepository driverRepository, DriverService driverService, EnterpriseService enterpriseService, BrandService brandService, BrandRepository brandRepository, EnterpriseRepository enterpriseRepository, RouteRepository routeRepository, RoutePointRepository routePointRepository) {
        this.carRepository = carRepository;
        this.driverRepository = driverRepository;
        this.driverService = driverService;
        this.enterpriseService = enterpriseService;
        this.brandService = brandService;
        this.brandRepository = brandRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.routeRepository = routeRepository;
        this.routePointRepository = routePointRepository;
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public List<CarDTO> getAllCars() {
        List<CarDTO> res = carRepository.findAll()
                .stream()
                .map(carDto -> new CarDTO(
                        carDto.getCarId(),
                        carDto.getBrand().getId(),
                        carDto.getMileage(),
                        carDto.getГодВыпуска(),
                        carDto.getReserve(),
                        carDto.getNumber(),
                        carDto.getActiveDriver() != null ? carDto.getActiveDriver().getId() : null,
                        getAllDriversId(carDto.getCarId()),
                        carDto.getEnterprise().getId(),
                        carDto.getPurchaseDateTime()))
                .collect(Collectors.toList());
        return res;
    }


    public CarDTO getCarDTO(UUID id) {
        for (CarDTO carDTO : getAllCars()) {
            if (carDTO.getCarId().equals(id)) {
                return carDTO;
            }
        }
        return null;
    }

    @Transactional
    public void save(Car car) {
        carRepository.save(car);
    }

    @Transactional
    public void update(UUID id, Car updatedCar) {
        updatedCar.setCarId(id);
        carRepository.save(updatedCar);
    }

    @Transactional
    public void update(UUID carId, CarDTO carDTO) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id " + carId));

        // Обновление данных
        car.setMileage(carDTO.getMileage());
        car.setГодВыпуска(carDTO.getГодВыпуска());
        car.setReserve(carDTO.getReserve());
        car.setNumber(carDTO.getNumber());
        car.setBrand(brandRepository.findById(carDTO.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found")));
        car.setPurchaseDateTime(carDTO.getPurchaseDateTime());
        carRepository.save(car); // Сохранение изменений
    }

    public Car findById(UUID id) {
        Optional<Car> car = carRepository.findByCarId(id);
        return car.orElse(null);
    }

    @Transactional
    public void delete(UUID id) {
        carRepository.deleteByCarId(id);
    }

    public List<Car> getAvailableCars() {
        return findAll().stream()
                .filter(car -> car.getEnterprise() == null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void assignDriver(UUID carId, int driverId) {
        Car car = findById(carId);
        Driver driver = driverService.findById(driverId);
        car.getDrivers().add(driver);
        carRepository.save(car);
    }

    public void assignActiveDriver(UUID carId, Long driverId) {
        Car car = carRepository.findByCarId(carId).orElseThrow(() -> new ResourceNotFoundException("Car not found"));

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

    public List<UUID> getAllDriversId(UUID car_id) {
        return findById(car_id).getDrivers()
                .stream()
                .map(Driver::getId) // Извлекаем id каждого водителя
                .collect(Collectors.toList());
    }

    public List<Car> findByEnterpriseId(UUID id) {
        return carRepository.findAll()
                .stream()
                .filter(car -> car.getEnterprise().getId().equals(id))
                .collect(Collectors.toList());
    }

    /*public List<CarDTO> getCarsByEnterprise(Long enterpriseId) {
        List<Car> cars = carRepository.findByEnterpriseId(enterpriseId);
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new IllegalArgumentException("Enterprise not found"));

        ZoneId enterpriseZoneId = enterprise.getTimeZone() != null
                ? ZoneId.of(enterprise.getTimeZone())
                : ZoneId.of("UTC");

        return cars.stream().map(car -> {
            CarDTO carDTO = new CarDTO();
            carDTO.setCarId(car.getCarId());
            carDTO.setNumber(car.getNumber());
            carDTO.setBrandId(car.getBrand().getId());
            carDTO.setPurchaseDateTime(car.getPurchaseDateTime());

            // Преобразование в зону предприятия
            carDTO.setPurchaseDateTimeInEnterpriseTimeZone(
                    car.getPurchaseDateTime().atZone(ZoneId.of("UTC"))
                            .withZoneSameInstant(enterpriseZoneId)
                            .toLocalDateTime()
                            .toString() // для корректного отображения
            );
            return carDTO;
        }).collect(Collectors.toList());
    }*/

    private ZoneId resolveEnterpriseTimeZone(UUID id) {
        Enterprise enterprise = enterpriseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Enterprise not found"));
        return enterprise.getTimeZone() != null
                ? ZoneId.of(enterprise.getTimeZone())
                : ZoneId.of("UTC");
    }

    public List<CarDTO> getCarsByEnterprise(UUID id) {
        ZoneId enterpriseZoneId = resolveEnterpriseTimeZone(id);

        return carRepository.findByEnterpriseId(id)
                .stream()
                .map(car -> toCarDTO(car, enterpriseZoneId))
                .collect(Collectors.toList());
    }

    private CarDTO toCarDTO(Car car, ZoneId enterpriseZoneId) {
        CarDTO carDTO = new CarDTO();
        carDTO.setCarId(car.getCarId());
        carDTO.setNumber(car.getNumber());
        carDTO.setBrandId(car.getBrand() != null ? car.getBrand().getId() : null);
        carDTO.setPurchaseDateTime(car.getPurchaseDateTime());
        carDTO.setPurchaseDateTimeInEnterpriseTimeZone(
                DateTimeUtil.convertToTimeZone(car.getPurchaseDateTime(), "UTC", enterpriseZoneId.getId()).toString()
        );
        return carDTO;
    }


    public boolean isManagerHasAccess(UUID managerId, UUID carId) {
        Car car = carRepository.findById(carId).orElseThrow(() -> new ResourceNotFoundException("Car not found"));
        return enterpriseService.isManagerHasAccess(managerId, car.getEnterprise().getId());
    }

    public void addCarToEnterprise(UUID id, CarDTO carDTO) {
        Car car = new Car();
        Brand brand = brandRepository.findById(carDTO.getBrandId()).orElse(null);
        car.setMileage(carDTO.getMileage());
        car.setГодВыпуска(carDTO.getГодВыпуска());
        car.setReserve(carDTO.getReserve());
        car.setNumber(carDTO.getNumber());
        car.setBrand(brand);
        car.setEnterprise(enterpriseService.findById(id));
        carRepository.save(car);
    }

    public Map<UUID, List<CarDTO>> getCarsDTOGroupedByEnterprise() {
        return carRepository.findAll().stream()
                .filter(car -> car.getEnterprise() != null) // Исключаем null
                .map(car -> new CarDTO(
                        car.getCarId(),
                        car.getBrand() != null ? car.getBrand().getId() : null,
                        car.getMileage(),
                        car.getГодВыпуска(),
                        car.getReserve(),
                        car.getNumber(),
                        car.getActiveDriver() != null ? car.getActiveDriver().getId() : null,
                        car.getDrivers() != null ? car.getDrivers().stream().map(Driver::getId).collect(Collectors.toList()) : List.of(),
                        car.getEnterprise().getId(),
                        car.getPurchaseDateTime(),
                        car.getEnterprise().getTimeZone()
                ))

                .collect(Collectors.groupingBy(CarDTO::getEnterpriseId));
    }

    public List<RouteDTO> getTripsByCar(UUID carId, UUID id) {
        // Получаем таймзону предприятия
        String enterpriseTimeZone = enterpriseRepository.findTimeZoneById(id);
        if (enterpriseTimeZone == null) {
            throw new IllegalArgumentException("Таймзона предприятия не найдена для ID: " + id);
        }

        // Получаем поездки для машины
        List<Route> trips = routeRepository.findByCarId(carId);

        if (trips == null || trips.isEmpty()) {
            System.out.println("Нет поездок для машины ID " + carId);
            return Collections.emptyList();
        }

        // Преобразуем поездки с учетом таймзоны предприятия
        return trips.stream()
                .map(trip -> {
                    // Преобразуем время поездок в таймзону предприятия
                    ZonedDateTime startTimeInEnterpriseZone = trip.getStartTimeUtc().withZoneSameInstant(ZoneId.of(enterpriseTimeZone));
                    ZonedDateTime endTimeInEnterpriseZone = trip.getEndTimeUtc().withZoneSameInstant(ZoneId.of(enterpriseTimeZone));

                    // Округляем endTimeUtc в большую сторону
                    ZonedDateTime roundedEndTimeUtc = roundUpToNextMinute(endTimeInEnterpriseZone);

                    return new RouteDTO(
                            trip.getId(),
                            getStartAddress(trip.getId()),  // Получаем начальный адрес
                            getEndAddress(trip.getId()),    // Получаем конечный адрес
                            startTimeInEnterpriseZone,      // Используем время в таймзоне предприятия
                            roundedEndTimeUtc               // Используем округленное время окончания
                    );
                })
                .collect(Collectors.toList());
    }

    private List<RouteDTO> getTripsByCar(UUID carId, Long enterpriseId, LocalDate startDate, LocalDate endDate) {
        // Проверяем, принадлежит ли машина данному enterpriseId
        Optional<Car> carOptional = carRepository.findByCarId(carId);
        if (carOptional.isEmpty() || !carOptional.get().getEnterprise().getId().equals(enterpriseId)) {
            return Collections.emptyList();
        }

        List<Route> routes;
        if (startDate != null && endDate != null) {
            routes = routeRepository.findByCarIdAndStartTimeUtcBetween(
                    carId,
                    startDate.atStartOfDay(ZoneOffset.UTC),
                    endDate.plusDays(1).atStartOfDay(ZoneOffset.UTC) // Включаем весь день endDate
            );
        } else {
            routes = routeRepository.findByCarId(carId);
        }

        return routes.stream()
                .map(route -> new RouteDTO(
                        route.getId(),
                        getStartAddress(route.getId()), // Получаем начальный адрес
                        getEndAddress(route.getId()),  // Получаем конечный адрес
                        route.getStartTimeUtc(),
                        route.getEndTimeUtc()
                ))
                .toList();
    }

    public String getStartAddress(Long routeId) {
        return routePointRepository.findFirstByRouteIdAndAddressNotInOrderByIdAsc(
                routeId, List.of("-", "Адрес не найден")
        ).map(RoutePoint::getAddress).orElse("Неизвестно");
    }

    public String getEndAddress(Long routeId) {
        return routePointRepository.findFirstByRouteIdAndAddressNotInOrderByIdDesc(
                routeId, List.of("-", "Адрес не найден")
        ).map(RoutePoint::getAddress).orElse("Неизвестно");
    }

    private ZonedDateTime roundUpToNextMinute(ZonedDateTime dateTime) {
        if (dateTime.getSecond() > 0 || dateTime.getNano() > 0) {
            // Добавляем 1 минуту и обнуляем секунды и наносекунды
            return dateTime.plusMinutes(1).withSecond(0).withNano(0);
        }
        // Если секунды и наносекунды уже равны 0, возвращаем исходное значение
        return dateTime;
    }

}