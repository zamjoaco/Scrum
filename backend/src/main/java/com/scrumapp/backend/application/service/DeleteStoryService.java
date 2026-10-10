package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.DeleteStoryUseCase;
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
public class DeleteStoryService implements DeleteStoryUseCase {

    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public DeleteStoryService(
            UserStoryRepository userStoryRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.userStoryRepository = userStoryRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public void deleteStory(UUID storyId, UUID requesterUserId) {
        UserStory userStory = userStoryRepository
                .findById(storyId)
                .orElseThrow(() -> new StoryNotFoundException(storyId));

        Project project = projectRepository
                .findById(userStory.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(userStory.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        userStoryRepository.delete(userStory);
    }
}
