package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.application.dto.request.MessageRequestDto;
import com.pragma.powerup.domain.spi.IMessageExternalServicePort;
import com.pragma.powerup.infrastructure.exception.InfraException;
import com.pragma.powerup.infrastructure.out.feign.IMessageFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageExternalAdapter implements IMessageExternalServicePort {

    private final IMessageFeignClient client;

    @Override
    public void send(String to, String message) {
        MessageRequestDto request = MessageRequestDto.builder().to(to).message(message).build();

        try {
            client.send(request);
        } catch (RuntimeException ex) {
            throw new InfraException(
                "Ocurrio error al enviar el mensaje a " + to,
                HttpStatus.BAD_REQUEST.value()
            );
        }
    }

}
