package ru.onegines.carpark.CarPark.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.onegines.carpark.CarPark.models.Manager;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;


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
        return Collections.singleton(new SimpleGrantedAuthority(this.manager.getRole()));
    }

    @Override
    public String getPassword() {
        return this.manager.getPassword();
    }

    @Override
    public String getUsername() {
        return this.manager.getUsername();
    }


    public UUID getId() {
        return this.manager.getId();
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
