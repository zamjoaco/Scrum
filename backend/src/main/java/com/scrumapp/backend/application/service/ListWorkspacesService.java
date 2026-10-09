package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.ListWorkspacesUseCase;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.workspace.Workspace;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListWorkspacesService implements ListWorkspacesUseCase {

    private final WorkspaceRepository workspaceRepository;

    public ListWorkspacesService(WorkspaceRepository workspaceRepository) {
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    public List<Workspace> listWorkspaces(UUID userId) {
        return workspaceRepository.listForUser(userId);
    }
}
