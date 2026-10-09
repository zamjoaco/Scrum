package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
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
class UpdateCurrentUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateCurrentUserService service;

    @Test
    void updatesNamesAndReturnsSavedUser() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "ana@example.com", "hash", "Ana", "Gomez", true, Instant.now());
        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = service.updateCurrentUser(id, "Ana Maria", "Gomez Perez");

        assertThat(result.getFirstNames()).isEqualTo("Ana Maria");
        assertThat(result.getLastNames()).isEqualTo("Gomez Perez");
        verify(userRepository).save(user);
    }

    @Test
    void throwsWhenTheUserDoesNotExistAndDoesNotSave() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateCurrentUser(id, "Ana", "Gomez"))
                .isInstanceOf(UserNotFoundException.class);
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
