package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private CreateStoryService service;

    @Test
    void createsStoryInProductBacklogAndFirstColumnWhenRequesterIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Board board = new Board(UUID.randomUUID(), project.getId(), "Board");
        BoardColumn first = new BoardColumn(UUID.randomUUID(), board.getId(), "To Do", 0, null);
        BoardColumn second = new BoardColumn(UUID.randomUUID(), board.getId(), "Done", 1, null);

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardRepository.findByProjectId(project.getId())).thenReturn(Optional.of(board));
        when(boardColumnRepository.listByBoardId(board.getId())).thenReturn(List.of(second, first));
        when(userStoryRepository.save(any(UserStory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserStory result = service.createStory(
                project.getId(), requesterId, "Como usuario quiero...", "desc",
                UserStory.Priority.HIGH, 5);

        assertThat(result.getSprintId()).isNull();
        assertThat(result.getStatusId()).isEqualTo(first.getId());
        assertThat(result.getCreatedBy()).isEqualTo(requesterId);
        assertThat(result.getProjectId()).isEqualTo(project.getId());
    }

    @Test
    void throwsNotFoundWhenProjectDoesNotExist() {
        UUID projectId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createStory(
                        projectId, requesterId, "title", null, UserStory.Priority.LOW, null))
                .isInstanceOf(ProjectNotFoundException.class);
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void rejectsAndDoesNotSaveWhenRequesterIsNotMemberOfTheProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.createStory(
                        project.getId(), requesterId, "title", null, UserStory.Priority.LOW, null))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }
}
