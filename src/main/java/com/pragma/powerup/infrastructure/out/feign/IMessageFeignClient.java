package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.application.dto.request.MessageRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "messages-service", url = "http://localhost:8084")
public interface IMessageFeignClient {

    @PostMapping("/api/v1/messages/")
    void send(@RequestBody MessageRequestDto message);

}
