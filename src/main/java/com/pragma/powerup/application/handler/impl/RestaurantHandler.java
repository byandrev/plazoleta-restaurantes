package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.application.dto.request.PaginationRequestDto;
import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.PaginationResponseDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.*;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantService;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;
    private final IEmployeeRequestDtoMapper  employeeRequestDtoMapper;
    private final IPaginationResponseMapper paginationResponseMapper;
    private final IPaginationRequestMapper paginationRequestMapper;

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
    public PaginationResponseDto<RestaurantResponseDto> getAll(PaginationRequestDto paginationRequest) {
        PaginationResult<RestaurantModel> restaurantList = restaurantService.getAll(paginationRequestMapper.toModel(paginationRequest));
        return paginationResponseMapper.toResponse(restaurantList.map(restaurantResponseMapper::toResponse));
    }

    @Override
    public void assignEmployee(Long ownerId, EmployeeRequestDto employeeRequest) {
        restaurantService.assignEmployee(ownerId, employeeRequestDtoMapper.toModel(employeeRequest));
    }

}
