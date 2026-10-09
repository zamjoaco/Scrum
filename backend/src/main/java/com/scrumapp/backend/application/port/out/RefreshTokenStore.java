package com.scrumapp.backend.application.port.out;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Puerto de salida para el ciclo de vida de refresh tokens. Pensado para
 * una implementacion con Redis, pero la interfaz no conoce ese detalle.
 */
public interface RefreshTokenStore {

    void store(UUID userId, String refreshToken, Instant expiresAt);

    /**
     * Valida el refresh token recibido y lo rota, devolviendo el nuevo.
     */
    String validateAndRotate(UUID userId, String refreshToken);

    void revoke(UUID userId, String refreshToken);

    /**
     * Vida util configurada para los refresh tokens, usada por quien
     * genera el token para calcular su expiresAt antes de llamar a store.
     */
    Duration ttl();
}
