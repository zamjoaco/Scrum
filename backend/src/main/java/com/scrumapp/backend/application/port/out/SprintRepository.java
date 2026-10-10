package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.sprint.Sprint;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de sprints. Trabaja exclusivamente con
 * la entidad de dominio Sprint, nunca con una entidad JPA.
 */
public interface SprintRepository {

    Optional<Sprint> findById(UUID id);

    Sprint save(Sprint sprint);

    List<Sprint> listByProjectId(UUID projectId);

    Optional<Sprint> findActiveByProjectId(UUID projectId);
}
