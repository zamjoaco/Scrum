package com.scrumapp.backend.adapter.in.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Envelope paginado de Project, segun
 * docs/api/components/schemas/project.yaml#/ProjectPage.
 */
public record ProjectPage(
        @JsonProperty("items") List<ProjectResponse> items,
        @JsonProperty("page") PageMetadata page) {
}
