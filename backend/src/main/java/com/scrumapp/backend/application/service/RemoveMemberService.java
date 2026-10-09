package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.RemoveMemberUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RemoveMemberService implements RemoveMemberUseCase {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public RemoveMemberService(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public void removeMember(UUID workspaceId, UUID requesterId, UUID targetUserId) {
        WorkspaceMember requester = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, requesterId)
                .orElseThrow(() -> new NotWorkspaceMemberException(requesterId, workspaceId));
        WorkspaceRoles.requireOwnerOrAdmin(requester.getRole(), requesterId, workspaceId);

        WorkspaceMember target = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, targetUserId)
                .orElseThrow(() -> new NotWorkspaceMemberException(targetUserId, workspaceId));
        workspaceMemberRepository.delete(target);
    }
}
