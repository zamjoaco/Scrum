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
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private CreateProjectService service;

    @Test
    void createsProjectWhenRequesterIsWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project result = service.createProject(workspaceId, requesterId, "Checkout", "CHK", "desc");

        assertThat(result.getId()).isNotNull();
        assertThat(result.getWorkspaceId()).isEqualTo(workspaceId);
        assertThat(result.getName()).isEqualTo("Checkout");
        assertThat(result.getKey()).isEqualTo("CHK");
        assertThat(result.getDescription()).isEqualTo("desc");
        assertThat(result.getArchivedAt()).isNull();
        assertThat(result.getCreatedAt()).isNotNull();
        verify(projectRepository).save(result);
    }

    @Test
    void rejectsAndDoesNotPersistWhenRequesterIsNotWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.createProject(workspaceId, requesterId, "Checkout", "CHK", null))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(projectRepository, never()).save(any(Project.class));
    }
}
