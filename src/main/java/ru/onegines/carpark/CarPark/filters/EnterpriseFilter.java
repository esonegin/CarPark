package ru.onegines.carpark.CarPark.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.services.ManagerService;

import java.util.Optional;

/**
 * @author onegines
 * @date 09.11.2024
 */
@Component
public class EnterpriseFilter {

    private final ManagerService managerService;

    @Autowired
    public EnterpriseFilter(ManagerService managerService) {
        this.managerService = managerService;
    }

    public boolean hasAccessToEnterprise(Authentication authentication, Long enterpriseId) {
        String username = authentication.getName();
        Optional<Manager> manager = managerService.findByUsername(username);

        return manager.map(m -> m.getEnterprises().stream()
                        .anyMatch(e -> e.getId().equals(enterpriseId)))
                .orElse(false);
    }
}
