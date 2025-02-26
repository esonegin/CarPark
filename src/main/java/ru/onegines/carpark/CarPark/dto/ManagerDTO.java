package ru.onegines.carpark.CarPark.dto;

import java.util.List;
import java.util.UUID;

/**
 * @author onegines
 * @date 05.11.2024
 */
public class ManagerDTO {
    private UUID id;
    private String managerName;
    private Integer managerSalary;
    private List<UUID> allEnterpiseId;
    private List<UUID> allDriversId;
    private List<UUID> allCarsId;

    public ManagerDTO(UUID id, String managerName) {
        this.id = id;
        this.managerName = managerName;
        this.managerSalary = managerSalary;
        this.allEnterpiseId = allEnterpiseId;
        this.allDriversId = this.allDriversId;
        this.allCarsId = this.allCarsId;
    }

    public ManagerDTO(UUID id, String username, List<UUID> allInterprisesId, List<UUID> allDriversId, List<UUID> allCarsId) {
    }


    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public List<UUID> getAllEnterpiseId() {
        return allEnterpiseId;
    }

    public void setAllEnterpiseId(List<UUID> allEnterpiseId) {
        this.allEnterpiseId = allEnterpiseId;
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


}
