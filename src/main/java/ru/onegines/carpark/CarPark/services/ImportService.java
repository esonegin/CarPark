package ru.onegines.carpark.CarPark.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.onegines.carpark.CarPark.dto.CarWithTripsDTO;
import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.dto.ImportDataDTO;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Service
public class ImportService {
    private final ObjectMapper objectMapper; // JSON парсер
    private final EnterpriseService enterpriseService;
    private final CarService carService;
    private final RouteService routeService;

    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    public ImportService(ObjectMapper objectMapper, EnterpriseService enterpriseService, CarService carService, RouteService routeService) {
        this.objectMapper = objectMapper;
        this.enterpriseService = enterpriseService;
        this.carService = carService;
        this.routeService = routeService;
    }

    /**
     * Импорт данных из JSON-файла.
     */
    @Transactional
    public void importFromJson(InputStream inputStream) throws IOException {
        log.info("Начинаем импорт JSON");

        // Читаем JSON и парсим в объект ImportDataDTO
        String json = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        json = json.replaceAll("\"GENERATED_GUID\"", "\"" + UUID.randomUUID() + "\"");
        ImportDataDTO importData = objectMapper.readValue(json, ImportDataDTO.class);

        // Импортируем предприятие
        enterpriseService.saveEnterprise(importData.getEnterprise());

        // Импортируем автомобили и поездки
        for (CarWithTripsDTO carWithTrips : importData.getCars()) {
            carService.addCarToEnterprise(importData.getEnterprise().getEnterpriseId(), carWithTrips.getCar());
            for (RouteDTO route : carWithTrips.getTrips()) {
                routeService.saveRoute(route);
            }
        }

        log.info("Импорт JSON завершен успешно");
    }

    /**
     * Импорт данных из CSV-файла.
     */
    @Transactional
    public void importFromCsv(InputStream inputStream) throws IOException {
        log.info("Начинаем импорт CSV");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader()
                     .setSkipHeaderRecord(true)
                     .build())) {

            for (CSVRecord record : csvParser) {
                EnterpriseDTO enterprise = new EnterpriseDTO(
                        UUID.fromString(record.get("enterpriseId")),
                        record.get("enterpriseName"),
                        record.get("enterpriseCity"),
                        List.of(), // Для CSV можно добавить обработку драйверов и машин
                        List.of(),
                        record.get("timeZone")
                );
                enterpriseService.saveEnterprise(enterprise);
            }
        }

        log.info("Импорт CSV завершен успешно");
    }

}
