package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceNotFoundException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetWorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private GetWorkspaceService service;

    @Test
    void returnsTheWorkspaceWhenTheRequesterIsAMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Workspace workspace = new Workspace(
                workspaceId, "Equipo Scrum", "equipo-scrum", requesterId, Instant.now());
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.of(workspace));

        Workspace result = service.getWorkspace(workspaceId, requesterId);

        assertThat(result).isSameAs(workspace);
    }

    @Test
    void throwsWhenTheRequesterIsNotAMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.getWorkspace(workspaceId, requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
    }

    @Test
    void throwsWhenTheWorkspaceDoesNotExist() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(workspaceRepository.findById(workspaceId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getWorkspace(workspaceId, requesterId))
                .isInstanceOf(WorkspaceNotFoundException.class);
    }
}
