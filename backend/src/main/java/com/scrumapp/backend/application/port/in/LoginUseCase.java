package com.scrumapp.backend.application.port.in;

public interface LoginUseCase {

    AuthTokens login(String email, String rawPassword);
}
