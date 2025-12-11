package com.pragma.powerup.domain.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ConvertDate {

    public static LocalDateTime getCurrentDateTimeUTC() {
        Instant instant = Instant.now();
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

}
