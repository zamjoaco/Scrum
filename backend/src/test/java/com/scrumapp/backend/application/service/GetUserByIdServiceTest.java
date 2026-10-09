package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
import java.time.Instant;
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

    @InjectMocks
    private GetUserByIdService service;

    @Test
    void returnsTheTargetUserWhenItExists() {
        UUID requesterId = UUID.randomUUID();
        UUID targetId = UUID.randomUUID();
        User target = new User(targetId, "leo@example.com", "hash", "Leo", "Diaz", true, Instant.now());
        when(userRepository.findById(targetId)).thenReturn(Optional.of(target));

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
}
