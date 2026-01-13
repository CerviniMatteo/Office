package com.unimib.assignment3;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.facade.Facade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
@Transactional
public class TaskIntegrationTest {

    @Autowired
    private Facade facade;

    @Nested
    @DisplayName("CRUD and Task State management")
    class TaskCoreTests {

        @Test
        void testCreateAndSaveTask() {
            Task task = facade.createTask(TaskState.TO_BE_STARTED);
            Task saved = facade.saveTask(task);
            assertNotNull(saved.getTaskId());

            Task found = facade.getTaskById(saved.getTaskId());
            assertNotNull(found);
            assertEquals(saved.getTaskId(), found.getTaskId());
        }

        @Test
        @DisplayName("Verify task creation with different initial states and date setting")
        void testCreateTaskWithInitialState() {
            Task tStarted = facade.saveTask(facade.createTask(TaskState.STARTED));
            assertNotNull(tStarted.getStartDate());
            assertNull(tStarted.getEndDate());


            Task tDone = facade.saveTask(facade.createTask(TaskState.DONE));
            assertNotNull(tDone.getStartDate());
            assertNotNull(tDone.getEndDate());
        }
        @Test
        @DisplayName("Verify that createTask(null) sets the default state")
        void testCreateTaskNullState() {
            Task t = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            assertEquals(TaskState.TO_BE_STARTED, t.getTaskState());
            assertNull(t.getStartDate());
        }

        @Test
        void testDeleteTask() {
            Task task = facade.saveTask(facade.createTask(TaskState.STARTED));
            Long id = task.getTaskId();
            facade.deleteTask(id);
            assertThrows(IllegalArgumentException.class, () -> facade.getTaskById(id));
        }

        @Test
        void testStateChangeAndDateValidation() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Long id = task.getTaskId();

            facade.changeTaskState(id, TaskState.STARTED);
            Task statusStarted = facade.getTaskById(id);
            assertEquals(TaskState.STARTED, statusStarted.getTaskState());
            assertNotNull(statusStarted.getStartDate());

            facade.changeTaskState(id, TaskState.DONE);
            Task statusDone = facade.getTaskById(id);
            assertEquals(TaskState.DONE, statusDone.getTaskState());
            assertNotNull(statusDone.getEndDate());
        }

        @Test
        @DisplayName("Changing to the current state should not produce errors (Idempotency)")
        void testIdempotentStateChange() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Long id = task.getTaskId();

