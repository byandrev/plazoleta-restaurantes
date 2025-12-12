package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.PaginationResult;
import com.pragma.powerup.infrastructure.exception.ResourceNotFound;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IPaginationResultMapper;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;
    private final IPaginationResultMapper paginationResultMapper;

    @Override
    public RestaurantModel save(RestaurantModel restaurantModel) {
        RestaurantEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
        return restaurantEntityMapper.toModel(restaurantEntity);
    }

    @Override
    public RestaurantModel getById(Long id) {
        RestaurantEntity restaurantEntity = restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFound("El restaurante no existe"));
        return restaurantEntityMapper.toModel(restaurantEntity);
    }

    @Override
    public PaginationResult<RestaurantModel> getAll(PageRequest pageRequest) {
        Page<RestaurantEntity> page = restaurantRepository.findAll(pageRequest);
        return paginationResultMapper.toModel(page.map(restaurantEntityMapper::toModel));
    }

}
