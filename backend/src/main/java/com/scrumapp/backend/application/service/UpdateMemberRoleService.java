package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateMemberRoleUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateMemberRoleService implements UpdateMemberRoleUseCase {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public UpdateMemberRoleService(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public WorkspaceMember updateMemberRole(
            UUID workspaceId, UUID requesterId, UUID targetUserId, Role newRole) {
        WorkspaceMember requester = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, requesterId)
                .orElseThrow(() -> new NotWorkspaceMemberException(requesterId, workspaceId));
        WorkspaceRoles.requireOwnerOrAdmin(requester.getRole(), requesterId, workspaceId);

        WorkspaceMember target = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, targetUserId)
                .orElseThrow(() -> new NotWorkspaceMemberException(targetUserId, workspaceId));

        // No se permite que el unico OWNER del workspace se quite a si mismo ese rol,
        // para que el workspace nunca quede sin propietario.
        boolean selfDemotingSoleOwner = requesterId.equals(targetUserId)
                && target.getRole() == Role.OWNER
                && newRole != Role.OWNER
                && isSoleOwner(workspaceId, targetUserId);
        if (selfDemotingSoleOwner) {
            throw new InsufficientWorkspaceRoleException(requesterId, workspaceId);
        }

        target.setRole(newRole);
        return workspaceMemberRepository.save(target);
    }

    private boolean isSoleOwner(UUID workspaceId, UUID ownerId) {
        List<WorkspaceMember> members = workspaceMemberRepository.listByWorkspaceId(workspaceId);
        return members.stream().filter(member -> member.getRole() == Role.OWNER).count() == 1
                && members.stream()
                        .anyMatch(member -> member.getRole() == Role.OWNER
                                && member.getUserId().equals(ownerId));
    }
}
