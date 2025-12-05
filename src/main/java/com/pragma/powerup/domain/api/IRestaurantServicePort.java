package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IRestaurantServicePort {

    void save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    Page<RestaurantModel> getAll(int page, int size);

}
