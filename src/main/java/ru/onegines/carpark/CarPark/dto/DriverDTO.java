package ru.onegines.carpark.CarPark.dto;

import java.util.List;

/**
 * @author onegines
 * @date 03.11.2024
 */
public class DriverDTO {
    private Long driver_id; // ID автомобиля
    private String driver_name; // ID бренда
    private int driver_salary;
    private Long enterprise_id;
    private List<Long> carIds;
    //private Long activeCar_id;

    public DriverDTO(Long driver_id, String driver_name, int driver_salary, Long enterprise_id, List<Long> carIds/*, Long activeCar_id*/) {
        this.driver_id = driver_id;
        this.driver_name = driver_name;
        this.driver_salary = driver_salary;
        this.enterprise_id = enterprise_id;
        this.carIds = carIds;
        //this.activeCar_id = activeCar_id;
    }



    public Long getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(Long driver_id) {
        this.driver_id = driver_id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public int getDriver_salary() {
        return driver_salary;
    }

    public void setDriver_salary(int driver_salary) {
        this.driver_salary = driver_salary;
    }



    public List<Long> getCarIds() {
        return carIds;
    }

    public void setCarIds(List<Long> carIds) {
        this.carIds = carIds;
    }

    public Long getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(Long enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    /*public Long getActiveCar_id() {
        return activeCar_id;
    }

    public void setActiveCar_id(Long activeCar_id) {
        this.activeCar_id = activeCar_id;
    }*/
}
