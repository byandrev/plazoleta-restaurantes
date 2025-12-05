package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IRestaurantPersistencePort {

    RestaurantModel save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    Page<RestaurantModel> getAll(PageRequest pageRequest);

}
