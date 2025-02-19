package ru.onegines.carpark.CarPark.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.onegines.carpark.CarPark.services.ManagerDetailsService;
import ru.onegines.carpark.CarPark.services.ManagerService;


/**
 * @author onegines
 * @date 08.11.2024
 */
@Configuration
@EnableWebSecurity

public class SecurityConfig {

    private final ManagerDetailsService managerDetailsService;
    private final ManagerService managerService;


    @Autowired
    public SecurityConfig(ManagerDetailsService managerDetailsService, ManagerService managerService) {
        this.managerDetailsService = managerDetailsService;
        this.managerService = managerService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/process_login").permitAll() // Разрешаем доступ к логину
                        .anyRequest().authenticated() // Остальные запросы только для авторизованных
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/process_login")
                        .defaultSuccessUrl("/enterprises", true)
                        .successHandler((request, response, authentication) -> {
                            String managerName = authentication.getName();
                            Long managerId = managerService.findByUsername(managerName).get().getId();
                            response.sendRedirect("/managers/" + managerId + "/enterprises");
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/auth/login?error"); // Редирект на логин при ошибке
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/auth/login"); // Перенаправляем на логин, если неавторизован
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            response.getWriter().write("{\"error\": \"Access Denied\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}