package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import ru.onegines.carpark.CarPark.enums.ReportType;

import java.util.UUID;

/**
 * @author onegines
 * @date 06.03.2025
 */
@Entity
@DiscriminatorValue("MILEAGE")
public class MileageReport extends Report {
    private UUID carId; // Идентификатор машины

    public MileageReport() {
        super.setType(ReportType.MILEAGE);
    }

    public UUID getCarId() {
        return carId;
    }

    public void setCarId(UUID carId) {
        this.carId = carId;
    }

}

