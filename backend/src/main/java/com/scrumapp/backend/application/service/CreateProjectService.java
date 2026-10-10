package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateProjectUseCase;
import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.BoardRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.Board;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateProjectService implements CreateProjectUseCase {

    private static final List<String> DEFAULT_COLUMN_NAMES = List.of("To Do", "In Progress", "Done");

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;

    public CreateProjectService(
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository,
            BoardRepository boardRepository,
            BoardColumnRepository boardColumnRepository) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
    }

    @Override
    public Project createProject(
            UUID workspaceId, UUID requesterUserId, String name, String key, String description) {
        requireMembership(workspaceId, requesterUserId);

        Project project = new Project(
                UUID.randomUUID(), workspaceId, name, key, description, null, Instant.now());
        Project savedProject = projectRepository.save(project);

        Board board = boardRepository.save(new Board(UUID.randomUUID(), savedProject.getId(), name));
        for (int orderIndex = 0; orderIndex < DEFAULT_COLUMN_NAMES.size(); orderIndex++) {
            boardColumnRepository.save(new BoardColumn(
                    UUID.randomUUID(), board.getId(), DEFAULT_COLUMN_NAMES.get(orderIndex), orderIndex, null));
        }

        return savedProject;
    }

    private void requireMembership(UUID workspaceId, UUID requesterUserId) {
        if (!workspaceMemberRepository.isMember(workspaceId, requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, workspaceId);
        }
    }
}
