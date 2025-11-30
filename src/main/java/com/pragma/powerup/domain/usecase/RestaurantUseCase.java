package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.api.IUserExternalServicePort;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalServicePort userExternalServicePort;

    @Override
    public void save(RestaurantModel restaurantModel) {
        restaurantPersistencePort.save(restaurantModel);
    }

    @Override
    public RestaurantModel getById(Long id) {
        return restaurantPersistencePort.getById(id);
    }

    @Override
    public List<RestaurantModel> getAll() {
        return restaurantPersistencePort.getAll();
    }

}
