package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.WorkerRole;
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
    private Employee createEmployee(WorkerRole role) {
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
        Employee d = createEmployee(WorkerRole.JUNIOR);
        d = facade.saveEmployee(d);

        assertTrue(facade.findEmployeeById(d.getWorkerId()).isPresent());
    }

    /**
     * Test finding an employee by email after creation.
     */
    @Transactional
    @Test
    void shouldFindEmployeeByEmail() {
        Employee d = createEmployee(WorkerRole.JUNIOR);
        d = facade.saveEmployee(d);

        assertEquals(facade.findEmployeeIdByEmail(d.getEmail()), Optional.of(d.getWorkerId()));
    }

    /**
     * Test retrieving all employees.
     */
    @Transactional
    @Test
    void shouldFindAllEmployees() {
        Employee d1 = createEmployee(WorkerRole.JUNIOR);
        Employee d2 = createEmployee(WorkerRole.SENIOR_SW_ENGINEER);
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
        Employee d1 = createEmployee(WorkerRole.JUNIOR);
        Employee d2 = createEmployee(WorkerRole.JUNIOR);
        facade.saveEmployee(d1);
        facade.saveEmployee(d2);

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2);
    }


    /**
     * Test that updating a non-existent employee throws {@link EntityNotFoundException}.
     */
    @Transactional
    @Test
    void shouldThrowIfEmployeeNotFound() {
        Employee manager = createEmployee(WorkerRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee finalManager = manager;
        assertThrows(EntityNotFoundException.class,
                () -> facade.updateWorkerRoleById(finalManager.getWorkerId(), 999L, WorkerRole.JUNIOR));
    }

    // ---------------- Employee Search & Sorting Tests ----------------

    // ---------------- Task Management Tests ----------------

    /**
     * Test retrieving tasks for an employee filtered by task state.
     */
    @Transactional
    @Test
    void testFindTaskByEmployeeAndState() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        t3.setTaskState(TaskState.STARTED);
        Task t4 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        t4.setTaskState(TaskState.STARTED);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getWorkerId());


        t4.setTaskState(TaskState.DONE);

        List<Task> openTasks = facade.findTasksByWorkerAndTaskState(e.getWorkerId(), TaskState.TO_BE_STARTED);
        assertEquals(2, openTasks.size());

        List<Task> startedTasks = facade.findTasksByWorkerAndTaskState(e.getWorkerId(), TaskState.STARTED);
        assertEquals(1, startedTasks.size());

        List<Task> endedTasks = facade.findTasksByWorkerAndTaskState(e.getWorkerId(), TaskState.DONE);
        assertEquals(1, endedTasks.size());
    }

    /**
     * Test retrieving tasks for an employee filtered by task state and starting date.
     */
    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateByStartDate() {
        // Create employee
        Employee e = createEmployee(WorkerRole.JUNIOR);
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
        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getWorkerId());

        // Query tasks with state TO_BE_STARTED and start date today
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByStartDate(e.getWorkerId(), TaskState.TO_BE_STARTED, today);

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
        Employee e = createEmployee(WorkerRole.JUNIOR);
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
        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        // Query tasks with state STARTED and end date today
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByEndDate(e.getWorkerId(), TaskState.STARTED, today);

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
        Employee e = createEmployee(WorkerRole.JUNIOR);
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
        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        // Query tasks
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByStartDateBetweenAndEndDateBetween(
                e.getWorkerId(),
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
        Employee e = createEmployee(WorkerRole.JUNIOR);
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
        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        // Query tasks by state TO_BE_STARTED ordered by start date DESC
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateOrderByStartDateDesc(
                e.getWorkerId(),
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
        Employee e = createEmployee(WorkerRole.JUNIOR);
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
        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        // Query tasks by state STARTED ordered by end date DESC
        List<Task> tasks = facade.findTasksByEmployeeByTaskStateOrderByEndDateDesc(
                e.getWorkerId(),
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
