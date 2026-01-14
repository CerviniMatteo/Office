package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Team;
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
    private Supervisor createSupervisor(EmployeeRole employeeRole) {
        return facade.createSupervisor("Supervisor", "Supervisor", employeeRole);
    }

    private Supervisor createSupervisor(double monthlySalary) {
        return facade.createSupervisor("Supervisor", "Supervisor", monthlySalary, EmployeeRole.MANAGER);
    }

    private Supervisor createSupervisor(Supervisor supervisor, List<Supervisor> subordinates) {
        return facade.createSupervisor("Supervisor", "Supervisor", EmployeeRole.MANAGER.getMonthlySalary(), EmployeeRole.MANAGER, supervisor, subordinates);
    }

    /**
     * Test creating supervisors and finding them by ID.
     */
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

    /**
     * Test that only valid roles can be assigned to supervisors.
     */
    @Test
    @Transactional
    void shouldPreventWrongRolesForSupervisor() {
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(EmployeeRole.JUNIOR));
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(EmployeeRole.SENIOR));
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(EmployeeRole.SENIOR_SW_ENGINEER));

        Supervisor supervisor = facade.saveSupervisor(createSupervisor(EmployeeRole.MANAGER));
        System.out.println(supervisor);
    }

    /**
     * Test retrieving all supervisors.
     */
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

    /**
     * Test deleting a supervisor by ID.
     */
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

    /**
     * Test assigning and removing subordinates to/from a supervisor.
     */
    @Test
    @Transactional
    void shouldAssignAndRemoveSubordinates() {
        Supervisor boss = createSupervisor();
        Supervisor sub = createSupervisor();

        boss = facade.saveSupervisor(boss);
        sub = facade.saveSupervisor(sub);

        facade.assignSubordinate(boss.getPersonId(), sub.getPersonId());

        Optional<Supervisor> optionalSupervisor = facade.findSupervisorById(sub.getPersonId());
        assertTrue(optionalSupervisor.isPresent());
        Supervisor subCheck = optionalSupervisor.get();
        Optional<Supervisor> optionalBoss = facade.findSupervisorById(boss.getPersonId());
        assertTrue(optionalBoss.isPresent());
        Supervisor bossCheck = optionalBoss.get();

        assertTrue(bossCheck.getSubordinates().contains(subCheck));
        assertEquals(subCheck.getSupervisor(), bossCheck);

        facade.removeSubordinate(boss.getPersonId(), sub.getPersonId());

        assertFalse(bossCheck.getSubordinates().contains(subCheck));
        assertNull(subCheck.getSupervisor());

        Supervisor finalBoss = boss;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalBoss.getPersonId(), finalBoss.getPersonId()));
    }

    /**
     * Test preventing cyclic relationships in supervisor-subordinate assignments.
     */
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

        assertEquals(b, c.getSupervisor());
        assertEquals(a, b.getSupervisor());
        assertNotEquals(a, c.getSupervisor());
    }

    /**
     * Test finding root supervisors (those without supervisors).
     */
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

    /**
     * Test finding supervisors without subordinates.
     */
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

    /**
     * Test finding supervisors without teams.
     */
    @Test
    @Transactional
    void shouldFindSupervisorsWithoutTeam() {
        // Create and save supervisor with team
        Supervisor sup1 = createSupervisor(EmployeeRole.MANAGER.getMonthlySalary() + 1000);
        Team team = facade.createTeam(sup1);
        sup1.addSupervisedTeam(team);      // assign team before saving
        sup1 = facade.saveSupervisor(sup1);
        team = facade.saveTeam(team);

        // Create supervisor without team
        Supervisor sup2 = facade.saveSupervisor(createSupervisor());

        // Fetch supervisors without teams
        List<Supervisor> withoutTeam = facade.findSupervisorsWithoutSupervisedTeam();

        // Assertions
        assertTrue(withoutTeam.contains(sup2));
        Supervisor withTeam = facade.getTeamById(team.getTeamId())
                .orElseThrow()
                .getSupervisor();
        assertEquals(sup1, withTeam);
    }

    /**
     * Test creating supervisor with correct hierarchy and full constructor
     */
    @Test
    @Transactional
    void shouldCreateSupervisorWithHierarchyAndTeams() {
        // Save the root supervisor first
        Supervisor rootSupervisor = facade.saveSupervisor(
                createSupervisor()
        );

        // Now create and save the teams
        Team team1 = facade.saveTeam(facade.createTeam(rootSupervisor));
        Team team2 = facade.saveTeam(facade.createTeam(rootSupervisor));
        // Create a subordinate supervisor that will be assigned under the new supervisor
        Supervisor subordinate = facade.saveSupervisor(
                facade.createSupervisor("Sub", "Sub", EmployeeRole.MANAGER.getMonthlySalary(), EmployeeRole.MANAGER)
        );

        // Create the supervisor under test, assigning superior, subordinates, and teams
        Supervisor newSupervisor = createSupervisor(
                rootSupervisor,
                List.of(subordinate)
        );

        newSupervisor = facade.saveSupervisor(newSupervisor);

        newSupervisor.addSupervisedTeam(team1);
        newSupervisor.addSupervisedTeam(team2);

        //  Assertions to verify everything is correctly set
        assertNotNull(newSupervisor.getPersonId(), "Supervisor should have a generated ID");
        assertEquals(rootSupervisor, newSupervisor.getSupervisor(), "Supervisor's superior should be correctly assigned");
        assertEquals(2, newSupervisor.getSupervisedTeams().size(), "Supervisor should have 2 assigned teams");
        assertTrue(newSupervisor.getSupervisedTeams().contains(team1), "Team1 should be assigned to the supervisor");
        assertTrue(newSupervisor.getSupervisedTeams().contains(team2), "Team2 should be assigned to the supervisor");
        assertTrue(newSupervisor.getSubordinates().contains(subordinate), "Subordinate should be correctly assigned to the supervisor");

        newSupervisor.removeAllSupervisedTeams();
        assertTrue(newSupervisor.getSupervisedTeams().isEmpty());
    }

    /**
     * Test preventing multi-level loops in supervisor-subordinate assignments.
     */
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
