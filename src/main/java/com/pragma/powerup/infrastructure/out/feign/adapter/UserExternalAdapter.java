package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.domain.exception.ResourceNotFound;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.infrastructure.input.rest.response.CustomResponse;
import com.pragma.powerup.infrastructure.out.feign.IUserFeignClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.IUserFeignMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserExternalAdapter implements IUserExternalServicePort {

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    @Override
    public UserModel getUserById(Long id) {
       try {
           CustomResponse<UserResponseDto> response = userFeignClient.getUser(id);
           UserResponseDto userDto = response.getData();

           return userFeignMapper.toModel(userDto);
       } catch (FeignException e) {
            throw new ResourceNotFound("El usuario no existe");
       }
    }

}
