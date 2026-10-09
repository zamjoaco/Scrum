package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.workspace.Workspace;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListWorkspacesServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @InjectMocks
    private ListWorkspacesService service;

    @Test
    void returnsTheWorkspacesWhereTheUserHasMembership() {
        UUID userId = UUID.randomUUID();
        Workspace workspace = new Workspace(
                UUID.randomUUID(), "Equipo Scrum", "equipo-scrum", userId, Instant.now());
        when(workspaceRepository.listForUser(userId)).thenReturn(List.of(workspace));

        List<Workspace> result = service.listWorkspaces(userId);

        assertThat(result).containsExactly(workspace);
    }

    @Test
    void returnsAnEmptyListWhenTheUserHasNoMemberships() {
        UUID userId = UUID.randomUUID();
        when(workspaceRepository.listForUser(userId)).thenReturn(List.of());

        List<Workspace> result = service.listWorkspaces(userId);

        assertThat(result).isEmpty();
    }
}
