package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.user.User;
import java.util.UUID;

/**
 * Caso de uso: obtener otro usuario por id.
 */
public interface GetUserByIdUseCase {

    User getUserById(UUID requesterId, UUID targetUserId);
}
