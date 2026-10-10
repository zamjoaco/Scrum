package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Cuerpo para actualizar una BoardColumn.
 */
public record UpdateBoardColumnRequest(
        @JsonProperty("name")
                @NotBlank(message = "es obligatorio")
                @Size(max = 255, message = "no puede superar los 255 caracteres")
                String name,
        @JsonProperty("order_index") @NotNull(message = "es obligatorio") @Min(0) Integer orderIndex,
        @JsonProperty("wip_limit") @Min(1) Integer wipLimit) {
}
