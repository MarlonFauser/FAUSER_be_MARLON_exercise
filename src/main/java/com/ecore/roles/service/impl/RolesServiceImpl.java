package com.ecore.roles.service.impl;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.client.model.User;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.RolesService;
import com.ecore.roles.service.TeamsService;
import com.ecore.roles.service.UsersService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
public class RolesServiceImpl implements RolesService {

    public static final String DEFAULT_ROLE = "Developer";

    private final RoleRepository roleRepository;
    private final MembershipRepository membershipRepository;
    private final TeamsService teamsService;

    private final UsersService usersService;

    @Autowired
    public RolesServiceImpl(
            RoleRepository roleRepository,
            MembershipRepository membershipRepository,
            TeamsService teamsService,
            UsersService usersService) {
        this.roleRepository = roleRepository;
        this.membershipRepository = membershipRepository;
        this.teamsService = teamsService;
        this.usersService = usersService;
    }

    @Override
    public Role CreateRole(@NonNull Role r) {
        if (roleRepository.findByName(r.getName()).isPresent()) {
            throw new ResourceExistsException(Role.class);
        }
        return roleRepository.save(r);
    }

    @Override
    public Role GetRole(@NonNull UUID rid) {
        return roleRepository.findById(rid)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, rid));
    }

    @Override
    public List<Role> GetRoles() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> GetRoles(UUID userId, UUID teamId) {
        List<Membership> memberships = membershipRepository.findByUserIdAndTeamId(Optional.ofNullable(userId),
                Optional.ofNullable(teamId));
        return memberships.stream().map(Membership::getRole).collect(Collectors.toList());
    }

    @Override
    public Role GetRole(UUID userId, UUID teamId) {
        if (teamsService.getTeam(teamId) == null)
            throw new ResourceNotFoundException(Team.class, teamId);

        if (usersService.getUser(userId) == null)
            throw new ResourceNotFoundException(User.class, userId);

        Membership membership = membershipRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class));

        return membership.getRole();
    }
}
