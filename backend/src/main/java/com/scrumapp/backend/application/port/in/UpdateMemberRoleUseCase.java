package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.UUID;

/**
 * Caso de uso: actualizar el rol de un miembro de un workspace. Requiere
 * que el solicitante tenga rol OWNER o ADMIN.
 */
public interface UpdateMemberRoleUseCase {

    WorkspaceMember updateMemberRole(
            UUID workspaceId, UUID requesterId, UUID targetUserId, Role newRole);
}
