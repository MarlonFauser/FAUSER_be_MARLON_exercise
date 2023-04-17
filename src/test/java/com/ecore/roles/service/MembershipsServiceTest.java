package com.ecore.roles.service;

import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.impl.MembershipsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.ecore.roles.utils.TestData.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MembershipsServiceTest {

    @InjectMocks
    private MembershipsServiceImpl membershipsService;
    @Mock
    private MembershipRepository membershipRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UsersService usersService;
    @Mock
    private TeamsService teamsService;

    @Test
    public void shouldCreateMembership() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(roleRepository.findById(expectedMembership.getRole().getId()))
                .thenReturn(Optional.ofNullable(DEVELOPER_ROLE()));
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(Optional.empty());
        when(membershipRepository
                .save(expectedMembership))
                        .thenReturn(expectedMembership);
        when(teamsService
                .getTeam(expectedMembership.getTeamId()))
                        .thenReturn(ORDINARY_CORAL_LYNX_TEAM());

        Membership actualMembership = membershipsService.createMembership(expectedMembership);

        assertNotNull(actualMembership);
        assertEquals(actualMembership, expectedMembership);
        verify(roleRepository).findById(expectedMembership.getRole().getId());
    }

    @Test
    public void shouldFailToCreateMembershipWhenMembershipsIsNull() {
        assertThrows(NullPointerException.class,
                () -> membershipsService.createMembership(null));
    }

    @Test
    public void shouldFailToCreateMembershipWhenItExists() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(Optional.of(expectedMembership));

        ResourceExistsException exception = assertThrows(ResourceExistsException.class,
                () -> membershipsService.createMembership(expectedMembership));

        assertEquals("Membership already exists", exception.getMessage());
        verify(membershipRepository, times(1)).findByUserIdAndTeamId(any(), any());
        verify(roleRepository, times(0)).getById(any());
        verify(usersService, times(0)).getUser(any());
        verify(teamsService, times(0)).getTeam(any());
    }

    @Test
    public void shouldFailToCreateMembershipWhenItHasInvalidRole() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        expectedMembership.setRole(null);

        InvalidArgumentException exception = assertThrows(InvalidArgumentException.class,
                () -> membershipsService.createMembership(expectedMembership));

        assertEquals("Invalid 'Role' object", exception.getMessage());
        verify(membershipRepository, times(1)).findByUserIdAndTeamId(any(), any());
        verify(roleRepository, times(0)).getById(any());
        verify(usersService, times(0)).getUser(any());
        verify(teamsService, times(0)).getTeam(any());
    }

    @Test
    public void shouldReturnMembership() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findByUserIdAndTeamId(expectedMembership.getUserId(),
                expectedMembership.getTeamId()))
                        .thenReturn(Optional.of(expectedMembership));

        Optional<Membership> actualMemberships = membershipsService
                .getMembership(expectedMembership.getUserId(), expectedMembership.getTeamId());

        assertTrue(actualMemberships.isPresent());
        assertEquals(expectedMembership, actualMemberships.get());
        verify(membershipRepository, times(1)).findByUserIdAndTeamId(any(), any());
    }

    @Test
    public void shouldReturnMembershipsByFilterWithoutUserId() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findByUserIdOrTeamId(null, expectedMembership.getTeamId()))
                .thenReturn(List.of(expectedMembership));

        List<Membership> actualMemberships =
                membershipsService.getMembershipsByFilter(null, expectedMembership.getTeamId());

        assertEquals(expectedMembership, actualMemberships.get(0));
        verify(membershipRepository, times(1)).findByUserIdOrTeamId(any(), any());
    }

    @Test
    public void shouldReturnMembershipsByFilterWithoutTeamId() {
        Membership expectedMembership = DEFAULT_MEMBERSHIP();
        when(membershipRepository.findByUserIdOrTeamId(expectedMembership.getUserId(), null))
                .thenReturn(List.of(expectedMembership));

        List<Membership> actualMemberships =
                membershipsService.getMembershipsByFilter(expectedMembership.getUserId(), null);

        assertEquals(expectedMembership, actualMemberships.get(0));
        verify(membershipRepository, times(1)).findByUserIdOrTeamId(any(), any());
    }

    @Test
    public void shouldFailToGetMembershipsWhenRoleIdIsNull() {
        assertThrows(NullPointerException.class,
                () -> membershipsService.getMembershipsByRoleId(null));
    }

}
