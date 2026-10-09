package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateWorkspaceUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.util.Locale;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateWorkspaceService implements CreateWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateWorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Workspace createWorkspace(UUID ownerId, String name) {
        Workspace workspace = new Workspace(
                UUID.randomUUID(), name, slugify(name), ownerId, Instant.now());
        Workspace saved = workspaceRepository.save(workspace);

        WorkspaceMember owner = new WorkspaceMember(
                UUID.randomUUID(), saved.getId(), ownerId, Role.OWNER, Instant.now());
        workspaceMemberRepository.save(owner);

        return saved;
    }

    private static String slugify(String name) {
        return name.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", "-");
    }
}
