package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateStoryUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateStoryService implements UpdateStoryUseCase {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public UpdateStoryService(
            UserStoryRepository userStoryRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.userStoryRepository = userStoryRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public UserStory updateStory(
            UUID storyId,
            UUID requesterUserId,
            String title,
            String description,
            UserStory.Priority priority,
            Integer storyPoints,
            UUID assigneeId,
            UUID sprintId) {
        UserStory userStory = userStoryRepository
                .findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(storyId));

        Project project = projectRepository
                .findById(userStory.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(userStory.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        if (title != null) {
            userStory.setTitle(title);
        }
        if (description != null) {
            userStory.setDescription(description);
        }
        if (priority != null) {
            userStory.setPriority(priority);
        }
        if (storyPoints != null) {
            userStory.setStoryPoints(storyPoints);
        }
        if (assigneeId != null) {
            userStory.setAssigneeId(assigneeId);
        }
        if (sprintId != null) {
            userStory.setSprintId(sprintId);
        }

        return userStoryRepository.save(userStory);
    }
}
