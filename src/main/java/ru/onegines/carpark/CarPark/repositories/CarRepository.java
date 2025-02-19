package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Enterprise;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByEnterpriseIn(Set<Enterprise> enterprises);

    List<Car> findByEnterpriseId(Long enterpriseId);
}
