package com.scrumapp.backend.adapter.out.persistence;

import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.domain.user.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Adaptador de persistencia que implementa el puerto UserRepository. Traduce
 * explicitamente entre la entidad de dominio User y la entidad JPA
 * UserJpaEntity; esta ultima no sale nunca de este subpaquete.
 */
@Repository
public class UserPersistenceAdapter implements UserRepository {

    private final UserJpaRepository jpaRepository;

    public UserPersistenceAdapter(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(UserPersistenceAdapter::toDomain);
    }

    @Override
    public User save(User user) {
        UserJpaEntity saved = jpaRepository.save(toJpaEntity(user));
        return toDomain(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private static UserJpaEntity toJpaEntity(User user) {
        return new UserJpaEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFirstNames(),
                user.getLastNames(),
                user.isActive(),
                toLocalDateTime(user.getCreatedAt()));
    }

    private static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstNames(),
                entity.getLastNames(),
                entity.isActive(),
                toInstant(entity.getCreatedAt()));
    }

    private static LocalDateTime toLocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.toInstant(ZoneOffset.UTC);
    }
}
