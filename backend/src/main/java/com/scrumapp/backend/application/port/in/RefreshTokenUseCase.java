package com.scrumapp.backend.application.port.in;

public interface RefreshTokenUseCase {

    AuthTokens refresh(String refreshToken);
}
