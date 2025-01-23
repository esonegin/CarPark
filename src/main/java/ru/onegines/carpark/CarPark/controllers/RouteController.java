package ru.onegines.carpark.CarPark.controllers;

import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.RouteRequestDto;
import ru.onegines.carpark.CarPark.services.RouteGenerationService;
/**
 * @author onegines
 * @date 23.01.2025
 */
@RestController
@RequestMapping("/route")
public class RouteController {

    private final RouteGenerationService routeGenerationService;

    public RouteController(RouteGenerationService routeGenerationService) {
        this.routeGenerationService = routeGenerationService;
    }

    @PostMapping("/generate/{carId}/{radius}/{trackLength}/{pointStep}")
    public String generateRoute(@PathVariable Long carId,
                                @PathVariable double radius,
                                @PathVariable double trackLength,
                                @PathVariable int pointStep,
                                @RequestBody RouteRequestDto routeRequest) {
        routeGenerationService.startRouteGeneration(carId, radius, trackLength, pointStep,
                routeRequest.getStartLat(),
                routeRequest.getStartLon(),
                routeRequest.getEndLat(),
                routeRequest.getEndLon());
        return "Маршрут для машины " + carId + " успешно сгенерирован!";
    }
}