package ru.onegines.carpark.CarPark.utils;

import org.mapstruct.Mapper;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.models.Route;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    RouteDTO toDto(Route route);
    List<RouteDTO> toDto(List<Route> routes);
}
