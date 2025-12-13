package com.pragma.powerup.domain.utils;

import java.security.SecureRandom;

public class PinGenerator {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Genera un PIN aleatorio compuesto solo por dígitos.
     * @param length La longitud deseada del PIN
     * @return El PIN generado como String.
     */
    public static String generatePin(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longitud del PIN debe ser positiva.");
        }

        StringBuilder pin = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = RANDOM.nextInt(DIGITS.length());
            pin.append(DIGITS.charAt(randomIndex));
        }

        return pin.toString();
    }

}
