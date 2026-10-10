package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.UpdateTaskUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.TaskRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.task.Task;
import com.scrumapp.backend.domain.task.TaskNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdateTaskService implements UpdateTaskUseCase {

    private final TaskRepository taskRepository;
    private final UserStoryRepository userStoryRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public UpdateTaskService(
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
    public Task updateTask(
            UUID taskId, UUID requesterUserId, String title, UUID assigneeId, UUID statusId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException(taskId));

        UserStory userStory = userStoryRepository
                .findById(task.getUserStoryId())
                .orElseThrow(() -> new StoryNotFoundException(task.getUserStoryId()));

        Project project = projectRepository
                .findById(userStory.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException(userStory.getProjectId()));

        if (!workspaceMemberRepository.isMember(project.getWorkspaceId(), requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, project.getWorkspaceId());
        }

        if (title != null) {
            task.setTitle(title);
        }
        if (assigneeId != null) {
            task.setAssigneeId(assigneeId);
        }
        if (statusId != null) {
            task.setStatusId(statusId);
        }

        return taskRepository.save(task);
    }
}
