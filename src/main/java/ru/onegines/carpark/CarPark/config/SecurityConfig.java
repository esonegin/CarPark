package ru.onegines.carpark.CarPark.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import ru.onegines.carpark.CarPark.services.ManagerDetailsService;

/**
 * @author onegines
 * @date 08.11.2024
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    /*private final ManagerDetailsService managerDetailsService;

    @Autowired
    public SecurityConfig(ManagerDetailsService managerDetailsService) {
        this.managerDetailsService = managerDetailsService;
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                //.csrf(csrf -> csrf.disable())
                .authorizeRequests()
                .requestMatchers("/auth/login", "/error").permitAll()
                .requestMatchers("/enterprise/**", "/cars/**", "/drivers/**").hasRole("MANAGER")
                .anyRequest().authenticated()
                .and()
                .formLogin(form -> form
                        .loginPage("/auth/login") // Указываем кастомную страницу входа
                        .loginProcessingUrl("/process_login")
                        .defaultSuccessUrl("/enterprises", true)
                        .failureUrl("/auth/login?error"))
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));


        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}