package com.pragma.powerup.domain.api;

import com.pragma.powerup.domain.model.EmployeeModel;
import com.pragma.powerup.domain.model.RestaurantModel;
import org.springframework.data.domain.Page;

public interface IRestaurantServicePort {

    void save(RestaurantModel restaurantModel);

    RestaurantModel getById(Long id);

    Page<RestaurantModel> getAll(int page, int size);

    void assignEmployee(Long ownerId, EmployeeModel employee);

}
