package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.ManagerEnterprise;

import java.util.UUID;

@Repository
public interface ManagerEnterpriseRepository extends JpaRepository<ManagerEnterprise, Long> {
    void deleteByIdEnterpriseId(UUID enterpriseId);
}
