package ru.onegines.carpark.CarPark.controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.CarDTO;
import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.dto.ManagerDTO;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.models.Brand;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.repositories.BrandRepository;
import ru.onegines.carpark.CarPark.services.CarService;
import ru.onegines.carpark.CarPark.services.EnterpriseService;
import ru.onegines.carpark.CarPark.services.ManagerService;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author onegines
 * @date 05.11.2024
 */
@Controller
//@RestController
@ControllerAdvice
@RequestMapping("/managers")
public class ManagerController {

    @Autowired
    private ManagerService managerService;
    @Autowired
    private CarService carService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CarController carController;


    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping("/{id}/enterprises")
    public String showEnterprises(@PathVariable("id") Long id, HttpSession session, Model model) {
        ManagerDTO manager = managerService.getManagerDTOById(id);
        List<EnterpriseDTO> enterprises = enterpriseService.getEnterprisesByManagerId(id);
        Map<UUID, List<CarDTO>> carsByEnterprise = carService.getCarsDTOGroupedByEnterprise();

        // Получение таймзоны клиента из сессии
        String clientTimezone = (String) session.getAttribute("clientTimezone");
        if (clientTimezone == null || clientTimezone.isEmpty()) {
            clientTimezone = "UTC"; // Установить значение по умолчанию
        }
        ZoneId clientZoneId = ZoneId.of(clientTimezone);

        // Обработка машин и преобразование времени в разные таймзоны
        carsByEnterprise.forEach((enterpriseId, cars) -> {
            // Таймзона предприятия
            String enterpriseTimezone = enterpriseService.getEnterpriseTimeZone(enterpriseId);
            ZoneId enterpriseZoneId = (enterpriseTimezone != null && !enterpriseTimezone.isEmpty())
                    ? ZoneId.of(enterpriseTimezone)
                    : ZoneId.of("UTC");

            cars.forEach(car -> {
                LocalDateTime purchaseDateTime = car.getPurchaseDateTime();
                if (purchaseDateTime != null) {
                    // Преобразуем время покупки в таймзону предприятия
                    ZonedDateTime enterpriseDateTime = purchaseDateTime.atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(enterpriseZoneId);
                    car.setEnterprisePurchaseDateTime(enterpriseDateTime.toLocalDateTime());

                    // Преобразуем время покупки в таймзону клиента
                    ZonedDateTime clientDateTime = purchaseDateTime.atZone(ZoneOffset.UTC)
                            .withZoneSameInstant(clientZoneId);
                    car.setPurchaseDateTime(clientDateTime.toLocalDateTime());
                }
            });
        });

        List<Brand> brands = brandRepository.findAll();

        model.addAttribute("manager", manager);
        model.addAttribute("enterprises", enterprises);
        model.addAttribute("carsByEnterprise", carsByEnterprise);
        model.addAttribute("brands", brands);
        model.addAttribute("clientTimezone", clientTimezone);
        model.addAttribute("newCar", new CarDTO()); // Добавление нового объекта для формы
        return "managers/assignedEnterprises";
    }

    @GetMapping("/{managerId}/enterprises/{enterpriseId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String showEnterpriseCars(@PathVariable Long managerId,
                                     @PathVariable UUID enterpriseId,
                                     Model model) {
        Enterprise enterprise = enterpriseService.findById(enterpriseId);
        List<CarDTO> cars = carService.getCarsByEnterprise(enterpriseId);

        model.addAttribute("enterprise", enterprise);
        model.addAttribute("cars", cars);
        model.addAttribute("managerId", managerId);

        return "managers/enterpriseCars";  // Имя Thymeleaf-шаблона
    }


    @GetMapping("/{managerId}/enterprises/{enterpriseId}/cars/{carId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String showCarDetails(@PathVariable Long managerId,
                                 @PathVariable UUID enterpriseId,
                                 @PathVariable Long carId,
                                 Model model) {
        CarDTO car = carService.getCarDTO(carId);
        List<RouteDTO> trips = carService.getTripsByCar(carId, enterpriseId);  // Передаем enterpriseId

        model.addAttribute("car", car);
        model.addAttribute("trips", trips);
        model.addAttribute("managerId", managerId);
        model.addAttribute("enterpriseId", enterpriseId);

        return "managers/carDetails";  // Имя Thymeleaf-шаблона
    }

