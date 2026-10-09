package com.scrumapp.backend.application.service;

import static org.mockito.Mockito.verify;

import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LogoutServiceTest {

    @Mock
    private RefreshTokenStore refreshTokenStore;

    @Test
    void revokesRefreshTokenForAuthenticatedUser() {
        LogoutService service = new LogoutService(refreshTokenStore);
        UUID userId = UUID.randomUUID();

        service.logout(userId);

        verify(refreshTokenStore).revoke(userId, null);
    }
}
