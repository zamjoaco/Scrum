package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.CreateProjectRequest;
import com.scrumapp.backend.adapter.in.web.dto.PageResponse;
import com.scrumapp.backend.adapter.in.web.dto.ProjectResponse;
import com.scrumapp.backend.adapter.in.web.dto.UpdateProjectRequest;
import com.scrumapp.backend.application.port.in.ArchiveProjectUseCase;
import com.scrumapp.backend.application.port.in.CreateProjectUseCase;
import com.scrumapp.backend.application.port.in.GetProjectUseCase;
import com.scrumapp.backend.application.port.in.ListProjectsUseCase;
import com.scrumapp.backend.application.port.in.UpdateProjectUseCase;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para el vertical slice de Projects. Todos los
 * endpoints exigen autenticacion y quedan cubiertos por el default
 * authenticated de SecurityConfig. El userId autenticado se lee del principal
 * expuesto por el filtro de JWT (Authentication.getName()).
 *
 * <p>La autorizacion anti-IDOR (membership en el workspace due del project)
 * vive en la capa de aplicacion; aca solo se traduce el rechazo a un 403.
 */
@RestController
public class ProjectsController {

    private final CreateProjectUseCase createProjectUseCase;
    private final ListProjectsUseCase listProjectsUseCase;
    private final GetProjectUseCase getProjectUseCase;
    private final UpdateProjectUseCase updateProjectUseCase;
    private final ArchiveProjectUseCase archiveProjectUseCase;

    public ProjectsController(
            CreateProjectUseCase createProjectUseCase,
            ListProjectsUseCase listProjectsUseCase,
            GetProjectUseCase getProjectUseCase,
            UpdateProjectUseCase updateProjectUseCase,
            ArchiveProjectUseCase archiveProjectUseCase) {
        this.createProjectUseCase = createProjectUseCase;
        this.listProjectsUseCase = listProjectsUseCase;
        this.getProjectUseCase = getProjectUseCase;
        this.updateProjectUseCase = updateProjectUseCase;
        this.archiveProjectUseCase = archiveProjectUseCase;
    }

    @PostMapping("/workspaces/{workspace_id}/projects")
    public ResponseEntity<ProjectResponse> createProject(
            @PathVariable("workspace_id") UUID workspaceId,
            Authentication authentication,
            @Valid @RequestBody CreateProjectRequest request) {
        try {
            Project project = createProjectUseCase.createProject(
                    workspaceId,
                    authenticatedUserId(authentication),
                    request.name(),
                    request.key(),
                    request.description());
            return ResponseEntity.status(HttpStatus.CREATED).body(ProjectResponse.from(project));
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/workspaces/{workspace_id}/projects")
    public PageResponse<ProjectResponse> listProjects(
            @PathVariable("workspace_id") UUID workspaceId,
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            List<Project> projects = listProjectsUseCase.listProjects(
                    workspaceId, authenticatedUserId(authentication));

            int safePage = Math.max(page, 0);
            int safeSize = size > 0 ? size : 20;
            return PageResponse.of(
                    projects.stream().map(ProjectResponse::from).toList(), safePage, safeSize);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/projects/{project_id}")
    public ProjectResponse getProject(
            @PathVariable("project_id") UUID projectId, Authentication authentication) {
        try {
            return ProjectResponse.from(
                    getProjectUseCase.getProject(projectId, authenticatedUserId(authentication)));
        } catch (ProjectNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PatchMapping("/projects/{project_id}")
    public ProjectResponse updateProject(
            @PathVariable("project_id") UUID projectId,
            Authentication authentication,
            @Valid @RequestBody UpdateProjectRequest request) {
        try {
            Project project = updateProjectUseCase.updateProject(
                    projectId,
                    authenticatedUserId(authentication),
                    request.name(),
                    request.description());
            return ProjectResponse.from(project);
        } catch (ProjectNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @DeleteMapping("/projects/{project_id}")
    public ResponseEntity<Void> archiveProject(
            @PathVariable("project_id") UUID projectId, Authentication authentication) {
        try {
            archiveProjectUseCase.archiveProject(projectId, authenticatedUserId(authentication));
            return ResponseEntity.noContent().build();
        } catch (ProjectNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    private UUID authenticatedUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private ApiException notFound(ProjectNotFoundException ex) {
        return new ApiException(
                ErrorTypes.NOT_FOUND, "Recurso no encontrado", HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ApiException forbidden(NotWorkspaceMemberException ex) {
        return new ApiException(
                ErrorTypes.FORBIDDEN, "Acceso denegado", HttpStatus.FORBIDDEN, ex.getMessage());
    }
}
