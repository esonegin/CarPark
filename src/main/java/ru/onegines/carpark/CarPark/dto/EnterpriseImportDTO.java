package ru.onegines.carpark.CarPark.dto;

import lombok.Data;
import java.util.List;

/**
 * @author onegines
 * @date 28.02.2025
 */
@Data
public class EnterpriseImportDTO {
    private EnterpriseDTO enterprise;
    private List<CarImportDTO> cars;
}

