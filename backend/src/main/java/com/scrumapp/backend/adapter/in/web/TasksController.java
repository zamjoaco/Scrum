package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.TaskResponse;
import com.scrumapp.backend.adapter.in.web.dto.UpdateTaskRequest;
import com.scrumapp.backend.application.port.in.DeleteTaskUseCase;
import com.scrumapp.backend.application.port.in.UpdateTaskUseCase;
import com.scrumapp.backend.domain.task.Task;
import com.scrumapp.backend.domain.task.TaskNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para el vertical slice de Task (subtarea de
 * una UserStory). Todos los endpoints exigen autenticacion y quedan
 * cubiertos por el default authenticated de SecurityConfig. El userId
 * autenticado se lee del principal expuesto por el filtro de JWT
 * (Authentication.getName()).
 *
 * <p>La autorizacion anti-IDOR (membership en el workspace dueno del
 * project al que pertenece, via Task -> UserStory -> project) vive en la
 * capa de aplicacion; aca solo se traduce el rechazo a un 403.
 */
@RestController
public class TasksController {

    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    public TasksController(UpdateTaskUseCase updateTaskUseCase, DeleteTaskUseCase deleteTaskUseCase) {
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @PatchMapping("/tasks/{task_id}")
    public TaskResponse updateTask(
            @PathVariable("task_id") UUID taskId,
            Authentication authentication,
            @Valid @RequestBody UpdateTaskRequest request) {
        try {
            Task task = updateTaskUseCase.updateTask(
                    taskId,
                    authenticatedUserId(authentication),
                    request.title(),
                    request.assigneeId(),
                    request.statusId());
            return TaskResponse.from(task);
        } catch (TaskNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @DeleteMapping("/tasks/{task_id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable("task_id") UUID taskId, Authentication authentication) {
        try {
            deleteTaskUseCase.deleteTask(taskId, authenticatedUserId(authentication));
            return ResponseEntity.noContent().build();
        } catch (TaskNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    private UUID authenticatedUserId(Authentication authentication) {
        return UUID.fromString(authentication.getName());
    }

    private ApiException notFound(TaskNotFoundException ex) {
        return new ApiException(
                ErrorTypes.NOT_FOUND, "Recurso no encontrado", HttpStatus.NOT_FOUND, ex.getMessage());
    }

    private ApiException forbidden(NotWorkspaceMemberException ex) {
        return new ApiException(
                ErrorTypes.FORBIDDEN, "Acceso denegado", HttpStatus.FORBIDDEN, ex.getMessage());
    }
}
