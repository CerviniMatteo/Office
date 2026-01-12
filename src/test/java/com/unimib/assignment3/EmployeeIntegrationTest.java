package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Employee and Task management functionality.
 * <p>
 * Tests include:
 * - Creating, finding, updating, and deleting employees.
 * - Role-based authorization checks (Manager vs Non-Manager).
 * - Sorting employees by salary or role.
 * - Finding tasks by employee and task state.
 */
@SpringBootTest
@ActiveProfiles("test")
class EmployeeIntegrationTest {

    @Autowired
    private Facade facade;

    /**
     * Helper method to create an employee with a default salary from the role.
     *
     * @param role the role of the employee
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

    /**
     * Helper method to create an employee with a custom salary.
     *
     * @param role   the role of the employee
     * @param salary the custom monthly salary
     * @return the saved employee
     */
    private Employee createEmployee(EmployeeRole role, Double salary) {
        return facade.createEmployee(
            "Employee",
            "Employee",
            salary,
            role
        );
    }

    /**
     * Test that an employee can be found by ID after creation.
     */
    @Transactional
    @Test
    void shouldFindEmployeeById() {
        Employee d = createEmployee(EmployeeRole.JUNIOR);
        d = facade.saveEmployee(d);

        assertTrue(facade.findEmployeeById(d.getPersonId()).isPresent());
    }

