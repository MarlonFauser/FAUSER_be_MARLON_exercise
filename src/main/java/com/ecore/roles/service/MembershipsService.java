package com.ecore.roles.service;

import com.ecore.roles.model.Membership;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipsService {
    Membership createMembership(Membership membership);

    Membership assignRoleToMembership(Membership membership);

    List<Membership> getMembershipsByRoleId(UUID roleId);

    List<Membership> getMembershipsByFilter(UUID userId, UUID teamId);

    Optional<Membership> getMembership(UUID userId, UUID teamId);
}
