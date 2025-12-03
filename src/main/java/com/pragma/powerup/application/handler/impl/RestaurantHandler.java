package com.pragma.powerup.application.handler.impl;

import com.pragma.powerup.application.dto.request.RestaurantRequestDto;
import com.pragma.powerup.application.dto.response.RestaurantResponseDto;
import com.pragma.powerup.application.handler.IRestaurantHandler;
import com.pragma.powerup.application.mapper.IRestaurantRequestMapper;
import com.pragma.powerup.application.mapper.IRestaurantResponseMapper;
import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
//@Transactional
public class RestaurantHandler implements IRestaurantHandler {

    private final IRestaurantServicePort restaurantService;
    private final IRestaurantRequestMapper restaurantRequestMapper;
    private final IRestaurantResponseMapper restaurantResponseMapper;

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
    public List<RestaurantResponseDto> getAll() {
        List<RestaurantModel> restaurantList = restaurantService.getAll();
        return restaurantResponseMapper.toResponseList(restaurantList);
    }

}
