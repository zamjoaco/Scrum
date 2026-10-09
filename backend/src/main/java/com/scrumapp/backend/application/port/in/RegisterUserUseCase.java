package com.scrumapp.backend.application.port.in;

public interface RegisterUserUseCase {

    AuthTokens register(RegisterCommand command);

    record RegisterCommand(String firstNames, String lastNames, String email, String rawPassword) {
    }
}
