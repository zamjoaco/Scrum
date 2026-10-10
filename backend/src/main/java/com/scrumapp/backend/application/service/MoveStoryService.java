package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.MoveStoryUseCase;
import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardColumnNotFoundException;
import com.scrumapp.backend.domain.board.WipLimitExceededException;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MoveStoryService implements MoveStoryUseCase {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public MoveStoryService(
            UserStoryRepository userStoryRepository,
            ProjectRepository projectRepository,
            BoardColumnRepository boardColumnRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.userStoryRepository = userStoryRepository;
        this.projectRepository = projectRepository;
        this.boardColumnRepository = boardColumnRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public UserStory moveStory(UUID storyId, UUID requesterUserId, UUID targetColumnId) {
        UserStory userStory = userStoryRepository
                .findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(storyId));

        Project project = projectRepository
                .findById(userStory.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(userStory.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        BoardColumn targetColumn = boardColumnRepository
                .findById(targetColumnId)
                .orElseThrow(() -> new BoardColumnNotFoundException(targetColumnId));

        if (targetColumn.getWipLimit() != null) {
            long currentCount = userStoryRepository.countByStatusId(targetColumnId);
            if (currentCount >= targetColumn.getWipLimit()) {
                throw new WipLimitExceededException(targetColumnId, targetColumn.getWipLimit());
            }
        }

        userStory.setStatusId(targetColumnId);
        return userStoryRepository.save(userStory);
    }
}
