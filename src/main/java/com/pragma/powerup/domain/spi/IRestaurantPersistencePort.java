package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.PaginationInfo;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.RestaurantModel;

public interface IRestaurantPersistencePort {

    RestaurantModel save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    PaginationResult<RestaurantModel> getAll(PaginationInfo pagination);

}
