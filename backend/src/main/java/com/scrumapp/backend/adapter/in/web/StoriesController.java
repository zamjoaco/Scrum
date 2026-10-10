package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.CreateStoryRequest;
import com.scrumapp.backend.adapter.in.web.dto.CreateTaskRequest;
import com.scrumapp.backend.adapter.in.web.dto.MoveStoryRequest;
import com.scrumapp.backend.adapter.in.web.dto.PageResponse;
import com.scrumapp.backend.adapter.in.web.dto.StoryResponse;
import com.scrumapp.backend.adapter.in.web.dto.TaskResponse;
import com.scrumapp.backend.adapter.in.web.dto.UpdateStoryRequest;
import com.scrumapp.backend.application.port.in.CreateStoryUseCase;
import com.scrumapp.backend.application.port.in.CreateTaskUseCase;
import com.scrumapp.backend.application.port.in.DeleteStoryUseCase;
import com.scrumapp.backend.application.port.in.GetStoryUseCase;
import com.scrumapp.backend.application.port.in.ListStoriesUseCase;
import com.scrumapp.backend.application.port.in.ListTasksUseCase;
import com.scrumapp.backend.application.port.in.MoveStoryUseCase;
import com.scrumapp.backend.application.port.in.UpdateStoryUseCase;
import com.scrumapp.backend.domain.board.BoardColumnNotFoundException;
import com.scrumapp.backend.domain.board.WipLimitExceededException;
import com.scrumapp.backend.domain.story.StoryNotFoundException;
import com.scrumapp.backend.domain.story.UserStory;
import com.scrumapp.backend.domain.task.Task;
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
 * Adaptador de entrada HTTP para el vertical slice de UserStory, incluyendo
 * la creacion/listado de sus Task anidadas. Todos los endpoints exigen
 * autenticacion y quedan cubiertos por el default authenticated de
 * SecurityConfig. El userId autenticado se lee del principal expuesto por
 * el filtro de JWT (Authentication.getName()).
 *
 * <p>La autorizacion anti-IDOR (membership en el workspace dueno del
 * project al que pertenece la historia) vive en la capa de aplicacion; aca
 * solo se traduce el rechazo a un 403.
 */
@RestController
public class StoriesController {

    private final CreateStoryUseCase createStoryUseCase;
    private final ListStoriesUseCase listStoriesUseCase;
    private final GetStoryUseCase getStoryUseCase;
    private final UpdateStoryUseCase updateStoryUseCase;
    private final DeleteStoryUseCase deleteStoryUseCase;
    private final MoveStoryUseCase moveStoryUseCase;
    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;

    public StoriesController(
            CreateStoryUseCase createStoryUseCase,
            ListStoriesUseCase listStoriesUseCase,
            GetStoryUseCase getStoryUseCase,
            UpdateStoryUseCase updateStoryUseCase,
            DeleteStoryUseCase deleteStoryUseCase,
            MoveStoryUseCase moveStoryUseCase,
            CreateTaskUseCase createTaskUseCase,
            ListTasksUseCase listTasksUseCase) {
        this.createStoryUseCase = createStoryUseCase;
        this.listStoriesUseCase = listStoriesUseCase;
        this.getStoryUseCase = getStoryUseCase;
        this.updateStoryUseCase = updateStoryUseCase;
        this.deleteStoryUseCase = deleteStoryUseCase;
        this.moveStoryUseCase = moveStoryUseCase;
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
    }

    @PostMapping("/projects/{project_id}/stories")
    public ResponseEntity<StoryResponse> createStory(
            @PathVariable("project_id") UUID projectId,
            Authentication authentication,
            @Valid @RequestBody CreateStoryRequest request) {
        try {
            UserStory userStory = createStoryUseCase.createStory(
                    projectId,
                    authenticatedUserId(authentication),
                    request.title(),
                    request.description(),
                    request.priority(),
                    request.storyPoints());
            return ResponseEntity.status(HttpStatus.CREATED).body(StoryResponse.from(userStory));
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/projects/{project_id}/stories")
    public PageResponse<StoryResponse> listStories(
            @PathVariable("project_id") UUID projectId,
            Authentication authentication,
            @RequestParam(name = "sprint_id", required = false) UUID sprintId,
            @RequestParam(name = "status_id", required = false) UUID statusId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            List<UserStory> stories = listStoriesUseCase.listStories(
                    projectId, authenticatedUserId(authentication), sprintId, statusId);

            int safePage = Math.max(page, 0);
            int safeSize = size > 0 ? size : 20;
            return PageResponse.of(
                    stories.stream().map(StoryResponse::from).toList(), safePage, safeSize);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/stories/{story_id}")
    public StoryResponse getStory(
            @PathVariable("story_id") UUID storyId, Authentication authentication) {
        try {
            return StoryResponse.from(
                    getStoryUseCase.getStory(storyId, authenticatedUserId(authentication)));
        } catch (StoryNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PatchMapping("/stories/{story_id}")
    public StoryResponse updateStory(
            @PathVariable("story_id") UUID storyId,
            Authentication authentication,
            @Valid @RequestBody UpdateStoryRequest request) {
        try {
            UserStory userStory = updateStoryUseCase.updateStory(
                    storyId,
                    authenticatedUserId(authentication),
                    request.title(),
                    request.description(),
                    request.priority(),
                    request.storyPoints(),
                    request.assigneeId(),
                    request.sprintId());
            return StoryResponse.from(userStory);
        } catch (StoryNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @DeleteMapping("/stories/{story_id}")
    public ResponseEntity<Void> deleteStory(
            @PathVariable("story_id") UUID storyId, Authentication authentication) {
        try {
            deleteStoryUseCase.deleteStory(storyId, authenticatedUserId(authentication));
            return ResponseEntity.noContent().build();
        } catch (StoryNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PostMapping("/stories/{story_id}/move")
    public StoryResponse moveStory(
            @PathVariable("story_id") UUID storyId,
            Authentication authentication,
            @Valid @RequestBody MoveStoryRequest request) {
        try {
            UserStory userStory = moveStoryUseCase.moveStory(
                    storyId, authenticatedUserId(authentication), request.targetColumnId());
            return StoryResponse.from(userStory);
        } catch (StoryNotFoundException | BoardColumnNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        } catch (WipLimitExceededException ex) {
            throw conflict(ex);
        }
    }

    @PostMapping("/stories/{story_id}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable("story_id") UUID storyId,
            Authentication authentication,
            @Valid @RequestBody CreateTaskRequest request) {
        try {
            Task task = createTaskUseCase.createTask(
                    storyId, authenticatedUserId(authentication), request.title(), request.assigneeId());
            return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
        } catch (StoryNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/stories/{story_id}/tasks")
    public PageResponse<TaskResponse> listTasks(
            @PathVariable("story_id") UUID storyId,
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            List<Task> tasks = listTasksUseCase.listTasks(storyId, authenticatedUserId(authentication));

            int safePage = Math.max(page, 0);
            int safeSize = size > 0 ? size : 20;
            return PageResponse.of(
                    tasks.stream().map(TaskResponse::from).toList(), safePage, safeSize);
        } catch (StoryNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
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

    private ApiException conflict(WipLimitExceededException ex) {
        return new ApiException(
                ErrorTypes.CONFLICT, "Conflicto con el estado actual del recurso",
                HttpStatus.CONFLICT, ex.getMessage());
    }
}
