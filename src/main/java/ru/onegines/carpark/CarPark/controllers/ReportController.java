package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
@Controller
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/salary")
    public String getSalaryReport(@RequestParam UUID enterpriseId,
                                  @RequestParam String startDate,
                                  @RequestParam String endDate,
                                  @RequestParam String period,
                                  Model model) {
        ReportDTO report = reportService.calculateSalary(enterpriseId,
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                period);

        model.addAttribute("reportType", "Зарплата водителей");
        model.addAttribute("report", report);
        return "managers/reportResult";
    }

    @GetMapping("/mileage")
    public String getMileageReport(@RequestParam UUID enterpriseId,
                                   @RequestParam UUID carId,
                                   @RequestParam String startDate,
                                   @RequestParam String endDate,
                                   @RequestParam String period,
                                   Model model) {
        ReportDTO report = reportService.calculateMileage(carId, enterpriseId,
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(endDate),
                period);

        model.addAttribute("reportType", "Пробег автомобиля");
        model.addAttribute("report", report);
        return "managers/reportResult";
    }
}
