package com.scrumapp.backend.application.service;

import com.scrumapp.backend.domain.user.InvalidCredentialsException;
import java.util.UUID;

/**
 * El RefreshTokenStore se indexa por userId (ver puerto en application.port.out),
 * pero el cliente solo envia el refresh token opaco, sin el userId. Para poder
 * resolver el userId antes de llamar al store, el valor que viaja al cliente
 * tiene el formato "{userId}:{secretoAleatorio}". Este codec es un detalle de
 * implementacion de application.service, no se expone como puerto.
 */
final class RefreshTokenCodec {

    private static final String SEPARATOR = ":";

    private RefreshTokenCodec() {
    }

    static String encode(UUID userId, String secret) {
        return userId + SEPARATOR + secret;
    }

    static UUID decodeUserId(String rawToken) {
        if (rawToken == null) {
            throw new InvalidCredentialsException();
        }
        int separatorIndex = rawToken.indexOf(SEPARATOR);
        if (separatorIndex <= 0) {
            throw new InvalidCredentialsException();
        }
        try {
            return UUID.fromString(rawToken.substring(0, separatorIndex));
        } catch (IllegalArgumentException ex) {
            throw new InvalidCredentialsException();
        }
    }
}
