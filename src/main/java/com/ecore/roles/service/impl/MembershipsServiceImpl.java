package com.ecore.roles.service.impl;

import com.ecore.roles.client.model.Team;
import com.ecore.roles.exception.ForbiddenException;
import com.ecore.roles.exception.InvalidArgumentException;
import com.ecore.roles.exception.ResourceExistsException;
import com.ecore.roles.exception.ResourceNotFoundException;
import com.ecore.roles.model.Membership;
import com.ecore.roles.model.Role;
import com.ecore.roles.repository.MembershipRepository;
import com.ecore.roles.repository.RoleRepository;
import com.ecore.roles.service.MembershipsService;
import com.ecore.roles.service.TeamsService;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Log4j2
@Service
public class MembershipsServiceImpl implements MembershipsService {

    private final MembershipRepository membershipRepository;
    private final RoleRepository roleRepository;

    private final TeamsService teamsService;

    @Autowired
    public MembershipsServiceImpl(
            MembershipRepository membershipRepository,
            RoleRepository roleRepository,
            TeamsService teamsService) {
        this.membershipRepository = membershipRepository;
        this.roleRepository = roleRepository;
        this.teamsService = teamsService;
    }

    @Override
    public Membership createMembership(@NonNull Membership m) {
        validateIfMembershipDoesNotExistYet(m);

        validateIfRoleExists(m);

        validateIfTeamExists(m);

        return membershipRepository.save(m);
    }

    @Override
    public Membership assignRoleToMembership(@NonNull Membership m) {
        validateIfRoleExists(m);

        validateIfUserBelongsToTeam(m);

        return membershipRepository.save(m);
    }

    @Override
    public List<Membership> getMembershipsByRoleId(@NonNull UUID rid) {
        return membershipRepository.findByRoleId(rid);
    }

    @Override
    public List<Membership> getMembershipsByFilter(UUID userId, UUID teamId) {
        return membershipRepository.findByUserIdOrTeamId(userId, teamId);
    }

    @Override
    public Optional<Membership> getMembership(UUID userId, UUID teamId) {
        return membershipRepository.findByUserIdAndTeamId(userId, teamId);
    }

    private void validateIfRoleExists(Membership m) {
        UUID roleId = ofNullable(m.getRole()).map(Role::getId)
                .orElseThrow(() -> new InvalidArgumentException(Role.class));

        roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException(Role.class, roleId));
    }

    private void validateIfTeamExists(Membership m) {
        if (teamsService.getTeam(m.getTeamId()) == null) {
            throw new ResourceNotFoundException(Team.class, m.getTeamId());
        }
    }

    private void validateIfMembershipDoesNotExistYet(Membership m) {
        if (getMembership(m.getUserId(), m.getTeamId()).isPresent()) {
            throw new ResourceExistsException(Membership.class);
        }
    }

    private void validateIfUserBelongsToTeam(Membership m) {
        if (getMembershipsByFilter(m.getUserId(), m.getTeamId()).isEmpty()) {
            throw new ForbiddenException(Membership.class,
                    "The provided user doesn't belong to the provided team.");
        }
    }
}
