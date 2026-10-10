package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
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
class UpdateStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private UpdateStoryService service;

    @Test
    void mergePatchesOnlyNonNullFieldsWhenRequesterIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(userStoryRepository.save(story)).thenReturn(story);

        UserStory result = service.updateStory(
                story.getId(), requesterId, "Nuevo titulo", null, null, 8, assigneeId, null);

        assertThat(result.getTitle()).isEqualTo("Nuevo titulo");
        assertThat(result.getDescription()).isEqualTo(story.getDescription());
        assertThat(result.getPriority()).isEqualTo(UserStory.Priority.LOW);
        assertThat(result.getStoryPoints()).isEqualTo(8);
        assertThat(result.getAssigneeId()).isEqualTo(assigneeId);
        verify(userStoryRepository).save(story);
    }

    @Test
    void throwsNotFoundWhenStoryDoesNotExist() {
        UUID storyId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(userStoryRepository.findById(storyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStory(
                        storyId, requesterId, "title", null, null, null, null, null))
                .isInstanceOf(StoryNotFoundException.class);
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    @Test
    void rejectsAndDoesNotSaveWhenRequesterIsNotMemberOfTheProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.updateStory(
                        story.getId(), requesterId, "title", null, null, null, null, null))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(userStoryRepository, never()).save(any(UserStory.class));
    }

    private Project project(UUID workspaceId) {
        return new Project(UUID.randomUUID(), workspaceId, "Checkout", "CHK", null, null, Instant.now());
    }

    private UserStory story(UUID projectId) {
        return new UserStory(
                UUID.randomUUID(), projectId, null, UUID.randomUUID(), null, UUID.randomUUID(),
                "title", "desc", 3, UserStory.Priority.LOW, Instant.now());
    }
}
