package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.TaskRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.task.Task;
import com.scrumapp.backend.domain.task.TaskNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DeleteTaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private DeleteTaskService service;

    @Test
    void deletesTaskWhenRequesterIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        Task task = task(story.getId());
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);

        service.deleteTask(task.getId(), requesterId);

        verify(taskRepository).delete(task);
    }

    @Test
    void throwsNotFoundWhenTaskDoesNotExist() {
        UUID taskId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deleteTask(taskId, requesterId))
                .isInstanceOf(TaskNotFoundException.class);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void rejectsAndDoesNotDeleteWhenRequesterIsNotMemberOfTheProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        Task task = task(story.getId());
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.deleteTask(task.getId(), requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }

    private UserStory story(UUID projectId) {
        return new UserStory(
                UUID.randomUUID(), projectId, null, UUID.randomUUID(), null, UUID.randomUUID(),
                "title", null, null, UserStory.Priority.LOW, Instant.now());
    }

    private Task task(UUID userStoryId) {
        return new Task(UUID.randomUUID(), userStoryId, UUID.randomUUID(), null, "Subtarea", Instant.now());
    }
}
