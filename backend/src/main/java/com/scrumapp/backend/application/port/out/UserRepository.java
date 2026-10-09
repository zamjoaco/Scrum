package com.scrumapp.backend.application.port.out;

import com.scrumapp.backend.domain.user.User;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida para persistencia de usuarios. Trabaja exclusivamente
 * con la entidad de dominio User, nunca con una entidad JPA.
 */
public interface UserRepository {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    User save(User user);

    boolean existsByEmail(String email);
}
