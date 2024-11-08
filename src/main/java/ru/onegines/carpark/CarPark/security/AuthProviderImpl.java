package ru.onegines.carpark.CarPark.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.onegines.carpark.CarPark.services.ManagerDetailsService;

import java.util.Collections;

/**
 * @author onegines
 * @date 08.11.2024
 */
@Component
public class AuthProviderImpl implements AuthenticationProvider {
    private final ManagerDetailsService managerDetailsService;

    @Autowired
    public AuthProviderImpl(ManagerDetailsService managerDetailsService) {
        this.managerDetailsService = managerDetailsService;
    }

  /*  @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        UserDetails managerDetails = managerDetailsService.loadUserByUsername(username);
        String password = authentication.getCredentials().toString();
        if(!password.equals(managerDetails.getPassword())){
            throw new BadCredentialsException("Неверный пароль");
        }
        return new UsernamePasswordAuthenticationToken(managerDetails, password, Collections.emptyList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }*/

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails managerDetails = managerDetailsService.loadUserByUsername(username);
        UserDetails user = managerDetailsService.loadUserByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("Invalid username or password");
        }
        if(!password.equals(managerDetails.getPassword())){
            throw new BadCredentialsException("Неверный пароль");
        }
        return new UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
        // Вставьте логику для проверки пароля и создания аутентификации

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
