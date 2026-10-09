package com.scrumapp.backend.domain.user;

/**
 * Base de toda excepcion de dominio propia del contexto de usuarios.
 * Nunca se usa RuntimeException pelada para senalizar errores de negocio.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
