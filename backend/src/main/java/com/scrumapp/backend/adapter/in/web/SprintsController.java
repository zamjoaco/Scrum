package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.CreateSprintRequest;
import com.scrumapp.backend.adapter.in.web.dto.PageResponse;
import com.scrumapp.backend.adapter.in.web.dto.SprintResponse;
import com.scrumapp.backend.adapter.in.web.dto.UpdateSprintRequest;
import com.scrumapp.backend.application.port.in.CloseSprintUseCase;
import com.scrumapp.backend.application.port.in.CreateSprintUseCase;
import com.scrumapp.backend.application.port.in.GetSprintUseCase;
import com.scrumapp.backend.application.port.in.ListSprintsUseCase;
import com.scrumapp.backend.application.port.in.StartSprintUseCase;
import com.scrumapp.backend.application.port.in.UpdateSprintUseCase;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.sprint.InvalidSprintTransitionException;
import com.scrumapp.backend.domain.sprint.Sprint;
import com.scrumapp.backend.domain.sprint.SprintNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para el vertical slice de Sprint. El userId
 * autenticado se lee del principal expuesto por el filtro de JWT
 * (Authentication.getName()). La autorizacion anti-IDOR vive en la capa de
 * aplicacion; aca solo se traduce el rechazo a un codigo HTTP.
 */
@RestController
public class SprintsController {

    private final CreateSprintUseCase createSprintUseCase;
    private final ListSprintsUseCase listSprintsUseCase;
    private final GetSprintUseCase getSprintUseCase;
    private final UpdateSprintUseCase updateSprintUseCase;
    private final StartSprintUseCase startSprintUseCase;
    private final CloseSprintUseCase closeSprintUseCase;

    public SprintsController(
            CreateSprintUseCase createSprintUseCase,
            ListSprintsUseCase listSprintsUseCase,
            GetSprintUseCase getSprintUseCase,
            UpdateSprintUseCase updateSprintUseCase,
            StartSprintUseCase startSprintUseCase,
            CloseSprintUseCase closeSprintUseCase) {
        this.createSprintUseCase = createSprintUseCase;
        this.listSprintsUseCase = listSprintsUseCase;
        this.getSprintUseCase = getSprintUseCase;
        this.updateSprintUseCase = updateSprintUseCase;
        this.startSprintUseCase = startSprintUseCase;
        this.closeSprintUseCase = closeSprintUseCase;
    }

    @PostMapping("/projects/{project_id}/sprints")
    public ResponseEntity<SprintResponse> createSprint(
            @PathVariable("project_id") UUID projectId,
            Authentication authentication,
            @Valid @RequestBody CreateSprintRequest request) {
        try {
            Sprint sprint = createSprintUseCase.createSprint(
                    projectId,
                    authenticatedUserId(authentication),
                    request.name(),
                    request.goal(),
                    request.startDate(),
                    request.endDate());
            return ResponseEntity.status(HttpStatus.CREATED).body(SprintResponse.from(sprint));
        } catch (ProjectNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/projects/{project_id}/sprints")
    public PageResponse<SprintResponse> listSprints(
            @PathVariable("project_id") UUID projectId,
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            List<Sprint> sprints =
                    listSprintsUseCase.listSprints(projectId, authenticatedUserId(authentication));

            int safePage = Math.max(page, 0);
            int safeSize = size > 0 ? size : 20;
            return PageResponse.of(
                    sprints.stream().map(SprintResponse::from).toList(), safePage, safeSize);
        } catch (ProjectNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/sprints/{sprint_id}")
    public SprintResponse getSprint(
            @PathVariable("sprint_id") UUID sprintId, Authentication authentication) {
        try {
            return SprintResponse.from(
                    getSprintUseCase.getSprint(sprintId, authenticatedUserId(authentication)));
        } catch (SprintNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PatchMapping("/sprints/{sprint_id}")
    public SprintResponse updateSprint(
            @PathVariable("sprint_id") UUID sprintId,
            Authentication authentication,
            @Valid @RequestBody UpdateSprintRequest request) {
        try {
            Sprint sprint = updateSprintUseCase.updateSprint(
                    sprintId,
                    authenticatedUserId(authentication),
                    request.name(),
                    request.goal(),
                    request.startDate(),
                    request.endDate());
            return SprintResponse.from(sprint);
        } catch (SprintNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PostMapping("/sprints/{sprint_id}/start")
    public SprintResponse startSprint(
            @PathVariable("sprint_id") UUID sprintId, Authentication authentication) {
        try {
            return SprintResponse.from(
                    startSprintUseCase.startSprint(sprintId, authenticatedUserId(authentication)));
        } catch (SprintNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        } catch (InvalidSprintTransitionException ex) {
            throw conflict(ex);
        }
    }

    @PostMapping("/sprints/{sprint_id}/close")
    public SprintResponse closeSprint(
            @PathVariable("sprint_id") UUID sprintId, Authentication authentication) {
        try {
            return SprintResponse.from(
                    closeSprintUseCase.closeSprint(sprintId, authenticatedUserId(authentication)));
        } catch (SprintNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        } catch (InvalidSprintTransitionException ex) {
            throw conflict(ex);
        }
    }

    private UUID authenticatedUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private ApiException notFound(RuntimeException ex) {
        return new ApiException(
                ErrorTypes.NOT_FOUND, "Recurso no encontrado", HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ApiException forbidden(NotWorkspaceMemberException ex) {
        return new ApiException(
                ErrorTypes.FORBIDDEN, "Acceso denegado", HttpStatus.FORBIDDEN, ex.getMessage());
    }

    private ApiException conflict(InvalidSprintTransitionException ex) {
        return new ApiException(
                ErrorTypes.CONFLICT,
                "Transicion de sprint invalida",
                HttpStatus.CONFLICT,
                ex.getMessage());
    }
}
