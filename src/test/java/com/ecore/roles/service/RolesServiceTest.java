package com.ecore.roles.service;

import com.ecore.roles.client.model.User;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.impl.RolesServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecore.roles.utils.TestData.*;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class RolesServiceTest {

    @InjectMocks
    private RolesServiceImpl rolesService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private MembershipsService membershipsService;

    @Mock
    private TeamsService teamsService;

    @Mock
    private UsersService usersService;

    @Test
    public void shouldCreateRole() {
        Role developerRole = DEVELOPER_ROLE();
        when(roleRepository.save(developerRole)).thenReturn(developerRole);

        Role role = rolesService.CreateRole(developerRole);

        assertNotNull(role);
        assertEquals(developerRole, role);
    }

    @Test
    public void shouldFailToCreateRoleWhenRoleIsNull() {
        assertThrows(NullPointerException.class,
                () -> rolesService.CreateRole(null));
    }

    @Test
    public void shouldReturnRoleWhenRoleIdExists() {
        Role developerRole = DEVELOPER_ROLE();
        when(roleRepository.findById(developerRole.getId())).thenReturn(Optional.of(developerRole));

        Role role = rolesService.GetRole(developerRole.getId());

        assertNotNull(role);
        assertEquals(developerRole, role);
    }

    @Test
    public void shouldReturnRolesByFilter() {
        User user = GIANNI_USER();
        List<Membership> expectedMemberships = List.of(DEFAULT_MEMBERSHIP());

        when(membershipsService.getMembershipsByFilter(user.getId(), ORDINARY_CORAL_LYNX_TEAM_UUID))
                .thenReturn(expectedMemberships);

        List<Role> roles = rolesService.GetRolesByFilter(user.getId(), ORDINARY_CORAL_LYNX_TEAM_UUID);

        assertEquals(roles,
                expectedMemberships.stream().map(Membership::getRole).collect(Collectors.toList()));
    }

    @Test
    public void shouldReturnRole() {
        User user = GIANNI_USER();
        Optional<Membership> expectedMembership = Optional.ofNullable(DEFAULT_MEMBERSHIP());

        when(teamsService.getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID)).thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService.getUser(user.getId())).thenReturn(user);

        when(membershipsService.getMembership(user.getId(), ORDINARY_CORAL_LYNX_TEAM_UUID))
                .thenReturn(expectedMembership);

        Role role = rolesService.GetRole(user.getId(), ORDINARY_CORAL_LYNX_TEAM_UUID);

        assertTrue(expectedMembership.isPresent());
        assertEquals(role, expectedMembership.get().getRole());
        verify(teamsService, times(1)).getTeam(any());
        verify(usersService, times(1)).getUser(any());
        verify(membershipsService, times(1)).getMembership(any(), any());
    }

    @Test
    public void shouldNotReturnRoleWhenTeamNotFound() {
        User user = GIANNI_USER();

        when(teamsService.getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID)).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.GetRole(user.getId(), ORDINARY_CORAL_LYNX_TEAM_UUID));

        assertEquals(format("Team %s not found", ORDINARY_CORAL_LYNX_TEAM_UUID), exception.getMessage());
        verify(teamsService, times(1)).getTeam(any());
        verify(usersService, times(0)).getUser(any());
        verify(membershipsService, times(0)).getMembership(any(), any());
    }

    @Test
    public void shouldNotReturnRoleWhenUserNotFound() {
        User user = GIANNI_USER();

        when(teamsService.getTeam(ORDINARY_CORAL_LYNX_TEAM_UUID)).thenReturn(ORDINARY_CORAL_LYNX_TEAM());
        when(usersService.getUser(user.getId())).thenReturn(null);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.GetRole(user.getId(), ORDINARY_CORAL_LYNX_TEAM_UUID));

        assertEquals(format("User %s not found", user.getId()), exception.getMessage());
        verify(teamsService, times(1)).getTeam(any());
        verify(usersService, times(1)).getUser(any());
        verify(membershipsService, times(0)).getMembership(any(), any());
    }

    @Test
    public void shouldFailToGetRoleWhenRoleIdDoesNotExist() {
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> rolesService.GetRole(UUID_1));

        assertEquals(format("Role %s not found", UUID_1), exception.getMessage());
    }
}
