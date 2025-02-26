package ru.onegines.carpark.CarPark.dto;


import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author onegines
 * @date 03.11.2024
 */
public class EnterpriseDTO {
    private UUID enterpriseId;
    private String enterpriseName;
    private String enterpriseCity;
    private List<UUID> allDriversId;
    private List<UUID> allCarsId;
    private String timeZone;

    public EnterpriseDTO(UUID enterpriseId, String enterpriseName, String enterpriseCity, List<UUID> allDriversId, List<UUID> allCarsId, String timeZone) {
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.enterpriseCity = enterpriseCity;
        this.allDriversId = allDriversId;
        this.allCarsId = allCarsId;
        this.timeZone = timeZone;
    }



    public EnterpriseDTO(UUID id, String name, String city, List<Driver> drivers, Set<Car> cars) {
    }

    public UUID getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(UUID enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getEnterpriseCity() {
        return enterpriseCity;
    }

    public void setEnterpriseCity(String enterpriseCity) {
        this.enterpriseCity = enterpriseCity;
    }

    public List<UUID> getAllDriversId() {
        return allDriversId;
    }

    public void setAllDriversId(List<UUID> allDriversId) {
        this.allDriversId = allDriversId;
    }

    public List<UUID> getAllCarsId() {
        return allCarsId;
    }

    public void setAllCarsId(List<UUID> allCarsId) {
        this.allCarsId = allCarsId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }


}
