package ru.onegines.carpark.CarPark.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author onegines
 * @date 10.12.2024
 */
public class DateTimeUtil {
    public static LocalDateTime convertToTimeZone(LocalDateTime dateTime, String sourceZone, String targetZone) {
        return dateTime.atZone(ZoneId.of(sourceZone))
                .withZoneSameInstant(ZoneId.of(targetZone))
                .toLocalDateTime();
    }
}
