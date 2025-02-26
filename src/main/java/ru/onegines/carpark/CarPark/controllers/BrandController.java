package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.services.BrandService;

import java.util.List;
import java.util.UUID;

/**
 * @author onegines
 * @date 30.10.2024
 */
@RestController
@ControllerAdvice
@RequestMapping("/brands")
public class BrandController {
    private final BrandService brandService;

    @Autowired
    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<Brand> brands = brandService.findAll();
        model.addAttribute("brands", brands);
        return "brands/brands";
    }

    @GetMapping("/api/brands")
    public ResponseEntity<List<Brand>> getAllBrands() {
        List<Brand> brands = brandService.findAll();
        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") UUID id, Model brand) {
        brand.addAttribute("brand", brandService.findById(id));
        return "brands/show";
    }

    @GetMapping("/new")
    public String showNewCarForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "brands/new";
    }

    @PostMapping("/save")
    public String saveBrand(@ModelAttribute Brand brand) {
        brandService.save(brand);
        return "redirect:/brands";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") UUID id) {
        model.addAttribute("brand", brandService.findById(id));
        return "brands/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("brand") Brand brand, BindingResult bindingResult,
                         @PathVariable("id") UUID id) {
        if (bindingResult.hasErrors()) {
            return "brands/edit";
        }
        brandService.update(id, brand);
        return "redirect:/brands";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") UUID id) {
        brandService.delete(id);
        return "redirect:/brands";
    }
}
