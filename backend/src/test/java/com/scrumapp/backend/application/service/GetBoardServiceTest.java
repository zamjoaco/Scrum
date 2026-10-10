package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.in.BoardView;
import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardNotFoundException;
import com.scrumapp.backend.domain.project.Project;
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
class GetBoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Mock
    private UserStoryRepository userStoryRepository;

    @InjectMocks
    private GetBoardService service;

    @Test
    void returnsBoardWithColumnsAndStoryIdsWhenRequesterIsWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Board board = new Board(UUID.randomUUID(), project.getId(), "Board");
        BoardColumn column = new BoardColumn(UUID.randomUUID(), board.getId(), "To Do", 0, null);
        UserStory story = new UserStory(
                UUID.randomUUID(),
                project.getId(),
                null,
                column.getId(),
                null,
                UUID.randomUUID(),
                "Title",
                null,
                null,
                UserStory.Priority.LOW,
                Instant.now());

        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardRepository.findByProjectId(project.getId())).thenReturn(Optional.of(board));
        when(boardColumnRepository.listByBoardId(board.getId())).thenReturn(List.of(column));
        when(userStoryRepository.listByProjectId(project.getId(), null, column.getId()))
                .thenReturn(List.of(story));

        BoardView result = service.getBoard(project.getId(), requesterId);

        assertThat(result.board()).isEqualTo(board);
        assertThat(result.columns()).hasSize(1);
        assertThat(result.columns().get(0).storyIds()).containsExactly(story.getId());
    }

    @Test
    void throwsNotFoundWhenProjectHasNoBoard() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardRepository.findByProjectId(project.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getBoard(project.getId(), requesterId))
                .isInstanceOf(BoardNotFoundException.class);
    }

    @Test
    void rejectsWhenRequesterIsNotWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.getBoard(project.getId(), requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }
}
