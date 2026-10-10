package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.TaskRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.task.Task;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListTasksServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private ListTasksService service;

    @Test
    void returnsTasksWhenRequesterIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        Task task = task(story.getId());
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(taskRepository.listByUserStoryId(story.getId())).thenReturn(List.of(task));

        List<Task> result = service.listTasks(story.getId(), requesterId);

        assertThat(result).containsExactly(task);
    }

    @Test
    void throwsNotFoundWhenParentStoryDoesNotExist() {
        UUID storyId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(userStoryRepository.findById(storyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.listTasks(storyId, requesterId))
                .isInstanceOf(StoryNotFoundException.class);
    }

    @Test
    void rejectsWhenRequesterIsNotMemberOfTheProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.listTasks(story.getId(), requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
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
