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
import com.scrumapp.backend.domain.sprint.InvalidSprintTransitionException;
import com.scrumapp.backend.domain.sprint.Sprint;
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
class StartSprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private StartSprintService service;

    @Test
    void startsPlannedSprintWhenNoOtherSprintIsActive() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Sprint sprint = sprint(project.getId(), Sprint.Status.PLANNED);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(sprintRepository.findActiveByProjectId(project.getId())).thenReturn(Optional.empty());
        when(sprintRepository.save(any(Sprint.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Sprint result = service.startSprint(sprint.getId(), requesterId);

        assertThat(result.getStatus()).isEqualTo(Sprint.Status.ACTIVE);
    }

    @Test
    void rejectsWhenSprintIsNotPlanned() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Sprint sprint = sprint(project.getId(), Sprint.Status.CLOSED);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);

        assertThatThrownBy(() -> service.startSprint(sprint.getId(), requesterId))
                .isInstanceOf(InvalidSprintTransitionException.class);
        verify(sprintRepository, never()).save(any(Sprint.class));
    }

    @Test
    void rejectsWhenProjectAlreadyHasAnActiveSprint() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Sprint sprint = sprint(project.getId(), Sprint.Status.PLANNED);
        Sprint activeSprint = sprint(project.getId(), Sprint.Status.ACTIVE);
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(sprintRepository.findActiveByProjectId(project.getId())).thenReturn(Optional.of(activeSprint));

        assertThatThrownBy(() -> service.startSprint(sprint.getId(), requesterId))
                .isInstanceOf(InvalidSprintTransitionException.class);
        verify(sprintRepository, never()).save(any(Sprint.class));
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }

    private Sprint sprint(UUID projectId, Sprint.Status status) {
        return new Sprint(
                UUID.randomUUID(), projectId, "Sprint 1", "Goal", LocalDate.now(), LocalDate.now(), status);
    }
}
