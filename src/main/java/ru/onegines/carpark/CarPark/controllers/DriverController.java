package ru.onegines.carpark.CarPark.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.dto.DriverDTO;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.repositories.DriverRepository;
import ru.onegines.carpark.CarPark.security.ManagerDetails;
import ru.onegines.carpark.CarPark.services.DriverService;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

/**
 * @author onegines
 * @date 01.11.2024
 */
@Controller

@ControllerAdvice
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;
    private final DriverRepository driverRepository;

    public DriverController(DriverService driverService, DriverRepository driverRepository) {
        this.driverService = driverService;
        this.driverRepository = driverRepository;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<Driver> drivers = driverService.findAll();
        model.addAttribute("drivers", drivers);
        return "drivers/drivers";
    }

    @GetMapping("/{id}")
    public String getDriverCars(@PathVariable Long id, Model model, Principal principal) {
        Long managerId = ((ManagerDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getManager().getId();
        Optional<Driver> driverOptional = driverService.getDriverWithCars(id);
        if (driverService.isManagerHasAccess(managerId, id)) {
            if (driverOptional.isPresent()) {
                model.addAttribute("driver", driverOptional.get());
                // Название шаблона
            } else {
                return "error"; // Шаблон ошибки, если водитель не найден
            }
        } else {
            return "redirect:/logout";
        }
        return "drivers/show";
    }

    @GetMapping("/new")
    public String showNewDriverForm(Model model) {
        model.addAttribute("driver", new Driver());
        return "drivers/new";
    }

    @PostMapping("/save")
    public String saveDriver(@ModelAttribute Driver driver) {
        driverService.save(driver);
        return "redirect:/drivers";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("driver", driverService.findById(id));
        return "drivers/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("driver") Driver driver, BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "drivers/edit";
        }
        driverService.update(id, driver);
        return "redirect:/drivers";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        driverService.delete(id);
        return "redirect:/drivers";
    }

    @GetMapping("/api/drivers")
    public ResponseEntity<List<DriverDTO>> getAllDrivers() {
        List<DriverDTO> cars = driverService.getAllDrivers();
        return ResponseEntity.ok(cars);
    }
}
