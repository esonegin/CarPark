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
    private Long id;

    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Car> cars;

    @Column(name = "name")
    private String name;

    @Column(name = "tank_volume")
    private int tank_volume;

    @Column(name = "lifting_capacity")
    private int lifting_capacity;

    @Column(name = "seats")
    private int seats;

    public Long getId() {
        return id;
    }

    public void setId(Long brand_id) {
        this.id = id;
    }

    public List<Car> getCars() {
        return cars;
    }

    public void setCars(List<Car> cars) {
        this.cars = cars;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
