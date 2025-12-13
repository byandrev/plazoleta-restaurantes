package com.pragma.powerup.domain.spi;

public interface IMessageExternalServicePort {

    void send(String to, String message);

}
