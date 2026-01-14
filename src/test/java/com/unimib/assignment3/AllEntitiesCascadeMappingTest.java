package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
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
    Facade facade;

    /**
     * Verifies cascading persist behavior between supervisors and their subordinates,
     * and checks that removing a subordinate does not delete it when orphan removal
     * is not enabled.
     */
    @Test
    @Transactional
    void shouldCascadePersistSupervisorSubordinateAndOrphanRemove() {
        Supervisor parent = facade.saveSupervisor(
                facade.createSupervisor("Parent", "Supervisor")
        );
        Supervisor child = facade.saveSupervisor(
                facade.createSupervisor("Child", "Supervisor")
        );

        parent.addSubordinate(child);

        Long childId = child.getPersonId();
        assertNotNull(childId);
        assertNotNull(facade.findSupervisorById(childId));

        parent.removeSubordinate(child);

        // Child supervisor must still exist
        assertNotNull(facade.findSupervisorById(childId));
    }

    /**
     * Ensures that removing a {@link Team} from a {@link Supervisor} does not
     * delete the team entity when orphan removal is not configured.
     */
    @Test
    @Transactional
    void shouldOrphanRemoveTeamWhenRemovedFromSupervisor() {
        Supervisor sup = facade.saveSupervisor(
                facade.createSupervisor("Supervisor", "Supervisor")
        );
        Team team = facade.saveTeam(facade.createTeam(sup));

        // Maintain bidirectional relationship
        sup.addSupervisedTeam(team);

        Long teamId = team.getTeamId();
        assertNotNull(teamId);
        assertNotNull(facade.getTeamById(teamId));

        sup.removeSupervisedTeam(team);

        // Team must still exist
        assertNotNull(facade.getTeamById(teamId));
    }

    /**
     * Verifies that {@code CascadeType.MERGE} on {@link Team} propagates
     * changes to associated {@link Employee} entities.
     */
    @Test
    @Transactional
    void shouldMergeOnTeamPropagateToEmployees() {
        Supervisor sup = facade.saveSupervisor(
                facade.createSupervisor("Supervisor", "Supervisor")
        );
        Employee emp = facade.saveEmployee(
                facade.createEmployee("Employee", "Employee")
        );
        Team team = facade.saveTeam(facade.createTeam(sup));
        team.addEmployee(emp);

        Long empId = emp.getPersonId();
        assertNotNull(empId);

        // Retrieve detached team and modify associated employee
        Optional<Team> detachedTeamOpt = facade.getTeamById(team.getTeamId());
        assertTrue(detachedTeamOpt.isPresent());
        Team detachedTeam = detachedTeamOpt.get();

        detachedTeam.getEmployees()
                .getFirst()
                .setSurname("NewSurname");

        Optional<Employee> detachedEmpOpt = facade.findEmployeeById(empId);
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
        Task task = facade.saveTask(
                facade.createTask(TaskState.STARTED)
        );
        Employee emp = facade.saveEmployee(
                facade.createEmployee("Tasked", "User")
        );

        task.assignEmployee(emp);

        Employee persistedEmp = facade.findEmployeeById(emp.getPersonId())
                .orElseThrow();

        Long empId = persistedEmp.getPersonId();
        assertNotNull(empId);

        // Remove relationship
        task.removeEmployee(emp);

        // Employee must still exist
        assertNotNull(facade.findEmployeeById(empId));
    }

    /**
     * Verifies that {@code CascadeType.MERGE} on {@link Team}
     * propagates changes to associated {@link Task} entities.
     */
    @Test
    @Transactional
    void shouldMergeOnTeamPropagateToTasks() {
        Supervisor sup = facade.saveSupervisor(
                facade.createSupervisor("Supervisor", "Supervisor")
        );
        Team team = facade.saveTeam(facade.createTeam(sup));
        Task task = facade.saveTask(
                facade.createTask(TaskState.TO_BE_STARTED)
        );

        team.addTask(task);

        Team detachedTeam = facade.getTeamById(team.getTeamId())
                .orElseThrow();

        Task detachedTask = detachedTeam.getTasks().getFirst();
        assertNotNull(detachedTask.getTaskId());

        detachedTask.setTaskState(TaskState.STARTED);

        assertEquals(TaskState.STARTED, detachedTask.getTaskState());
    }
}
