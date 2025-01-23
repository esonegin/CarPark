package ru.onegines.carpark.CarPark.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.onegines.carpark.CarPark.models.RoutePoint;

@Repository
public interface RoutePointRepository extends JpaRepository<RoutePoint, Long> {
    // Вы можете добавить методы для кастомных запросов, если необходимо
}
