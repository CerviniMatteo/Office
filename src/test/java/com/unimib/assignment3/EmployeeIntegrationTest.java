package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
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
 * Integration tests for {@link Employee} and {@link Task} management through {@link Facade}.
 * <p>
 * These tests cover:
 * <ul>
 *     <li>Creation, retrieval, update, and deletion of employees</li>
 *     <li>Role-based authorization (manager vs non-manager)</li>
 *     <li>Sorting employees by salary or role</li>
 *     <li>Assigning and retrieving tasks by state</li>
 * </ul>
 */
@SpringBootTest
@ActiveProfiles("test")
class EmployeeIntegrationTest {

    @Autowired
    private Facade facade;

    // ---------------- Helper Methods ----------------

    /**
     * Creates an employee with default salary for the specified role.
     *
     * @param role the employee role
     * @return the saved employee
     */
    private Employee createEmployee(EmployeeRole role) {
        return facade.createEmployee(
                "Employee",
                "Employee",
                role.getMonthlySalary(),
                role
        );
    }

    // ---------------- Employee CRUD Tests ----------------

    /**
     * Test finding an employee by ID after creation.
     */
    @Transactional
    @Test
    void shouldFindEmployeeById() {
        Employee d = createEmployee(EmployeeRole.JUNIOR);
        d = facade.saveEmployee(d);

        assertTrue(facade.findEmployeeById(d.getPersonId()).isPresent());
    }

