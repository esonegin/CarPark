package ru.onegines.carpark.CarPark.dto;

import java.util.List;

/**
 * @author onegines
 * @date 05.11.2024
 */
public class ManagerDTO {
    private Long id;
    private String manager_name;
    private Integer manager_salary;
    private List<Long> allEnterpiseId;
    private List<Long> allDriversId;
    private List<Long> allCarsId;

    public ManagerDTO(Long id, String manager_name, Integer manager_salary, List<Long> allEnterpiseId, List<Long> allDriversId, List<Long> allCarsId) {
        this.id = id;
        this.manager_name = manager_name;
        this.manager_salary = manager_salary;
        this.allEnterpiseId = allEnterpiseId;
        this.allDriversId = allDriversId;
        this.allCarsId = allCarsId;
    }



    public Long getId() {
        return id;
    }

    public void setManager_id(Long id) {
        this.id = id;
    }

    public String getManager_name() {
        return manager_name;
    }

    public void setManager_name(String manager_name) {
        this.manager_name = manager_name;
    }

    public Integer getManager_salary() {
        return manager_salary;
    }

    public void setManager_salary(Integer manager_salary) {
        this.manager_salary = manager_salary;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getAllEnterpiseId() {
        return allEnterpiseId;
    }

    public void setAllEnterpiseId(List<Long> allEnterpiseId) {
        this.allEnterpiseId = allEnterpiseId;
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
