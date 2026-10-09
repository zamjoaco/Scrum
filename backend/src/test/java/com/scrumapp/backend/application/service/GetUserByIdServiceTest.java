package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
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
class GetUserByIdServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private GetUserByIdService service;

    @Test
    void returnsTheTargetUserWhenItExistsAndSharesAWorkspaceWithTheRequester() {
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        UUID workspaceId = UUID.randomUUID();
        User target = new User(targetId, "leo@example.com", "hash", "Leo", "Diaz", true, Instant.now());
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(workspaceMemberRepository.listWorkspaceIdsForUser(requesterId))
                .thenReturn(List.of(workspaceId));
        when(workspaceMemberRepository.isMember(workspaceId, targetId)).thenReturn(true);

        User result = service.getUserById(requesterId, targetId);

        assertThat(result).isSameAs(target);
        verify(userRepository).findById(targetId);
    }

    @Test
    void throwsWhenTheTargetUserDoesNotExist() {
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        when(userRepository.findById(targetId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getUserById(requesterId, targetId))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void throwsWhenTheRequesterDoesNotShareAnyWorkspaceWithTheTarget() {
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        User target = new User(targetId, "leo@example.com", "hash", "Leo", "Diaz", true, Instant.now());
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));
        when(workspaceMemberRepository.listWorkspaceIdsForUser(requesterId)).thenReturn(List.of());

        assertThatThrownBy(() -> service.getUserById(requesterId, targetId))
                .isInstanceOf(UserNotFoundException.class);
    }
}
