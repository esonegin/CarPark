package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.onegines.carpark.CarPark.enums.ReportType;

/**
 * @author onegines
 * @date 11.03.2025
 */
@Entity
@DiscriminatorValue("SALARY")
public class SalaryReport extends Report {
    private double totalSalary;
    public SalaryReport() {
        super.setType(ReportType.SALARY);
    }

    public double getTotalSalary() {
        return totalSalary;
    }

    public void setTotalSalary(double totalSalary) {
        this.totalSalary = totalSalary;
    }
}

