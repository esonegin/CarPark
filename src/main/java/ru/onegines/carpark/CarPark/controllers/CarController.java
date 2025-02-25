package ru.onegines.carpark.CarPark.controllers;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.exceptions.DuplicateActiveDriverException;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.repositories.BrandRepository;
import ru.onegines.carpark.CarPark.repositories.EnterpriseRepository;

import ru.onegines.carpark.CarPark.services.CarService;
import ru.onegines.carpark.CarPark.services.DriverService;
import ru.onegines.carpark.CarPark.services.RouteService;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * @author onegines
 * @date 29.10.2024
 */
@RestController
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
    public ResponseEntity<List<CarDTO>> adminPage(Pageable pageable) {
        List<CarDTO> cars = carService.getAllCars();
        //model.addAttribute("cars", cars);
        return ResponseEntity.ok(cars);
    }

    /*@GetMapping("/new")
    public String showNewCarForm(Model model) {
        model.addAttribute("car", new Car());
        model.addAttribute("brands", brandRepository.findAll());  // Передаем список брендов
        model.addAttribute("enterprises", enterpriseRepository.findAll());
        return "cars/new";
    }*/

    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/new")
    public ResponseEntity<Object> showNewCarForm() {
        Map<String, Object> response = new HashMap<>();
        response.put("brands", brandRepository.findAll());
        response.put("enterprises", enterpriseRepository.findAll());
        return ResponseEntity.ok(response);
    }

   /* @PostMapping("/save")
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
    }*/

    @PostMapping("/save")
    @Transactional
    public ResponseEntity<String> saveCar(@RequestBody CarDTO carDTO) {
        Brand brand = brandRepository.findById(carDTO.getBrandId()).orElse(null);
        if (brand == null) {
            return ResponseEntity.badRequest().body("Brand not found");
        }

        Enterprise enterprise = enterpriseRepository.findById(carDTO.getEnterpriseId()).orElse(null);
        if (enterprise == null) {
            return ResponseEntity.badRequest().body("Enterprise not found");
        }

        // Создаем и сохраняем новый автомобиль
        Car car = new Car();
        car.setMileage(carDTO.getMileage());
        car.setГодВыпуска(carDTO.getГодВыпуска());
        car.setReserve(carDTO.getReserve());
        car.setNumber(carDTO.getNumber());
        car.setBrand(brand);
        car.setEnterprise(enterprise);
        car.setPurchaseDateTime(LocalDateTime.now(ZoneOffset.UTC));


        carService.save(car);
        return ResponseEntity.status(HttpStatus.OK).body("Машина создана. Статус " + HttpStatus.OK.value());
    }


   /* @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model car, Principal principal) {
        Long managerId = ((ManagerDetails) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getManager().getId();
        if (carService.isManagerHasAccess(managerId, id)) {
            car.addAttribute("car", carService.findById(id));
        } else {
            return "redirect:/logout";
        }
        return "cars/show";
    }*/

    //Bill доступны 10, 11 недоступны 9, 8
    //Ilon доступны 9, 8 недоступны 10, 11
    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/{id}")
    public ResponseEntity<Object> show(@PathVariable("id") Long id, Model car) {
      /*  car.addAttribute("car", carService.findById(id));
        return "cars/show";*/
        CarDTO carDTO = carService.getCarDTO(id);
        if (carDTO == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(carDTO);
    }


    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("car", carService.findById(id));
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("enterprisse", enterpriseRepository.findAll());
        return "cars/edit";
    }

    /*@PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
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
    }*/

    @PatchMapping("/{id}")
    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    public ResponseEntity<String> updateCar(
            @PathVariable Long id,
            @RequestBody CarDTO carDTO,
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body("Некорректные данные.");
        }

        Car car = carService.findById(id);
        if (car == null) {
            return ResponseEntity.notFound().build();
        }

        // Преобразование даты покупки в UTC перед сохранением
        LocalDateTime localPurchaseDateTime = carDTO.getPurchaseDateTime();
        if (localPurchaseDateTime != null) {
            ZoneId clientZoneId = ZoneId.of("Asia/Tokyo"); // Таймзона клиента (можно получить из сессии)
            ZonedDateTime clientZonedDateTime = localPurchaseDateTime.atZone(clientZoneId);

            // Переводим дату в UTC
            ZonedDateTime utcZonedDateTime = clientZonedDateTime.withZoneSameInstant(ZoneOffset.UTC);
            car.setPurchaseDateTime(utcZonedDateTime.toLocalDateTime());
        }

        // Остальные изменения
        car.setMileage(carDTO.getMileage());
        car.setReserve(carDTO.getReserve());
        car.setNumber(carDTO.getNumber());
        car.setBrand(brandRepository.findById(carDTO.getBrandId()).orElse(null));
        carService.save(car);

        return ResponseEntity.ok("Автомобиль обновлен.");
    }


    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable("id") long id) {
        carService.delete(id);
        if (carService.findById(id) == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Машина удалена. Статус " + HttpStatus.NO_CONTENT.value());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Машина не удалена. Статус " + HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/{id}/assignDrivers")
    public String showAssignDriversPage(@PathVariable("id") int id, Model model) {
        Car car = carService.findById(id);
        List<Driver> drivers = driverService.findAll(); // получить всех водителей
        model.addAttribute("car", car);
        model.addAttribute("drivers", drivers);
        return "cars/carAssignDrivers";
    }

    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @PostMapping("/{id}/assignDrivers")
    public String assignDriverToCar(@PathVariable("id") Long id, @RequestParam int driver_id) {
        carService.assignDriver(id, driver_id);
        carService.update(id, carService.findById(id));
        return "redirect:/cars/" + id;
    }

    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @GetMapping("/{id}/assignActiveDriver")
    public String showAssignActiveDriverForm(@PathVariable Long id, Model model) {
        Car car = carService.findById(id);
        Set<Driver> assignedDrivers = driverService.findDriversByCarId(id); // Получаем назначенных водителей
        model.addAttribute("car", car);
        model.addAttribute("assignedDrivers", assignedDrivers);
        return "cars/assignActiveDriver";
    }

    @PreAuthorize("@carService.isManagerHasAccess(authentication.principal.id, #id)")
    @PostMapping("/{id}/assignActiveDriver")
    public String assignActiveDriver(@PathVariable("id") Long id, @RequestParam Long driverId) throws Exception, DuplicateActiveDriverException {
        // Логика назначения активного водителя
        carService.assignActiveDriver(id, driverId);
        carService.update(id, carService.findById(id));
        return "redirect:/cars/" + id;
    }
}
