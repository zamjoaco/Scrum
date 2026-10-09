package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.ArchiveProjectUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ArchiveProjectService implements ArchiveProjectUseCase {

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ArchiveProjectService(
            ProjectRepository projectRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    /**
     * Soft-delete: setea archivedAt con la fecha/hora actual. La fila nunca se
     * elimina fisicamente de la base.
     */
    @Override
    public Project archiveProject(UUID projectId, UUID requesterUserId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        project.setArchivedAt(Instant.now());
        return projectRepository.save(project);
    }
}
