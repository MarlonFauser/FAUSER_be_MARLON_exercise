package com.ecore.roles.service;

import com.ecore.roles.model.Membership;

import java.util.List;
import java.util.UUID;

public interface MembershipsService {
    Membership createMembership(Membership membership);

    Membership assignRoleToMembership(Membership membership);

    List<Membership> getMemberships(UUID roleId);
}
