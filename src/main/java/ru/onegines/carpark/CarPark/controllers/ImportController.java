package ru.onegines.carpark.CarPark.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.onegines.carpark.CarPark.services.ImportService;

import java.io.IOException;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class ImportController {
    @Autowired
    private ImportService importService;
    private static final Logger log = LoggerFactory.getLogger(ImportController.class);


    @PostMapping("/enterprise")
    public ResponseEntity<?> importEnterpriseData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не загружен");
        }

        String fileType = getFileType(file);
        log.info("Получен файл для импорта: имя={}, тип={}", file.getOriginalFilename(), fileType);

        try {
            switch (fileType) {
                case "csv" -> importService.importFromCsv(file.getInputStream());
                case "json" -> importService.importFromJson(file.getInputStream());
                default -> {
                    log.warn("Неподдерживаемый формат файла: {}", fileType);
                    return ResponseEntity.badRequest().body("Неподдерживаемый формат файла: " + fileType);
                }
            }
            log.info("Импорт завершен успешно для файла {}", file.getOriginalFilename());
            return ResponseEntity.ok("Импорт успешно выполнен");
        } catch (IOException e) {
            log.error("Ошибка обработки файла {}: {}", file.getOriginalFilename(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Ошибка обработки файла: " + e.getMessage());
        }
    }

    /**
     * Определение типа файла по Content-Type или расширению.
     */
    private String getFileType(MultipartFile file) {
        String contentType = file.getContentType();
        String originalFilename = file.getOriginalFilename();

        if (contentType != null) {
            if (contentType.equalsIgnoreCase("text/csv")) {
                return "csv";
            } else if (contentType.equalsIgnoreCase("application/json") || contentType.equalsIgnoreCase("application/octet-stream")) {
                return "json";
            }
        }

        if (originalFilename != null) {
            if (originalFilename.endsWith(".csv")) {
                return "csv";
            } else if (originalFilename.endsWith(".json")) {
                return "json";
            }
        }

        return "unknown";
    }
}
