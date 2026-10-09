package com.scrumapp.backend.application.port.in;

import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: listar los miembros de un workspace. Requiere que el
 * solicitante sea miembro (cualquier rol).
 */
public interface ListMembersUseCase {

    List<WorkspaceMember> listMembers(UUID workspaceId, UUID requesterId);
}
