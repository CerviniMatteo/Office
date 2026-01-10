package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
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
        return facade.createSupervisor("nome" + counter, "cognome");
    }
    private void createSupervisor(EmployeeRole employeeRole) {
        counter++;
        facade.createSupervisor("nome" + counter, "cognome", employeeRole);
    }
    private Supervisore createSupervisor(double monthlySalary, EmployeeRole employeeRole) {
        counter++;
        return facade.createSupervisor("nome" + counter, "cognome", monthlySalary, employeeRole);
    }



    @Test
    @Transactional
    void shouldCreateSupervisorsAndFindById() {
        Supervisore s1 = createSupervisor();
        Supervisore s2 = createSupervisor();
        Supervisore boss = createSupervisor();

        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);

        // Assign subordinates to boss
        boss.addSubordinate(s1);
        boss.addSubordinate(s2);

        boss =facade.saveSupervisor(boss);

        assertNotNull(s1.getId());
        assertNotNull(s2.getId());
        assertNotNull(boss.getId());
        assertNotEquals(s1.getId(), s2.getId());

        Optional<Supervisore> found = facade.findSupervisorById(s1.getId());
        assertTrue(found.isPresent());
        assertEquals(s1.getNome(), found.get().getNome());

        Supervisore finalBoss = boss;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findSupervisorById(finalBoss.getId() + 1000)
        );
    }

    @Test
    @Transactional
    void shouldPreventWrongRolesForSupervisor() {
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(EmployeeRole.JUNIOR));
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(EmployeeRole.SENIOR));
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(EmployeeRole.SENIOR_SW_ENGINEER));
        createSupervisor(EmployeeRole.SW_ARCHITECT);
        createSupervisor(EmployeeRole.SENIOR_SW_ARCHITECT);
        createSupervisor(EmployeeRole.MANAGER);

    }

    @Test
    @Transactional
    void shouldFindAllSupervisors() {
        Supervisore s1 = createSupervisor();
        Supervisore s2 = createSupervisor();
        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);

        List<Supervisore> all = facade.findAllSupervisors();

        assertTrue(all.contains(s1));
        assertTrue(all.contains(s2));
    }

    @Test
    @Transactional
    void shouldDeleteSupervisor() {
        Supervisore supervisor = createSupervisor();
        supervisor = facade.saveSupervisor(supervisor);
        assertTrue(facade.findSupervisorById(supervisor.getId()).isPresent());

        facade.deleteSupervisorById(supervisor.getId());

        Supervisore finalSupervisor = supervisor;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findSupervisorById(finalSupervisor.getId())
        );
    }

    @Test
    @Transactional
    void shouldAssignAndRemoveSubordinates() {
        Supervisore boss = createSupervisor();
        Supervisore sub = createSupervisor();

        boss = facade.saveSupervisor(boss);
        sub = facade.saveSupervisor(sub);

        facade.assignSubordinate(boss.getId(), sub.getId());

        Supervisore bossCheck = facade.findSupervisorById(boss.getId()).get();
        Supervisore subCheck = facade.findSupervisorById(sub.getId()).get();

        assertTrue(bossCheck.getSupervisoriSupervisionati().contains(subCheck));
        assertEquals(subCheck.getSupervisore(), bossCheck);

        facade.removeSubordinate(boss.getId(), sub.getId());

        assertFalse(bossCheck.getSupervisoriSupervisionati().contains(subCheck));
        assertNull(subCheck.getSupervisore());

        Supervisore finalBoss = boss;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalBoss.getId(), finalBoss.getId()));
    }

    @Test
    @Transactional
    void shouldPreventComplexLoop() {
        Supervisore a = createSupervisor();
        Supervisore b = createSupervisor();
        Supervisore c = createSupervisor();

        a = facade.saveSupervisor(a);
        b = facade.saveSupervisor(b);
        c = facade.saveSupervisor(c);

        facade.assignSubordinate(a.getId(), b.getId());
        facade.assignSubordinate(b.getId(), c.getId());

        Supervisore finalC = c;
        Supervisore finalA = a;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalC.getId(), finalA.getId()));

        assertEquals(b, c.getSupervisore());
        assertEquals(a, b.getSupervisore());
        assertNotEquals(a, c.getSupervisore());
    }

    @Test
    @Transactional
    void shouldFindRootSupervisors() {
        Supervisore root = createSupervisor();
        Supervisore child = createSupervisor();

        root = facade.saveSupervisor(root);
        child = facade.saveSupervisor(child);

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

        sub = facade.saveSupervisor(sub);
        supervisor = facade.saveSupervisor(supervisor);
        supervisor2 = facade.saveSupervisor(supervisor2);

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

        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);
        s3 = facade.saveSupervisor(s3);
        s4 = facade.saveSupervisor(s4);

        facade.assignSubordinate(s1.getId(), s2.getId());
        facade.assignSubordinate(s2.getId(), s3.getId());
        facade.assignSubordinate(s3.getId(), s4.getId());

        Supervisore finalS = s4;
        Supervisore finalS1 = s1;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalS.getId(), finalS1.getId()));
    }
}
