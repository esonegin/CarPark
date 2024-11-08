package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.dto.EnterpriseDTO;
import ru.onegines.carpark.CarPark.dto.ManagerDTO;
import ru.onegines.carpark.CarPark.models.Car;
import ru.onegines.carpark.CarPark.models.Driver;
import ru.onegines.carpark.CarPark.models.Enterprise;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.services.EnterpriseService;
import ru.onegines.carpark.CarPark.services.ManagerService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author onegines
 * @date 05.11.2024
 */
@Controller
@ControllerAdvice
@RequestMapping("/managers")
public class ManagerController {

    @Autowired
    private ManagerService managerService;

    @Autowired
    private EnterpriseService enterpriseService;

    @GetMapping("")
    public String adminPage(Model model) {
        List<Manager> managers = managerService.findAll();
        model.addAttribute("managers", managers);
        return "managers/managers";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Long id, Model manager) {
        manager.addAttribute("enterprises", enterpriseService.getEnterprisesByManager(id));
        manager.addAttribute("manager", managerService.findById(id));
        return "managers/show";
    }

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
            @PathVariable Long enterpriseId) {

        managerService.assignEnterpriseToManager(managerId, enterpriseId);
        return ResponseEntity.ok("Enterprise assigned to Manager successfully.");
    }

    @GetMapping("/api/managers")
    public ResponseEntity<List<ManagerDTO>> getAllManagers() {
        List<ManagerDTO> managers = managerService.getAllManagers();
        return ResponseEntity.ok(managers);
    }
}
