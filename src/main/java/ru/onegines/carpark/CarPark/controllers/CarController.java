package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.exceptions.DuplicateActiveDriverException;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.repositories.BrandRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;
import ru.onegines.carpark.CarPark.security.ManagerDetails;
import ru.onegines.carpark.CarPark.services.CarService;
import ru.onegines.carpark.CarPark.services.DriverService;

import java.security.Principal;
import java.util.List;
import java.util.Set;


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
    private final DriverService driverService;
    private final EnterpriseRepository enterpriseRepository;

    @Autowired
    public CarController(CarService carService, BrandRepository brandRepository, DriverService driverService, EnterpriseRepository enterpriseRepository) {
        this.carService = carService;
        this.brandRepository = brandRepository;
        this.driverService = driverService;
        this.enterpriseRepository = enterpriseRepository;
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<Car> cars = carService.findAll();
        model.addAttribute("cars", cars);
        return "cars/cars";
    }

    @GetMapping("/api/cars")
    public ResponseEntity<List<CarDTO>> getAllCars() {
        List<CarDTO> cars = carService.getAllCars();
        return ResponseEntity.ok(cars);
    }

    @GetMapping("/new")
    public String showNewCarForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("brands", brandRepository.findAll());  // Передаем список брендов
        model.addAttribute("enterprises", enterpriseRepository.findAll());
        return "cars/new";
    }

    @PostMapping("/save")
    public String saveCar(@ModelAttribute Car car, @RequestParam Long brandId, @RequestParam Long enterprise_id) {
        Brand brand = brandRepository.findById(brandId).orElse(null);  // Находим бренд по ID
        if (brand == null) {
            // Если бренд не найден, вернем сообщение об ошибке или перенаправим
            return "redirect:/cars/new?error=BrandNotFound";
        }
        Enterprise enterprise = enterpriseRepository.findById(enterprise_id).orElse(null);  // Находим бренд по ID
        if (enterprise == null) {
            // Если бренд не найден, вернем сообщение об ошибке или перенаправим
            return "redirect:/cars/new?error=EnterpriseNotFound";
        }
        car.setBrand(brand);
        car.setEnterprise(enterprise);// Устанавливаем бренд для автомобиля
        carService.save(car);  // Сохраняем автомобиль
        return "redirect:/cars";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model car, Principal principal) {
        Long managerId = ((ManagerDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getManager().getId();
        if (carService.isManagerHasAccess(managerId, id)) {
            car.addAttribute("car", carService.findById(id));
        } else {
            return "redirect:/logout";
        }
        return "cars/show";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("car", carService.findById(id));
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("enterprisse", enterpriseRepository.findAll());
        return "cars/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("car") Car car, @RequestParam Long brandId,
                         @RequestParam Long enterprise_id, BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "cars/edit";
        }
        Brand brand = brandRepository.findById(brandId).orElse(null);  // Находим бренд по ID
        if (brand == null) {
            // Если бренд не найден, вернем сообщение об ошибке или перенаправим
            return "redirect:/cars/new?error=BrandNotFound";
        }
        Enterprise enterprise = enterpriseRepository.findById(enterprise_id).orElse(null);  // Находим бренд по ID
        if (enterprise == null) {
            // Если бренд не найден, вернем сообщение об ошибке или перенаправим
            return "redirect:/cars/new?error=EnterpriseNotFound";
        }
        car.setBrand(brand);
        car.setEnterprise(enterprise);
        carService.update(id, car);
        return "redirect:/cars";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) {
        carService.delete(id);
        return "redirect:/cars";
    }

    @GetMapping("/{id}/assignDrivers")
    public String showAssignDriversPage(@PathVariable("id") int id, Model model) {
        Car car = carService.findById(id);
        List<Driver> drivers = driverService.findAll(); // получить всех водителей
        model.addAttribute("car", car);
        model.addAttribute("drivers", drivers);
        return "cars/carAssignDrivers";
    }

    @PostMapping("/{id}/assignDrivers")
    public String assignDriverToCar(@PathVariable("id") Long id, @RequestParam int driver_id) {
        carService.assignDriver(id, driver_id);
        carService.update(id, carService.findById(id));
        return "redirect:/cars/" + id;
    }

    @GetMapping("/{id}/assignActiveDriver")
    public String showAssignActiveDriverForm(@PathVariable Long id, Model model) {
        Car car = carService.findById(id);
        Set<Driver> assignedDrivers = driverService.findDriversByCarId(id); // Получаем назначенных водителей
        model.addAttribute("car", car);
        model.addAttribute("assignedDrivers", assignedDrivers);
        return "cars/assignActiveDriver";
    }

    @PostMapping("/{id}/assignActiveDriver")
    public String assignActiveDriver(@PathVariable("id") Long id, @RequestParam Long driverId) throws Exception, DuplicateActiveDriverException {
        // Логика назначения активного водителя
        carService.assignActiveDriver(id, driverId);
        carService.update(id, carService.findById(id));
        return "redirect:/cars/" + id;
    }


}
