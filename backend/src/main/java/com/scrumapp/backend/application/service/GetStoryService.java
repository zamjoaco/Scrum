package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.GetStoryUseCase;
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
public class GetStoryService implements GetStoryUseCase {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public GetStoryService(
            UserStoryRepository userStoryRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.userStoryRepository = userStoryRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public UserStory getStory(UUID storyId, UUID requesterUserId) {
        UserStory userStory = userStoryRepository
                .findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(storyId));

        Project project = projectRepository
                .findById(userStory.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(userStory.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        return userStory;
    }
}