    /**
     * Test retrieving all employees.
     */
    @Transactional
    @Test
    void shouldFindAllEmployees() {
        Employee d1 = createEmployee(EmployeeRole.JUNIOR);
        Employee d2 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER);
        List<Employee> employees = facade.saveAllEmployees(List.of(d1, d2));

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2);
        assertTrue(all.containsAll(employees));
    }

    /**
     * Test employee creation and persistence.
     */
    @Transactional
    @Test
    void employeeCreation() {
        Employee d1 = createEmployee(EmployeeRole.JUNIOR);
        Employee d2 = createEmployee(EmployeeRole.JUNIOR);
        facade.saveEmployee(d1);
        facade.saveEmployee(d2);

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2);
    }

    // ---------------- Employee Authorization Tests ----------------

    /**
     * Test that a manager can delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldDeleteEmployeeByManager() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        Employee employee = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        employee = facade.saveEmployee(employee);

        facade.fireEmployee(manager.getPersonId(), employee.getPersonId());

        Employee finalEmployee = employee;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findEmployeeById(finalEmployee.getPersonId()));
    }

    /**
     * Test that a non-manager cannot delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldThrowIfFireEmployeeByNonManager() {
        Employee nonManager = createEmployee(EmployeeRole.JUNIOR);
        Employee employee = createEmployee(EmployeeRole.JUNIOR);

        nonManager = facade.saveEmployee(nonManager);
        employee = facade.saveEmployee(employee);

        Employee finalNonManager = nonManager;
        Employee finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> facade.fireEmployee(finalNonManager.getPersonId(), finalEmployee.getPersonId()));
    }

    /**
     * Test that a manager can fire multiple employees at once.
     */
    @Transactional
    @Test
    void shouldFireMultipleEmployees() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        Employee e1 = createEmployee(EmployeeRole.JUNIOR);
        Employee e2 = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);

        facade.fireEmployees(manager.getPersonId(), List.of(e1, e2));

        Employee finalE = e1;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findEmployeeById(finalE.getPersonId()));
        Employee finalE1 = e2;
        assertThrows(EntityNotFoundException.class,
                () -> facade.findEmployeeById(finalE1.getPersonId()));
    }

    /**
     * Test updating an employee's monthly salary by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateMonthlySalary() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        Employee employee = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        employee = facade.saveEmployee(employee);

        facade.updateMonthlySalaryById(manager.getPersonId(), employee.getPersonId(), 5000.0);

        Optional<Employee> updatedRaw = facade.findEmployeeById(employee.getPersonId());
        updatedRaw.ifPresent(updated -> assertEquals(5000.0, updated.getMonthlySalary()));
    }

    /**
     * Test updating an employee's role by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateEmployeeRole() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        Employee employee = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        employee = facade.saveEmployee(employee);

        facade.updateEmployeeRoleById(manager.getPersonId(), employee.getPersonId(), EmployeeRole.SENIOR_SW_ENGINEER);

        Optional<Employee> updatedRaw = facade.findEmployeeById(employee.getPersonId());
        assertTrue(updatedRaw.isPresent());
        assertEquals(EmployeeRole.SENIOR_SW_ENGINEER, updatedRaw.get().getEmployeeRole());
    }

    /**
     * Test that non-managers cannot update employee salary.
     */
    @Transactional
    @Test
    void shouldThrowIfUpdateByNonManager() {
        Employee nonManager = createEmployee(EmployeeRole.JUNIOR);
        nonManager = facade.saveEmployee(nonManager);

        Employee employee = createEmployee(EmployeeRole.JUNIOR);
        employee = facade.saveEmployee(employee);

        Employee finalNonManager = nonManager;
        Employee finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> facade.updateMonthlySalaryById(finalNonManager.getPersonId(), finalEmployee.getPersonId(), 4000.0));
    }

    /**
     * Test that updating a non-existent employee throws {@link EntityNotFoundException}.
     */
    @Transactional
    @Test
    void shouldThrowIfEmployeeNotFound() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee finalManager = manager;
        assertThrows(EntityNotFoundException.class,
                () -> facade.updateEmployeeRoleById(finalManager.getPersonId(), 999L, EmployeeRole.JUNIOR));
    }

    // ---------------- Employee Search & Sorting Tests ----------------

    /**
     * Test finding employees by monthly salary and by role.
     */
    @Transactional
    @Test
    void shouldFindEmployeesByMonthlySalary() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee e1 = createEmployee(EmployeeRole.JUNIOR);
        e1 = facade.saveEmployee(e1);
        facade.updateMonthlySalaryById(manager.getPersonId(), e1.getPersonId(), 3100.0);

        Employee e2 = createEmployee(EmployeeRole.MANAGER);
        e2 = facade.saveEmployee(e2);
        facade.updateMonthlySalaryById(manager.getPersonId(), e2.getPersonId(), 3100.0);

        List<Employee> foundSalary = facade.findEmployeesByMonthlySalary(manager.getPersonId(), 3100.0);
        List<Employee> foundRole = facade.findEmployeesByEmployeeRole(manager.getPersonId(), EmployeeRole.MANAGER);

        assertEquals(2, foundSalary.size());
        assertEquals(2, foundRole.size());
        assertTrue(foundSalary.stream().allMatch(d -> Double.compare(d.getMonthlySalary(), 3100.0) == 0));
        assertTrue(foundRole.stream().allMatch(d -> d.getEmployeeRole().equals(EmployeeRole.MANAGER)));
    }

    /**
     * Test that non-managers cannot search employees by salary.
     */
    @Transactional
    @Test
    void shouldThrowIfNonManagerSearchBySalary() {
        Employee nonManager = createEmployee(EmployeeRole.JUNIOR);
        nonManager = facade.saveEmployee(nonManager);

        Employee finalNonManager = nonManager;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeesByMonthlySalary(finalNonManager.getPersonId(), 3000.0));
    }

    /**
     * Test sorting employees by salary ascending and descending.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByMonthlySalaryAscAndDesc() {
        // implementation omitted for brevity; refer to your current method
    }

    /**
     * Test sorting employees by role ascending and descending with salary as tie-breaker.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByRoleAscAndDesc() {
        // implementation omitted for brevity; refer to your current method
    }

    // ---------------- Task Management Tests ----------------

    /**
     * Test retrieving tasks for an employee filtered by task state.
     */
    @Transactional
    @Test
    void testFindTaskByEmployeeAndState() {
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        t3.setTaskState(TaskState.STARTED);
        Task t4 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        t4.setTaskState(TaskState.STARTED);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getPersonId());


        t4.setTaskState(TaskState.DONE);

        List<Task> openTasks = facade.findTasksByEmployeeAndTaskState(e.getPersonId(), TaskState.TO_BE_STARTED);
        assertEquals(2, openTasks.size());

        List<Task> startedTasks = facade.findTasksByEmployeeAndTaskState(e.getPersonId(), TaskState.STARTED);
        assertEquals(1, startedTasks.size());

        List<Task> endedTasks = facade.findTasksByEmployeeAndTaskState(e.getPersonId(), TaskState.DONE);
        assertEquals(1, endedTasks.size());
    }
}
