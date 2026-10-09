package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListProjectsServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private ListProjectsService service;

    @Test
    void listsOnlyNonArchivedProjectsWhenRequesterIsWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project active = project(workspaceId, "Activo", null);
        Project archived = project(workspaceId, "Archivado", Instant.now());
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(projectRepository.listByWorkspaceId(workspaceId)).thenReturn(List.of(active, archived));

        List<Project> result = service.listProjects(workspaceId, requesterId);

        assertThat(result).containsExactly(active);
    }

    @Test
    void rejectsWhenRequesterIsNotWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.listProjects(workspaceId, requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(projectRepository, never()).listByWorkspaceId(workspaceId);
    }

    private Project project(UUID workspaceId, String name, Instant archivedAt) {
        return new Project(UUID.randomUUID(), workspaceId, name, "KEY", null, archivedAt, Instant.now());
    }
}
