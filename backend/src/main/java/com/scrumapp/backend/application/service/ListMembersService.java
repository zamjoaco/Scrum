package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.ListMembersUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListMembersService implements ListMembersUseCase {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ListMembersService(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    public List<WorkspaceMember> listMembers(UUID workspaceId, UUID requesterId) {
        if (!workspaceMemberRepository.isMember(workspaceId, requesterId)) {
            throw new NotWorkspaceMemberException(requesterId, workspaceId);
        }
        return workspaceMemberRepository.listByWorkspaceId(workspaceId);
    }
}
