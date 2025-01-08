package ru.onegines.carpark.CarPark.config;

/**
 * @author onegines
 * @date 09.12.2024
 */
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TimeZoneFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String timezone = request.getParameter("timezone");
        System.out.println("Timezone from request: " + timezone); // Логирование таймзоны
        if (timezone != null && !timezone.isEmpty()) {
            HttpSession session = request.getSession();
            session.setAttribute("clientTimezone", timezone);
        }
        filterChain.doFilter(request, response);
    }
}



