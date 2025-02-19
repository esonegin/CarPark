package ru.onegines.carpark.CarPark.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author onegines
 * @date 23.01.2025
 */
@Controller
@RequestMapping("/map")
public class MapController {

    @GetMapping("/route")
    public String showRoutePage() {
        return "route_map"; // Имя HTML-файла без расширения, например, route_map.html
    }
}
