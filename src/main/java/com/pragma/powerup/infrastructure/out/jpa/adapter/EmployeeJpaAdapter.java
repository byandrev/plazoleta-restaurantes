package com.pragma.powerup.infrastructure.out.jpa.adapter;

import com.pragma.powerup.domain.model.EmployeeModel;
import com.pragma.powerup.domain.spi.IEmployeePersistencePort;
import com.pragma.powerup.infrastructure.out.jpa.entity.EmployeeRestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.EmployeeRestaurantId;
import com.pragma.powerup.infrastructure.out.jpa.mapper.IEmployeeRestaurantEntityMapper;
import com.pragma.powerup.infrastructure.out.jpa.repository.IEmployeeRestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class EmployeeJpaAdapter implements IEmployeePersistencePort {

    private final IEmployeeRestaurantRepository repository;

    private final IEmployeeRestaurantEntityMapper mapper;

    @Override
    public void saveEmployee(EmployeeModel employee) {
        EmployeeRestaurantId employeeRestaurantId = new EmployeeRestaurantId(employee.getRestaurantId(), employee.getUserId());
        EmployeeRestaurantEntity employeeRestaurantEntity = mapper.toEntity(employee);
        employeeRestaurantEntity.setId(employeeRestaurantId);
        repository.save(employeeRestaurantEntity);
    }

    @Override
    public boolean existsById(Long userId, Long restaurantId) {
        EmployeeRestaurantId employeeRestaurantId = new EmployeeRestaurantId(restaurantId, userId);
        return repository.existsById(employeeRestaurantId);
    }

}
