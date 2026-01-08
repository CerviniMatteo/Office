package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisore;
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

    /**
     * Helper method to create and save a supervisor via the facade.
     */
    private Supervisore createSupervisor(String nome, String cognome) {
        return facade.saveSupervisor(new Supervisore(nome, cognome));
    }

    @Test
    @Transactional
    void shouldCreateSupervisorsAndFindById() {
        Supervisore s1 = createSupervisor("Matteo", "Cervini");
        Supervisore s2 = createSupervisor("Andrea", "Aivaliotis");
        Supervisore boss = createSupervisor("Le Yang", "Shi");

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
        assertEquals("Matteo", found.get().getNome());

        Optional<Supervisore> notFound = facade.findSupervisorById(boss.getId() + 1000);
        assertTrue(notFound.isEmpty());
    }

    @Test
    @Transactional
    void shouldFindAllSupervisors() {
        Supervisore s1 = createSupervisor("Alice", "Verdi");
        Supervisore s2 = createSupervisor("Bob", "Neri");

        List<Supervisore> all = facade.findAllSupervisors();

        assertTrue(all.contains(s1));
        assertTrue(all.contains(s2));
    }

    @Test
    @Transactional
    void shouldDeleteSupervisor() {
        Supervisore supervisor = createSupervisor("Carlo", "Blu");
        assertTrue(facade.findSupervisorById(supervisor.getId()).isPresent());

        facade.deleteSupervisorById(supervisor.getId());
        assertTrue(facade.findSupervisorById(supervisor.getId()).isEmpty());
    }

    @Test
    @Transactional
    void shouldAssignAndRemoveSubordinates() {
        Supervisore boss = createSupervisor("Boss", "Tech");
        Supervisore sub = createSupervisor("Dev", "Uno");

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
        Supervisore a = createSupervisor("A", "Tech");
        Supervisore b = createSupervisor("B", "Dev");
        Supervisore c = createSupervisor("C", "Ops");

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
        Supervisore root = createSupervisor("Root", "Leader");
        Supervisore child = createSupervisor("Child", "Member");

        facade.assignSubordinate(root.getId(), child.getId());

        List<Supervisore> roots = facade.findSupervisorsWithoutSupervisor();
        assertTrue(roots.contains(root));
        assertFalse(roots.contains(child));
    }

    @Test
    @Transactional
    void shouldFindSupervisorsWithoutSubordinates() {
        Supervisore sub = createSupervisor("Sub", "Member");
        Supervisore supervisor = createSupervisor("Supervisor1", "Leader");
        Supervisore supervisor2 = createSupervisor("supervisor2", "Member");

        facade.assignSubordinate(supervisor.getId(), sub.getId());
        // sub2 is not assigned -> should appear in "without subordinates"
        List<Supervisore> withoutSubordinates = facade.findSupervisorsWithoutSubordinates();

        assertTrue(withoutSubordinates.contains(supervisor2));
        assertFalse(withoutSubordinates.contains(supervisor));
    }


    @Test
    @Transactional
    void shouldPreventMultiLevelLoop() {
        Supervisore s1 = createSupervisor("S1", "Tech");
        Supervisore s2 = createSupervisor("S2", "Dev");
        Supervisore s3 = createSupervisor("S3", "Ops");
        Supervisore s4 = createSupervisor("S4", "QA");

        facade.assignSubordinate(s1.getId(), s2.getId());
        facade.assignSubordinate(s2.getId(), s3.getId());
        facade.assignSubordinate(s3.getId(), s4.getId());

        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(s4.getId(), s1.getId()));
    }
}
