package com.scrumapp.backend.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.scrumapp.backend.domain.user.User;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * Test de integracion del adaptador de persistencia (UserJpaEntity + repositorio
 * Spring Data). Corre sobre H2 en memoria para no depender de Docker en local.
 * En CI real deberia correr contra PostgreSQL via Testcontainers (las
 * dependencias testcontainers/postgresql ya estan declaradas en el pom).
 */
@DataJpaTest(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        })
@Import(UserPersistenceAdapter.class)
class UserPersistenceAdapterIntegrationTest {

    @Autowired
    private UserPersistenceAdapter adapter;

    @Autowired
    private UserJpaRepository jpaRepository;

    @Test
    void savesAndReadsUserByIdAndEmail() {
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.parse("2026-01-15T09:30:00Z");
        User user = new User(id, "ana@example.com", "hash", "Ana Maria", "Gomez", true, createdAt);

        User saved = adapter.save(user);

        assertThat(saved.getId()).isEqualTo(id);
        assertThat(saved.getEmail()).isEqualTo("ana@example.com");

        Optional<User> byId = adapter.findById(id);
        assertThat(byId).isPresent();
        assertThat(byId.get().getFirstNames()).isEqualTo("Ana Maria");
        assertThat(byId.get().getLastNames()).isEqualTo("Gomez");
        assertThat(byId.get().isActive()).isTrue();
        assertThat(byId.get().getCreatedAt()).isEqualTo(createdAt);

        assertThat(adapter.findByEmail("ana@example.com")).isPresent();
        assertThat(adapter.existsByEmail("ana@example.com")).isTrue();
        assertThat(adapter.existsByEmail("nadie@example.com")).isFalse();
    }

    @Test
    void persistsGeneratedIdAndTimestampWhenMissing() {
        User user = new User(null, "leo@example.com", "hash", "Leo", "Diaz", false, null);

        User saved = adapter.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(jpaRepository.findById(saved.getId())).isPresent();
    }
}
