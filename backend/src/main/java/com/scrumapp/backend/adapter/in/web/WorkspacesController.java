package com.scrumapp.backend.adapter.in.web;

import com.scrumapp.backend.adapter.in.web.dto.CreateInvitationRequest;
import com.scrumapp.backend.adapter.in.web.dto.CreateWorkspaceRequest;
import com.scrumapp.backend.adapter.in.web.dto.InvitationResponse;
import com.scrumapp.backend.adapter.in.web.dto.PageResponse;
import com.scrumapp.backend.adapter.in.web.dto.UpdateMemberRoleRequest;
import com.scrumapp.backend.adapter.in.web.dto.UpdateWorkspaceRequest;
import com.scrumapp.backend.adapter.in.web.dto.WorkspaceMemberResponse;
import com.scrumapp.backend.adapter.in.web.dto.WorkspaceResponse;
import com.scrumapp.backend.application.port.in.AcceptInvitationUseCase;
import com.scrumapp.backend.application.port.in.CreateInvitationUseCase;
import com.scrumapp.backend.application.port.in.CreateWorkspaceUseCase;
import com.scrumapp.backend.application.port.in.DeleteWorkspaceUseCase;
import com.scrumapp.backend.application.port.in.GetWorkspaceUseCase;
import com.scrumapp.backend.application.port.in.ListMembersUseCase;
import com.scrumapp.backend.application.port.in.ListWorkspacesUseCase;
import com.scrumapp.backend.application.port.in.RemoveMemberUseCase;
import com.scrumapp.backend.application.port.in.UpdateMemberRoleUseCase;
import com.scrumapp.backend.application.port.in.UpdateWorkspaceUseCase;
import com.scrumapp.backend.domain.workspace.Invitation;
import com.scrumapp.backend.domain.workspace.InsufficientWorkspaceRoleException;
import com.scrumapp.backend.domain.workspace.InvitationExpiredException;
import com.scrumapp.backend.domain.workspace.InvitationNotFoundException;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import com.scrumapp.backend.domain.workspace.WorkspaceNotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Adaptador de entrada HTTP para el vertical slice de Workspace. El userId
 * autenticado se lee del principal expuesto por el filtro de JWT
 * (Authentication.getName()), mismo patron que UsersController.
 */
@RestController
public class WorkspacesController {

    private final CreateWorkspaceUseCase createWorkspaceUseCase;
    private final ListWorkspacesUseCase listWorkspacesUseCase;
    private final GetWorkspaceUseCase getWorkspaceUseCase;
    private final UpdateWorkspaceUseCase updateWorkspaceUseCase;
    private final DeleteWorkspaceUseCase deleteWorkspaceUseCase;
    private final ListMembersUseCase listMembersUseCase;
    private final CreateInvitationUseCase createInvitationUseCase;
    private final AcceptInvitationUseCase acceptInvitationUseCase;
    private final UpdateMemberRoleUseCase updateMemberRoleUseCase;
    private final RemoveMemberUseCase removeMemberUseCase;

    public WorkspacesController(
            CreateWorkspaceUseCase createWorkspaceUseCase,
            ListWorkspacesUseCase listWorkspacesUseCase,
            GetWorkspaceUseCase getWorkspaceUseCase,
            UpdateWorkspaceUseCase updateWorkspaceUseCase,
            DeleteWorkspaceUseCase deleteWorkspaceUseCase,
            ListMembersUseCase listMembersUseCase,
            CreateInvitationUseCase createInvitationUseCase,
            AcceptInvitationUseCase acceptInvitationUseCase,
            UpdateMemberRoleUseCase updateMemberRoleUseCase,
            RemoveMemberUseCase removeMemberUseCase) {
        this.createWorkspaceUseCase = createWorkspaceUseCase;
        this.listWorkspacesUseCase = listWorkspacesUseCase;
        this.getWorkspaceUseCase = getWorkspaceUseCase;
        this.updateWorkspaceUseCase = updateWorkspaceUseCase;
        this.deleteWorkspaceUseCase = deleteWorkspaceUseCase;
        this.listMembersUseCase = listMembersUseCase;
        this.createInvitationUseCase = createInvitationUseCase;
        this.acceptInvitationUseCase = acceptInvitationUseCase;
        this.updateMemberRoleUseCase = updateMemberRoleUseCase;
        this.removeMemberUseCase = removeMemberUseCase;
    }

    @PostMapping("/workspaces")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkspaceResponse createWorkspace(
            Authentication authentication, @Valid @RequestBody CreateWorkspaceRequest request) {
        Workspace workspace =
                createWorkspaceUseCase.createWorkspace(authenticatedUserId(authentication), request.name());
        return WorkspaceResponse.from(workspace);
    }

    @GetMapping("/workspaces")
    public PageResponse<WorkspaceResponse> listWorkspaces(
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        List<Workspace> workspaces =
                listWorkspacesUseCase.listWorkspaces(authenticatedUserId(authentication));
        return PageResponse.of(workspaces.stream().map(WorkspaceResponse::from).toList(), page, size);
    }

