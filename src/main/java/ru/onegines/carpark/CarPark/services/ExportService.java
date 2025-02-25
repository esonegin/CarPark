package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Route;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.RouteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.time.*;
import java.util.*;

@Service
public class ExportService {
    private static final Logger log = LoggerFactory.getLogger(ExportService.class);
    private final EnterpriseRepository enterpriseRepository;
    private final CarRepository carRepository;
    private final RouteRepository routeRepository;
    private final CarService carService;
    private final ObjectMapper objectMapper;

    public ExportService(EnterpriseRepository enterpriseRepository, CarRepository carRepository,
                         RouteRepository routeRepository, CarService carService, ObjectMapper objectMapper) {
        this.enterpriseRepository = enterpriseRepository;
        this.carRepository = carRepository;
        this.routeRepository = routeRepository;
        this.carService = carService;
        this.objectMapper = objectMapper;
    }

    public String exportToCsv(Long enterpriseId, LocalDate startDate, LocalDate endDate) {
        StringWriter writer = new StringWriter();
        try {
            writer.write('\uFEFF'); // Добавляем BOM для корректного отображения UTF-8 в Excel

            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                    .withDelimiter(';') // Используем ; как разделитель, так как Excel в русской локали ожидает его
                    .withHeader("Enterprise ID", "Enterprise Name", "Car ID", "Brand ID", "Mileage", "Year", "Reserve", "Number", "Trip ID", "Start Time", "End Time", "Start Address", "End Address"));

            Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                    .orElseThrow(() -> new IllegalArgumentException("Предприятие не найдено с ID: " + enterpriseId));
            List<Car> cars = carRepository.findByEnterpriseId(enterpriseId);

            for (Car car : cars) {
                List<RouteDTO> trips = getTripsByCar(car.getCarId(), enterpriseId, startDate, endDate);

                if (trips.isEmpty()) {
                    csvPrinter.printRecord(
                            enterprise.getId(), enterprise.getName(),
                            car.getCarId(), car.getBrand().getId(), car.getMileage(), car.getГодВыпуска(), car.getReserve(), car.getNumber(),
                            "", "", "", "", ""
                    );
                } else {
                    for (RouteDTO trip : trips) {
                        csvPrinter.printRecord(
                                enterprise.getId(), enterprise.getName(),
                                car.getCarId(), car.getBrand().getId(), car.getMileage(), car.getГодВыпуска(), car.getReserve(), car.getNumber(),
                                trip.getId(), trip.getStartTimeUtc(), trip.getEndTimeUtc(), trip.getStartAddress(), trip.getEndAddress()
                        );
                    }
                }
            }
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании CSV", e);
        }
        return writer.toString();
    }



    public String exportToJson(Long enterpriseId, LocalDate startDate, LocalDate endDate) {
        Enterprise enterprise = enterpriseRepository.findById(enterpriseId)
                .orElseThrow(() -> new IllegalArgumentException("Предприятие не найдено с ID: " + enterpriseId));

        List<Car> cars = carRepository.findByEnterpriseId(enterpriseId);
        List<Map<String, Object>> carDTOList = new ArrayList<>();

        for (Car car : cars) {
            List<RouteDTO> trips = getTripsByCar(car.getCarId(), enterpriseId, startDate, endDate);
            log.info("Получено маршрутов: {}", trips.size());

            CarDTO carDTO = new CarDTO(
                    car.getCarId(),
                    car.getBrand().getId(),
                    car.getMileage(),
                    car.getГодВыпуска(),
                    car.getReserve(),
                    car.getNumber(),
                    car.getActiveDriver() != null ? car.getActiveDriver().getId() : null,
                    car.getDrivers().stream().map(Driver::getId).toList(),
                    car.getEnterprise().getId(),
                    car.getPurchaseDateTime(),
                    convertToEnterpriseTimeZone(car.getPurchaseDateTime(), enterprise.getTimeZone())
            );

            // Создаём объект с машиной и её поездками
            Map<String, Object> carWithTrips = new HashMap<>();
            carWithTrips.put("car", carDTO);
            carWithTrips.put("trips", trips); // Добавляем поездки

            carDTOList.add(carWithTrips); // Добавляем объект в список
        }

        EnterpriseDTO enterpriseDTO = new EnterpriseDTO(
                enterprise.getId(),
                enterprise.getName(),
                enterprise.getCity(),
                enterprise.getDrivers().stream().map(Driver::getId).toList(),
                enterprise.getCars().stream().map(Car::getCarId).toList(),
                enterprise.getTimeZone()
        );

        // Собираем полный объект для экспорта
        Map<String, Object> exportData = new HashMap<>();
        exportData.put("enterprise", enterpriseDTO);
        exportData.put("cars", carDTOList); // Здесь теперь машины с поездками

        try {
            return objectMapper.writeValueAsString(exportData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка при создании JSON", e);
        }
    }



    private String convertToEnterpriseTimeZone(LocalDateTime dateTime, String timeZone) {
        if (dateTime == null || timeZone == null) {
            return null;
        }
        ZonedDateTime zonedDateTime = dateTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(timeZone));
        return zonedDateTime.toString();
    }



    private List<RouteDTO> getTripsByCar(Long carId, Long enterpriseId, LocalDate startDate, LocalDate endDate) {
        // Проверяем, принадлежит ли машина данному enterpriseId
        Optional<Car> carOptional = carRepository.findById(carId);
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
                        carService.getStartAddress(route.getId()), // Получаем начальный адрес
                        carService.getEndAddress(route.getId()),  // Получаем конечный адрес
                        route.getStartTimeUtc(),
                        route.getEndTimeUtc()
                ))
                .toList();
    }
}
