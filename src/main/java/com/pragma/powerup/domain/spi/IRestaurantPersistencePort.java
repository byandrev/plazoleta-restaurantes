package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.List;

public interface IRestaurantPersistencePort {

    RestaurantModel save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    List<RestaurantModel> getAll();

}
