package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;

import java.util.List;
import java.util.Set;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {

}