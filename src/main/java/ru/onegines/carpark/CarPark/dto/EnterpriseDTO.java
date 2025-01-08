package ru.onegines.carpark.CarPark.dto;


import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;

import java.util.List;
import java.util.Set;

/**
 * @author onegines
 * @date 03.11.2024
 */
public class EnterpriseDTO {
    private Long enterpriseId;
    private String enterpriseName;
    private String enterpriseCity;
    private List<Long> allDriversId;
    private List<Long> allCarsId;
    private String timeZone;

    public EnterpriseDTO(Long enterpriseId, String enterpriseName, String enterpriseCity, List<Long> allDriversId, List<Long> allCarsId, String timeZone) {
        this.enterpriseId = enterpriseId;
        this.enterpriseName = enterpriseName;
        this.enterpriseCity = enterpriseCity;
        this.allDriversId = allDriversId;
        this.allCarsId = allCarsId;
        this.timeZone = timeZone;
    }



    public EnterpriseDTO(Long id, String name, String city, List<Driver> drivers, Set<Car> cars) {
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
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

    public List<Long> getAllDriversId() {
        return allDriversId;
    }

    public void setAllDriversId(List<Long> allDriversId) {
        this.allDriversId = allDriversId;
    }

    public List<Long> getAllCarsId() {
        return allCarsId;
    }

    public void setAllCarsId(List<Long> allCarsId) {
        this.allCarsId = allCarsId;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }


}
