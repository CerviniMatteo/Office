package com.unimib.backend;

import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Supervisor;
import com.unimib.backend.POJO.Team;
import com.unimib.backend.POJO.Worker;
import com.unimib.backend.enums.WorkerRole;
import com.unimib.backend.facade.EmployeeFacade;
import com.unimib.backend.facade.SupervisorFacade;
import com.unimib.backend.facade.TeamFacade;
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
    private SupervisorFacade supervisorFacade;
    @Autowired
    private EmployeeFacade employeeFacade;
    @Autowired
    private TeamFacade teamFacade;

    /**
     * Helper method to create and save a supervisor via the facade.
     */
    private Supervisor createSupervisor() {
        return supervisorFacade.createSupervisor("Supervisor" , "Supervisor");
    }
    private Supervisor createSupervisor(WorkerRole workerRole) {
        return supervisorFacade.createSupervisor("Supervisor", "Supervisor", workerRole);
    }

    private Supervisor createSupervisor(double monthlySalary) {
        return supervisorFacade.createSupervisor("Supervisor", "Supervisor", monthlySalary, WorkerRole.MANAGER);
    }

    private Supervisor createSupervisor(Supervisor supervisor, List<Supervisor> subordinates) {
        return supervisorFacade.createSupervisor("Supervisor", "Supervisor", WorkerRole.MANAGER.getMonthlySalary(), WorkerRole.MANAGER, supervisor, subordinates);
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

        boss = supervisorFacade.saveSupervisor(boss);
        s1 = supervisorFacade.saveSupervisor(s1);
        s2 = supervisorFacade.saveSupervisor(s2);

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

        Optional<Supervisor> found = supervisorFacade.findSupervisorById(s1.getWorkerId());
        assertTrue(found.isPresent());
        assertEquals(s1.getName(), found.get().getName());

        Supervisor finalBoss = boss;
        assertThrows(EntityNotFoundException.class,
                () -> supervisorFacade.findSupervisorById(finalBoss.getWorkerId() + 1000)
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

        Supervisor supervisor = supervisorFacade.saveSupervisor(createSupervisor(WorkerRole.MANAGER));
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
        s1 = supervisorFacade.saveSupervisor(s1);
        s2 = supervisorFacade.saveSupervisor(s2);

        List<Supervisor> all = supervisorFacade.findAllSupervisors();

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
        supervisor = supervisorFacade.saveSupervisor(supervisor);
        assertTrue(supervisorFacade.findSupervisorById(supervisor.getWorkerId()).isPresent());

        supervisorFacade.deleteSupervisorById(supervisor.getWorkerId());

        Supervisor finalSupervisor = supervisor;
        assertThrows(EntityNotFoundException.class,
                () -> supervisorFacade.findSupervisorById(finalSupervisor.getWorkerId())
        );
    }


    /**
     * Test that a manager can delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldDeleteEmployeeByManager() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee employee = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = supervisorFacade.saveSupervisor(manager);
        employee = employeeFacade.saveEmployee(employee);

        supervisorFacade.fireEmployee(manager.getWorkerId(), employee.getWorkerId());

        Employee finalEmployee = employee;
        assertThrows(EntityNotFoundException.class,
                () -> employeeFacade.findEmployeeById(finalEmployee.getWorkerId()));
    }


    /**
     * Test that a manager can fire multiple employees at once.
     */
    @Transactional
    @Test
    void shouldFireMultipleEmployees() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee e1 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e2 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = supervisorFacade.saveSupervisor(manager);
        e1 = employeeFacade.saveEmployee(e1);
        e2 = employeeFacade.saveEmployee(e2);

        supervisorFacade.fireEmployees(manager.getWorkerId(), List.of(e1, e2));

        Employee finalE = e1;
        assertThrows(EntityNotFoundException.class,
                () -> employeeFacade.findEmployeeById(finalE.getWorkerId()));
        Employee finalE1 = e2;
        assertThrows(EntityNotFoundException.class,
                () -> employeeFacade.findEmployeeById(finalE1.getWorkerId()));
    }


    /**
     * Test updating an employee's monthly salary by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateMonthlySalary() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee employee = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        manager = supervisorFacade.saveSupervisor(manager);
        employee = employeeFacade.saveEmployee(employee);

        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), employee.getWorkerId(), 5000.0);

        Optional<Employee> updatedRaw = employeeFacade.findEmployeeById(employee.getWorkerId());
        updatedRaw.ifPresent(updated -> assertEquals(5000.0, updated.getMonthlySalary()));
    }

    /**
     * Test that a non-manager cannot delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldThrowIfFireEmployeeByNonManager() {
        Employee nonManager = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee employee = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        nonManager = employeeFacade.saveEmployee(nonManager);
        employee = employeeFacade.saveEmployee(employee);

        Employee finalNonManager = nonManager;
        Employee finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> supervisorFacade.fireEmployee(finalNonManager.getWorkerId(), finalEmployee.getWorkerId()));
    }

    /**
     * Test updating an employee's role by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateEmployeeRole() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        Employee employee = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);


        manager = supervisorFacade.saveSupervisor(manager);
        employee = employeeFacade.saveEmployee(employee);

        supervisorFacade.updateWorkerRoleById(manager.getWorkerId(), employee.getWorkerId(), WorkerRole.SENIOR_SW_ENGINEER);

        Optional<Employee> updatedRaw = employeeFacade.findEmployeeById(employee.getWorkerId());
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
        manager = supervisorFacade.saveSupervisor(manager);

        Employee e1 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        e1 = employeeFacade.saveEmployee(e1);
        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e1.getWorkerId(), 3100.0);

        Employee e2 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        e2 = employeeFacade.saveEmployee(e2);
        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e2.getWorkerId(), WorkerRole.MANAGER.getMonthlySalary());
        supervisorFacade.updateWorkerRoleById(manager.getWorkerId(), e2.getWorkerId(), WorkerRole.MANAGER);

        List<Worker> foundSalary = (List<Worker>) supervisorFacade.findWorkersByMonthlySalary(manager.getWorkerId(), WorkerRole.MANAGER.getMonthlySalary());
        List<Worker> foundRole = (List<Worker>) supervisorFacade.findWorkersByWorkerRole(manager.getWorkerId(), WorkerRole.MANAGER);

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
        Employee nonManager = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        nonManager = employeeFacade.saveEmployee(nonManager);

        Employee finalNonManager = nonManager;
        assertThrows(IllegalArgumentException.class,
                () -> supervisorFacade.findWorkersByMonthlySalary(finalNonManager.getWorkerId(), 3000.0));
    }



    /**
     * Test sorting employees by salary ascending and descending.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByMonthlySalaryAscAndDesc() {
        Supervisor manager = createSupervisor(WorkerRole.MANAGER);
        manager = supervisorFacade.saveSupervisor(manager);

        Employee e1 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e2 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e3 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        e1 = employeeFacade.saveEmployee(e1);
        e2 = employeeFacade.saveEmployee(e2);
        e3 = employeeFacade.saveEmployee(e3);

        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e1.getWorkerId(), 3500.0);
        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e2.getWorkerId(), 5500.0);
        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e3.getWorkerId(), 2500.0);

        List<Worker> asc = (List<Worker>)
                supervisorFacade.findWorkersByWorkerRoleAscByMonthlySalary(manager.getWorkerId(), WorkerRole.JUNIOR);

        assertEquals(3, asc.size());
        assertTrue(asc.get(0).getMonthlySalary() <= asc.get(1).getMonthlySalary());

        List<Worker> desc = (List<Worker>)
                supervisorFacade.findWorkersByWorkerRoleDescByMonthlySalary(manager.getWorkerId(), WorkerRole.JUNIOR);

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
        manager = supervisorFacade.saveSupervisor(manager);

        Employee e1 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e2 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);
        Employee e3 = employeeFacade.createEmployee("Prova", "Prova", WorkerRole.JUNIOR.getMonthlySalary(), WorkerRole.JUNIOR);

        e1 = employeeFacade.saveEmployee(e1);
        e2 = employeeFacade.saveEmployee(e2);
        e3 = employeeFacade.saveEmployee(e3);

        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e1.getWorkerId(), 3000.0);
        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e2.getWorkerId(), 6000.0);
        supervisorFacade.updateMonthlySalaryById(manager.getWorkerId(), e3.getWorkerId(), 4000.0);

        List<Employee> asc = (List<Employee>)
                supervisorFacade.findWorkersByMonthlySalaryAscByWorkerRole(manager.getWorkerId(), 3000.0);

        assertFalse(asc.isEmpty());
        for (int i = 1; i < asc.size(); i++) {
            assertTrue(
                    asc.get(i - 1).getWorkerRole().compareTo(asc.get(i).getWorkerRole()) <= 0
            );
        }

        List<Employee> desc = (List<Employee>)
                supervisorFacade.findWorkersByMonthlySalaryDescByWorkerRole(manager.getWorkerId(), 3000.0);

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

        boss = supervisorFacade.saveSupervisor(boss);
        sub = supervisorFacade.saveSupervisor(sub);

        supervisorFacade.assignSubordinate(boss.getWorkerId(), sub.getWorkerId());

        Optional<Supervisor> optionalSupervisor = supervisorFacade.findSupervisorById(sub.getWorkerId());
        assertTrue(optionalSupervisor.isPresent());
        Supervisor subCheck = optionalSupervisor.get();
        Optional<Supervisor> optionalBoss = supervisorFacade.findSupervisorById(boss.getWorkerId());
        assertTrue(optionalBoss.isPresent());
        Supervisor bossCheck = optionalBoss.get();

        assertTrue(bossCheck.getSubordinates().contains(subCheck));
        assertEquals(subCheck.getSupervisor(), bossCheck);

        supervisorFacade.removeSubordinate(boss.getWorkerId(), sub.getWorkerId());

        assertFalse(bossCheck.getSubordinates().contains(subCheck));
        assertNull(subCheck.getSupervisor());

        Supervisor finalBoss = boss;
        assertThrows(IllegalStateException.class,
                () -> supervisorFacade.assignSubordinate(finalBoss.getWorkerId(), finalBoss.getWorkerId()));
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

        a = supervisorFacade.saveSupervisor(a);
        b = supervisorFacade.saveSupervisor(b);
        c = supervisorFacade.saveSupervisor(c);

        supervisorFacade.assignSubordinate(a.getWorkerId(), b.getWorkerId());
        supervisorFacade.assignSubordinate(b.getWorkerId(), c.getWorkerId());

        Supervisor finalC = c;
        Supervisor finalA = a;
        assertThrows(IllegalStateException.class,
                () -> supervisorFacade.assignSubordinate(finalC.getWorkerId(), finalA.getWorkerId()));

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

        root = supervisorFacade.saveSupervisor(root);
        child = supervisorFacade.saveSupervisor(child);

        supervisorFacade.assignSubordinate(root.getWorkerId(), child.getWorkerId());

        List<Supervisor> roots = supervisorFacade.findSupervisorsWithoutSupervisor();
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

        sub = supervisorFacade.saveSupervisor(sub);
        supervisor = supervisorFacade.saveSupervisor(supervisor);
        supervisor2 = supervisorFacade.saveSupervisor(supervisor2);

        supervisorFacade.assignSubordinate(supervisor.getWorkerId(), sub.getWorkerId());
        // sub2 is not assigned -> should appear in "without subordinates"
        List<Supervisor> withoutSubordinates = supervisorFacade.findSupervisorsWithoutSubordinates();

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
        Team team = teamFacade.createTeam(sup1);
        sup1.addSupervisedTeam(team);      // assign team before saving
        sup1 = supervisorFacade.saveSupervisor(sup1);
        team = teamFacade.saveTeam(team);

        // Create supervisor without team
        Supervisor sup2 = supervisorFacade.saveSupervisor(createSupervisor());

        // Fetch supervisors without teams
        List<Supervisor> withoutTeam = supervisorFacade.findSupervisorsWithoutSupervisedTeam();

        // Assertions
        assertTrue(withoutTeam.contains(sup2));
        Supervisor withTeam = teamFacade.getTeamById(team.getTeamId())
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
        Supervisor rootSupervisor = supervisorFacade.saveSupervisor(
                createSupervisor()
        );

        // Now create and save the teams
        Team team1 = teamFacade.saveTeam(teamFacade.createTeam(rootSupervisor));
        Team team2 = teamFacade.saveTeam(teamFacade.createTeam(rootSupervisor));
        // Create a subordinate supervisor that will be assigned under the new supervisor
        Supervisor subordinate = supervisorFacade.saveSupervisor(
                supervisorFacade.createSupervisor("Sub", "Sub", WorkerRole.MANAGER.getMonthlySalary(), WorkerRole.MANAGER)
        );

        // Create the supervisor under test, assigning superior, subordinates, and teams
        Supervisor newSupervisor = createSupervisor(
                rootSupervisor,
                List.of(subordinate)
        );

        newSupervisor = supervisorFacade.saveSupervisor(newSupervisor);

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

        s1 = supervisorFacade.saveSupervisor(s1);
        s2 = supervisorFacade.saveSupervisor(s2);
        s3 = supervisorFacade.saveSupervisor(s3);
        s4 = supervisorFacade.saveSupervisor(s4);

        supervisorFacade.assignSubordinate(s1.getWorkerId(), s2.getWorkerId());
        supervisorFacade.assignSubordinate(s2.getWorkerId(), s3.getWorkerId());
        supervisorFacade.assignSubordinate(s3.getWorkerId(), s4.getWorkerId());

        Supervisor finalS = s4;
        Supervisor finalS1 = s1;
        assertThrows(IllegalStateException.class,
                () -> supervisorFacade.assignSubordinate(finalS.getWorkerId(), finalS1.getWorkerId()));
    }
}
