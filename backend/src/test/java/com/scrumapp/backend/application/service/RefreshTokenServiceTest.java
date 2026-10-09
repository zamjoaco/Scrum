package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.in.AuthTokens;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import com.scrumapp.backend.application.port.out.TokenProvider;
import com.scrumapp.backend.domain.user.InvalidCredentialsException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private TokenProvider tokenProvider;
    @Mock
    private RefreshTokenStore refreshTokenStore;

    private RefreshTokenService service;

    @Test
    void refreshesTokensForValidRefreshToken() {
        service = new RefreshTokenService(tokenProvider, refreshTokenStore);

        UUID userId = UUID.randomUUID();
        String refreshToken = userId + ":secret";
        when(refreshTokenStore.validateAndRotate(userId, refreshToken)).thenReturn(userId + ":new-secret");
        when(tokenProvider.generateAccessToken(userId)).thenReturn("new-access-token");
        when(tokenProvider.accessTokenTtlSeconds()).thenReturn(900L);

        AuthTokens tokens = service.refresh(refreshToken);

        assertThat(tokens.accessToken()).isEqualTo("new-access-token");
        assertThat(tokens.refreshToken()).isEqualTo(userId + ":new-secret");
    }

    @Test
    void rejectsMalformedRefreshToken() {
        service = new RefreshTokenService(tokenProvider, refreshTokenStore);

        assertThatThrownBy(() -> service.refresh("not-a-valid-token"))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}
