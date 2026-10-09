package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Metadata de paginacion, segun
 * docs/api/components/schemas/common.yaml#/PageMetadata.
 */
public record PageMetadata(
        @JsonProperty("number") int number,
        @JsonProperty("size") int size,
        @JsonProperty("total_elements") long totalElements,
        @JsonProperty("total_pages") int totalPages) {
}
