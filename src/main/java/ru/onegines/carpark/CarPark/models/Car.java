package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

/**
 * @author onegines
 * @date 29.10.2024
 */
@Entity
@Table(name = "cars")
public class Car {
    public Car() {
    }

    public Car(Integer mileage, Brand brand) {
        this.mileage = mileage;
        this.brand = brand;
    }

    @Id
    @Column(name = "car_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int car_id;

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

    public int getCar_id() {
        return car_id;
    }

    public void setCar_id(int car_id) {
        this.car_id = car_id;
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



    @Override
    public String toString() {
        return "Car{" +
                "car_id=" + car_id +
                ", mileage=" + mileage +
                ", год_выпуска=" + год_выпуска +
                ", reserve=" + reserve +
                '}';
    }
}
