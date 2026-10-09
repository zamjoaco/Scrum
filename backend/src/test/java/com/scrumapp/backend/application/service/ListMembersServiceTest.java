package com.scrumapp.backend.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.scrumapp.backend.application.port.out.WorkspaceMemberRepository;
import com.scrumapp.backend.domain.user.Role;
import com.scrumapp.backend.domain.workspace.NotWorkspaceMemberException;
import com.scrumapp.backend.domain.workspace.WorkspaceMember;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListMembersServiceTest {

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private ListMembersService service;

    @Test
    void returnsTheMembersWhenTheRequesterIsAMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        WorkspaceMember member = new WorkspaceMember(
                UUID.randomUUID(), workspaceId, requesterId, Role.MEMBER, Instant.now());
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(true);
        when(workspaceMemberRepository.listByWorkspaceId(workspaceId)).thenReturn(List.of(member));

        List<WorkspaceMember> result = service.listMembers(workspaceId, requesterId);

        assertThat(result).containsExactly(member);
    }

    @Test
    void throwsWhenTheRequesterIsNotAMember() {
        UUID workspaceId = UUID.randomUUID();
        UUID requesterId = UUID.randomUUID();
        when(workspaceMemberRepository.isMember(workspaceId, requesterId)).thenReturn(false);

        assertThatThrownBy(() -> service.listMembers(workspaceId, requesterId))
                .isInstanceOf(NotWorkspaceMemberException.class);
    }
}
