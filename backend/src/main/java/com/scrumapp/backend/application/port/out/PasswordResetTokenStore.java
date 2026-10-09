package com.scrumapp.backend.application.port.out;

import java.time.Duration;
import java.util.Optional;

/**
 * Puerto de salida para tokens de un solo uso de restablecimiento de
 * contrasena. Pensado para una implementacion con TTL en Redis.
 */
public interface PasswordResetTokenStore {

    void store(String token, String email, Duration ttl);

    /**
     * Consume el token: si es valido devuelve el email asociado y lo
     * invalida de inmediato (no puede reutilizarse).
     */
    Optional<String> consume(String token);
}
