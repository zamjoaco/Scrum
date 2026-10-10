package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CloseSprintUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.sprint.InvalidSprintTransitionException;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.sprint.SprintNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CloseSprintService implements CloseSprintUseCase {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserStoryRepository userStoryRepository;

    public CloseSprintService(
            SprintRepository sprintRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            UserStoryRepository userStoryRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userStoryRepository = userStoryRepository;
    }

    @Override
    public Sprint closeSprint(UUID sprintId, UUID requesterUserId) {
        Sprint sprint = sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException(sprintId));

        Project project = projectRepository
                .findById(sprint.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(sprint.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        if (sprint.getStatus() != Sprint.Status.ACTIVE) {
            throw new InvalidSprintTransitionException(
                    "El sprint " + sprintId + " no esta ACTIVE, no se puede cerrar");
        }

        sprint.setStatus(Sprint.Status.CLOSED);
        Sprint closed = sprintRepository.save(sprint);

        /*
         * Heuristica de "columna final": la columna con mayor orderIndex del
         * board del project. reassignSprintToNull implementa esa heuristica
         * en el adapter de persistencia (fuera del scope de este worker) y
         * devuelve al backlog (sprintId = null) toda historia de este sprint
         * que no haya terminado ahi.
         */
        userStoryRepository.reassignSprintToNull(sprintId);

        return closed;
    }
}
