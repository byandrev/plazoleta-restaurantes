package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.application.dto.response.UserResponseDto;
import com.pragma.powerup.domain.spi.IUserExternalServicePort;
import com.pragma.powerup.domain.model.UserModel;
import com.pragma.powerup.infrastructure.out.feign.IUserFeignClient;
import com.pragma.powerup.infrastructure.out.feign.mapper.IUserFeignMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserExternalAdapter implements IUserExternalServicePort {

    private final IUserFeignClient userFeignClient;
    private final IUserFeignMapper userFeignMapper;

    @Override
    public UserModel getUserById(Long id) {
        UserResponseDto userDto = userFeignClient.getUser(id);
        return userFeignMapper.toModel(userDto);
    }

}
