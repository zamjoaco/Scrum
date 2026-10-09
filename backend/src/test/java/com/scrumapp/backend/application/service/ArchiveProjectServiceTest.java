package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArchiveProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private ArchiveProjectService service;

    @Test
    void setsArchivedAtInsteadOfDeletingWhenRequesterIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(projectRepository.save(project)).thenReturn(project);

        Project result = service.archiveProject(project.getId(), requesterId);

        assertThat(result.getArchivedAt()).isNotNull();
        verify(projectRepository).save(project);
    }

    @Test
    void throwsNotFoundWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.archiveProject(projectId, requesterId))
                .isInstanceOf(ProjectNotFoundException.class);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void rejectsAndDoesNotSaveWhenRequesterIsNotMemberOfTheProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.archiveProject(project.getId(), requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
        assertThat(project.getArchivedAt()).isNull();
        verify(projectRepository, never()).save(any(Project.class));
        verify(workspaceMemberRepository).isMember(workspaceId, requesterId);
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }
}
