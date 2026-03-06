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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class EmployeeIntegrationTest {

    @Autowired
    private Facade facade;

    // ---------------- Helper Methods ----------------
    private Employee createEmployee(WorkerRole role) {
        return facade.createEmployee(
                "Employee",
                "Employee",
                role.getMonthlySalary(),
                role
        );
    }

    private Task createTask(TaskState state, LocalDate date) {
        Task task = facade.createTask(state);
        if (state != TaskState.TO_BE_STARTED && date != null) {
            task.setStartDate(LocalDateTime.of(date, LocalTime.of(9, 0)));
            task.setEndDate(LocalDateTime.of(date, LocalTime.of(18, 0)));
        }
        return task;
    }

    // ---------------- Employee CRUD Tests ----------------
    @Transactional
    @Test
    void shouldFindEmployeeById() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);
        assertTrue(facade.findEmployeeById(e.getWorkerId()).isPresent());
    }

    @Transactional
    @Test
    void shouldFindEmployeeByEmail() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);
        assertEquals(facade.findEmployeeIdByEmail(e.getEmail()), Optional.of(e.getWorkerId()));
    }

    @Transactional
    @Test
    void shouldFindAllEmployees() {
        Employee e1 = createEmployee(WorkerRole.JUNIOR);
        Employee e2 = createEmployee(WorkerRole.SENIOR_SW_ENGINEER);
        List<Employee> employees = facade.saveAllEmployees(List.of(e1, e2));

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2);
        assertTrue(all.containsAll(employees));
    }

    @Transactional
    @Test
    void employeeCreation() {
        Employee e1 = createEmployee(WorkerRole.JUNIOR);
        Employee e2 = createEmployee(WorkerRole.JUNIOR);
        facade.saveEmployee(e1);
        facade.saveEmployee(e2);

        List<Employee> all = facade.findAllEmployees();
        assertTrue(all.size() >= 2);
    }

    @Transactional
    @Test
    void shouldThrowIfEmployeeNotFound() {
        Employee manager = createEmployee(WorkerRole.MANAGER);
        manager = facade.saveEmployee(manager);

        Employee finalManager = manager;
        assertThrows(EntityNotFoundException.class,
                () -> facade.updateWorkerRoleById(finalManager.getWorkerId(), 999L, WorkerRole.JUNIOR));
    }

    // ---------------- Task Management Tests ----------------
    @Transactional
    @Test
    void testFindTaskByEmployeeAndState() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t2 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
        Task t3 = facade.saveTask(facade.createTask(TaskState.STARTED));
        Task t4 = facade.saveTask(facade.createTask(TaskState.DONE));

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getWorkerId());

        List<Task> openTasks = facade.findTasksByWorkerAndTaskState(e.getWorkerId(), TaskState.TO_BE_STARTED);
        assertEquals(2, openTasks.size());

        List<Task> startedTasks = facade.findTasksByWorkerAndTaskState(e.getWorkerId(), TaskState.STARTED);
        assertEquals(1, startedTasks.size());

        List<Task> doneTasks = facade.findTasksByWorkerAndTaskState(e.getWorkerId(), TaskState.DONE);
        assertEquals(1, doneTasks.size());
    }

    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateByStartDate() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        LocalDate today = LocalDate.now();

        Task t1 = createTask(TaskState.TO_BE_STARTED, today);
        Task t2 = createTask(TaskState.TO_BE_STARTED, today.minusDays(1));
        Task t3 = createTask(TaskState.STARTED, today);
        Task t4 = createTask(TaskState.TO_BE_STARTED, today);

        facade.saveTask(t1);
        facade.saveTask(t2);
        facade.saveTask(t3);
        facade.saveTask(t4);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t4.getTaskId(), e.getWorkerId());

        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByStartDate(e.getWorkerId(), TaskState.TO_BE_STARTED, today);

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.TO_BE_STARTED));
        assertTrue(tasks.stream().allMatch(t -> t.getStartDate().toLocalDate().equals(today)));
    }

    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateByEndDate() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        LocalDate today = LocalDate.now();

        Task t1 = createTask(TaskState.STARTED, today);
        Task t2 = createTask(TaskState.STARTED, today);
        Task t3 = createTask(TaskState.TO_BE_STARTED, today);

        facade.saveTask(t1);
        facade.saveTask(t2);
        facade.saveTask(t3);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByEndDate(e.getWorkerId(), TaskState.STARTED, today);

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.STARTED));
        assertTrue(tasks.stream().allMatch(t -> t.getEndDate().toLocalDate().equals(today)));
    }

    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateBetweenStartAndEndDates() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        LocalDate start = LocalDate.now();
        LocalDate end = start.plusDays(5);

        Task t1 = createTask(TaskState.TO_BE_STARTED, start.plusDays(1));
        Task t2 = createTask(TaskState.TO_BE_STARTED, start.minusDays(1));
        Task t3 = createTask(TaskState.TO_BE_STARTED, start.plusDays(2));

        t1.setEndDate(LocalDateTime.of(start.plusDays(3), LocalTime.of(18, 0)));
        t2.setEndDate(LocalDateTime.of(start.plusDays(2), LocalTime.of(18, 0)));
        t3.setEndDate(LocalDateTime.of(start.plusDays(4), LocalTime.of(18, 0)));

        facade.saveTask(t1);
        facade.saveTask(t2);
        facade.saveTask(t3);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        List<Task> tasks = facade.findTasksByEmployeeByTaskStateByStartDateBetweenAndEndDateBetween(
                e.getWorkerId(),
                TaskState.TO_BE_STARTED,
                start,
                end
        );

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.TO_BE_STARTED));
    }

    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateOrderByStartDateDesc() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        LocalDate today = LocalDate.now();

        Task t1 = createTask(TaskState.TO_BE_STARTED, today.minusDays(2));
        Task t2 = createTask(TaskState.TO_BE_STARTED, today);
        Task t3 = createTask(TaskState.STARTED, today.minusDays(1));

        facade.saveTask(t1);
        facade.saveTask(t2);
        facade.saveTask(t3);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        List<Task> tasks = facade.findTasksByEmployeeByTaskStateOrderByStartDateDesc(
                e.getWorkerId(),
                TaskState.TO_BE_STARTED
        );

        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).getStartDate().isAfter(tasks.get(1).getStartDate())
                || tasks.get(0).getStartDate().isEqual(tasks.get(1).getStartDate()));
    }

    @Transactional
    @Test
    void shouldFindTasksByEmployeeByTaskStateOrderByEndDateDesc() {
        Employee e = createEmployee(WorkerRole.JUNIOR);
        e = facade.saveEmployee(e);

        LocalDate today = LocalDate.now();

        Task t1 = createTask(TaskState.STARTED, today);
        Task t2 = createTask(TaskState.STARTED, today);
        Task t3 = createTask(TaskState.TO_BE_STARTED, today);

        t1.setEndDate(LocalDateTime.of(today, LocalTime.of(16, 0)));
        t2.setEndDate(LocalDateTime.of(today, LocalTime.of(18, 0)));
        t3.setEndDate(LocalDateTime.of(today, LocalTime.of(18, 0)));

        facade.saveTask(t1);
        facade.saveTask(t2);
        facade.saveTask(t3);

        facade.assignEmployeeToTask(t1.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t2.getTaskId(), e.getWorkerId());
        facade.assignEmployeeToTask(t3.getTaskId(), e.getWorkerId());

        List<Task> tasks = facade.findTasksByEmployeeByTaskStateOrderByEndDateDesc(
                e.getWorkerId(),
                TaskState.STARTED
        );

        assertEquals(2, tasks.size());
        assertTrue(tasks.get(0).getEndDate().isAfter(tasks.get(1).getEndDate())
                || tasks.get(0).getEndDate().isEqual(tasks.get(1).getEndDate()));
        assertTrue(tasks.stream().allMatch(t -> t.getTaskState() == TaskState.STARTED));
    }
}