package com.scrumapp.backend.domain.sprint;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad de dominio pura. No lleva anotaciones de JPA ni de Spring:
 * la persistencia se resuelve en adapter.out.persistence.
 */
public class Sprint {

    private final UUID id;
    private final UUID projectId;
    private String name;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    public Sprint(
            UUID id,
            UUID projectId,
            String name,
            String goal,
            LocalDate startDate,
            LocalDate endDate,
            Status status) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.goal = goal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        PLANNED,
        ACTIVE,
        CLOSED
    }
}
