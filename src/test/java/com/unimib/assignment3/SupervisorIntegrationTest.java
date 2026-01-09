package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.constants.SupervisorConstants;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.engine.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Supervisore entity and related operations.
 * <p>
 * Tests include:
 * - Creating supervisors
 * - Assigning/removing subordinates
 * - Preventing cyclic relationships
 * - Retrieving root supervisors, supervisors without subordinates, and supervisors without teams
 */
//TODO add tests for finding supervisors without teams
@SpringBootTest
@ActiveProfiles("test")
class SupervisorIntegrationTest {

    @Autowired
    private Facade facade;
    private static long counter = 0;

    /**
     * Helper method to create and save a supervisor via the facade.
     */
    private Supervisore createSupervisor() {
        counter++;
        return facade.saveSupervisor(
                new Supervisore(
                        "Supervisor" + counter,
                        "Supervisor" + counter

                )
        );
    }
    private Supervisore createSupervisor(EmployeeRole employeeRole) {
        counter++;
        return facade.saveSupervisor(
                new Supervisore(
                        "Supervisor" + counter,
                        "Supervisor" + counter,
                        employeeRole.getMonthlySalary(),
                        employeeRole

                )
        );
    }


    @Test
    @Transactional
    void shouldCreateSupervisorsAndFindById() {
        Supervisore s1 = createSupervisor();
        Supervisore s2 = createSupervisor();
        Supervisore boss = createSupervisor();

        // Assign subordinates to boss
        boss.addSubordinate(s1);
        boss.addSubordinate(s2);
        facade.saveSupervisor(boss);

        assertNotNull(s1.getId());
        assertNotNull(s2.getId());
        assertNotNull(boss.getId());
        assertNotEquals(s1.getId(), s2.getId());

        Optional<Supervisore> found = facade.findSupervisorById(s1.getId());
        assertTrue(found.isPresent());
        assertEquals(s1.getNome(), found.get().getNome());

        Optional<Supervisore> notFound = facade.findSupervisorById(boss.getId() + 1000);
        assertTrue(notFound.isEmpty());
    }

    @Test
    @Transactional
    void shouldPreventWrongRolesForSupervisor() {
        assertThrows(IllegalArgumentException.class, () -> {
            createSupervisor(EmployeeRole.JUNIOR);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            createSupervisor(EmployeeRole.SENIOR);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            createSupervisor(EmployeeRole.SENIOR_SW_ENGINEER);
        });
        createSupervisor(EmployeeRole.SW_ARCHITECT);
        createSupervisor(EmployeeRole.SENIOR_SW_ARCHITECT);
        createSupervisor(EmployeeRole.MANAGER);

    }

    @Test
    @Transactional
    void shouldFindAllSupervisors() {
        Supervisore s1 = createSupervisor();
        Supervisore s2 = createSupervisor();

        List<Supervisore> all = facade.findAllSupervisors();

        assertTrue(all.contains(s1));
        assertTrue(all.contains(s2));
    }

    @Test
    @Transactional
    void shouldDeleteSupervisor() {
        Supervisore supervisor = createSupervisor();
        assertTrue(facade.findSupervisorById(supervisor.getId()).isPresent());

        facade.deleteSupervisorById(supervisor.getId());
        assertTrue(facade.findSupervisorById(supervisor.getId()).isEmpty());
    }

    @Test
    @Transactional
    void shouldAssignAndRemoveSubordinates() {
        Supervisore boss = createSupervisor();
        Supervisore sub = createSupervisor();

        facade.assignSubordinate(boss.getId(), sub.getId());

        Supervisore bossCheck = facade.findSupervisorById(boss.getId()).get();
        Supervisore subCheck = facade.findSupervisorById(sub.getId()).get();

        assertTrue(bossCheck.getSupervisoriSupervisionati().contains(subCheck));
        assertEquals(subCheck.getSupervisore(), bossCheck);

        facade.removeSubordinate(boss.getId(), sub.getId());

        assertFalse(bossCheck.getSupervisoriSupervisionati().contains(subCheck));
        assertNull(subCheck.getSupervisore());

        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(boss.getId(), boss.getId()));
    }

    @Test
    @Transactional
    void shouldPreventComplexLoop() {
        Supervisore a = createSupervisor();
        Supervisore b = createSupervisor();
        Supervisore c = createSupervisor();

        facade.assignSubordinate(a.getId(), b.getId());
        facade.assignSubordinate(b.getId(), c.getId());

        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(c.getId(), a.getId()));

        assertEquals(b, c.getSupervisore());
        assertEquals(a, b.getSupervisore());
        assertNotEquals(a, c.getSupervisore());
    }

    @Test
    @Transactional
    void shouldFindRootSupervisors() {
        Supervisore root = createSupervisor();
        Supervisore child = createSupervisor();

        facade.assignSubordinate(root.getId(), child.getId());

        List<Supervisore> roots = facade.findSupervisorsWithoutSupervisor();
        assertTrue(roots.contains(root));
        assertFalse(roots.contains(child));
    }

    @Test
    @Transactional
    void shouldFindSupervisorsWithoutSubordinates() {
        Supervisore sub = createSupervisor();
        Supervisore supervisor = createSupervisor();
        Supervisore supervisor2 = createSupervisor();

        facade.assignSubordinate(supervisor.getId(), sub.getId());
        // sub2 is not assigned -> should appear in "without subordinates"
        List<Supervisore> withoutSubordinates = facade.findSupervisorsWithoutSubordinates();

        assertTrue(withoutSubordinates.contains(supervisor2));
        assertFalse(withoutSubordinates.contains(supervisor));
    }


    @Test
    @Transactional
    void shouldPreventMultiLevelLoop() {
        Supervisore s1 = createSupervisor();
        Supervisore s2 = createSupervisor();
        Supervisore s3 = createSupervisor();
        Supervisore s4 = createSupervisor();

        facade.assignSubordinate(s1.getId(), s2.getId());
        facade.assignSubordinate(s2.getId(), s3.getId());
        facade.assignSubordinate(s3.getId(), s4.getId());

        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(s4.getId(), s1.getId()));
    }
}
