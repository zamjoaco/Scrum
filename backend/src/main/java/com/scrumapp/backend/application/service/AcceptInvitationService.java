package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.AcceptInvitationUseCase;
import com.scrumapp.backend.application.port.out.InvitationRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.workspace.Invitation;
import com.scrumapp.backend.domain.workspace.InvitationExpiredException;
import com.scrumapp.backend.domain.workspace.InvitationNotFoundException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AcceptInvitationService implements AcceptInvitationUseCase {

    private final InvitationRepository invitationRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public AcceptInvitationService(
            InvitationRepository invitationRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.invitationRepository = invitationRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public WorkspaceMember acceptInvitation(String token, UUID userId) {
        Invitation invitation = invitationRepository
                .findByToken(token)
                .orElseThrow(() -> new InvitationNotFoundException(token));
        if (invitation.getExpiresAt().isBefore(Instant.now())) {
            throw new InvitationExpiredException(token);
        }

        WorkspaceMember member = new WorkspaceMember(
                UUID.randomUUID(),
                invitation.getWorkspaceId(),
                userId,
                invitation.getRole(),
                Instant.now());
        WorkspaceMember saved = workspaceMemberRepository.save(member);

        // Se borra para que el mismo token no pueda reusarse en una segunda aceptacion.
        invitationRepository.delete(invitation);

        return saved;
    }
}
