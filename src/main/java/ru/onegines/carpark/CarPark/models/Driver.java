package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author onegines
 * @date 01.11.2024
 */
@Entity
@Table(name = "drivers")
public class Driver {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "salary")
    private int salary;

    @ManyToOne
    @JoinColumn(name = "enterprise_id") // Внешний ключ
    private Enterprise enterprise;

    @ManyToMany(mappedBy = "drivers")
    private Set<Car> cars = new HashSet<>();

    // Поле для хранения активного автомобиля
    @OneToOne(mappedBy = "activeDriver")
    private Car activeCar;

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

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }

    public Car getActiveCar() {
        return activeCar;
    }

    public void setActiveCar(Car activeCar) {
        this.activeCar = activeCar;
    }

    public void setActive(boolean b) {
    }
}
