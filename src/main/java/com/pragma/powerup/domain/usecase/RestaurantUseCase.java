package com.pragma.powerup.domain.usecase;

import com.pragma.powerup.domain.api.IRestaurantServicePort;
import com.pragma.powerup.domain.exception.DomainException;
import com.pragma.powerup.domain.model.*;
import com.pragma.powerup.domain.spi.IEmployeePersistencePort;
import com.pragma.powerup.domain.spi.IRestaurantPersistencePort;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RestaurantUseCase implements IRestaurantServicePort {

    private final IRestaurantPersistencePort restaurantPersistencePort;
    private final IUserExternalServicePort userExternalServicePort;
    private final IEmployeePersistencePort employeePersistence;

    @Override
    public void save(RestaurantModel restaurantModel) {
        UserModel user = userExternalServicePort.getUserById(restaurantModel.getIdPropietario());

        if (!user.getRol().getNombre().equals(RolType.PROPIETARIO)) {
            throw new DomainException("El usuario no tiene permisos para realizar esta acción.");
        }

        restaurantPersistencePort.save(restaurantModel);
    }

    @Override
    public RestaurantModel getById(Long id) {
        return restaurantPersistencePort.getById(id);
    }

    @Override
    public PaginationResult<RestaurantModel> getAll(PaginationInfo pagination) {
        return  restaurantPersistencePort.getAll(pagination);
    }

    @Override
    public void assignEmployee(Long ownerId, EmployeeModel employee) {
        RestaurantModel restaurant = restaurantPersistencePort.getById(employee.getRestaurantId());
        UserModel employeeSaved = userExternalServicePort.getUserById(employee.getUserId());

        if (!Objects.equals(restaurant.getIdPropietario(), ownerId)) {
            throw new DomainException("El usuario no es el propietario del restaurante y no puede realizar esta accion");
        }

        if (!employeeSaved.getRol().getNombre().equals(RolType.EMPLEADO)) {
            throw new DomainException("El usuario debe tener rol empleado para ser asignado a un restaurante");
        }

        if (employeePersistence.existsById(employeeSaved.getId(), restaurant.getId())) {
            throw new DomainException("El empleado ya esta asignado al restaurante");
        }

        employeePersistence.saveEmployee(employee);
    }

}
