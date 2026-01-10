package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Dipendente;
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

    private static long counter = 0;

    /**
     * Helper method to create an employee with a default salary from the role.
     *
     * @param role the role of the employee
     * @return the saved employee
     */
    private Dipendente createEmployee(EmployeeRole role) {
        counter++;
        return facade.createEmployee(
            "Employee" + counter,
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
    private Dipendente createEmployee(EmployeeRole role, Double salary) {
        counter++;
        return facade.createEmployee(
            "Employee" + counter,
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
        Dipendente d = createEmployee(EmployeeRole.JUNIOR);
        d = facade.saveEmployee(d);

        assertTrue(facade.findEmployeeById(d.getId()).isPresent());
    }

    /**
     * Test that all employees can be retrieved.
     */
    @Transactional
    @Test
    void shouldFindAllEmployees() {
        Dipendente d1 = createEmployee(EmployeeRole.JUNIOR);
        Dipendente d2 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER);
        facade.saveEmployee(d1);
        facade.saveEmployee(d2);

        List<Dipendente> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2); // at least 2 employees exist
    }

    /**
     * Test that a manager can delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldDeleteEmployeeByManager() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        employee = facade.saveEmployee(employee);

        facade.fireEmployee(manager.getId(), employee.getId());

        Dipendente finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeeById(finalEmployee.getId())
        );
    }

    /**
     * Test that a non-manager cannot delete/fire an employee.
     */
    @Transactional
    @Test
    void shouldThrowIfFireEmployeeByNonManager() {
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        nonManager = facade.saveEmployee(nonManager);
        employee = facade.saveEmployee(employee);

        Dipendente finalNonManager = nonManager;
        Dipendente finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> facade.fireEmployee(finalNonManager.getId(), finalEmployee.getId()));
    }

    /**
     * Test that a manager can fire multiple employees at once.
     */
    @Transactional
    @Test
    void shouldFireMultipleEmployees() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR);
        Dipendente e2 = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);

        facade.fireEmployees(manager.getId(), List.of(e1, e2));

        Dipendente finalE = e1;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeeById(finalE.getId())
        );
        Dipendente finalE1 = e2;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeeById(finalE1.getId())
        );
    }

    /**
     * Test updating an employee's monthly salary by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateMonthlySalary() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        employee = facade.saveEmployee(employee);

        facade.updateMonthlySalaryById(
                manager.getId(),
                employee.getId(),
                5000.0
        );

        Dipendente updated = facade.findEmployeeById(employee.getId()).get();
        assertEquals(5000.0, updated.getMonthlySalary());
    }

    /**
     * Test updating an employee's role by a manager.
     */
    @Transactional
    @Test
    void shouldUpdateEmployeeRole() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);

        manager = facade.saveEmployee(manager);
        employee = facade.saveEmployee(employee);

        facade.updateEmployeeRoleById(
                manager.getId(),
                employee.getId(),
                EmployeeRole.SENIOR_SW_ENGINEER
        );

        Dipendente updated = facade.findEmployeeById(employee.getId()).get();
        assertEquals(EmployeeRole.SENIOR_SW_ENGINEER, updated.getEmployeeRole());
    }

    /**
     * Test that a non-manager cannot update salary.
     */
    @Transactional
    @Test
    void shouldThrowIfUpdateByNonManager() {
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);
        nonManager = facade.saveEmployee(nonManager);

        Dipendente employee = createEmployee(EmployeeRole.JUNIOR);
        employee = facade.saveEmployee(employee);

        Dipendente finalNonManager = nonManager;
        Dipendente finalEmployee = employee;
        assertThrows(IllegalArgumentException.class,
                () -> facade.updateMonthlySalaryById(
                        finalNonManager.getId(),
                        finalEmployee.getId(),
                        4000.0
                ));
    }

    /**
     * Test that updating a non-existent employee throws an exception.
     */
    @Transactional
    @Test
    void shouldThrowIfEmployeeNotFound() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Dipendente finalManager = manager;
        assertThrows(IllegalArgumentException.class,
                () -> facade.updateEmployeeRoleById(
                        finalManager.getId(),
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
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR);
        e1 = facade.saveEmployee(e1);

        facade.updateMonthlySalaryById(manager.getId(), e1.getId(), 3100.0);

        Dipendente e2 = createEmployee(EmployeeRole.MANAGER);
        e2 = facade.saveEmployee(e2);

        facade.updateMonthlySalaryById(manager.getId(), e2.getId(), 3100.0);

        List<Dipendente> foundSalary = facade.findEmployeesByMonthlySalary(manager.getId(), 3100.0);
        List<Dipendente> foundRole = facade.findEmployeesByEmployeeRole(manager.getId(), EmployeeRole.MANAGER);

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
        Dipendente nonManager = createEmployee(EmployeeRole.JUNIOR);
        nonManager = facade.saveEmployee(nonManager);

        Dipendente finalNonManager = nonManager;
        assertThrows(IllegalArgumentException.class,
                () -> facade.findEmployeesByMonthlySalary(finalNonManager.getId(), 3000.0));
    }

    /**
     * Test sorting employees by salary (ascending and descending).
     */
    @Transactional
    @Test
    void shouldSortEmployeesByMonthlySalaryAscAndDesc() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR, 1500.0);
        e1 = facade.saveEmployee(e1);

        Dipendente e2 = createEmployee(EmployeeRole.SENIOR, 1500.0);
        e2 = facade.saveEmployee(e2);

        Dipendente e3 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER, 1500.0);
        e3 = facade.saveEmployee(e3);

        // Ensure salaries are updated through service
        facade.updateMonthlySalaryById(manager.getId(), e1.getId(), e1.getMonthlySalary());
        facade.updateMonthlySalaryById(manager.getId(), e2.getId(), e2.getMonthlySalary());
        facade.updateMonthlySalaryById(manager.getId(), e3.getId(), e3.getMonthlySalary());

        // Ascending order by role
        List<Dipendente> asc = facade.findEmployeesByMonthlySalaryAscByEmployeeRole(manager.getId(), 1500.0);
        List<EmployeeRole> ascSalaries = asc.stream().map(Dipendente::getEmployeeRole).toList();
        assertEquals(List.of(EmployeeRole.JUNIOR, EmployeeRole.SENIOR, EmployeeRole.SENIOR_SW_ENGINEER), ascSalaries);

        // Descending order by role
        List<Dipendente> desc = facade.findEmployeesByMonthlySalaryDescByEmployeeRole(manager.getId(), 1500.0);
        List<EmployeeRole> descSalaries = desc.stream().map(Dipendente::getEmployeeRole).toList();
        assertEquals(List.of(EmployeeRole.SENIOR_SW_ENGINEER, EmployeeRole.SENIOR, EmployeeRole.JUNIOR), descSalaries);
    }

    /**
     * Test sorting employees by role (ascending and descending), with salary tie-breaker.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByRoleAscAndDesc() {
        Dipendente manager = createEmployee(EmployeeRole.MANAGER, 5000.0);
        manager = facade.saveEmployee(manager);

        Dipendente e1 = createEmployee(EmployeeRole.JUNIOR, 2000.0);
        facade.saveEmployee(e1);

        Dipendente e2 = createEmployee(EmployeeRole.JUNIOR, 3000.0);facade.saveEmployee(e2);
        facade.saveEmployee(e2);

        Dipendente e3 = createEmployee(EmployeeRole.JUNIOR, 6000.0);
        facade.saveEmployee(e3);

        Dipendente e4 = createEmployee(EmployeeRole.JUNIOR, 4000.0);
        facade.saveEmployee(e4);

        // Ascending role order (all same role -> sorted by salary)
        List<Dipendente> roleAsc = facade.findEmployeesByEmployeeRoleAscByMonthlySalary(manager.getId(), EmployeeRole.JUNIOR);
        List<Double> roleAscSalaries = roleAsc.stream().map(Dipendente::getMonthlySalary).toList();
        assertFalse(roleAscSalaries.isEmpty());
        assertEquals(List.of(2000.0, 3000.0, 4000.0, 6000.0), roleAscSalaries);

        // Descending role order (all same role -> sorted by salary descending)
        List<Dipendente> roleDesc = facade.findEmployeesByEmployeeRoleDescByMonthlySalary(manager.getId(), EmployeeRole.JUNIOR);
        List<Double> roleDescSalaries = roleDesc.stream().map(Dipendente::getMonthlySalary).toList();
        assertFalse(roleDescSalaries.isEmpty());
        assertEquals(List.of(6000.0, 4000.0, 3000.0, 2000.0), roleDescSalaries);
    }

    /**
     * Test retrieving tasks for an employee by task state.
     */
    @Transactional
    @Test
    void testFindTaskByDipendenteAndState() {
        Dipendente e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        Task t1 = facade.saveTask(new Task());
        Task t2 = facade.saveTask(new Task());
        Task t3 = facade.saveTask(new Task());
        t3.setTaskState(TaskState.INIZIATO); // started
        Task t4 = facade.saveTask(new Task());
        t4.setTaskState(TaskState.INIZIATO);

        // Assign tasks to employee
        facade.assegnaDipendenteATask(t1.getIdTask(), e.getId());
        facade.assegnaDipendenteATask(t2.getIdTask(), e.getId());
        facade.assegnaDipendenteATask(t3.getIdTask(), e.getId());
        facade.assegnaDipendenteATask(t4.getIdTask(), e.getId());

        t4.setTaskState(TaskState.FINITO); // finished

        // Retrieve tasks by state
        List<Task> openTasks = facade.findTasksByEmployeeAndTaskState(e.getId(), TaskState.DAINIZIARE);
        assertEquals(2, openTasks.size());
        openTasks.forEach(t -> assertEquals(TaskState.DAINIZIARE, t.getTaskState()));

        List<Task> startedTasks = facade.findTasksByEmployeeAndTaskState(e.getId(), TaskState.INIZIATO);
        assertEquals(1, startedTasks.size());
        assertEquals(TaskState.INIZIATO, startedTasks.getFirst().getTaskState());

        List<Task> endedTasks = facade.findTasksByEmployeeAndTaskState(e.getId(), TaskState.FINITO);
        assertEquals(1, endedTasks.size());
        assertEquals(TaskState.FINITO, endedTasks.getFirst().getTaskState());
    }

}
