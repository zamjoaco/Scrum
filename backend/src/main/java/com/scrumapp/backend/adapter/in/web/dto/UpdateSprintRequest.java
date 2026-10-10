package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Cuerpo para actualizar un Sprint.
 */
public record UpdateSprintRequest(
        @JsonProperty("name")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String name,
        @JsonProperty("goal")
                @Size(max = 1000, message = "no puede superar los 1000 caracteres")
                String goal,
        @JsonProperty("start_date") @NotNull(message = "es obligatorio") LocalDate startDate,
        @JsonProperty("end_date") @NotNull(message = "es obligatorio") LocalDate endDate) {
}
