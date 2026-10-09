package com.scrumapp.backend.application.port.out;

import java.time.Instant;
import java.util.UUID;

/**
 * Puerto de salida para generacion y validacion de access tokens.
 */
public interface TokenProvider {

    String generateAccessToken(UUID userId);

    TokenClaims parseAndValidate(String token);

    /**
     * Vida util del access token en segundos, usada para informar
     * expires_in en las respuestas de autenticacion.
     */
    long accessTokenTtlSeconds();

    /**
     * Claims minimos extraidos de un access token valido.
     */
    record TokenClaims(UUID userId, Instant expiresAt) {
    }
}
