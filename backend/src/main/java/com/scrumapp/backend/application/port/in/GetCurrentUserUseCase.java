package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.user.User;
import java.util.UUID;

/**
 * Caso de uso: obtener el perfil del usuario autenticado.
 */
public interface GetCurrentUserUseCase {

    User getCurrentUser(UUID userId);
}
