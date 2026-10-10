package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.BoardColumnRepository;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.UserStoryRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardColumnNotFoundException;
import com.scrumapp.backend.domain.board.WipLimitExceededException;
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
class MoveStoryServiceTest {

    @Mock
    private UserStoryRepository userStoryRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private BoardColumnRepository boardColumnRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private MoveStoryService service;

    @Test
    void movesStoryToTargetColumnWhenWipLimitNotReached() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        BoardColumn target = new BoardColumn(UUID.randomUUID(), UUID.randomUUID(), "In Progress", 1, 3);
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardColumnRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(userStoryRepository.countByStatusId(target.getId())).thenReturn(2L);
        when(userStoryRepository.save(story)).thenReturn(story);

        UserStory result = service.moveStory(story.getId(), requesterId, target.getId());

        assertThat(result.getStatusId()).isEqualTo(target.getId());
        verify(userStoryRepository).save(story);
    }

    @Test
    void throwsWipLimitExceededAndDoesNotMoveWhenTargetColumnIsFull() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        BoardColumn target = new BoardColumn(UUID.randomUUID(), UUID.randomUUID(), "In Progress", 1, 2);
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardColumnRepository.findById(target.getId())).thenReturn(Optional.of(target));
        when(userStoryRepository.countByStatusId(target.getId())).thenReturn(2L);

        assertThatThrownBy(() -> service.moveStory(story.getId(), requesterId, target.getId()))
                .isInstanceOf(WipLimitExceededException.class);
        verify(userStoryRepository, never()).save(any(UserStory.class));
        assertThat(story.getStatusId()).isNotEqualTo(target.getId());
    }

    @Test
    void throwsNotFoundWhenStoryDoesNotExist() {
        UUID storyId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(userStoryRepository.findById(storyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.moveStory(storyId, requesterId, UUID.randomUUID()))
                .isInstanceOf(StoryNotFoundException.class);
    }

    @Test
    void throwsBoardColumnNotFoundWhenTargetColumnDoesNotExist() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        UUID targetColumnId = UUID.randomUUID();
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(boardColumnRepository.findById(targetColumnId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.moveStory(story.getId(), requesterId, targetColumnId))
                .isInstanceOf(BoardColumnNotFoundException.class);
    }

    @Test
    void rejectsAndDoesNotMoveWhenRequesterIsNotMemberOfTheProjectWorkspace() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        Project project = project(workspaceId);
        UserStory story = story(project.getId());
        when(userStoryRepository.findById(story.getId())).thenReturn(Optional.of(story));
        when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.moveStory(story.getId(), requesterId, UUID.randomUUID()))
                .isInstanceOf(NotWorkspaceMemberException.class);
        verify(userStoryRepository, never()).save(any(UserStory.class));
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
