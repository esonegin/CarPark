package ru.onegines.carpark.CarPark.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
                // Ранее закомментированное добавление фильтра
                .csrf(csrf -> csrf.ignoringRequestMatchers("/set-timezone"))
                //.addFilterAfter(new TimeZoneFilter(), UsernamePasswordAuthenticationFilter.class) // Добавляем TimeZoneFilter после аутентификации;
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/set-timezone").permitAll()
                        .requestMatchers(HttpMethod.POST, "/managers/{managerId}/enterprises/{enterpriseId}/cars").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/cars/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.POST, "/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PUT, "/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.PATCH, "/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("MANAGER")
                        .requestMatchers(HttpMethod.DELETE, "/managers/**").hasRole("MANAGER")
                        .requestMatchers("/process_login").permitAll()
                        .requestMatchers("/auth/login", "/error").permitAll()
                        .requestMatchers("/enterprises").hasRole("MANAGER")
                        .anyRequest().authenticated()
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
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((request, response, authentication) -> response.sendRedirect("/auth/login"))
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