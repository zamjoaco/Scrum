package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.GetProjectUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetProjectService implements GetProjectUseCase {

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public GetProjectService(
            ProjectRepository projectRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    /**
     * Orden anti-IDOR: primero se confirma que el project existe (404), y
     * recien despues se autoriza la membership contra el workspaceId real del
     * project (403), nunca contra un workspaceId provisto por el cliente.
     */
    @Override
    public Project getProject(UUID projectId, UUID requesterUserId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }
        return project;
    }
}
