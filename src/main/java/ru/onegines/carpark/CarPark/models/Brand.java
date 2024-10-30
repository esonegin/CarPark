package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import java.util.List;

/**
 * @author onegines
 * @date 30.10.2024
 */
@Entity
@Table(name = "brands")
public class Brand {

    @Id
    @Column(name = "brand_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int brand_id;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @Column(name = "brand_name")
    private String brand_name;

    @Column(name = "tank_volume")
    private int tank_volume;

    @Column(name = "lifting_capacity")
    private int lifting_capacity;

    @Column(name = "seats")
    private int seats;

    public int getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(int brand_id) {
        this.brand_id = brand_id;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public int getTank_volume() {
        return tank_volume;
    }

    public void setTank_volume(int tank_volume) {
        this.tank_volume = tank_volume;
    }

    public int getLifting_capacity() {
        return lifting_capacity;
    }

    public void setLifting_capacity(int lifting_capacity) {
        this.lifting_capacity = lifting_capacity;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }
}
