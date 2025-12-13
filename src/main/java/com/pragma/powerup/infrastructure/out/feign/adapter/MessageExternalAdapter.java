package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.application.dto.request.MessageRequestDto;
import com.pragma.powerup.domain.spi.IMessageExternalServicePort;
import com.pragma.powerup.infrastructure.out.feign.IMessageFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageExternalAdapter implements IMessageExternalServicePort {

    private final IMessageFeignClient client;

    @Override
    public void send(String to, String message) {
        MessageRequestDto request = MessageRequestDto.builder().to(to).message(message).build();
        client.send(request);
    }

}
