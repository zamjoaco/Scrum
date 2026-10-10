package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.story.UserStory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de user stories. Trabaja
 * exclusivamente con la entidad de dominio UserStory, nunca con una
 * entidad JPA.
 */
public interface UserStoryRepository {

    Optional<UserStory> findById(UUID id);

    UserStory save(UserStory userStory);

    void delete(UserStory userStory);

    /**
     * Lista las historias de un project, con filtros opcionales. Pasar
     * {@code null} en sprintId o statusId significa "no filtrar por ese
     * campo" (no se usa para filtrar por Product Backlog explicitamente;
     * para eso el caller filtra el resultado por {@code sprintId == null}).
     */
    List<UserStory> listByProjectId(UUID projectId, UUID sprintId, UUID statusId);

    /** Cantidad de historias actualmente en una BoardColumn, para el chequeo de wipLimit. */
    long countByStatusId(UUID columnId);

    /**
     * Devuelve al Product Backlog (sprintId = null) todas las historias de
     * un sprint que no quedaron en un statusId terminal al cerrarlo. No se
     * modela como un metodo generico; el caller es responsable de decidir
     * que historias se consideran no completadas antes de invocar esto.
     */
    void reassignSprintToNull(UUID sprintId);
}
