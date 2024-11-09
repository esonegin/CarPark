package ru.onegines.carpark.CarPark.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.security.ManagerDetails;
import ru.onegines.carpark.CarPark.services.CarService;
import ru.onegines.carpark.CarPark.services.DriverService;
import ru.onegines.carpark.CarPark.services.EnterpriseService;
import ru.onegines.carpark.CarPark.services.ManagerService;

import java.security.Principal;
import java.util.List;
import java.util.Set;

/**
 * @author onegines
 * @date 02.11.2024
 */
@Controller

@ControllerAdvice
@RequestMapping("/enterprises")
public class EnterpriseController {
    private final EnterpriseService enterpriseService;
    private final EnterpriseRepository enterpriseRepository;
    private final CarService carService;
    private final DriverService driverService;
    private final ManagerService managerService;

    public EnterpriseController(EnterpriseService enterpriseServiceService, EnterpriseRepository enterpriseRepository, CarService carService, DriverService driverService, ManagerService managerService) {
        this.enterpriseService = enterpriseServiceService;
        this.enterpriseRepository = enterpriseRepository;
        this.carService = carService;
        this.driverService = driverService;
        this.managerService = managerService;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<Enterprise> enterprises = enterpriseService.findAll();
        model.addAttribute("enterprises", enterprises);
        return "enterprises/enterprises";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model enterprise, Principal principal) {
        Long managerId = ((ManagerDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getManager().getId();
        if (enterpriseService.isManagerHasAccess(managerId, (long) id)) {
            enterprise.addAttribute("enterprise", enterpriseService.findById(id));
        } else {
            return "redirect:/logout";
        }
        enterprise.addAttribute("enterprise", enterpriseService.findById(id));
        return "enterprises/show";
    }

    @GetMapping("/new")
    public String showNewEnterpriseForm(Model model) {
        model.addAttribute("enterprise", new Enterprise());
        return "enterprises/new";
    }

    @PostMapping("/save")
    public String saveEnterprise(@ModelAttribute Enterprise enterprise) {
        enterpriseService.save(enterprise);
        return "redirect:/enterprises";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("enterprise", enterpriseService.findById(id));
        return "enterprises/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("enterprise") Enterprise enterprise, BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "enterprises/edit";
        }
        enterpriseService.update(id, enterprise);
        return "redirect:/enterprises";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        enterpriseService.delete(id);
        return "redirect:/enterprises";
    }

    @GetMapping("/{id}/assignCars")
    public String showAssignCarsToEnterpriseForm(@PathVariable Long id, Model model) {
        Enterprise enterprise = enterpriseService.findById(id);
        List<Car> availableCars = carService.getAvailableCars(); // Автомобили без привязки к предприятию
        model.addAttribute("enterprise", enterprise);
        model.addAttribute("availableCars", availableCars);
        return "enterprises/enterpriseAssignCars";  // имя Thymeleaf-шаблона
    }

    @GetMapping("/{id}/assignDrivers")
    public String showAssignCDriversToEnterpriseForm(@PathVariable Long id, Model model) {
        Enterprise enterprise = enterpriseService.findById(id);
        List<Driver> drivers = driverService.findAll(); // Автомобили без привязки к предприятию
        model.addAttribute("enterprise", enterprise);
        model.addAttribute("drivers", drivers);
        return "enterprises/enterpriseAssignDrivers";  // имя Thymeleaf-шаблона
    }

    @PostMapping("/{id}/assignDrivers")
    public String assignDriversToEnterprise(@PathVariable Long id, @RequestParam Long driver_id) {
        enterpriseService.assignDriver(id, driver_id);
        return "redirect:/enterprises/" + id;
    }

    @GetMapping("/api")
    public ResponseEntity<List<EnterpriseDTO>> getAllEnterprises() {
        List<EnterpriseDTO> enterprises = enterpriseService.getAllEnterprises();
        return ResponseEntity.ok(enterprises);
    }

    @GetMapping("/available")
    public Set<Enterprise> getEnterprisesForCurrentManager() {
        // Получаем текущего менеджера из контекста безопасности
        Manager currentManager = (Manager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Возвращаем только те предприятия, к которым у менеджера есть доступ
        return currentManager.getEnterprises();
    }
}
