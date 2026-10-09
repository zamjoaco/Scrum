package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
class UpdateWorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private UpdateWorkspaceService service;

    @Test
    void updatesTheNameWhenTheRequesterIsOwnerOrAdmin() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.ADMIN, Instant.now());
        Workspace workspace = new Workspace(
                workspaceId, "Equipo Scrum", "equipo-scrum", UUID.randomUUID(), Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));
        when(workspaceRepository.save(any(Workspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Workspace result = service.updateWorkspace(workspaceId, requesterId, "Nuevo Nombre");

        assertThat(result.getName()).isEqualTo("Nuevo Nombre");
    }

    @Test
    void throwsWhenTheRequesterHasInsufficientRole() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember requester = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.MEMBER, Instant.now());
        when(workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, requesterId))
                .thenReturn(Optional.of(requester));

        assertThatThrownBy(() -> service.updateWorkspace(workspaceId, requesterId, "Nuevo Nombre"))
                .isInstanceOf(InsufficientWorkspaceRoleException.class);
    }
}
