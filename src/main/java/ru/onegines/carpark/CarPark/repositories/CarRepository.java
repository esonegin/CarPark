package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Enterprise;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {

    List<Car> findByEnterpriseIn(Set<Enterprise> enterprises);

    List<Car> findByEnterpriseId(UUID enterpriseId);

    Optional<Car> findByCarId(UUID id);

    void deleteByCarId(UUID id);
}