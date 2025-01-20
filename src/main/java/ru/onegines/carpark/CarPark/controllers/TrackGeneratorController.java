package ru.onegines.carpark.CarPark.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.onegines.carpark.CarPark.services.TrackScheduler;

/**
 * @author onegines
 * @date 13.01.2025
 */
@RestController
@RequestMapping("/track-generator")
public class TrackGeneratorController {

    private final TrackScheduler scheduledTrackGenerator;

    public TrackGeneratorController(TrackScheduler scheduledTrackGenerator) {
        this.scheduledTrackGenerator = scheduledTrackGenerator;
    }

    @PostMapping("/start/{carId}")
    public ResponseEntity<Void> startTracking(@PathVariable Long carId) {
        scheduledTrackGenerator.addCarId(carId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/stop/{carId}")
    public String stopTracking(@PathVariable Long carId) {
        scheduledTrackGenerator.removeCarId(carId);
        return "Tracking stopped for carId: " + carId;
    }
}