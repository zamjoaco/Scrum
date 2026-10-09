package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateWorkspaceUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import com.scrumapp.backend.domain.workspace.WorkspaceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateWorkspaceService implements UpdateWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public UpdateWorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Workspace updateWorkspace(UUID workspaceId, UUID requesterId, String name) {
        WorkspaceMember requester = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, requesterId)
                .orElseThrow(() -> new NotWorkspaceMemberException(requesterId, workspaceId));
        WorkspaceRoles.requireOwnerOrAdmin(requester.getRole(), requesterId, workspaceId);

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        workspace.setName(name);
        return workspaceRepository.save(workspace);
    }
}
