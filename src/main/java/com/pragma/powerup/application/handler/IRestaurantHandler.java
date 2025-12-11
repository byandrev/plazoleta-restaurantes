package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import org.springframework.data.domain.Page;

public interface IRestaurantHandler {

    void save(RestaurantRequestDto restaurantRequestDto);

    RestaurantResponseDto getById(Long id);

    Page<RestaurantResponseDto> getAll(int page, int size);

    void assignEmployee(Long ownerId, EmployeeRequestDto employeeRequest);

}
