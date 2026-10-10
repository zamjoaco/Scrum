package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.BoardView;
import com.scrumapp.backend.application.port.in.GetBoardUseCase;
import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardNotFoundException;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetBoardService implements GetBoardUseCase {

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserStoryRepository userStoryRepository;

    public GetBoardService(
            BoardRepository boardRepository,
            BoardColumnRepository boardColumnRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            UserStoryRepository userStoryRepository) {
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userStoryRepository = userStoryRepository;
    }

    @Override
    public BoardView getBoard(UUID projectId, UUID requesterUserId) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        Board board = boardRepository
                .findByProjectId(projectId)
                .orElseThrow(() -> new BoardNotFoundException(projectId));

        List<BoardColumn> columns = boardColumnRepository.listByBoardId(board.getId());
        List<BoardView.ColumnView> columnViews = columns.stream()
                .map(column -> new BoardView.ColumnView(column, storyIdsInColumn(projectId, column)))
                .toList();

        return new BoardView(board, columnViews);
    }

    private List<UUID> storyIdsInColumn(UUID projectId, BoardColumn column) {
        return userStoryRepository.listByProjectId(projectId, null, column.getId()).stream()
                .map(UserStory::getId)
                .toList();
    }
}
