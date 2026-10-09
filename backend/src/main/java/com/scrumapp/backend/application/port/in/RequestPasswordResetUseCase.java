package com.scrumapp.backend.application.port.in;

public interface RequestPasswordResetUseCase {

    /**
     * Siempre completa sin error (salvo rate limit excedido): no revela si
     * el email existe. El controller es responsable de responder 202 en
     * todos los casos.
     */
    void requestReset(String email);
}
