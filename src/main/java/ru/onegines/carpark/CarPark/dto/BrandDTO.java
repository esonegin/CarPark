package ru.onegines.carpark.CarPark.dto;

import java.util.List;

/**
 * @author onegines
 * @date 09.12.2024
 */
public class BrandDTO {
    private Long id;
    private String name;
    private Integer tankVolume;
    private Integer liftingCapacity;
    private Integer seats;

    public BrandDTO(Long id, String name, Integer tankVolume, Integer liftingCapacity, Integer seats) {
        this.id = id;
        this.name = name;
        this.tankVolume = tankVolume;
        this.liftingCapacity = liftingCapacity;
        this.seats = seats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTankVolume() {
        return tankVolume;
    }

    public void setTankVolume(Integer tankVolume) {
        this.tankVolume = tankVolume;
    }

    public Integer getLiftingCapacity() {
        return liftingCapacity;
    }

    public void setLiftingCapacity(Integer liftingCapacity) {
        this.liftingCapacity = liftingCapacity;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }
}
