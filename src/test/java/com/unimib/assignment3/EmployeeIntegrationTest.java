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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee e1 = createEmployee(EmployeeRole.JUNIOR);
        Employee e2 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER);
        Employee e3 = createEmployee(EmployeeRole.JUNIOR);

        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);
        e3 = facade.saveEmployee(e3);

        facade.updateMonthlySalaryById(manager.getPersonId(), e1.getPersonId(), 3500.0);
        facade.updateMonthlySalaryById(manager.getPersonId(), e2.getPersonId(), 5500.0);
        facade.updateMonthlySalaryById(manager.getPersonId(), e3.getPersonId(), 2500.0);

        List<Employee> asc =
                facade.findEmployeesByEmployeeRoleAscByMonthlySalary(manager.getPersonId(), EmployeeRole.JUNIOR);

        assertEquals(2, asc.size());
        assertTrue(asc.get(0).getMonthlySalary() <= asc.get(1).getMonthlySalary());

        List<Employee> desc =
                facade.findEmployeesByEmployeeRoleDescByMonthlySalary(manager.getPersonId(), EmployeeRole.JUNIOR);

        assertEquals(2, desc.size());
        assertTrue(desc.get(0).getMonthlySalary() >= desc.get(1).getMonthlySalary());
    }


    /**
     * Test sorting employees by role ascending and descending with salary as tie-breaker.
     */
    @Transactional
    @Test
    void shouldSortEmployeesByRoleAscAndDesc() {
        Employee manager = createEmployee(EmployeeRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee e1 = createEmployee(EmployeeRole.JUNIOR);
        Employee e2 = createEmployee(EmployeeRole.SENIOR_SW_ENGINEER);
        Employee e3 = createEmployee(EmployeeRole.JUNIOR);

        e1 = facade.saveEmployee(e1);
        e2 = facade.saveEmployee(e2);
        e3 = facade.saveEmployee(e3);

        facade.updateMonthlySalaryById(manager.getPersonId(), e1.getPersonId(), 3000.0);
        facade.updateMonthlySalaryById(manager.getPersonId(), e2.getPersonId(), 6000.0);
        facade.updateMonthlySalaryById(manager.getPersonId(), e3.getPersonId(), 4000.0);

        List<Employee> asc =
                facade.findEmployeesByMonthlySalaryAscByEmployeeRole(manager.getPersonId(), 3000.0);

        assertFalse(asc.isEmpty());
        for (int i = 1; i < asc.size(); i++) {
            assertTrue(
                    asc.get(i - 1).getEmployeeRole().compareTo(asc.get(i).getEmployeeRole()) <= 0
            );
        }

        List<Employee> desc =
                facade.findEmployeesByMonthlySalaryDescByEmployeeRole(manager.getPersonId(), 3000.0);

        assertFalse(desc.isEmpty());
        for (int i = 1; i < desc.size(); i++) {
            assertTrue(
                    desc.get(i - 1).getEmployeeRole().compareTo(desc.get(i).getEmployeeRole()) >= 0
            );
        }
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

    /**
     * Test retrieving tasks for an employee filtered by task state and starting date.
     */
    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateByStartDate() {
        // Create employee
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        // Create tasks
        Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.STARTED));
        Task t4 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        // Set start dates
        LocalDate today = LocalDate.now();
        t1.setStartDate(today);
        t2.setStartDate(today.minusDays(1));
        t3.setStartDate(today);
        t4.setStartDate(today);

        // Save updated tasks
        t1 = facade.saveTask(t1);
        t2 = facade.saveTask(t2);
        t3 = facade.saveTask(t3);
        t4 = facade.saveTask(t4);

        // Assign tasks to employee
        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getPersonId());

        // Query tasks with state TO_BE_STARTED and start date today
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByStartDate(e.getPersonId(), TaskState.TO_BE_STARTED, today);

        // Assertions
        assertEquals(2, tasks.size()); // t1 and t4 match
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.TO_BE_STARTED));
        assertTrue(tasks.stream().allMatch(t -> t.getStartDate().equals(today)));
    }

    /**
     * Test retrieving tasks for an employee filtered by task state and ending date.
     */
    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateByEndDate() {
        // Create employee
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        // Create tasks
        Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        // Set end dates
        LocalDate today = LocalDate.now();
        t1.setEndDate(today);
        t2.setEndDate(today);
        t3.setEndDate(today);

        // Save updated tasks
        t1 = facade.saveTask(t1);
        t2 = facade.saveTask(t2);
        t3 = facade.saveTask(t3);

        // Assign tasks to employee
        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());

        // Query tasks with state STARTED and end date today
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByEndDate(e.getPersonId(), TaskState.STARTED, today);

        // Assertions
        assertEquals(2, tasks.size()); // Only t1 matches
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.STARTED));
        assertTrue(tasks.stream().allMatch(t -> t.getEndDate().equals(today)));
    }

    /**
     * Test retrieving tasks for an employee filtered by task state and between a starting and ending dates.
     */
    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateBetweenStartAndEndDates() {
        // Create employee
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        // Create tasks
        Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.STARTED));

        // Set start and end dates
        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(5);

        t1.setStartDate(start.plusDays(1));
        t1.setEndDate(start.plusDays(3));

        t2.setStartDate(start.minusDays(1)); // Outside range
        t2.setEndDate(start.plusDays(2));

        t3.setStartDate(start.plusDays(2));
        t3.setEndDate(start.plusDays(4));

        // Save updated tasks
        t1 = facade.saveTask(t1);
        t2 = facade.saveTask(t2);
        t3 = facade.saveTask(t3);

        // Assign tasks
        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());

        // Query tasks
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByStartDateBetweenAndEndDateBetween(
                e.getPersonId(),
                TaskState.TO_BE_STARTED,
                start,
                end
        );

        // Assertions
        assertEquals(2, tasks.size()); // Only t1 matches both state and date ranges
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.TO_BE_STARTED));
    }

    /**
     * Test retrieving tasks for an employee filtered by task state ordered by starting date descending
     */
    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateOrderByStartDateDesc() {
        // Create employee
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        // Create tasks
        Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.STARTED));

        LocalDate today = LocalDate.now();

        t1.setStartDate(today.minusDays(2));
        t2.setStartDate(today);
        t3.setStartDate(today.minusDays(1));

        t1 = facade.saveTask(t1);
        t2 = facade.saveTask(t2);
        t3 = facade.saveTask(t3);

        // Assign tasks
        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());

        // Query tasks by state TO_BE_STARTED ordered by start date DESC
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateOrderByStartDateDesc(
                e.getPersonId(),
                TaskState.TO_BE_STARTED
        );

        // Assertions
        assertEquals(2, tasks.size()); // t1 and t2 match state
        assertTrue(tasks.get(0).getStartDate().isAfter(tasks.get(1).getStartDate()) ||
                tasks.get(0).getStartDate().isEqual(tasks.get(1).getStartDate()));
    }

    /**
     * Test retrieving tasks for an employee filtered by task state ordered by ending date descending
     */
    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateOrderByEndDateDesc() {
        // Create an employee
        Employee e = createEmployee(EmployeeRole.JUNIOR);
        e = facade.saveEmployee(e);

        // Create tasks
        Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

        LocalDate today = LocalDate.now();

        // Set end dates
        t1.setEndDate(today);   // earliest
        t2.setEndDate(today);                 // latest
        t3.setEndDate(today);    // irrelevant state

        // Save updated tasks
        t1 = facade.saveTask(t1);
        t2 = facade.saveTask(t2);
        t3 = facade.saveTask(t3);

        // Assign tasks to employee
        facade.assignEmployeeToTask(t1.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getPersonId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getPersonId());

        // Query tasks by state STARTED ordered by end date DESC
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateOrderByEndDateDesc(
                e.getPersonId(),
                TaskState.STARTED // can be any date; method filters by state and relevant end date
        );

        // Assertions
        assertEquals(2, tasks.size());  // t1 and t2 match STARTED
        assertTrue(tasks.get(0).getEndDate().isAfter(tasks.get(1).getEndDate()) ||
                tasks.get(0).getEndDate().isEqual(tasks.get(1).getEndDate()));

        // Ensure all returned tasks have the correct state
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.STARTED));
    }

}
