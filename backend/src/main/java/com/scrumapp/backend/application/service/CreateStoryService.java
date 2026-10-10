package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateStoryUseCase;
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
import java.util.Comparator;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateStoryService implements CreateStoryUseCase {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateStoryService(
            UserStoryRepository userStoryRepository,
            ProjectRepository projectRepository,
            BoardRepository boardRepository,
            BoardColumnRepository boardColumnRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.userStoryRepository = userStoryRepository;
        this.projectRepository = projectRepository;
        this.boardRepository = boardRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public UserStory createStory(
            UUID projectId,
            UUID requesterUserId,
            String title,
            String description,
            UserStory.Priority priority,
            Integer storyPoints) {
        Project project = projectRepository
                .findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        Board board = boardRepository
                .findByProjectId(projectId)
                .orElseThrow(() -> new IllegalStateException(
                        "El project " + projectId + " no tiene un board"));
        BoardColumn firstColumn = boardColumnRepository.listByBoardId(board.getId()).stream()
                .min(Comparator.comparingInt(BoardColumn::getOrderIndex))
                .orElseThrow(() -> new IllegalStateException(
                        "El board " + board.getId() + " no tiene columnas"));

        UserStory userStory = new UserStory(
                UUID.randomUUID(),
                projectId,
                null,
                firstColumn.getId(),
                null,
                requesterUserId,
                title,
                description,
                storyPoints,
                priority,
                Instant.now());
        return userStoryRepository.save(userStory);
    }
}
