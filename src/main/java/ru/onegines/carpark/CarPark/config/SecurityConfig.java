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

import java.util.Optional;
import java.util.UUID;

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
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/import/enterprise")
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login", "/process_login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/process_login")
                        .defaultSuccessUrl("/enterprises", true)
                        .successHandler((request, response, authentication) -> {
                            String managerName = authentication.getName();
                            Optional<UUID> managerId = managerService.findByUsername(managerName)
                                    .map(manager -> manager.getId());

                            if (managerId.isPresent()) {
                                response.sendRedirect("/managers/" + managerId.get() + "/enterprises");
                            } else {
                                response.sendRedirect("/auth/login?error=manager_not_found");
                            }
                        })
                        .failureHandler((request, response, exception) -> {
                            response.sendRedirect("/auth/login?error");
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
                            response.sendRedirect("/auth/login");
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
