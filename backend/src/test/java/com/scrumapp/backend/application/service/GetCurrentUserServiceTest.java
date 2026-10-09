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
class GetCurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetCurrentUserService service;

    @Test
    void returnsTheUserWhenItExists() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "ana@example.com", "hash", "Ana", "Gomez", true, Instant.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        User result = service.getCurrentUser(id);

        assertThat(result).isSameAs(user);
        verify(userRepository).findById(id);
    }

    @Test
    void throwsWhenTheUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getCurrentUser(id)).isInstanceOf(UserNotFoundException.class);
    }
}
