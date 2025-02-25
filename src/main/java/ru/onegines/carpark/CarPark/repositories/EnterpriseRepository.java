package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Enterprise;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {
    Set<Enterprise> findByManagers_Id(Long managerId);



    //@Query("SELECT e.timeZone FROM Enterprise e WHERE e.id = :enterpriseId")
    //String findTimeZoneById(Long enterpriseId);

    @Query("SELECT e.timeZone FROM Enterprise e WHERE e.id = :enterpriseId")
    String findTimeZoneById(@Param("enterpriseId") UUID enterpriseId);

    Optional<Enterprise> findById(UUID id);

    void deleteById(UUID id);
}