package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Invitation;
import java.util.UUID;

/**
 * Caso de uso: invitar a un email a un workspace. Requiere que el
 * solicitante tenga rol OWNER o ADMIN.
 */
public interface CreateInvitationUseCase {

    Invitation createInvitation(UUID workspaceId, UUID requesterId, String email, Role role);
}
