package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UpdateMemberRoleServiceTest {

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private UpdateMemberRoleService service;

    @Test
    void updatesTheTargetRoleWhenTheRequesterIsOwnerOrAdmin() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.ADMIN, Instant.now());
        WorkspaceMember target = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, targetId, Role.MEMBER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, targetId))
                .thenReturn(Optional.of(target));
        when(workspaceMemberRepository.save(any(WorkspaceMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkspaceMember result =
                service.updateMemberRole(workspaceId, requesterId, targetId, Role.ADMIN);

        assertThat(result.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void throwsWhenTheRequesterHasInsufficientRole() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.MEMBER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));

        assertThatThrownBy(
                () -> service.updateMemberRole(workspaceId, requesterId, targetId, Role.ADMIN))
                .isInstanceOf(InsufficientWorkspaceRoleException.class);
    }

    @Test
    void throwsWhenTheSoleOwnerTriesToDemoteItself() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.OWNER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));
        when(workspaceMemberRepository.listByWorkspaceId(workspaceId)).thenReturn(List.of(requester));

        assertThatThrownBy(
                () -> service.updateMemberRole(workspaceId, requesterId, requesterId, Role.ADMIN))
                .isInstanceOf(InsufficientWorkspaceRoleException.class);
    }
}
