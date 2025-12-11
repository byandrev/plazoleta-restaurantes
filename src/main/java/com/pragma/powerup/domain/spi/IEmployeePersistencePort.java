package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.EmployeeModel;

public interface IEmployeePersistencePort {

    void saveEmployee(EmployeeModel employee);

    boolean existsById(Long userId, Long restaurantId);

}
