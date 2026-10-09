package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateProjectUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateProjectService implements CreateProjectUseCase {

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateProjectService(
            ProjectRepository projectRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Project createProject(
            UUID workspaceId, UUID requesterUserId, String name, String key, String description) {
        requireMembership(workspaceId, requesterUserId);

        Project project = new Project(
                UUID.randomUUID(), workspaceId, name, key, description, null, Instant.now());
        return projectRepository.save(project);
    }

    private void requireMembership(UUID workspaceId, UUID requesterUserId) {
        if (!workspaceMemberRepository.isMember(workspaceId, requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, workspaceId);
        }
    }
}
