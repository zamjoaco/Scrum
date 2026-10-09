package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.application.port.out.WorkspaceRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.Workspace;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateWorkspaceServiceTest {

    @Mock
    private WorkspaceRepository workspaceRepository;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private CreateWorkspaceService service;

    @Test
    void createsTheWorkspaceWithASlugAndAnOwnerMembership() {
        UUID ownerId = UUID.randomUUID();
        when(workspaceRepository.save(any(Workspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(workspaceMemberRepository.save(any(WorkspaceMember.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Workspace result = service.createWorkspace(ownerId, "Equipo Scrum");

        assertThat(result.getName()).isEqualTo("Equipo Scrum");
        assertThat(result.getSlug()).isEqualTo("equipo-scrum");
        assertThat(result.getOwnerId()).isEqualTo(ownerId);

        ArgumentCaptor<WorkspaceMember> memberCaptor = ArgumentCaptor.forClass(WorkspaceMember.class);
        verify(workspaceMemberRepository).save(memberCaptor.capture());
        WorkspaceMember savedMember = memberCaptor.getValue();
        assertThat(savedMember.getRole()).isEqualTo(Role.OWNER);
        assertThat(savedMember.getUserId()).isEqualTo(ownerId);
        assertThat(savedMember.getWorkspaceId()).isEqualTo(result.getId());
    }
}
