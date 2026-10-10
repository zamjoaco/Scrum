package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateBoardColumnUseCase;
import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardColumnNotFoundException;
import com.scrumapp.backend.domain.board.BoardNotFoundException;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateBoardColumnService implements UpdateBoardColumnUseCase {

    private final BoardColumnRepository boardColumnRepository;
    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public UpdateBoardColumnService(
            BoardColumnRepository boardColumnRepository,
            BoardRepository boardRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.boardColumnRepository = boardColumnRepository;
        this.boardRepository = boardRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    /**
     * Orden anti-IDOR: se busca la BoardColumn (404), despues su Board y el
     * Project al que pertenece (404 si hay un dato inconsistente), y recien
     * ahi se autoriza la membership contra el workspaceId real (403).
     */
    @Override
    public BoardColumn updateBoardColumn(
            UUID columnId, UUID requesterUserId, String name, int orderIndex, Integer wipLimit) {
        BoardColumn column = boardColumnRepository
                .findById(columnId)
                .orElseThrow(() -> new BoardColumnNotFoundException(columnId));

        Board board = boardRepository
                .findById(column.getBoardId())
                .orElseThrow(() -> new BoardNotFoundException(column.getBoardId()));

        Project project = projectRepository
                .findById(board.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(board.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        column.setName(name);
        column.setOrderIndex(orderIndex);
        column.setWipLimit(wipLimit);
        return boardColumnRepository.save(column);
    }
}
