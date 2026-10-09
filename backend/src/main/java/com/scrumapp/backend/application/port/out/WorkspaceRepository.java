package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.workspace.Workspace;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de workspaces. Trabaja exclusivamente
 * con la entidad de dominio Workspace, nunca con una entidad JPA.
 */
public interface WorkspaceRepository {

    Optional<Workspace> findById(UUID id);

    Workspace save(Workspace workspace);

    void delete(Workspace workspace);

    List<Workspace> listForUser(UUID userId);
}
