package ru.onegines.carpark.CarPark.dto;


import java.util.List;

/**
 * @author onegines
 * @date 03.11.2024
 */
public class EnterpriseDTO {
    private Long enterprise_id;
    private String enterprise_name;// ID бренда
    private String enterprise_city;
    private List<Long> allDriversId;
    private List<Long> allCarsId;


    public EnterpriseDTO(Long enterprise_id, String enterprise_name, String enterprise_city, List<Long> allDriversId, List<Long> allCarsId) {
        this.enterprise_id = enterprise_id;
        this.enterprise_name = enterprise_name;
        this.enterprise_city = enterprise_city;
        this.allDriversId = allDriversId;
        this.allCarsId = allCarsId;
    }

    public Long getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(Long enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    public String getEnterprise_name() {
        return enterprise_name;
    }

    public void setEnterprise_name(String enterprise_name) {
        this.enterprise_name = enterprise_name;
    }

    public String getEnterprise_city() {
        return enterprise_city;
    }

    public void setEnterprise_city(String enterprise_city) {
        this.enterprise_city = enterprise_city;
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
}
