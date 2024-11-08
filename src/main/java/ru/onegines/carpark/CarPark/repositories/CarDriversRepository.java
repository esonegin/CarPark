package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.CarDriver;

@Repository
public interface CarDriversRepository extends JpaRepository<CarDriver, Long> {

}
