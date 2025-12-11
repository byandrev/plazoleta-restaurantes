package com.pragma.powerup.infrastructure.out.jpa.mapper;

import com.pragma.powerup.domain.model.EmployeeModel;
import com.pragma.powerup.infrastructure.out.jpa.entity.EmployeeRestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface IEmployeeRestaurantEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "restaurantId", target = "restaurant", qualifiedByName = "longToRestaurant")
    EmployeeRestaurantEntity toEntity (EmployeeModel employee);

    @Named("longToRestaurant")
    default RestaurantEntity longToRestaurant(Long restaurantId) {
        RestaurantEntity restaurant = new RestaurantEntity();
        restaurant.setId(restaurantId);
        return restaurant;
    }

}
