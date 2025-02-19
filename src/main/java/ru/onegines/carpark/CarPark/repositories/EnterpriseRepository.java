package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Enterprise;

import java.util.List;
import java.util.Set;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    Set<Enterprise> findByManagers_Id(Long managerId);
}
