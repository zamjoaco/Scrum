package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.project.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de projects. Trabaja exclusivamente
 * con la entidad de dominio Project, nunca con una entidad JPA.
 *
 * <p>No expone un metodo {@code archive} separado: archivar un project es
 * setear su {@code archivedAt} en el dominio y llamar a {@code save}.
 */
public interface ProjectRepository {

    Optional<Project> findById(UUID id);

    Project save(Project project);

    List<Project> listByWorkspaceId(UUID workspaceId);
}