    /**
     * Test that all employees can be retrieved.
     */
    @Transactional
    @Test
    void shouldFindAllEmployees() {
        Employee d1 = createEmployee(EmployeeRole.JUNIOR);
        Employee d2 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER);
        facade.saveEmployee(d1);
        facade.saveEmployee(d2);

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2); // at least 2 employees exist
    }

    /**
     * Test employee creation.
     */
    @Transactional
    @Test
    void employeeCreation() {
        Employee d1 = createEmployee(EmployeeRole.JUNIOR);
        Employee d2 = createEmployee(EmployeeRole.JUNIOR);
        facade.saveEmployee(d1);
        facade.saveEmployee(d2);

        System.out.println(d1);
        System.out.println(d2);

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2); // at least 2 employees exist
    }

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
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeeById(finalEmployee.getPersonId())
        );
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
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeeById(finalE.getPersonId())
        );
        Employee finalE1 = e2;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeeById(finalE1.getPersonId())
        );
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

        facade.updateMonthlySalaryById(
                manager.getPersonId(),
                employee.getPersonId(),
                5000.0
        );

        Employee updated = facade.findEmployeeById(employee.getPersonId()).get();
        assertEquals(5000.0, updated.getMonthlySalary());
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

        facade.updateEmployeeRoleById(
                manager.getPersonId(),
                employee.getPersonId(),
                EmployeeRole.SENIOR_SW_ENGINEER
        );

        Employee updated = facade.findEmployeeById(employee.getPersonId()).get();
        assertEquals(EmployeeRole.SENIOR_SW_ENGINEER, updated.getEmployeeRole());
    }

    /**
     * Test that a non-manager cannot update salary.
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
                () -> facade.updateMonthlySalaryById(
                        finalNonManager.getPersonId(),
                        finalEmployee.getPersonId(),
                        4000.0
                ));
    }

    /**
     * Test that updating a non-existent employee throws an exception.
     */
    @Transactional
    @Test
    void shouldThrowIfEmployeeNotFound() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee finalManager = manager;
        assertThrows(IllegalArgumentException.class,
                () -> facade.updateEmployeeRoleById(
                        finalManager.getPersonId(),
                        999L,
                        EmployeeRole.JUNIOR
                ));
    }

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
        assertTrue(foundSalary.stream().allMatch(d -> d.getMonthlySalary().equals(3100.0)));
        assertTrue(foundRole.stream().allMatch(d -> d.getEmployeeRole().equals(EmployeeRole.MANAGER)));
    }

    /**
     * Test that a non-manager cannot search employees by salary.
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
     * Test sorting employees by salary (ascending and descending).
     */
    @Transactional
    @Test
    void shouldSortEmployeesByMonthlySalaryAscAndDesc() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee e1 = createEmployee(EmployeeRole.JUNIOR, 1500.0);
        e1 = facade.saveEmployee(e1);

        Employee e2 = createEmployee(EmployeeRole.SENIOR, 1500.0);
        e2 = facade.saveEmployee(e2);

        Employee e3 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER, 1500.0);
        e3 = facade.saveEmployee(e3);

        // Ensure salaries are updated through service
        facade.updateMonthlySalaryById(manager.getPersonId(), e1.getPersonId(), e1.getMonthlySalary());
        facade.updateMonthlySalaryById(manager.getPersonId(), e2.getPersonId(), e2.getMonthlySalary());
        facade.updateMonthlySalaryById(manager.getPersonId(), e3.getPersonId(), e3.getMonthlySalary());

        // Ascending order by role
        List<Employee> asc = facade.findEmployeesByMonthlySalaryAscByEmployeeRole(manager.getPersonId(), 1500.0);
        List<EmployeeRole> ascSalaries = asc.stream().map(Employee::getEmployeeRole).toList();
        assertEquals(List.of(EmployeeRole.JUNIOR, EmployeeRole.SENIOR, EmployeeRole.SENIOR_SW_ENGINEER), ascSalaries);

        // Descending order by role
        List<Employee> desc = facade.findEmployeesByMonthlySalaryDescByEmployeeRole(manager.getPersonId(), 1500.0);
        List<EmployeeRole> descSalaries = desc.stream().map(Employee::getEmployeeRole).toList();
        assertEquals(List.of(EmployeeRole.SENIOR_SW_ENGINEER, EmployeeRole.SENIOR, EmployeeRole.JUNIOR), descSalaries);
    }

    /**
     * Test sorting employees by role (ascending and descending), with salary tie-breaker.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByRoleAscAndDesc() {
        Employee manager = createEmployee(EmployeeRole.MANAGER, 5000.0);
        manager = facade.saveEmployee(manager);

        Employee e1 = createEmployee(EmployeeRole.JUNIOR, 2000.0);
        facade.saveEmployee(e1);

        Employee e2 = createEmployee(EmployeeRole.JUNIOR, 3000.0);
        facade.saveEmployee(e2);

        Employee e3 = createEmployee(EmployeeRole.JUNIOR, 6000.0);
        facade.saveEmployee(e3);

        Employee e4 = createEmployee(EmployeeRole.JUNIOR, 4000.0);
        facade.saveEmployee(e4);

        // Ascending role order (all same role -> sorted by salary)
        List<Employee> roleAsc = facade.findEmployeesByEmployeeRoleAscByMonthlySalary(manager.getPersonId(), EmployeeRole.JUNIOR);
        List<Double> roleAscSalaries = roleAsc.stream().map(Employee::getMonthlySalary).toList();
        assertFalse(roleAscSalaries.isEmpty());
        assertEquals(List.of(2000.0, 3000.0, 4000.0, 6000.0), roleAscSalaries);

        // Descending role order (all same role -> sorted by salary descending)
        List<Employee> roleDesc = facade.findEmployeesByEmployeeRoleDescByMonthlySalary(manager.getPersonId(), EmployeeRole.JUNIOR);
        List<Double> roleDescSalaries = roleDesc.stream().map(Employee::getMonthlySalary).toList();
        assertFalse(roleDescSalaries.isEmpty());
        assertEquals(List.of(6000.0, 4000.0, 3000.0, 2000.0), roleDescSalaries);
    }

    /**
     * Test retrieving tasks for an employee by task state.
     */
    @Transactional
    @Test
    void testFindTaskByDipendenteAndState() {
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        Task t1 = facade.saveTask(new Task());
        Task t2 = facade.saveTask(new Task());
        Task t3 = facade.saveTask(new Task());
        t3.setTaskState(TaskState.TO_BE_STARTED); // started
        Task t4 = facade.saveTask(new Task());
        t4.setTaskState(TaskState.TO_BE_STARTED);

        // Assign tasks to employee
        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getPersonId());

        t4.setTaskState(TaskState.DONE); // finished

        // Retrieve tasks by state
        List<Task> openTasks = facade.findTasksByEmployeeAndTaskState(e.getPersonId(), TaskState.STARTED);
        assertEquals(2, openTasks.size());
        openTasks.forEach(t -> assertEquals(TaskState.STARTED, t.getTaskState()));

        List<Task> startedTasks = facade.findTasksByEmployeeAndTaskState(e.getPersonId(), TaskState.TO_BE_STARTED);
        assertEquals(1, startedTasks.size());
        assertEquals(TaskState.TO_BE_STARTED, startedTasks.getFirst().getTaskState());

        List<Task> endedTasks = facade.findTasksByEmployeeAndTaskState(e.getPersonId(), TaskState.DONE);
        assertEquals(1, endedTasks.size());
        assertEquals(TaskState.DONE, endedTasks.getFirst().getTaskState());
    }

}
