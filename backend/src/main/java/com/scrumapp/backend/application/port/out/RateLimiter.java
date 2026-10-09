package com.scrumapp.backend.application.port.out;

import java.time.Duration;

/**
 * Puerto de salida para limitar intentos repetidos sobre una clave logica
 * (ej. "login:email" o "pwd-reset:email") dentro de una ventana de tiempo.
 */
public interface RateLimiter {

    /**
     * Intenta consumir un intento para la clave dada. Devuelve false si se
     * supero el limite dentro de la ventana, en cuyo caso tambien informa
     * cuanto falta para que haya cupo nuevamente.
     */
    Result tryConsume(String key, long maxAttempts, Duration window);

    record Result(boolean allowed, long retryAfterSeconds) {
    }
}
