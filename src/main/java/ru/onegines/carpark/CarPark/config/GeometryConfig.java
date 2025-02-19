package ru.onegines.carpark.CarPark.config;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * @author onegines
 * @date 23.01.2025
 */
@Configuration
public class GeometryConfig {

    @Bean
    public GeometryFactory geometryFactory() {
        // Указываем точность геометрии (по умолчанию - 4326 для координат с широтой и долготой)
        return new GeometryFactory(new PrecisionModel(), 4326);
    }
}
