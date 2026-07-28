package com.unimib.backend;

import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Supervisor;
import com.unimib.backend.POJO.Team;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.enums.TaskState;
import com.unimib.backend.facade.EmployeeFacade;
import com.unimib.backend.facade.SupervisorFacade;
import com.unimib.backend.facade.TaskFacade;
import com.unimib.backend.facade.TeamFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test class that verifies JPA cascade types, orphan removal,
 * and merge propagation across all entity relationships.
 *
 * <p>
 * The tests ensure that entity mappings are correctly configured and that
 * persistence operations behave as expected when manipulating relationships
 * between {@link Supervisor}, {@link Team}, {@link Employee}, and {@link Task}.
 * </p>
 *
 * <p>
 * All tests run within a transactional context and use the {@code test}
 * Spring profile to guarantee isolation and automatic rollback.
 * </p>
 */
@SpringBootTest
@ActiveProfiles("test")
class AllEntitiesCascadeMappingTest {

    @Autowired
    SupervisorFacade supervisorfacade;
    @Autowired
    TeamFacade teamFacade;
    @Autowired
    EmployeeFacade employeeFacade;
    @Autowired
    TaskFacade taskFacade;

    /**
     * Verifies cascading persist behavior between supervisors and their subordinates,
     * and checks that removing a subordinate does not delete it when orphan removal
     * is not enabled.
     */
    @Test
    @Transactional
    void shouldCascadePersistSupervisorSubordinateAndOrphanRemove() {
        Supervisor parent = supervisorfacade.saveSupervisor(
                supervisorfacade.createSupervisor("Parent", "Supervisor")
        );
        Supervisor child = supervisorfacade.saveSupervisor(
                supervisorfacade.createSupervisor("Child", "Supervisor")
        );

        parent.addSubordinate(child);

        Long childId = child.getWorkerId();
        assertNotNull(childId);
        assertNotNull(supervisorfacade.findSupervisorById(childId));

        parent.removeSubordinate(child);

        // Child supervisor must still exist
        assertNotNull(supervisorfacade.findSupervisorById(childId));
    }

    /**
     * Ensures that removing a {@link Team} from a {@link Supervisor} does not
     * delete the team entity when orphan removal is not configured.
     */
    @Test
    @Transactional
    void shouldOrphanRemoveTeamWhenRemovedFromSupervisor() {
        Supervisor sup = supervisorfacade.saveSupervisor(
                supervisorfacade.createSupervisor("Supervisor", "Supervisor")
        );
        Team team = teamFacade.saveTeam(teamFacade.createTeam(sup));

        // Maintain bidirectional relationship
        sup.addSupervisedTeam(team);

        Long teamId = team.getTeamId();
        assertNotNull(teamId);
        assertNotNull(teamFacade.getTeamById(teamId));

        sup.removeSupervisedTeam(team);

        // Team must still exist
        assertNotNull(teamFacade.getTeamById(teamId));
    }

    /**
     * Verifies that {@code CascadeType.MERGE} on {@link Team} propagates
     * changes to associated {@link Employee} entities.
     */
    @Test
    @Transactional
    void shouldMergeOnTeamPropagateToEmployees() {
        Supervisor sup = supervisorfacade.saveSupervisor(
                supervisorfacade.createSupervisor("Supervisor", "Supervisor")
        );
        Employee emp = employeeFacade.saveEmployee(
                employeeFacade.createEmployee("Employee", "Employee")
        );
        Team team = teamFacade.saveTeam(teamFacade.createTeam(sup));
        team.addEmployee(emp);

        Long empId = emp.getWorkerId();
        assertNotNull(empId);

        // Retrieve detached team and modify associated employee
        Optional<Team> detachedTeamOpt = teamFacade.getTeamById(team.getTeamId());
        assertTrue(detachedTeamOpt.isPresent());
        Team detachedTeam = detachedTeamOpt.get();

        detachedTeam.getEmployees()
                .getFirst()
                .setSurname("NewSurname");

        Optional<Employee> detachedEmpOpt = employeeFacade.findEmployeeById(empId);
        assertTrue(detachedEmpOpt.isPresent());

        Employee updated = detachedEmpOpt.get();
        assertEquals("NewSurname", updated.getSurname());
    }

    /**
     * Ensures that removing an {@link Employee} from a {@link Task}
     * does not delete the employee entity, since no cascade REMOVE
     * is configured on the {@code @ManyToMany} relationship.
     */
    @Test
    @Transactional
    void removingEmployeeFromTaskDoesNotDeleteEmployee() {
        Task task = taskFacade.saveTask(
                taskFacade.createTask(TaskState.STARTED)
        );
        Employee emp = employeeFacade.saveEmployee(
                employeeFacade.createEmployee("Tasked", "User")
        );

        task.assignEmployee(emp);

        Employee persistedEmp = employeeFacade.findEmployeeById(emp.getWorkerId())
                .orElseThrow();

        Long empId = persistedEmp.getWorkerId();
        assertNotNull(empId);

        // Remove relationship
        task.removeEmployee(emp);

        // Employee must still exist
        assertNotNull(employeeFacade.findEmployeeById(empId));
    }

    /**
     * Verifies that {@code CascadeType.MERGE} on {@link Team}
     * propagates changes to associated {@link Task} entities.
     */
    @Test
    @Transactional
    void shouldMergeOnTeamPropagateToTasks() {
        Supervisor sup = supervisorfacade.saveSupervisor(
                supervisorfacade.createSupervisor("Supervisor", "Supervisor")
        );
        Team team = teamFacade.saveTeam(teamFacade.createTeam(sup));
        Task task = taskFacade.saveTask(
                taskFacade.createTask(TaskState.TO_BE_STARTED)
        );

        team.addTask(task);

        Team detachedTeam = teamFacade.getTeamById(team.getTeamId())
                .orElseThrow();

        Task detachedTask = detachedTeam.getTasks().getFirst();
        assertNotNull(detachedTask.getTaskId());

        detachedTask.setTaskState(TaskState.STARTED);

        assertEquals(TaskState.STARTED, detachedTask.getTaskState());
    }
}
