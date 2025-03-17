package ru.onegines.carpark.CarPark.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author onegines
 * @date 05.03.2025
 */

public class ReportDTO {
    private String name;
    private String period;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Map<LocalDate, Double> result;
    private double totalMileage;

    public ReportDTO(String name, String period, LocalDateTime startDate, LocalDateTime endDate, Map<LocalDate, Double> result, double totalMileage) {
        this.name = name;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.result = result;
        this.totalMileage = totalMileage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Map<LocalDate, Double> getResult() {
        return result;
    }

    public void setResult(Map<LocalDate, Double> result) {
        this.result = result;
    }

    public double getTotalMileage() {
        return totalMileage;
    }

    public void setTotalMileage(double totalMileage) {
        this.totalMileage = totalMileage;
    }
}

