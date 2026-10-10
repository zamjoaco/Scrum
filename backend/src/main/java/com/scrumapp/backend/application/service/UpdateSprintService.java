package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateSprintUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.sprint.SprintNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.LocalDate;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateSprintService implements UpdateSprintUseCase {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public UpdateSprintService(
            SprintRepository sprintRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Sprint updateSprint(
            UUID sprintId,
            UUID requesterUserId,
            String name,
            String goal,
            LocalDate startDate,
            LocalDate endDate) {
        Sprint sprint = sprintRepository
                .findById(sprintId)
                .orElseThrow(() -> new SprintNotFoundException(sprintId));

        Project project = projectRepository
                .findById(sprint.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(sprint.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        sprint.setName(name);
        sprint.setGoal(goal);
        sprint.setStartDate(startDate);
        sprint.setEndDate(endDate);
        return sprintRepository.save(sprint);
    }
}
