package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateInvitationUseCase;
import com.scrumapp.backend.application.port.out.InvitationRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Invitation;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateInvitationService implements CreateInvitationUseCase {

    private static final Duration EXPIRATION = Duration.ofDays(7);

    private final InvitationRepository invitationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateInvitationService(
            InvitationRepository invitationRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.invitationRepository = invitationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Invitation createInvitation(
            UUID workspaceId, UUID requesterId, String email, Role role) {
        WorkspaceMember requester = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, requesterId)
                .orElseThrow(() -> new NotWorkspaceMemberException(requesterId, workspaceId));
        WorkspaceRoles.requireOwnerOrAdmin(requester.getRole(), requesterId, workspaceId);

        Invitation invitation = new Invitation(
                UUID.randomUUID(),
                workspaceId,
                email,
                role,
                UUID.randomUUID().toString(),
                Invitation.Status.PENDING,
                Instant.now().plus(EXPIRATION));
        return invitationRepository.save(invitation);
    }
}
