package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import java.time.Instant;
import java.util.UUID;

/**
 * Genera y persiste un par access/refresh token para un userId dado.
 * Compartido por los casos de uso que emiten sesion nueva (register, login,
 * refresh). Ver RefreshTokenCodec para el formato del refresh token.
 */
final class AuthTokensIssuer {

    private AuthTokensIssuer() {
    }

    static AuthTokens issue(UUID userId, TokenProvider tokenProvider, RefreshTokenStore refreshTokenStore) {
        String accessToken = tokenProvider.generateAccessToken(userId);

        String refreshSecret = UUID.randomUUID().toString();
        String refreshToken = RefreshTokenCodec.encode(userId, refreshSecret);
        Instant expiresAt = Instant.now().plus(refreshTokenStore.ttl());
        refreshTokenStore.store(userId, refreshToken, expiresAt);

        return new AuthTokens(accessToken, refreshToken, "Bearer", tokenProvider.accessTokenTtlSeconds());
    }
}
