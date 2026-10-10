package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.ListSprintsUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListSprintsService implements ListSprintsUseCase {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ListSprintsService(
            SprintRepository sprintRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public List<Sprint> listSprints(UUID projectId, UUID requesterUserId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        return sprintRepository.listByProjectId(projectId);
    }
}
