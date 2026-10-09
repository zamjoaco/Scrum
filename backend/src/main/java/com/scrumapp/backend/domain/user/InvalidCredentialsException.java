package com.scrumapp.backend.domain.user;

public class InvalidCredentialsException extends DomainException {

    public InvalidCredentialsException() {
        super("Credenciales invalidas");
    }
}
