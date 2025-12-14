package com.pragma.powerup.infrastructure.out.jpa.repository;

import com.pragma.powerup.infrastructure.out.jpa.entity.EmployeeRestaurantEntity;
import com.pragma.powerup.infrastructure.out.jpa.entity.EmployeeRestaurantId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IEmployeeRestaurantRepository extends JpaRepository<EmployeeRestaurantEntity, EmployeeRestaurantId> {

    boolean existsById(EmployeeRestaurantId id);

}
