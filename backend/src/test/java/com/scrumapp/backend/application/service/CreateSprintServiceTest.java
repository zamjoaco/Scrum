package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateSprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private CreateSprintService service;

    @Test
    void createsPlannedSprintWhenRequesterIsWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sprint result = service.createSprint(
                project.getId(),
                requesterId,
                "Sprint 1",
                "Goal",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 1, 14));

        assertThat(result.getProjectId()).isEqualTo(project.getId());
        assertThat(result.getStatus()).isEqualTo(Sprint.Status.PLANNED);
        verify(sprintRepository).save(result);
    }

    @Test
    void throwsNotFoundWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createSprint(
                        projectId, requesterId, "Sprint 1", null, LocalDate.now(), LocalDate.now()))
                .isInstanceOf(ProjectNotFoundException.class);
        verify(sprintRepository, never()).save(any(Sprint.class));
    }

    @Test
    void rejectsWhenRequesterIsNotWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.createSprint(
                        project.getId(), requesterId, "Sprint 1", null, LocalDate.now(), LocalDate.now()))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(sprintRepository, never()).save(any(Sprint.class));
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }
}
