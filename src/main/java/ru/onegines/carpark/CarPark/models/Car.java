package ru.onegines.carpark.CarPark.models;


import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author onegines
 * @date 29.10.2024
 */
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Long carId;

    @Column(name = "mileage")
    private int mileage;

    @Column(name = "год_выпуска")
    private int год_выпуска;

    @Column(name = "reserve")
    private int reserve;

    @Column(name = "number")
    private String number;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false) // Столбец для внешнего ключа
    private Enterprise enterprise;

    @ManyToMany
    @JoinTable(
            name = "car_drivers",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "driver_id")
    )
    private Set<Driver> drivers = new HashSet<>();


    // Поле для хранения активного водителя
    @OneToOne
    @JoinColumn(name = "active_driver_id")
    private Driver activeDriver;

    public Car() {
    }

    public Car(Integer mileage, Brand brand) {
        this.mileage = mileage;
        this.brand = brand;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public Set<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(Set<Driver> drivers) {
        this.drivers = drivers;
    }

    public Driver getActiveDriver() {
        return activeDriver;
    }

    public void setActiveDriver(Driver activeDriver) {
        this.activeDriver = activeDriver;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", mileage=" + mileage +
                ", год_выпуска=" + год_выпуска +
                ", reserve=" + reserve +
                ", number='" + number + '\'' +
                ", brand=" + brand +
                ", enterprise=" + enterprise +
                ", drivers=" + drivers +
                ", activeDriver=" + activeDriver +
                '}';
    }
}
