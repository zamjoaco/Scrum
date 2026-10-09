package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.LogoutUseCase;
import com.scrumapp.backend.application.port.out.RefreshTokenStore;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LogoutService implements LogoutUseCase {

    private final RefreshTokenStore refreshTokenStore;

    public LogoutService(RefreshTokenStore refreshTokenStore) {
        this.refreshTokenStore = refreshTokenStore;
    }

    @Override
    public void logout(UUID userId) {
        // El endpoint de logout no recibe el refresh token en el body (ver
        // docs/api/paths/auth.yaml): solo hay un refresh token activo por
        // usuario, asi que se revoca por userId sin exigir el valor exacto.
        refreshTokenStore.revoke(userId, null);
    }
}
