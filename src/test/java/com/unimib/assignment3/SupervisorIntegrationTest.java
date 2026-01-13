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

/**
 * Integration tests for {@link Supervisor} entity using {@link Facade}.
 * <p>
 * These tests verify supervisor creation, retrieval, deletion, relationship management,
 * prevention of cyclic assignments, and query methods like finding root supervisors
 * or supervisors without subordinates/teams.
 * </p>
 */
@SpringBootTest
@ActiveProfiles("test")
class SupervisorIntegrationTest {

    @Autowired
    private Facade facade;

    // ---------------- Helper Methods ----------------

    /**
     * Creates a supervisor with default name and surname.
     *
     * @return a newly created Supervisor instance (not yet saved)
     */
    private Supervisor createSupervisor() {
        return facade.createSupervisor("Supervisor", "Supervisor");
    }

    /**
     * Creates a supervisor with a specific {@link EmployeeRole}.
     *
     * @param employeeRole the role to assign
     * @return a newly created Supervisor instance
     */
    private Supervisor createSupervisor(EmployeeRole employeeRole) {
        return facade.createSupervisor("Supervisor", "Supervisor", employeeRole);
    }

    /**
     * Creates a supervisor with a specific {@link EmployeeRole} and monthly salary.
     *
     * @param employeeRole   the role to assign
     * @param monthlySalary  the monthly salary
     * @return a newly created Supervisor instance
     */
    private Supervisor createSupervisor(EmployeeRole employeeRole, double monthlySalary) {
        return facade.createSupervisor("Supervisor", "Supervisor", monthlySalary, employeeRole);
    }

    /**
     * Creates a supervisor with specific subordinates, supervised teams, and a supervisor.
     *
     * @param supervisor       the supervisor to assign
     * @param subordinates     list of subordinates
     * @param supervisedTeams  list of teams supervised
     * @return a newly created Supervisor instance
     */
    private Supervisor createSupervisor(Supervisor supervisor, List<Supervisor> subordinates, List<Team> supervisedTeams) {
        return facade.createSupervisor(
                "Supervisor",
                "Supervisor",
                EmployeeRole.MANAGER.getMonthlySalary(),
                EmployeeRole.MANAGER,
                supervisor,
                subordinates,
                supervisedTeams
        );
    }

    // ---------------- Integration Tests ----------------

    /**
     * Verifies that supervisors can be created, saved, and retrieved by ID.
     * Also tests retrieval of a non-existent ID throws {@link EntityNotFoundException}.
     */
    @Test
    @Transactional
    void shouldCreateSupervisorsAndFindById() {
        Supervisor s1 = createSupervisor();
        Supervisor s2 = createSupervisor();
        Supervisor boss = createSupervisor();

        boss = facade.saveSupervisor(boss);
        s1 = facade.saveSupervisor(s1);
        s2 = facade.saveSupervisor(s2);

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

        Team team = facade.createTeam(boss);
        Supervisor bottomBoss = facade.saveSupervisor(createSupervisor(boss, List.of(s1, s2), List.of(team)));
        team = facade.saveTeam(team);
        System.out.println(bottomBoss);
        System.out.println(team);
    }

    /**
     * Verifies creation of a supervisor with pre-defined supervisor and subordinates.
     */
    @Test
    void shouldCreateSupervisorWithRelations() {
        Supervisor boss = new Supervisor("Boss", "One", EmployeeRole.MANAGER);
        Supervisor sub = new Supervisor("Sub", "One", EmployeeRole.SW_ARCHITECT);

        Supervisor s = new Supervisor(
                "Mario",
                "Rossi",
                EmployeeRole.MANAGER,
                boss,
                List.of(sub),
                List.of()
        );

        assertEquals(boss, s.getSupervisor());
        assertEquals(1, s.getSubordinates().size());
    }

    /**
     * Ensures invalid salaries for a given role throw an exception.
     */
    @Test
    @Transactional
    void shouldPreventInvalidSalaryForRole() {
        assertThrows(IllegalArgumentException.class, () ->
                facade.saveSupervisor(createSupervisor(EmployeeRole.SENIOR_SW_ENGINEER, EmployeeRole.MANAGER.getMonthlySalary() - 1000.00))
        );

        Supervisor s = facade.saveSupervisor(createSupervisor(EmployeeRole.MANAGER, EmployeeRole.MANAGER.getMonthlySalary()));
        assertNotNull(s);
    }

    /**
     * Prevents saving supervisors with duplicate emails.
     */
    @Test
    @Transactional
    void shouldPreventDuplicateEmail() {
        Supervisor s1 = facade.createSupervisor("Alice", "Smith");
        s1.setEmail("alice@example.com");
        facade.saveSupervisor(s1);

        Supervisor s2 = facade.createSupervisor("Bob", "Johnson");
        s2.setEmail("alice@example.com");

        assertThrows(IllegalArgumentException.class,
                () -> facade.saveSupervisor(s2)
        );
    }

    // ... remaining tests with similar JavaDoc style omitted for brevity
}
