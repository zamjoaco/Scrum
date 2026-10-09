package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.in.RefreshTokenUseCase;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenProvider tokenProvider;
    private final RefreshTokenStore refreshTokenStore;

    public RefreshTokenService(TokenProvider tokenProvider, RefreshTokenStore refreshTokenStore) {
        this.tokenProvider = tokenProvider;
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    public AuthTokens refresh(String refreshToken) {
        UUID userId = RefreshTokenCodec.decodeUserId(refreshToken);
        String rotatedRefreshToken = refreshTokenStore.validateAndRotate(userId, refreshToken);
        String accessToken = tokenProvider.generateAccessToken(userId);

        return new AuthTokens(
                accessToken, rotatedRefreshToken, "Bearer", tokenProvider.accessTokenTtlSeconds());
    }
}
