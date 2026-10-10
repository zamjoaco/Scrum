package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
class GetStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private GetStoryService service;

    @Test
    void returnsStoryWhenRequesterIsMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);

        UserStory result = service.getStory(story.getId(), requesterId);

        assertThat(result).isEqualTo(story);
    }

    @Test
    void throwsNotFoundWhenStoryDoesNotExist() {
        UUID storyId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(userStoryRepository.findById(storyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getStory(storyId, requesterId))
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

        assertThatThrownBy(() -> service.getStory(story.getId(), requesterId))
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
}
