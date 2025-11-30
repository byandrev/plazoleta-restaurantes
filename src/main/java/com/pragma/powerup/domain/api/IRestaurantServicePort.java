package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantModel;

import java.util.List;

public interface IRestaurantServicePort {

    void save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    List<RestaurantModel> getAll();

}
