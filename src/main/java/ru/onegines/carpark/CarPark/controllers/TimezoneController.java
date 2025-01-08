package ru.onegines.carpark.CarPark.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author onegines
 * @date 11.12.2024
 */
@RestController
public class TimezoneController {
    @PostMapping("/set-timezone")
    public ResponseEntity<?> setTimezone(@RequestBody Map<String, String> payload, HttpSession session) {
        String timezone = payload.get("timezone");
        if (timezone != null && !timezone.isEmpty()) {
            session.setAttribute("clientTimezone", timezone);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
