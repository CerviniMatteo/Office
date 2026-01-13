package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.facade.Facade;
import jakarta.persistence.EntityNotFoundException;
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

    /**
     * Helper method to create and save a supervisor via the facade.
     */
    private Supervisor createSupervisor() {
        return facade.createSupervisor("Supervisor" , "Supervisor");
    }
    private void createSupervisor(EmployeeRole employeeRole) {
        facade.createSupervisor("Supervisor", "Supervisor", employeeRole);
    }

    @Test
    @Transactional
    void shouldCreateSupervisorsAndFindById() {
        Supervisor s1 = createSupervisor();
        Supervisor s2 = createSupervisor();
        Supervisor boss = createSupervisor();

        boss =facade.saveSupervisor(boss);
        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);

        System.out.println(boss);
        System.out.println(s1);
        System.out.println(s2);

        // Assign subordinates to boss
        boss.addSubordinate(s1);
        boss.addSubordinate(s2);

        assertNotNull(s1.getPersonId());
        assertNotNull(s2.getPersonId());
        assertNotNull(boss.getPersonId());
        assertNotEquals(s1.getPersonId(), s2.getPersonId());

        Optional<Supervisor> found = facade.findSupervisorById(s1.getPersonId());
        assertTrue(found.isPresent());
        assertEquals(s1.getName(), found.get().getName());

        Supervisor finalBoss = boss;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findSupervisorById(finalBoss.getPersonId() + 1000)
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
        Supervisor s1 = createSupervisor();
        Supervisor s2 = createSupervisor();
        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);

        List<Supervisor> all = facade.findAllSupervisors();

        assertTrue(all.contains(s1));
        assertTrue(all.contains(s2));
    }

    @Test
    @Transactional
    void shouldDeleteSupervisor() {
        Supervisor supervisor = createSupervisor();
        supervisor = facade.saveSupervisor(supervisor);
        assertTrue(facade.findSupervisorById(supervisor.getPersonId()).isPresent());

        facade.deleteSupervisorById(supervisor.getPersonId());

        Supervisor finalSupervisor = supervisor;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findSupervisorById(finalSupervisor.getPersonId())
        );
    }

    @Test
    @Transactional
    void shouldAssignAndRemoveSubordinates() {
        Supervisor boss = createSupervisor();
        Supervisor sub = createSupervisor();

        boss = facade.saveSupervisor(boss);
        sub = facade.saveSupervisor(sub);

        facade.assignSubordinate(boss.getPersonId(), sub.getPersonId());

        Supervisor bossCheck = facade.findSupervisorById(boss.getPersonId()).get();
        Supervisor subCheck = facade.findSupervisorById(sub.getPersonId()).get();

        assertTrue(bossCheck.getSubordinates().contains(subCheck));
        assertEquals(subCheck.getSupervisore(), bossCheck);

        facade.removeSubordinate(boss.getPersonId(), sub.getPersonId());

        assertFalse(bossCheck.getSubordinates().contains(subCheck));
        assertNull(subCheck.getSupervisore());

        Supervisor finalBoss = boss;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalBoss.getPersonId(), finalBoss.getPersonId()));
    }

    @Test
    @Transactional
    void shouldPreventComplexLoop() {
        Supervisor a = createSupervisor();
        Supervisor b = createSupervisor();
        Supervisor c = createSupervisor();

        a = facade.saveSupervisor(a);
        b = facade.saveSupervisor(b);
        c = facade.saveSupervisor(c);

        facade.assignSubordinate(a.getPersonId(), b.getPersonId());
        facade.assignSubordinate(b.getPersonId(), c.getPersonId());

        Supervisor finalC = c;
        Supervisor finalA = a;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalC.getPersonId(), finalA.getPersonId()));

        assertEquals(b, c.getSupervisore());
        assertEquals(a, b.getSupervisore());
        assertNotEquals(a, c.getSupervisore());
    }

    @Test
    @Transactional
    void shouldFindRootSupervisors() {
        Supervisor root = createSupervisor();
        Supervisor child = createSupervisor();

        root = facade.saveSupervisor(root);
        child = facade.saveSupervisor(child);

        facade.assignSubordinate(root.getPersonId(), child.getPersonId());

        List<Supervisor> roots = facade.findSupervisorsWithoutSupervisor();
        assertTrue(roots.contains(root));
        assertFalse(roots.contains(child));
    }

    @Test
    @Transactional
    void shouldFindSupervisorsWithoutSubordinates() {
        Supervisor sub = createSupervisor();
        Supervisor supervisor = createSupervisor();
        Supervisor supervisor2 = createSupervisor();

        sub = facade.saveSupervisor(sub);
        supervisor = facade.saveSupervisor(supervisor);
        supervisor2 = facade.saveSupervisor(supervisor2);

        facade.assignSubordinate(supervisor.getPersonId(), sub.getPersonId());
        // sub2 is not assigned -> should appear in "without subordinates"
        List<Supervisor> withoutSubordinates = facade.findSupervisorsWithoutSubordinates();

        assertTrue(withoutSubordinates.contains(supervisor2));
        assertFalse(withoutSubordinates.contains(supervisor));
    }


    @Test
    @Transactional
    void shouldPreventMultiLevelLoop() {
        Supervisor s1 = createSupervisor();
        Supervisor s2 = createSupervisor();
        Supervisor s3 = createSupervisor();
        Supervisor s4 = createSupervisor();

        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);
        s3 = facade.saveSupervisor(s3);
        s4 = facade.saveSupervisor(s4);

        facade.assignSubordinate(s1.getPersonId(), s2.getPersonId());
        facade.assignSubordinate(s2.getPersonId(), s3.getPersonId());
        facade.assignSubordinate(s3.getPersonId(), s4.getPersonId());

        Supervisor finalS = s4;
        Supervisor finalS1 = s1;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalS.getPersonId(), finalS1.getPersonId()));
    }
}
