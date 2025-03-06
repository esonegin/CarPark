package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;
import ru.onegines.carpark.CarPark.enums.ReportPeriod;
import ru.onegines.carpark.CarPark.enums.ReportType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author onegines
 * @date 06.03.2025
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "report_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private ReportType type;

    @Enumerated(EnumType.STRING)
    private ReportPeriod period;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String result; // JSON-строка с результатами

    // Геттеры, сеттеры, конструкторы

    public Report(UUID id, String name, ReportType type, ReportPeriod period, LocalDateTime startDate, LocalDateTime endDate, String result) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.result = result;
    }

    public Report() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ReportType getType() {
        return type;
    }

    public void setType(ReportType type) {
        this.type = type;
    }

    public ReportPeriod getPeriod() {
        return period;
    }

    public void setPeriod(ReportPeriod period) {
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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
