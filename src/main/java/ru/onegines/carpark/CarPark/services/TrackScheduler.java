package ru.onegines.carpark.CarPark.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author onegines
 * @date 12.01.2025
 */
@Service
public class TrackScheduler {

    private final TrackGenerationService trackGenerationService;
    private final Set<Long> activeCarIds = ConcurrentHashMap.newKeySet(); // Потокобезопасный Set

    public TrackScheduler(TrackGenerationService trackGenerationService) {
        this.trackGenerationService = trackGenerationService;
    }

    // Добавить carId для отслеживания
    public void addCarId(Long carId) {
        activeCarIds.add(carId);
    }

    // Удалить carId из отслеживания
    public void removeCarId(Long carId) {
        activeCarIds.remove(carId);
    }

    // Планировщик вызывает генерацию точек для всех активных carId
    @Scheduled(fixedRate = 10000) // Каждые 10 секунд
    public void generateTrackPoints() {
        for (Long carId : activeCarIds) {
            trackGenerationService.addTrackPoint(carId, 1.0); // Радиус задается вручную
        }
    }
}