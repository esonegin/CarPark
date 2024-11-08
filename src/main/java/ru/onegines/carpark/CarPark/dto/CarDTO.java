package ru.onegines.carpark.CarPark.dto;

import java.util.List;

/**
 * @author onegines
 * @date 31.10.2024
 */
public class CarDTO {
    private Long car_id; // ID автомобиля
    private int brand_id; // ID бренда
    private int mileage;
    private int год_выпуска;
    private int reserve;
    private String number;
    private Long active_driver_id;
    private List<Long> allDriversId;
    private Long enterprise_id;


    public CarDTO(Long car_id, int brand_id, int mileage, int год_выпуска, int reserve, String number, Long active_driver_id, List<Long> allDriversId, Long enterprise_id) {
        this.car_id = car_id;
        this.brand_id = brand_id;
        this.mileage = mileage;
        this.год_выпуска = год_выпуска;
        this.reserve = reserve;
        this.number = number;
        this.active_driver_id = active_driver_id;
        this.allDriversId = allDriversId;
        this.enterprise_id = enterprise_id;
    }

    public Long getCar_id() {
        return car_id;
    }

    public void setCar_id(Long car_id) {
        this.car_id = car_id;
    }

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getГод_выпуска() {
        return год_выпуска;
    }

    public void setГод_выпуска(int год_выпуска) {
        this.год_выпуска = год_выпуска;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Long getActive_driver_id() {
        return active_driver_id;
    }

    public void setActive_driver_id(Long active_driver_id) {
        this.active_driver_id = active_driver_id;
    }

    public Long getEnterprise_id() {
        return enterprise_id;
    }

    public void setEnterprise_id(Long enterprise_id) {
        this.enterprise_id = enterprise_id;
    }

    public List<Long> getAllDriversId() {
        return allDriversId;
    }

    public void setAllDriversId(List<Long> allDriversId) {
        this.allDriversId = allDriversId;
    }
}
