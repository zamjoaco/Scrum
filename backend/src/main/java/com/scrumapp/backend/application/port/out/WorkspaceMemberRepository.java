package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de membresias de workspace. Trabaja
 * exclusivamente con la entidad de dominio WorkspaceMember, nunca con una
 * entidad JPA.
 */
public interface WorkspaceMemberRepository {

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    boolean isMember(UUID workspaceId, UUID userId);

    WorkspaceMember save(WorkspaceMember member);

    void delete(WorkspaceMember member);

    List<WorkspaceMember> listByWorkspaceId(UUID workspaceId);

    List<UUID> listWorkspaceIdsForUser(UUID userId);
}
