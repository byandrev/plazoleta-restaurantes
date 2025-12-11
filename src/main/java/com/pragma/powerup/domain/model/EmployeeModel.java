package com.pragma.powerup.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmployeeModel {

    private Long userId;

    private Long restaurantId;

}
