package com.pragma.powerup.application.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageRequestDto {

    private String to;

    private String message;

}
