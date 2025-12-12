package com.pragma.powerup.application.handler;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;

public interface IRestaurantHandler {

    void save(RestaurantRequestDto restaurantRequestDto);

    RestaurantResponseDto getById(Long id);

    PaginationResponseDto<RestaurantResponseDto> getAll(PaginationRequestDto paginationRequest);

    void assignEmployee(Long ownerId, EmployeeRequestDto employeeRequest);

}
