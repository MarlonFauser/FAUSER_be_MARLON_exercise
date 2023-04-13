package com.ecore.roles.repository;

import com.ecore.roles.model.Membership;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ecore.roles.utils.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void testFindByUserIdAndTeamId() {
        Membership membership1 = DEFAULT_MEMBERSHIP();
        membership1.setUserId(UUID.randomUUID());
        membership1.setTeamId(UUID.randomUUID());

        Membership membership2 = DEFAULT_MEMBERSHIP();
        membership2.setUserId(UUID.randomUUID());
        membership2.setTeamId(UUID.randomUUID());

        Membership membership3 = DEFAULT_MEMBERSHIP();
        membership3.setUserId(UUID.randomUUID());
        membership3.setTeamId(UUID.randomUUID());

        membershipRepository.saveAll(List.of(membership1, membership2, membership3));

        Optional<Membership> membership =
                membershipRepository.findByUserIdAndTeamId(membership1.getUserId(), membership1.getTeamId());

        assertTrue(membership.isPresent());
        assertEquals(membership.get().getUserId(), membership1.getUserId());
        assertEquals(membership.get().getTeamId(), membership1.getTeamId());
    }

    @Test
    public void testFindByUserId() {
        UUID actualUserId = UUID.randomUUID();

        Membership membership1 = DEFAULT_MEMBERSHIP();
        membership1.setUserId(actualUserId);
        membership1.setTeamId(UUID.randomUUID());

        Membership membership2 = DEFAULT_MEMBERSHIP();
        membership2.setUserId(UUID.randomUUID());
        membership2.setTeamId(UUID.randomUUID());

        Membership membership3 = DEFAULT_MEMBERSHIP();
        membership3.setUserId(UUID.randomUUID());
        membership3.setTeamId(UUID.randomUUID());

        membershipRepository.saveAll(List.of(membership1, membership2, membership3));

        List<Membership> foundMemberships = membershipRepository.findByUserIdOrTeamId(actualUserId, null);

        assertEquals(1, foundMemberships.size());
        assertEquals(foundMemberships.get(0).getUserId(), actualUserId);
    }

    @Test
    public void testFindByUserTeamId() {
        UUID actualTeamId = UUID.randomUUID();

        Membership membership1 = DEFAULT_MEMBERSHIP();
        membership1.setUserId(UUID.randomUUID());
        membership1.setRole(DEVELOPER_ROLE());
        membership1.setTeamId(actualTeamId);

        Membership membership2 = DEFAULT_MEMBERSHIP();
        membership2.setUserId(UUID.randomUUID());
        membership2.setRole(DEVELOPER_ROLE());
        membership2.setTeamId(actualTeamId);

        Membership membership3 = DEFAULT_MEMBERSHIP();
        membership3.setUserId(UUID.randomUUID());
        membership3.setRole(DEVELOPER_ROLE());

        membershipRepository.saveAll(List.of(membership1, membership2, membership3));

        List<Membership> foundMemberships = membershipRepository.findByUserIdOrTeamId(null, actualTeamId);

        assertEquals(2, foundMemberships.size());
        assertEquals(2, foundMemberships.stream()
                .filter(m -> m.getTeamId() == actualTeamId).count());
    }

    @Test
    public void testFindByRoleId() {
        Membership membership1 = DEFAULT_MEMBERSHIP();
        membership1.setUserId(UUID.randomUUID());
        membership1.setRole(DEVELOPER_ROLE());

        Membership membership2 = DEFAULT_MEMBERSHIP();
        membership2.setUserId(UUID.randomUUID());
        membership2.setRole(DEVELOPER_ROLE());

        Membership membership3 = DEFAULT_MEMBERSHIP();
        membership3.setRole(PRODUCT_OWNER_ROLE());
        membership3.setUserId(UUID.randomUUID());

        membershipRepository.saveAll(List.of(membership1, membership2, membership3));

        List<Membership> foundMemberships = membershipRepository.findByRoleId(DEVELOPER_ROLE().getId());

        assertEquals(2, foundMemberships.size());
        assertEquals(2, foundMemberships.stream().filter(m -> m.getRole().getId() == DEVELOPER_ROLE().getId())
                .count());
    }
}
