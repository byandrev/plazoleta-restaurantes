package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import com.pragma.powerup.infrastructure.out.jpa.entity.RestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RestaurantJpaAdapter implements IRestaurantPersistencePort {

    private final IRestaurantRepository restaurantRepository;
    private final IRestaurantEntityMapper restaurantEntityMapper;

    @Override
    public RestaurantModel save(RestaurantModel restaurantModel) {
        RestaurantEntity restaurantEntity = restaurantRepository.save(restaurantEntityMapper.toEntity(restaurantModel));
        return restaurantEntityMapper.toModel(restaurantEntity);
    }

    @Override
    public RestaurantModel getById(Long id) {
        Optional<RestaurantEntity> restaurantEntity = restaurantRepository.findById(id);

        if  (restaurantEntity.isEmpty()) {
            throw new NoDataFoundException("El restaurante no existe");
        }

        return restaurantEntityMapper.toModel(restaurantEntity.get());
    }

    @Override
    public List<RestaurantModel> getAll() {
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();
        return restaurantEntityMapper.toModelList(restaurantEntities);
    }

}
