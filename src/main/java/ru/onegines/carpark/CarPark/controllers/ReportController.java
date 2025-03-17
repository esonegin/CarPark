package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.onegines.carpark.CarPark.dto.ReportDTO;
import ru.onegines.carpark.CarPark.services.EnterpriseService;
import ru.onegines.carpark.CarPark.services.ReportService;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author onegines
 * @date 05.03.2025
 */
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final EnterpriseService enterpriseService;

    @Autowired
    public ReportController(ReportService reportService, EnterpriseService enterpriseService) {
        this.reportService = reportService;
        this.enterpriseService = enterpriseService;
    }

    @GetMapping("/mileage")
    public ResponseEntity<ReportDTO> getMileageReport(
            @RequestParam UUID carId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam String period) {
        return ResponseEntity.ok(reportService.calculateMileage(carId, startDate, endDate, period));
    }

    @GetMapping("/salary")
    public ResponseEntity<ReportDTO> getSalaryReport(
            @RequestParam UUID enterpriseId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam String period) {
        return ResponseEntity.ok(reportService.calculateSalary(enterpriseId, startDate, endDate, period));
    }
}
