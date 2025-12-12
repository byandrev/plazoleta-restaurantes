package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.EmployeeModel;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.RestaurantModel;

public interface IRestaurantServicePort {

    void save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    PaginationResult<RestaurantModel> getAll(int page, int size);

    void assignEmployee(Long ownerId, EmployeeModel employee);

}
