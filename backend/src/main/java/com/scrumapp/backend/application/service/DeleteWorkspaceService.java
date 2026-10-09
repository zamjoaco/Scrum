package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.DeleteWorkspaceUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import com.scrumapp.backend.domain.workspace.WorkspaceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DeleteWorkspaceService implements DeleteWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public DeleteWorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public void deleteWorkspace(UUID workspaceId, UUID requesterId) {
        WorkspaceMember requester = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, requesterId)
                .orElseThrow(() -> new NotWorkspaceMemberException(requesterId, workspaceId));
        // Borrar el workspace completo exige el rol mas estricto: solo OWNER.
        if (requester.getRole() != Role.OWNER) {
            throw new InsufficientWorkspaceRoleException(requesterId, workspaceId);
        }

        Workspace workspace = workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
        workspaceRepository.delete(workspace);
    }
}
