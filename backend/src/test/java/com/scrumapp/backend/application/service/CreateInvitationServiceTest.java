package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.InvitationRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Invitation;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateInvitationServiceTest {

    @Mock
    private InvitationRepository invitationRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private CreateInvitationService service;

    @Test
    void createsAPendingInvitationWhenTheRequesterIsOwnerOrAdmin() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.OWNER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));
        when(invitationRepository.save(any(Invitation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Invitation result = service.createInvitation(
                workspaceId, requesterId, "nuevo@example.com", Role.MEMBER);

        assertThat(result.getEmail()).isEqualTo("nuevo@example.com");
        assertThat(result.getRole()).isEqualTo(Role.MEMBER);
        assertThat(result.getStatus()).isEqualTo(Invitation.Status.PENDING);
        assertThat(result.getExpiresAt()).isAfter(Instant.now());
        assertThat(result.getToken()).isNotBlank();
    }

    @Test
    void throwsWhenTheRequesterHasInsufficientRole() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.VIEWER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> service.createInvitation(
                workspaceId, requesterId, "nuevo@example.com", Role.MEMBER))
                .isInstanceOf(InsufficientWorkspaceRoleException.class);
    }
}
