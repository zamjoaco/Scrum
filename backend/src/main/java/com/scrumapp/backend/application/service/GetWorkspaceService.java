package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.GetWorkspaceUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceNotFoundException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetWorkspaceService implements GetWorkspaceUseCase {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public GetWorkspaceService(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Workspace getWorkspace(UUID workspaceId, UUID requesterId) {
        if (!workspaceMemberRepository.isMember(workspaceId, requesterId)) {
            throw new NotWorkspaceMemberException(requesterId, workspaceId);
        }
        return workspaceRepository
                .findById(workspaceId)
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId));
    }
}
