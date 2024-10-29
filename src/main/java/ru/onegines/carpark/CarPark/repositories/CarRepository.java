package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Vehicle;

@Repository
public interface CarRepository extends JpaRepository<Vehicle, Long> {
}
