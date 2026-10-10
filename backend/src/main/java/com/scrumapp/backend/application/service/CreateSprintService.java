package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateSprintUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateSprintService implements CreateSprintUseCase {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateSprintService(
            SprintRepository sprintRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    /**
     * Orden anti-IDOR: primero se confirma que el project existe (404), y
     * recien despues se autoriza la membership contra el workspaceId real del
     * project (403).
     */
    @Override
    public Sprint createSprint(
            UUID projectId,
            UUID requesterUserId,
            String name,
            String goal,
            LocalDate startDate,
            LocalDate endDate) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        Sprint sprint = new Sprint(
                UUID.randomUUID(), projectId, name, goal, startDate, endDate, Sprint.Status.PLANNED);
        return sprintRepository.save(sprint);
    }
}
