package ru.onegines.carpark.CarPark.dto;

/**
 * @author onegines
 * @date 01.03.2025
 */
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ImportDataDTO {
    private EnterpriseDTO enterprise;
    private List<CarWithTripsDTO> cars;

    public ImportDataDTO() {
    }

    public EnterpriseDTO getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(EnterpriseDTO enterprise) {
        this.enterprise = enterprise;
    }

    public List<CarWithTripsDTO> getCars() {
        return cars;
    }

    public void setCars(List<CarWithTripsDTO> cars) {
        this.cars = cars;
    }
}