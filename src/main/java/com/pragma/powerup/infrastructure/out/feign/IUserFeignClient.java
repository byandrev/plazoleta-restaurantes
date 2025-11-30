package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.application.dto.response.UserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service", url = "http://localhost:8081")
public interface IUserFeignClient {

    @GetMapping("/api/v1/users/{id}")
    UserResponseDto getUser(@PathVariable Long id);

}
