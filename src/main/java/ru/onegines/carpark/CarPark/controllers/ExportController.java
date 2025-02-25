package ru.onegines.carpark.CarPark.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.services.ExportService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

/**
 * @author onegines
 * @date 24.02.2025
 */
@RestController
@RequestMapping("/export")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/enterprise/{enterpriseId}")
    public ResponseEntity<?> exportEnterpriseData(
            @PathVariable UUID enterpriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String format) throws IOException {

        if ("csv".equalsIgnoreCase(format)) {
            String csvData = exportService.exportToCsv(enterpriseId, startDate, endDate);
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enterprise_data.csv")
                    .body(csvData);
        } else if ("json".equalsIgnoreCase(format)) {
            String jsonData = exportService.exportToJson(enterpriseId, startDate, endDate);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=enterprise_data.json")
                    .body(jsonData);
        } else {
            return ResponseEntity.badRequest().body("Unsupported format: " + format);
        }
    }
}