package ru.onegines.carpark.CarPark.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.repositories.CarRepository;
import ru.onegines.carpark.CarPark.repositories.ManagerEnterpriseRepository;
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
    private final CarService carService;
    private final DriverService driverService;
    private final ManagerService managerService;
    private final CarRepository carRepository;
    private final ManagerEnterpriseRepository managerEnterpriseRepository;


    public EnterpriseController(EnterpriseService enterpriseServiceService, CarService carService, DriverService driverService, ManagerService managerService, CarRepository carRepository, ManagerEnterpriseRepository managerEnterpriseRepository) {
        this.enterpriseService = enterpriseServiceService;
        this.carService = carService;
        this.driverService = driverService;
        this.managerService = managerService;
        this.carRepository = carRepository;
        this.managerEnterpriseRepository = managerEnterpriseRepository;
    }

    @GetMapping("")
    public ResponseEntity<List<EnterpriseDTO>> getAllEnterprises() {
        List<EnterpriseDTO> enterprises = enterpriseService.getAllEnterprises();
        return ResponseEntity.ok(enterprises);
    }

    //Bill - доступны 2, 3 Недоступны 1
    @PreAuthorize("@enterpriseService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable("id") long id, Model enterprise, Principal principal) {
        EnterpriseDTO enterpriseDTO = enterpriseService.getEnterpriseDTO(id);
        if (enterpriseDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(enterpriseDTO);
    }

    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/new")
    public String showNewEnterpriseForm(Model model) {
        model.addAttribute("enterprise", new Enterprise());
        return "enterprises/new";
    }


    @PostMapping("/save")
    public String saveEnterprise(@ModelAttribute Enterprise enterprise, Principal principal) {
        enterpriseService.save(enterprise);
        Long managerId = ((ManagerDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getManager().getId();
        managerService.assignEnterpriseToManager(managerId, enterprise.getId());
        return "redirect:/enterprises";
    }

    @PreAuthorize("@enterpriseService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("enterprise", enterpriseService.findById(id));
        return "enterprises/edit";
    }

    @PreAuthorize("@enterpriseService.isManagerHasAccess(authentication.principal.id, #id)")
    @PatchMapping("/{id}")
    public String update(@ModelAttribute("enterprise") Enterprise enterprise, BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "enterprises/edit";
        }
        enterpriseService.update(id, enterprise);
        return "redirect:/enterprises";
    }

    @PreAuthorize("@enterpriseService.isManagerHasAccess(authentication.principal.id, #id)")
    @Transactional
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        // Обновляем машины, чтобы `enterprise_id` стал `null`
        List<Car> cars = carService.findByEnterpriseId(id);
        for (Car car : cars) {
            car.setEnterprise(null);
        }
        carRepository.saveAll(cars); // Сохраняем изменения в базе данных
        managerEnterpriseRepository.deleteByIdEnterpriseId(id);
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

    @GetMapping("/available")
    public Set<Enterprise> getEnterprisesForCurrentManager() {
        // Получаем текущего менеджера из контекста безопасности
        Manager currentManager = (Manager) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Возвращаем только те предприятия, к которым у менеджера есть доступ
        return currentManager.getEnterprises();
    }
}