    @GetMapping("/{managerId}/enterprises/{enterpriseId}/cars/{carId}/edit")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String showEditCarForm(
            @PathVariable Long managerId,
            @PathVariable Long enterpriseId,
            @PathVariable Long carId,
            Model model) {
        // Получение информации о машине для редактирования
        CarDTO car = carService.getCarDTO(carId);

        // Добавление данных в модель
        model.addAttribute("car", car);
        model.addAttribute("enterpriseId", enterpriseId);
        model.addAttribute("managerId", managerId);
        model.addAttribute("brands", brandRepository.findAll()); // Для выбора бренда

        return "managers/editCar";
    }

    @PostMapping("/{managerId}/enterprises/{enterpriseId}/cars/{carId}/update")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public String updateCar(
            @PathVariable Long managerId,
            @PathVariable Long enterpriseId,
            @PathVariable Long carId,
            @ModelAttribute("car") CarDTO carDTO) {
        // Обновление информации о машине
        carService.update(carId, carDTO);

        // Редирект обратно на страницу предприятий
        return "redirect:/managers/" + managerId + "/enterprises";
    }


    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Transactional
    @PostMapping("/{managerId}/enterprises/{enterpriseId}/cars")
    public String addCarToEnterprise(
            @PathVariable("enterpriseId") UUID enterpriseId,
            @PathVariable("managerId") Long managerId,
            @ModelAttribute("newCar") CarDTO carDTO,
            Model model) {
        carDTO.setEnterpriseId(enterpriseId);
        carService.addCarToEnterprise(enterpriseId, carDTO);
        // Добавление данных в модель для отображения
        model.addAttribute("enterprise", enterpriseService.findById(enterpriseId));
        model.addAttribute("cars", carService.getCarsByEnterprise(enterpriseId));
        model.addAttribute("manager", managerService.findById(managerId));
        return "redirect:/managers/" + managerId + "/enterprises";
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{managerId}/enterprises/{enterpriseId}/cars/{carId}")
    public String deleteCarFromEnterprise(
            @PathVariable Long managerId,
            @PathVariable UUID enterpriseId,
            @PathVariable Long carId,
            Model model) {
        carService.delete(carId); // Удаление машины из базы
        // Обновление данных в модели
        model.addAttribute("enterprise", enterpriseService.findById(enterpriseId));
        model.addAttribute("cars", carService.findByEnterpriseId(enterpriseId));
        model.addAttribute("manager", managerService.findById(managerId));
        return "redirect:/managers/" + managerId + "/enterprises";
    }

    @GetMapping("")
    public String adminPage(Model model) {
        List<Manager> managers = managerService.findAll();
        model.addAttribute("managers", managers);
        return "managers/managers";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Отображение формы входа
    }



    /*@GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model manager) {
        manager.addAttribute("enterprises", enterpriseService.getEnterprisesForManager(id));
        manager.addAttribute("manager", managerService.findById(id));
        return "managers/show";
    }*/

    @GetMapping("/new")
    public String showNewManagerForm(Model model) {
        model.addAttribute("manager", new Manager());
        return "managers/new";
    }

    @PostMapping("/save")
    public String saveManager(@ModelAttribute Manager manager) {
        managerService.save(manager);
        return "redirect:/managers";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
        model.addAttribute("manager", managerService.findById(id));
        return "managers/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("manager") Manager manager, BindingResult bindingResult,
                         @PathVariable("id") Long id) {
        if (bindingResult.hasErrors()) {
            return "managers/edit";
        }
        managerService.update(id, manager);
        return "redirect:/managers";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") Long id) {
        managerService.delete(id);
        return "redirect:/managers";
    }

    @GetMapping("/{id}/assignEnterprise")
    public String showAssignEnterpriseToManagerForm(@PathVariable Long id, Model model) {
        Manager manager = managerService.findById(id);
        List<Enterprise> availableEnterprises = managerService.getAvailableEnterprises();
        model.addAttribute("manager", manager);
        model.addAttribute("availableEnterprises", availableEnterprises);
        return "managers/assignEnterprise";  // имя Thymeleaf-шаблона
    }

    @PostMapping("/{managerId}/assignEnterprise/{enterpriseId}")
    public ResponseEntity<String> assignEnterprise(
            @PathVariable Long managerId,
            @PathVariable UUID enterpriseId) {

        managerService.assignEnterpriseToManager(managerId, enterpriseId);
        return ResponseEntity.ok("Enterprise assigned to Manager successfully.");
    }

    @GetMapping("/api/managers")
    public ResponseEntity<List<ManagerDTO>> getAllManagers() {
        List<ManagerDTO> managers = managerService.getAllManagers();
        return ResponseEntity.ok(managers);
    }
}