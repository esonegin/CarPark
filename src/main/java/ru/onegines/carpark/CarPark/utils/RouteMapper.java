package ru.onegines.carpark.CarPark.utils;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.onegines.carpark.CarPark.dto.RouteDTO;
import ru.onegines.carpark.CarPark.models.Route;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    RouteMapper INSTANCE = Mappers.getMapper(RouteMapper.class);

    default RouteDTO toDto(Route route) {
        return new RouteDTO(route.getId(), route.getStartTimeUtc(), route.getEndTimeUtc(), route.getCarId());
    }

    List<RouteDTO> toDto(List<Route> routes);
}