package com.scrumapp.backend.application.service;

import com.scrumapp.backend.application.port.in.ListProjectsUseCase;
import com.scrumapp.backend.application.port.out.ProjectRepository;
import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.project.Project;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ListProjectsService implements ListProjectsUseCase {

    private final ProjectRepository projectRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public ListProjectsService(
            ProjectRepository projectRepository, WorkspaceMemberRepository workspaceMemberRepository) {
        this.projectRepository = projectRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    /**
     * Devuelve los proyectos NO archivados del workspace. Se excluyen los
     * archivados por default (archivedAt != null) para que el listado muestre
     * solo lo vigente; el contrato OpenAPI no define un parametro para
     * incluirlos, por lo que no se expone esa opcion todavia.
     */
    @Override
    public List<Project> listProjects(UUID workspaceId, UUID requesterUserId) {
        if (!workspaceMemberRepository.isMember(workspaceId, requesterUserId)) {
            throw new NotWorkspaceMemberException(requesterUserId, workspaceId);
        }

        return projectRepository.listByWorkspaceId(workspaceId).stream()
                .filter(project -> project.getArchivedAt() == null)
                .toList();
    }
}
