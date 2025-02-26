package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.Brand;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface BrandRepository extends JpaRepository<Brand, UUID> {
    Optional<Brand> findById(UUID brandId);
}