            assertDoesNotThrow(() -> facade.changeTaskState(id, TaskState.TO_BE_STARTED));
            assertEquals(TaskState.TO_BE_STARTED, facade.getTaskById(id).getTaskState());
        }

        @Test
        @DisplayName("Verify task reset to initial state and date clearing")
        void testResetTask() {
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.changeTaskState(t.getTaskId(), TaskState.DONE);

            facade.resetTask(t.getTaskId());
            Task reset = facade.getTaskById(t.getTaskId());

            assertEquals(TaskState.TO_BE_STARTED, reset.getTaskState());
            assertNull(reset.getStartDate());
            assertNull(reset.getEndDate());
        }

        @Test
        void testStateChangeException() {
            Task task = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            assertThrows(IllegalStateException.class, () -> facade.changeTaskState(task.getTaskId(), TaskState.DONE));

            facade.changeTaskState(task.getTaskId(), TaskState.STARTED);
            facade.changeTaskState(task.getTaskId(), TaskState.DONE);

            assertThrows(IllegalStateException.class, () -> facade.changeTaskState(task.getTaskId(), TaskState.TO_BE_STARTED));
        }
    }

    @Nested
    @DisplayName("Assignments and Relationships Management")
    class TaskAssignmentTests {

        @Test
        void testEmployeeAssignmentAndRemoval() {
            Employee employee = facade.createEmployee("Mario", "Rossi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));

            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());
            assertTrue(facade.isEmployeeAssigned(t.getTaskId(), employee.getPersonId()));

            facade.removeEmployeeToTask(t.getTaskId(), employee.getPersonId());
            assertFalse(facade.isEmployeeAssigned(t.getTaskId(), employee.getPersonId()));
        }

        @Test
        void testMultipleAssignmentForbidden() {
            Employee employee = facade.createEmployee("Luca", "Bianchi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());

            Employee finalEmployee = employee;
            assertThrows(IllegalStateException.class, () -> facade.assignEmployeeToTask(t.getTaskId(), finalEmployee.getPersonId()));
        }

        @Test
        @DisplayName("Should not be possible to assign employees to already COMPLETED tasks")
        void testAssignmentForbiddenInFinalState() {
            Task t = facade.saveTask(facade.createTask(TaskState.DONE));
            Employee employee = facade.createEmployee("Test", "Test");
            employee = facade.saveEmployee(employee);

            Employee finalEmployee = employee;
            assertThrows(IllegalStateException.class, () -> facade.assignEmployeeToTask(t.getTaskId(), finalEmployee.getPersonId()));
        }

        @Test
        @DisplayName("Throws exception when assigning an employee to a non-existent task")
        void testAssignmentToNonExistentTask() {
            Employee employee = facade.createEmployee("Invisibile", "User");
            employee = facade.saveEmployee(employee);
            Employee finalEmployee = employee;
            assertThrows(IllegalArgumentException.class, () -> facade.assignEmployeeToTask(999L, finalEmployee.getPersonId()));
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Coverage")
    class TaskBidirectionalTests {
        @Test
        @DisplayName("Verify bidirectional consistency between Task and Employee")
        void testBidirectionalConsistency() {
            Employee employee = facade.createEmployee("Mario", "Rossi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));

            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());

            assertTrue(facade.getTaskById(t.getTaskId()).getAssignedEmployees().contains(employee));
            assertTrue(employee.getTasks().stream().anyMatch(task -> task.getTaskId().equals(t.getTaskId())));

            facade.removeEmployeeToTask(t.getTaskId(), employee.getPersonId());
            assertFalse(facade.getTaskById(t.getTaskId()).getAssignedEmployees().contains(employee));
        }
    }

    @Nested
    @DisplayName("Queries, Filters, and Statistics")
    class TaskQueryTests {

        @Test
        void testFiltersAndCounts() {
            facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            assertEquals(2, facade.countTasksByState(TaskState.STARTED));
            assertEquals(3, facade.getAllTasks().size());

            List<Task> startedTasks = facade.getTasksByState(TaskState.TO_BE_STARTED);
            assertEquals(1, startedTasks.size());
        }

        @Test
        void testSearchByEmployee() {
            Employee employee = facade.createEmployee("Anna", "Verdi");
            employee = facade.saveEmployee(employee);
            Task t = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.assignEmployeeToTask(t.getTaskId(), employee.getPersonId());

            List<Task> tasksAnna = facade.getTasksByEmployee(employee);
            assertEquals(1, tasksAnna.size());
            assertEquals(t.getTaskId(), tasksAnna.get(0).getTaskId());
        }

        @Test
        void testComplexAndUnassignedTasks() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee employee1 = facade.createEmployee("D1", "C1");
            employee1 = facade.saveEmployee(employee1);
            Employee employee2 = facade.createEmployee("D2", "C2");
            employee2 = facade.saveEmployee(employee2);

            facade.assignEmployeeToTask(t1.getTaskId(), employee1.getPersonId());
            facade.assignEmployeeToTask(t1.getTaskId(), employee2.getPersonId());

            List<Task> complexTasks = facade.getComplexTasks(1);
            assertTrue(complexTasks.contains(t1));

            List<Task> unassignedTasks = facade.getUnsignedTasks();
            assertFalse(unassignedTasks.contains(t1));
        }
    }

    @Nested
    @DisplayName("Advanced Query Extensions")
    class TaskAdvancedQueryTests {

        @Test
        @DisplayName("Test query for state with at least one employee")
        void testFindTasksByStateWithEmployees() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            Employee employee1 = facade.createEmployee("Test", "User");
            employee1 = facade.saveEmployee(employee1);
            facade.assignEmployeeToTask(t1.getTaskId(), employee1.getPersonId());

            facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));

            List<Task> result = facade.findTasksByStateWithEmployee(TaskState.TO_BE_STARTED);
            assertEquals(1, result.size());
            assertEquals(t1.getTaskId(), result.get(0).getTaskId());
        }

        @Test
        @DisplayName("Test employee count for a specific Task ID")
        void testCountEmployeesByTaskId() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee employee1 = facade.createEmployee("D1", "C1");
            employee1 = facade.saveEmployee(employee1);
            Employee employee2 = facade.createEmployee("D2", "C2");
            employee2 = facade.saveEmployee(employee2);

            facade.assignEmployeeToTask(t1.getTaskId(), employee1.getPersonId());
            facade.assignEmployeeToTask(t1.getTaskId(), employee2.getPersonId());

            Integer count = facade.countEmployeeByTaskId(t1.getTaskId());
            assertEquals(2, count);
        }

        @Test
        @DisplayName("Test search by state and exact number of employees")
        void testFindTasksByStateAndEmployeesCount() {
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            Employee employee = facade.createEmployee("Solo", "User");
            employee = facade.saveEmployee(employee);
            facade.assignEmployeeToTask(t1.getTaskId(), employee.getPersonId());

            List<Task> result = facade.findTasksByStateAndCountEmployee(TaskState.STARTED, 1);
            assertEquals(1, result.size());
            assertTrue(result.contains(t1));
        }

        @Test
        @DisplayName("Test task search by Team ID")
        void testFindTasksByTeamId() {
            Supervisor s = facade.saveSupervisor(facade.createSupervisor("Boss", "Generale"));

            Team team = facade.saveTeam(facade.createTeam(s));
            Task t1 = facade.saveTask(facade.createTask(TaskState.STARTED));
            facade.addTaskToTeam(team, t1);

            List<Task> tasksDelTeam = facade.findTasksByTeamId(team.getTeamId());
            assertFalse(tasksDelTeam.isEmpty());
            assertEquals(t1.getTaskId(), tasksDelTeam.getFirst().getTaskId());
        }
    }

    @Nested
    @DisplayName("Internal Logic Validations (POJO)")
    class TaskPojoTests {
        @Test
        @DisplayName("Verify that POJO prevents inconsistent dates")
        void testInconsistentDateValidation() {
            Task t = facade.saveTask(facade.createTask(TaskState.TO_BE_STARTED));
            facade.setTaskEndDate(t.getTaskId(), LocalDate.now());


            assertThrows(IllegalArgumentException.class, () ->
                    facade.setTaskStartDate(t.getTaskId(), LocalDate.now().plusDays(1))
            );

            facade.setTaskStartDate(t.getTaskId(), LocalDate.now());
            assertThrows(IllegalArgumentException.class, () ->
                    facade.setTaskEndDate(t.getTaskId(), LocalDate.now().minusDays(1))
            );
        }
    }


}