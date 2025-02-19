package ru.onegines.carpark.CarPark.dto;

import java.util.List;

/**
 * @author onegines
 * @date 05.11.2024
 */
public class ManagerDTO {
    private Long id;
    private String managerName;
    private Integer managerSalary;
    private List<Long> allEnterpiseId;
    private List<Long> allDriversId;
    private List<Long> allCarsId;

    public ManagerDTO(Long id, String managerName) {
        this.id = id;
        this.managerName = managerName;
        this.managerSalary = managerSalary;
        this.allEnterpiseId = allEnterpiseId;
        this.allDriversId = allDriversId;
        this.allCarsId = allCarsId;
    }

    public ManagerDTO(Long id, String username, List<Long> allInterprisesId, List<Long> allDriversId, List<Long> allCarsId) {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Integer getManagerSalary() {
        return managerSalary;
    }

    public void setManagerSalary(Integer managerSalary) {
        this.managerSalary = managerSalary;
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
