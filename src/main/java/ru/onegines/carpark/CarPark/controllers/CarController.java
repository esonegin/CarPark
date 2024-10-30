package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.repositories.BrandRepository;
import ru.onegines.carpark.CarPark.services.CarService;

import java.util.List;

/**
 * @author onegines
 * @date 29.10.2024
 */
@Controller
@ControllerAdvice
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;
    private final BrandRepository brandRepository;

    @Autowired
    public CarController(CarService carService, BrandRepository brandRepository) {
        this.carService = carService;
        this.brandRepository = brandRepository;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "cars/cars";
    }

    @GetMapping("/new")
    public String showNewCarForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("brands", brandRepository.findAll());  // Передаем список брендов
        return "cars/new";
    }

    @PostMapping()
    public String create(@ModelAttribute("car") Car car,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "cars/new";
        carService.save(car);
        return "redirect:/cars";
    }

    @PostMapping("/save")
    public String saveCar(@ModelAttribute Car car, @RequestParam Long brandId) {
        Brand brand = brandRepository.findById(brandId).orElse(null);  // Находим бренд по ID
        if (brand == null) {
            // Если бренд не найден, вернем сообщение об ошибке или перенаправим
            return "redirect:/cars/new?error=BrandNotFound";
        }
        car.setBrand(brand);  // Устанавливаем бренд для автомобиля
        carService.save(car);  // Сохраняем автомобиль
        return "redirect:/cars";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model car) {
        car.addAttribute("car", carService.findById(id));
        return "cars/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("car", carService.findById(id));
        model.addAttribute("brands", brandRepository.findAll());
        return "cars/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("car") Car car, @RequestParam Long brandId, BindingResult bindingResult,
                         @PathVariable("id") int id) {
        if (bindingResult.hasErrors()) {
            return "cars/edit";
        }
        Brand brand = brandRepository.findById(brandId).orElse(null);  // Находим бренд по ID
        if (brand == null) {
            // Если бренд не найден, вернем сообщение об ошибке или перенаправим
            return "redirect:/cars/new?error=BrandNotFound";
        }
        car.setBrand(brand);
        carService.update(id, car);
        return "redirect:/cars";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        carService.delete(id);
        return "redirect:/cars";
    }
}
