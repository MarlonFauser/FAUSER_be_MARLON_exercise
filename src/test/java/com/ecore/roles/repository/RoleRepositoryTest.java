package com.ecore.roles.repository;

import com.ecore.roles.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static com.ecore.roles.utils.TestData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void shouldReturnRoleByName() {
        Role developerRole = DEVELOPER_ROLE();
        Role devopsRole = DEVOPS_ROLE();

        roleRepository.saveAll(List.of(developerRole, devopsRole));

        Optional<Role> role = roleRepository.findByName(developerRole.getName());

        assertTrue(role.isPresent());
        assertEquals(role.get().getName(), developerRole.getName());
    }
}
