package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.POJO.Worker;
import com.unimib.assignment3.enums.WorkerRole;
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
    private Supervisor createSupervisor(WorkerRole workerRole) {
        return facade.createSupervisor("Supervisor", "Supervisor", workerRole);
    }

    private Supervisor createSupervisor(double monthlySalary) {
        return facade.createSupervisor("Supervisor", "Supervisor", monthlySalary, WorkerRole.MANAGER);
    }

    private Supervisor createSupervisor(Supervisor supervisor, List<Supervisor> subordinates) {
        return facade.createSupervisor("Supervisor", "Supervisor", WorkerRole.MANAGER.getMonthlySalary(), WorkerRole.MANAGER, supervisor, subordinates);
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

        assertNotNull(s1.getWorkerId());
        assertNotNull(s2.getWorkerId());
        assertNotNull(boss.getWorkerId());
        assertNotEquals(s1.getWorkerId(), s2.getWorkerId());

        Optional<Supervisor> found = facade.findSupervisorById(s1.getWorkerId());
        assertTrue(found.isPresent());
        assertEquals(s1.getName(), found.get().getName());

        Supervisor finalBoss = boss;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findSupervisorById(finalBoss.getWorkerId() + 1000)
        );
    }

    /**
     * Test that only valid roles can be assigned to supervisors.
     */
    @Test
    @Transactional
    void shouldPreventWrongRolesForSupervisor() {
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(WorkerRole.JUNIOR));
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(WorkerRole.SENIOR));
        assertThrows(IllegalArgumentException.class, () -> createSupervisor(WorkerRole.SENIOR_SW_ENGINEER));

        Supervisor supervisor = facade.saveSupervisor(createSupervisor(WorkerRole.MANAGER));
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
        assertTrue(facade.findSupervisorById(supervisor.getWorkerId()).isPresent());

        facade.deleteSupervisorById(supervisor.getWorkerId());

        Supervisor finalSupervisor = supervisor;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findSupervisorById(finalSupervisor.getWorkerId())
        );
    }


    /**
     * Test that a manager can delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldDeleteEmployeeByManager() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee employee = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = facade.saveSupervisor(manager);
        employee = facade.saveEmployee(employee);

        facade.fireEmployee(manager.getWorkerId(), employee.getWorkerId());

        Employee finalEmployee = employee;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findEmployeeById(finalEmployee.getWorkerId()));
    }


    /**
     * Test that a manager can fire multiple employees at once.
     */
    @Transactional
    @Test
    void shouldFireMultipleEmployees() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee e1 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e2 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = facade.saveSupervisor(manager);
        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);

        facade.fireEmployees(manager.getWorkerId(), List.of(e1, e2));

        Employee finalE = e1;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findEmployeeById(finalE.getWorkerId()));
        Employee finalE1 = e2;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findEmployeeById(finalE1.getWorkerId()));
    }


    /**
     * Test updating an employee's monthly salary by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateMonthlySalary() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee employee = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = facade.saveSupervisor(manager);
        employee = facade.saveEmployee(employee);

        facade.updateMonthlySalaryById(manager.getWorkerId(), employee.getWorkerId(), 5000.0);

        Optional<Employee> updatedRaw = facade.findEmployeeById(employee.getWorkerId());
        updatedRaw.ifPresent(updated -> assertEquals(5000.0, updated.getMonthlySalary()));
    }

    /**
     * Test that a non-manager cannot delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldThrowIfFireEmployeeByNonManager() {
        Employee nonManager = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee employee = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        nonManager = facade.saveEmployee(nonManager);
        employee = facade.saveEmployee(employee);

        Employee finalNonManager = nonManager;
        Employee finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> facade.fireEmployee(finalNonManager.getWorkerId(), finalEmployee.getWorkerId()));
    }

    /**
     * Test updating an employee's role by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateEmployeeRole() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee employee = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = facade.saveSupervisor(manager);
        employee = facade.saveEmployee(employee);

        facade.updateWorkerRoleById(manager.getWorkerId(), employee.getWorkerId(), WorkerRole.SENIOR_SW_ENGINEER);

        Optional<Employee> updatedRaw = facade.findEmployeeById(employee.getWorkerId());
        assertTrue(updatedRaw.isPresent());
        assertEquals(WorkerRole.SENIOR_SW_ENGINEER, updatedRaw.get().getWorkerRole());
    }

    /**
     * Test finding employees by monthly salary and by role.
     */
    @Transactional
    @Test
    void shouldFindEmployeesByMonthlySalary() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        manager = facade.saveSupervisor(manager);

        Employee e1 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        e1 = facade.saveEmployee(e1);
        facade.updateMonthlySalaryById(manager.getWorkerId(), e1.getWorkerId(), 3100.0);

        Employee e2 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        e2 = facade.saveEmployee(e2);
        facade.updateMonthlySalaryById(manager.getWorkerId(), e2.getWorkerId(), WorkerRole.MANAGER.getMonthlySalary());
        facade.updateWorkerRoleById(manager.getWorkerId(), e2.getWorkerId(), WorkerRole.MANAGER);

        List<Worker> foundSalary = (List<Worker>) facade.findWorkersByMonthlySalary(manager.getWorkerId(), WorkerRole.MANAGER.getMonthlySalary());
        List<Worker> foundRole = (List<Worker>) facade.findWorkersByWorkerRole(manager.getWorkerId(), WorkerRole.MANAGER);

        assertEquals(2, foundSalary.size());
        assertEquals(2, foundRole.size());
        assertTrue(foundSalary.stream().allMatch(e -> Double.compare(e.getMonthlySalary(), WorkerRole.MANAGER.getMonthlySalary()) == 0));
        assertTrue(foundRole.stream().allMatch(e -> e.getWorkerRole().equals(WorkerRole.MANAGER)));
    }

    /**
     * Test that non-managers cannot search employees by salary.
     */
    @Transactional
    @Test
    void shouldThrowIfNonManagerSearchBySalary() {
        Employee nonManager = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        nonManager = facade.saveEmployee(nonManager);

        Employee finalNonManager = nonManager;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findWorkersByMonthlySalary(finalNonManager.getWorkerId(), 3000.0));
    }



    /**
     * Test sorting employees by salary ascending and descending.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByMonthlySalaryAscAndDesc() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        manager = facade.saveSupervisor(manager);

        Employee e1 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e2 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e3 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);
        e3 = facade.saveEmployee(e3);

        facade.updateMonthlySalaryById(manager.getWorkerId(), e1.getWorkerId(), 3500.0);
        facade.updateMonthlySalaryById(manager.getWorkerId(), e2.getWorkerId(), 5500.0);
        facade.updateMonthlySalaryById(manager.getWorkerId(), e3.getWorkerId(), 2500.0);

        List<Worker> asc = (List<Worker>)
                facade.findWorkersByWorkerRoleAscByMonthlySalary(manager.getWorkerId(), WorkerRole.JUNIOR);

        assertEquals(3, asc.size());
        assertTrue(asc.get(0).getMonthlySalary() <= asc.get(1).getMonthlySalary());

        List<Worker> desc = (List<Worker>)
                facade.findWorkersByWorkerRoleDescByMonthlySalary(manager.getWorkerId(), WorkerRole.JUNIOR);

        assertEquals(3, desc.size());
        assertTrue(desc.get(0).getMonthlySalary() >= desc.get(1).getMonthlySalary());
    }

    /**
     * Test sorting employees by role ascending and descending with salary as tie-breaker.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByRoleAscAndDesc() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        manager = facade.saveSupervisor(manager);

        Employee e1 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e2 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e3 = facade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);
        e3 = facade.saveEmployee(e3);

        facade.updateMonthlySalaryById(manager.getWorkerId(), e1.getWorkerId(), 3000.0);
        facade.updateMonthlySalaryById(manager.getWorkerId(), e2.getWorkerId(), 6000.0);
        facade.updateMonthlySalaryById(manager.getWorkerId(), e3.getWorkerId(), 4000.0);

        List<Employee> asc = (List<Employee>)
                facade.findWorkersByMonthlySalaryAscByWorkerRole(manager.getWorkerId(), 3000.0);

        assertFalse(asc.isEmpty());
        for (int i = 1; i < asc.size(); i++) {
            assertTrue(
                    asc.get(i - 1).getWorkerRole().compareTo(asc.get(i).getWorkerRole()) <= 0
            );
        }

        List<Employee> desc = (List<Employee>)
                facade.findWorkersByMonthlySalaryDescByWorkerRole(manager.getWorkerId(), 3000.0);

        assertFalse(desc.isEmpty());
        for (int i = 1; i < desc.size(); i++) {
            assertTrue(
                    desc.get(i - 1).getWorkerRole().compareTo(desc.get(i).getWorkerRole()) >= 0
            );
        }
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

        facade.assignSubordinate(boss.getWorkerId(), sub.getWorkerId());

        Optional<Supervisor> optionalSupervisor = facade.findSupervisorById(sub.getWorkerId());
        assertTrue(optionalSupervisor.isPresent());
        Supervisor subCheck = optionalSupervisor.get();
        Optional<Supervisor> optionalBoss = facade.findSupervisorById(boss.getWorkerId());
        assertTrue(optionalBoss.isPresent());
        Supervisor bossCheck = optionalBoss.get();

        assertTrue(bossCheck.getSubordinates().contains(subCheck));
        assertEquals(subCheck.getSupervisor(), bossCheck);

        facade.removeSubordinate(boss.getWorkerId(), sub.getWorkerId());

        assertFalse(bossCheck.getSubordinates().contains(subCheck));
        assertNull(subCheck.getSupervisor());

        Supervisor finalBoss = boss;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalBoss.getWorkerId(), finalBoss.getWorkerId()));
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

        facade.assignSubordinate(a.getWorkerId(), b.getWorkerId());
        facade.assignSubordinate(b.getWorkerId(), c.getWorkerId());

        Supervisor finalC = c;
        Supervisor finalA = a;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalC.getWorkerId(), finalA.getWorkerId()));

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

        facade.assignSubordinate(root.getWorkerId(), child.getWorkerId());

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

        facade.assignSubordinate(supervisor.getWorkerId(), sub.getWorkerId());
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
        Supervisor sup1 = createSupervisor(WorkerRole.MANAGER.getMonthlySalary() + 1000);
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
                facade.createSupervisor("Sub", "Sub", WorkerRole.MANAGER.getMonthlySalary(), WorkerRole.MANAGER)
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
        assertNotNull(newSupervisor.getWorkerId(), "Supervisor should have a generated ID");
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

        facade.assignSubordinate(s1.getWorkerId(), s2.getWorkerId());
        facade.assignSubordinate(s2.getWorkerId(), s3.getWorkerId());
        facade.assignSubordinate(s3.getWorkerId(), s4.getWorkerId());

        Supervisor finalS = s4;
        Supervisor finalS1 = s1;
        assertThrows(IllegalStateException.class,
                () -> facade.assignSubordinate(finalS.getWorkerId(), finalS1.getWorkerId()));
    }
}
