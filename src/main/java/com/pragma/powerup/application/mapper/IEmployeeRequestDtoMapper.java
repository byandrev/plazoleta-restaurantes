package com.pragma.powerup.application.mapper;

import com.pragma.powerup.application.dto.request.EmployeeRequestDto;
import com.pragma.powerup.domain.model.EmployeeModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IEmployeeRequestDtoMapper {

    EmployeeModel toModel (EmployeeRequestDto employeeDto);

}
