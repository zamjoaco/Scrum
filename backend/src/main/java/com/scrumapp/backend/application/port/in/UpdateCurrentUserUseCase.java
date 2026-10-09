package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.user.User;
import java.util.UUID;

/**
 * Caso de uso: actualizar los datos basicos del perfil del usuario autenticado.
 */
public interface UpdateCurrentUserUseCase {

    User updateCurrentUser(UUID userId, String firstNames, String lastNames);
}
