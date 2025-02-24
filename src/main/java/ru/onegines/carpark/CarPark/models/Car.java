package ru.onegines.carpark.CarPark.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;
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
    private Integer mileage;

    @Column(name = "год_выпуска")
    private Integer годВыпуска;

    @Column(name = "reserve")
    private Integer reserve;

    @Column(name = "number")
    private String number;

    @Column(name = "created_at")
    private LocalDateTime purchaseDateTime;

    @ManyToOne
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne
    @JoinColumn(name = "enterprise_id", nullable = false) // Внешний ключ
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

    private String purchaseDateTimeInEnterpriseTimeZone;

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

    public int getГодВыпуска() {
        return годВыпуска;
    }

    public void setГодВыпуска(int годВыпуска) {
        this.годВыпуска = годВыпуска;
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

    public LocalDateTime getPurchaseDateTime() {
        return purchaseDateTime;
    }

    public String getPurchaseDateTimeInEnterpriseTimeZone() {
        return purchaseDateTimeInEnterpriseTimeZone;
    }

    public void setPurchaseDateTimeInEnterpriseTimeZone(String purchaseDateTimeInEnterpriseTimeZone) {
        this.purchaseDateTimeInEnterpriseTimeZone = purchaseDateTimeInEnterpriseTimeZone;
    }

    public void setPurchaseDateTime(LocalDateTime purchaseDateTime) {
        this.purchaseDateTime = purchaseDateTime;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public void setГодВыпуска(Integer годВыпуска) {
        this.годВыпуска = годВыпуска;
    }

    public void setReserve(Integer reserve) {
        this.reserve = reserve;
    }

    @Override
    public String toString() {
        return "Car{" +
                "carId=" + carId +
                ", mileage=" + mileage +
                ", год_выпуска=" + годВыпуска +
                ", reserve=" + reserve +
                ", number='" + number + '\'' +
                ", brand=" + brand +
                ", enterprise=" + enterprise +
                ", drivers=" + drivers +
                ", activeDriver=" + activeDriver +
                '}';
    }


}
