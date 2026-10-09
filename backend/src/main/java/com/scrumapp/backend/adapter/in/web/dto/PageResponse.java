package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Envelope paginado generico (items + metadata de pagina), reusado por
 * cualquier recurso que se liste paginado. Mismo shape que los schemas
 * "*Page" de docs/api/components/schemas/workspace.yaml.
 */
public record PageResponse<T>(
        @JsonProperty("items") List<T> items, @JsonProperty("page") PageMetadataResponse page) {

    public static <T> PageResponse<T> of(List<T> allItems, int pageNumber, int size) {
        int totalElements = allItems.size();
        int totalPages = size <= 0 ? 0 : (int) Math.ceil((double) totalElements / size);
        int fromIndex = Math.min(Math.max(pageNumber, 0) * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<T> slice = allItems.subList(fromIndex, toIndex);
        return new PageResponse<>(
                slice, new PageMetadataResponse(pageNumber, size, totalElements, totalPages));
    }
}
