package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.exception.UnauthorizedUserException;
import com.pragma.powerup.domain.model.RestaurantModel;
import com.pragma.powerup.domain.model.RolType;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.infrastructure.exception.NoDataFoundException;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalServicePort userExternalServicePort;

    @Override
    public void save(RestaurantModel restaurantModel) {
        UserModel user = userExternalServicePort.getUserById(restaurantModel.getIdPropietario());

        if (user == null) {
            throw new NoDataFoundException("El usuario no existe");
        }

        if (!user.getRol().getNombre().equals(RolType.PROPIETARIO)) {
            throw new UnauthorizedUserException("El usuario no tiene permisos para realizar esta acción.");
        }

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
