package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.InvitationRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Invitation;
import com.scrumapp.backend.domain.workspace.InvitationExpiredException;
import com.scrumapp.backend.domain.workspace.InvitationNotFoundException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AcceptInvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private AcceptInvitationService service;

    @Test
    void createsTheMembershipAndDeletesTheInvitationWhenValid() {
        UUID workspaceId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Invitation invitation = new Invitation(
                UUID.randomUUID(),
                workspaceId,
                "nuevo@example.com",
                Role.MEMBER,
                "token-123",
                Invitation.Status.PENDING,
                Instant.now().plus(1, ChronoUnit.DAYS));
        when(invitationRepository.findByToken("token-123")).thenReturn(Optional.of(invitation));
        when(workspaceMemberRepository.save(any(WorkspaceMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkspaceMember result = service.acceptInvitation("token-123", userId);

        assertThat(result.getWorkspaceId()).isEqualTo(workspaceId);
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getRole()).isEqualTo(Role.MEMBER);
        verify(invitationRepository).delete(invitation);
    }

    @Test
    void throwsWhenTheTokenDoesNotExist() {
        when(invitationRepository.findByToken("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.acceptInvitation("missing", UUID.randomUUID()))
                .isInstanceOf(InvitationNotFoundException.class);
    }

    @Test
    void throwsWhenTheInvitationIsExpired() {
        Invitation invitation = new Invitation(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "nuevo@example.com",
                Role.MEMBER,
                "token-123",
                Invitation.Status.PENDING,
                Instant.now().minus(1, ChronoUnit.DAYS));
        when(invitationRepository.findByToken("token-123")).thenReturn(Optional.of(invitation));

        assertThatThrownBy(() -> service.acceptInvitation("token-123", UUID.randomUUID()))
                .isInstanceOf(InvitationExpiredException.class);
    }
}
