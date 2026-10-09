package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.GetUserByIdUseCase;
import com.scrumapp.backend.application.port.out.UserRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.User;
import com.scrumapp.backend.domain.user.UserNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepository userRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public GetUserByIdService(
            UserRepository userRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.userRepository = userRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public User getUserById(UUID requesterId, UUID targetUserId) {
        User target =
                userRepository
                        .findById(targetUserId)
                        .orElseThrow(() -> new UserNotFoundException(targetUserId));

        // Se usa UserNotFoundException en vez de NotWorkspaceMemberException para no
        // filtrar al requester si el usuario existe o no: mismo patron que el resto
        // del proyecto, donde "no encontrado" y "no autorizado a verlo" son
        // indistinguibles para quien hace la consulta.
        if (!shareAnyWorkspace(requesterId, targetUserId)) {
            throw new UserNotFoundException(targetUserId);
        }

        return target;
    }

    private boolean shareAnyWorkspace(UUID requesterId, UUID targetUserId) {
        List<UUID> requesterWorkspaceIds =
                workspaceMemberRepository.listWorkspaceIdsForUser(requesterId);
        return requesterWorkspaceIds.stream()
                .anyMatch(
                        workspaceId -> workspaceMemberRepository.isMember(workspaceId, targetUserId));
    }
}
