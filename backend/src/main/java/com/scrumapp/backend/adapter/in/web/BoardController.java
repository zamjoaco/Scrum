package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.BoardColumnResponse;
import com.scrumapp.backend.adapter.in.web.dto.BoardResponse;
import com.scrumapp.backend.adapter.in.web.dto.CreateBoardColumnRequest;
import com.scrumapp.backend.adapter.in.web.dto.UpdateBoardColumnRequest;
import com.scrumapp.backend.application.port.in.CreateBoardColumnUseCase;
import com.scrumapp.backend.application.port.in.GetBoardUseCase;
import com.scrumapp.backend.application.port.in.UpdateBoardColumnUseCase;
import com.scrumapp.backend.domain.board.BoardColumn;
import com.scrumapp.backend.domain.board.BoardColumnNotFoundException;
import com.scrumapp.backend.domain.board.BoardNotFoundException;
import com.scrumapp.backend.domain.project.ProjectNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para el vertical slice de Board/BoardColumn. El
 * userId autenticado se lee del principal expuesto por el filtro de JWT
 * (Authentication.getName()). La autorizacion anti-IDOR vive en la capa de
 * aplicacion; aca solo se traduce el rechazo a un codigo HTTP.
 */
@RestController
public class BoardController {

    private final GetBoardUseCase getBoardUseCase;
    private final CreateBoardColumnUseCase createBoardColumnUseCase;
    private final UpdateBoardColumnUseCase updateBoardColumnUseCase;

    public BoardController(
            GetBoardUseCase getBoardUseCase,
            CreateBoardColumnUseCase createBoardColumnUseCase,
            UpdateBoardColumnUseCase updateBoardColumnUseCase) {
        this.getBoardUseCase = getBoardUseCase;
        this.createBoardColumnUseCase = createBoardColumnUseCase;
        this.updateBoardColumnUseCase = updateBoardColumnUseCase;
    }

    @GetMapping("/projects/{project_id}/board")
    public BoardResponse getBoard(
            @PathVariable("project_id") UUID projectId, Authentication authentication) {
        try {
            return BoardResponse.from(
                    getBoardUseCase.getBoard(projectId, authenticatedUserId(authentication)));
        } catch (ProjectNotFoundException | BoardNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PostMapping("/projects/{project_id}/board/columns")
    public ResponseEntity<BoardColumnResponse> createBoardColumn(
            @PathVariable("project_id") UUID projectId,
            Authentication authentication,
            @Valid @RequestBody CreateBoardColumnRequest request) {
        try {
            BoardColumn column = createBoardColumnUseCase.createBoardColumn(
                    projectId,
                    authenticatedUserId(authentication),
                    request.name(),
                    request.orderIndex(),
                    request.wipLimit());
            return ResponseEntity.status(HttpStatus.CREATED).body(BoardColumnResponse.from(column));
        } catch (ProjectNotFoundException | BoardNotFoundException ex) {
            throw notFound(ex);
        } catch (NotWorkspaceMemberException ex) {
            throw forbidden(ex);
        }
    }

    @PatchMapping("/board/columns/{column_id}")
    public BoardColumnResponse updateBoardColumn(
            @PathVariable("column_id") UUID columnId,
            Authentication authentication,
            @Valid @RequestBody UpdateBoardColumnRequest request) {
        try {
            BoardColumn column = updateBoardColumnUseCase.updateBoardColumn(
                    columnId,
                    authenticatedUserId(authentication),
                    request.name(),
                    request.orderIndex(),
                    request.wipLimit());
            return BoardColumnResponse.from(column);
        } catch (BoardColumnNotFoundException | BoardNotFoundException | ProjectNotFoundException ex) {
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
}
