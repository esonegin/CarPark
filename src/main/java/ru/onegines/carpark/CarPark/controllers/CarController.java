package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import ru.onegines.carpark.CarPark.models.Vehicle;
import ru.onegines.carpark.CarPark.services.CarService;

import java.util.List;

/**
 * @author onegines
 * @date 29.10.2024
 */
@Controller
@ControllerAdvice
public class CarController {
    private final CarService carService;

    @Autowired
    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/cars")
    public String adminPage(Model model) {
        List<Vehicle> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "cars";
    }
}
