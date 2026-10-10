package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.SprintRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.sprint.SprintNotFoundException;
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
class GetSprintServiceTest {

    @Mock
    private SprintRepository sprintRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private GetSprintService service;

    @Test
    void returnsSprintWhenRequesterIsMemberOfItsProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Sprint sprint = sprint(project.getId());
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);

        Sprint result = service.getSprint(sprint.getId(), requesterId);

        assertThat(result).isEqualTo(sprint);
    }

    @Test
    void throwsNotFoundWhenSprintDoesNotExist() {
        UUID sprintId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(sprintRepository.findById(sprintId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getSprint(sprintId, requesterId))
                .isInstanceOf(SprintNotFoundException.class);
    }

    @Test
    void rejectsWhenRequesterIsNotMemberOfTheSprintProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Sprint sprint = sprint(project.getId());
        when(sprintRepository.findById(sprint.getId())).thenReturn(Optional.of(sprint));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.getSprint(sprint.getId(), requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }

    private Sprint sprint(UUID projectId) {
        return new Sprint(
                UUID.randomUUID(),
                projectId,
                "Sprint 1",
                null,
                LocalDate.now(),
                LocalDate.now(),
                Sprint.Status.PLANNED);
    }
}
