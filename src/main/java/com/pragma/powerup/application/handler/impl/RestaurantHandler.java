package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.IEmployeeRequestDtoMapper;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantService;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IEmployeeRequestDtoMapper  employeeRequestDtoMapper;

    @Override
    public void save(RestaurantRequestDto restaurantRequestDto) {
        RestaurantModel restaurant = restaurantRequestMapper.toRestaurant(restaurantRequestDto);
        restaurantService.save(restaurant);
    }

    @Override
    public RestaurantResponseDto getById(Long id) {
        RestaurantModel restaurant = restaurantService.getById(id);
        return restaurantResponseMapper.toResponse(restaurant);
    }

    @Override
    public Page<RestaurantResponseDto> getAll(int page, int size) {
        Page<RestaurantModel> restaurantList = restaurantService.getAll(page, size);
        return restaurantList.map(restaurantResponseMapper::toResponse);
    }

    @Override
    public void assignEmployee(Long ownerId, EmployeeRequestDto employeeRequest) {
        restaurantService.assignEmployee(ownerId, employeeRequestDtoMapper.toModel(employeeRequest));
    }

}
