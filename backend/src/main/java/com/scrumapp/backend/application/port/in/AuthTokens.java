package com.scrumapp.backend.application.port.in;

/**
 * Resultado comun de los casos de uso que emiten o renuevan una sesion.
 */
public record AuthTokens(String accessToken, String refreshToken, String tokenType, long expiresIn) {
}
