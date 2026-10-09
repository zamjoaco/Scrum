package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
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
class RemoveMemberServiceTest {

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private RemoveMemberService service;

    @Test
    void removesTheTargetMemberWhenTheRequesterIsOwnerOrAdmin() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.OWNER, Instant.now());
        WorkspaceMember target = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, targetId, Role.MEMBER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, targetId))
                .thenReturn(Optional.of(target));

        service.removeMember(workspaceId, requesterId, targetId);

        verify(workspaceMemberRepository).delete(target);
    }

    @Test
    void throwsWhenTheRequesterHasInsufficientRole() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.VIEWER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> service.removeMember(workspaceId, requesterId, targetId))
                .isInstanceOf(InsufficientWorkspaceRoleException.class);
    }
}
