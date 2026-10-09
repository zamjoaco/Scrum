package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import com.scrumapp.backend.domain.workspace.Workspace;
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
class DeleteWorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private DeleteWorkspaceService service;

    @Test
    void deletesTheWorkspaceWhenTheRequesterIsOwner() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.OWNER, Instant.now());
        Workspace workspace = new Workspace(
                workspaceId, "Equipo Scrum", "equipo-scrum", requesterId, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

        service.deleteWorkspace(workspaceId, requesterId);

        verify(workspaceRepository).delete(workspace);
    }

    @Test
    void throwsWhenTheRequesterIsAdminButNotOwner() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.ADMIN, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> service.deleteWorkspace(workspaceId, requesterId))
                .isInstanceOf(InsufficientWorkspaceRoleException.class);
    }
}
