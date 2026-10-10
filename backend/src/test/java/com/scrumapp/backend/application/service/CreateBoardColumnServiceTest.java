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
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.project.Project;
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
class CreateBoardColumnServiceTest {

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private CreateBoardColumnService service;

    @Test
    void createsColumnWhenRequesterIsWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        Board board = new Board(UUID.randomUUID(), project.getId(), "Board");
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardRepository.findByProjectId(project.getId())).thenReturn(Optional.of(board));
        when(boardColumnRepository.save(any(BoardColumn.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        BoardColumn result =
                service.createBoardColumn(project.getId(), requesterId, "Review", 3, 5);

        assertThat(result.getBoardId()).isEqualTo(board.getId());
        assertThat(result.getName()).isEqualTo("Review");
        assertThat(result.getOrderIndex()).isEqualTo(3);
        assertThat(result.getWipLimit()).isEqualTo(5);
    }

    @Test
    void rejectsWhenRequesterIsNotWorkspaceMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(
                        () -> service.createBoardColumn(project.getId(), requesterId, "Review", 3, null))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(boardColumnRepository, never()).save(any(BoardColumn.class));
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }
}
