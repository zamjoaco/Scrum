package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.UUID;

/**
 * Caso de uso: aceptar una invitacion a un workspace mediante su token.
 */
public interface AcceptInvitationUseCase {

    WorkspaceMember acceptInvitation(String token, UUID userId);
}
