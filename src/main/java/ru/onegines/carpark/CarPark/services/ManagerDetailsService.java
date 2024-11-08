package ru.onegines.carpark.CarPark.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.onegines.carpark.CarPark.models.Manager;
import ru.onegines.carpark.CarPark.repositories.ManagerRepository;
import ru.onegines.carpark.CarPark.security.ManagerDetails;

import java.util.Optional;

/**
 * @author onegines
 * @date 08.11.2024
 */
@Service
public class ManagerDetailsService implements UserDetailsService {
    private final ManagerRepository managerRepository;

    @Autowired
    public ManagerDetailsService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Manager> person = managerRepository.findByName(username);
        if (person.isEmpty()) {
            throw new UsernameNotFoundException("Пользователь не найден");
        }
        return new ManagerDetails(person.get());
    }
}
