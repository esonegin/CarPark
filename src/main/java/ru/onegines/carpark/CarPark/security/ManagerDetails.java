package ru.onegines.carpark.CarPark.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.onegines.carpark.CarPark.models.Manager;

import java.util.Collection;

/**
 * @author onegines
 * @date 08.11.2024
 */
public class ManagerDetails implements UserDetails {
    private final Manager manager;

    public ManagerDetails(Manager manager) {
        this.manager = manager;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return this.manager.getPassword();
    }

    @Override
    public String getUsername() {
        return this.manager.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //Для получения данных аутентифицированного менеджера
    public Manager getManager(){
        return this.manager;
    }
}