    @GetMapping("/workspaces/{workspace_id}")
    public WorkspaceResponse getWorkspace(
            @PathVariable("workspace_id") UUID workspaceId, Authentication authentication) {
        try {
            Workspace workspace =
                    getWorkspaceUseCase.getWorkspace(workspaceId, authenticatedUserId(authentication));
            return WorkspaceResponse.from(workspace);
        } catch (NotWorkspaceMemberException | WorkspaceNotFoundException ex) {
            throw notFound(ex);
        }
    }

    @PatchMapping("/workspaces/{workspace_id}")
    public WorkspaceResponse updateWorkspace(
            @PathVariable("workspace_id") UUID workspaceId,
            Authentication authentication,
            @Valid @RequestBody UpdateWorkspaceRequest request) {
        try {
            Workspace workspace = updateWorkspaceUseCase.updateWorkspace(
                    workspaceId, authenticatedUserId(authentication), request.name());
            return WorkspaceResponse.from(workspace);
        } catch (NotWorkspaceMemberException | WorkspaceNotFoundException ex) {
            throw notFound(ex);
        } catch (InsufficientWorkspaceRoleException ex) {
            throw forbidden(ex);
        }
    }

    @DeleteMapping("/workspaces/{workspace_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWorkspace(
            @PathVariable("workspace_id") UUID workspaceId, Authentication authentication) {
        try {
            deleteWorkspaceUseCase.deleteWorkspace(workspaceId, authenticatedUserId(authentication));
        } catch (NotWorkspaceMemberException | WorkspaceNotFoundException ex) {
            throw notFound(ex);
        } catch (InsufficientWorkspaceRoleException ex) {
            throw forbidden(ex);
        }
    }

    @GetMapping("/workspaces/{workspace_id}/members")
    public PageResponse<WorkspaceMemberResponse> listMembers(
            @PathVariable("workspace_id") UUID workspaceId,
            Authentication authentication,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size) {
        try {
            List<WorkspaceMember> members =
                    listMembersUseCase.listMembers(workspaceId, authenticatedUserId(authentication));
            return PageResponse.of(
                    members.stream().map(WorkspaceMemberResponse::from).toList(), page, size);
        } catch (NotWorkspaceMemberException ex) {
            throw notFound(ex);
        }
    }

    @PostMapping("/workspaces/{workspace_id}/invitations")
    @ResponseStatus(HttpStatus.CREATED)
    public InvitationResponse createInvitation(
            @PathVariable("workspace_id") UUID workspaceId,
            Authentication authentication,
            @Valid @RequestBody CreateInvitationRequest request) {
        try {
            Invitation invitation = createInvitationUseCase.createInvitation(
                    workspaceId, authenticatedUserId(authentication), request.email(), request.role());
            return InvitationResponse.from(invitation);
        } catch (NotWorkspaceMemberException ex) {
            throw notFound(ex);
        } catch (InsufficientWorkspaceRoleException ex) {
            throw forbidden(ex);
        }
    }

    @PostMapping("/invitations/{token}/accept")
    public WorkspaceMemberResponse acceptInvitation(
            @PathVariable("token") String token, Authentication authentication) {
        try {
            WorkspaceMember member =
                    acceptInvitationUseCase.acceptInvitation(token, authenticatedUserId(authentication));
            return WorkspaceMemberResponse.from(member);
        } catch (InvitationNotFoundException ex) {
            throw notFound(ex);
        } catch (InvitationExpiredException ex) {
            throw new ApiException(
                    ErrorTypes.CONFLICT, "Invitacion expirada", HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @PatchMapping("/workspaces/{workspace_id}/members/{user_id}")
    public WorkspaceMemberResponse updateMemberRole(
            @PathVariable("workspace_id") UUID workspaceId,
            @PathVariable("user_id") UUID targetUserId,
            Authentication authentication,
            @Valid @RequestBody UpdateMemberRoleRequest request) {
        try {
            WorkspaceMember member = updateMemberRoleUseCase.updateMemberRole(
                    workspaceId, authenticatedUserId(authentication), targetUserId, request.role());
            return WorkspaceMemberResponse.from(member);
        } catch (NotWorkspaceMemberException ex) {
            throw notFound(ex);
        } catch (InsufficientWorkspaceRoleException ex) {
            throw forbidden(ex);
        }
    }

    @DeleteMapping("/workspaces/{workspace_id}/members/{user_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable("workspace_id") UUID workspaceId,
            @PathVariable("user_id") UUID targetUserId,
            Authentication authentication) {
        try {
            removeMemberUseCase.removeMember(
                    workspaceId, authenticatedUserId(authentication), targetUserId);
        } catch (NotWorkspaceMemberException ex) {
            throw notFound(ex);
        } catch (InsufficientWorkspaceRoleException ex) {
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

    private ApiException forbidden(RuntimeException ex) {
        return new ApiException(
                ErrorTypes.FORBIDDEN, "No tiene permisos para esta operacion", HttpStatus.FORBIDDEN,
                ex.getMessage());
    }
}
