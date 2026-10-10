package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateBoardColumnUseCase;
import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardNotFoundException;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateBoardColumnService implements CreateBoardColumnUseCase {

    private final BoardColumnRepository boardColumnRepository;
    private final BoardRepository boardRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateBoardColumnService(
            BoardColumnRepository boardColumnRepository,
            BoardRepository boardRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.boardColumnRepository = boardColumnRepository;
        this.boardRepository = boardRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public BoardColumn createBoardColumn(
            UUID projectId, UUID requesterUserId, String name, int orderIndex, Integer wipLimit) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        Board board = boardRepository
                .findByProjectId(projectId)
                .orElseThrow(() -> new BoardNotFoundException(projectId));

        BoardColumn column = new BoardColumn(UUID.randomUUID(), board.getId(), name, orderIndex, wipLimit);
        return boardColumnRepository.save(column);
    }
}
