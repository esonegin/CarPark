package ru.onegines.carpark.CarPark.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import ru.onegines.carpark.CarPark.models.RoutePoint;
import ru.onegines.carpark.CarPark.services.CarService;
import ru.onegines.carpark.CarPark.services.EnterpriseService;
import ru.onegines.carpark.CarPark.services.RouteService;

import java.beans.PropertyEditorSupport;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * @author onegines
 * @date 26.12.2024
 */
@RestController
@RequestMapping("/routes")
public class RouteController {
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(ZonedDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                try {
                    // Пробуем парсить с часовым поясом
                    setValue(ZonedDateTime.parse(text, DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                } catch (DateTimeParseException e) {
                    // Если часовой пояс отсутствует, добавляем его
                    LocalDateTime localDateTime = LocalDateTime.parse(text, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    setValue(localDateTime.atZone(ZoneId.of("+03:00")));
                }
            }
        });
    }

    @Autowired
    private RouteService routeService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private CarService carService;


    @GetMapping("/geojson")
    public String getRouteGeoJson(@RequestParam Long carId,
                                  @RequestParam String start,
                                  @RequestParam String end) {
        // Логируем входные параметры
        System.out.println("Start: " + start);
        System.out.println("End: " + end);

        // Получаем таймзону предприятия
        String timeZoneId = enterpriseService.getEnterpriseTimeZone(carService.findById(carId).getEnterprise().getId());
        ZoneId enterpriseZone;
        try {
            enterpriseZone = ZoneId.of(timeZoneId);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некорректный идентификатор таймзоны: " + timeZoneId);
        }

        // Парсим входные параметры как LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.parse(start, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDateTime = LocalDateTime.parse(end, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        // Преобразуем в ZonedDateTime в таймзоне предприятия
        ZonedDateTime startZoned = startDateTime.atZone(enterpriseZone);
        ZonedDateTime endZoned = endDateTime.atZone(enterpriseZone);

        // Конвертируем в UTC для выполнения запроса
        ZonedDateTime startUtc = startZoned.withZoneSameInstant(ZoneOffset.UTC);
        ZonedDateTime endUtc = endZoned.withZoneSameInstant(ZoneOffset.UTC);

        // Логируем преобразованные данные
        System.out.println("Start UTC: " + startUtc);
        System.out.println("End UTC: " + endUtc);

        // Передаем UTC-временные интервалы в сервис
        return routeService.getRouteGeoJson(carId, startUtc, endUtc);
    }

    @GetMapping("/geojson/all")
    public String getAllRoutePoints(@RequestParam Long carId) {
        return routeService.getAllRoutePoints(carId);
    }
}
