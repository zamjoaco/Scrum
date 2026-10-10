package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.StartSprintUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
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
public class StartSprintService implements StartSprintUseCase {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public StartSprintService(
            SprintRepository sprintRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Sprint startSprint(UUID sprintId, UUID requesterUserId) {
        Sprint sprint = sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException(sprintId));

        Project project = projectRepository
                .findById(sprint.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(sprint.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        if (sprint.getStatus() != Sprint.Status.PLANNED) {
            throw new InvalidSprintTransitionException(
                    "El sprint " + sprintId + " no esta PLANNED, no se puede iniciar");
        }

        sprintRepository
                .findActiveByProjectId(sprint.getProjectId())
                .ifPresent(activeSprint -> {
                    throw InvalidSprintTransitionException.alreadyActiveSprint(
                            sprint.getProjectId(), activeSprint.getId());
                });

        sprint.setStatus(Sprint.Status.ACTIVE);
        return sprintRepository.save(sprint);
    }
}
