package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.CreateTaskUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.TaskRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.task.Task;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepository taskRepository;
    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public CreateTaskService(
            TaskRepository taskRepository,
            UserStoryRepository userStoryRepository,
            ProjectRepository projectRepository,
            WorkspaceMemberRepository workspaceMemberRepository) {
        this.taskRepository = taskRepository;
        this.userStoryRepository = userStoryRepository;
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public Task createTask(UUID userStoryId, UUID requesterUserId, String title, UUID assigneeId) {
        UserStory userStory = userStoryRepository
                .findById(userStoryId)
                .orElseThrow(() -> new StoryNotFoundException(userStoryId));

        Project project = projectRepository
                .findById(userStory.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(userStory.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        Task task = new Task(
                UUID.randomUUID(),
                userStoryId,
                userStory.getStatusId(),
                assigneeId,
                title,
                Instant.now());
        return taskRepository.save(task);
    }
}
