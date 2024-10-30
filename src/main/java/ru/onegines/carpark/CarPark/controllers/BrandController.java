package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.services.BrandService;

import java.util.List;

/**
 * @author onegines
 * @date 30.10.2024
 */
@Controller
@ControllerAdvice
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/brands")
    public String adminPage(Model model) {
        List<Brand> brands = brandService.findAll();
        model.addAttribute("brands", brands);
        return "brands";
    }
}
