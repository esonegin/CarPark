package ru.onegines.carpark.CarPark.models;

import jakarta.persistence.*;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author onegines
 * @date 01.11.2024
 */
@Entity
@Table(name = "enterprises")
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enterprise_id")
    private Long id;

    @ManyToMany(mappedBy = "enterprises")
    private Set<Manager> managers;

    @Column(name = "name")
    private String name;

    @Column(name = "city")
    private String city;

    @Column(nullable = true)
    private String timeZone;

    // Один ко многим: предприятию принадлежит несколько автомобилей
    @OneToMany(mappedBy = "enterprise")
    private Set<Car> cars;

    @OneToMany(mappedBy = "enterprise")
    private List<Driver> drivers;

    public Enterprise() {

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }

    public List<Driver> getDrivers() {
        return drivers;
    }

    public void setDrivers(List<Driver> drivers) {
        this.drivers = drivers;
    }

    public Set<Manager> getManagers() {
        return managers;
    }

    public void setManagers(Set<Manager> managers) {
        this.managers = managers;
    }

    public String getTimeZone() {
        return timeZone != null ? timeZone : "UTC";
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public ZoneId getZoneId() {
        return ZoneId.of(getTimeZone());
    }

}
