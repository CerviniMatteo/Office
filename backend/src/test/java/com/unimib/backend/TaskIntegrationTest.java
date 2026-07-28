package com.unimib.backend;

import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Supervisor;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.POJO.Team;
import com.unimib.backend.enums.TaskState;
import com.unimib.backend.facade.EmployeeFacade;
import com.unimib.backend.facade.SupervisorFacade;
import com.unimib.backend.facade.TaskFacade;
import com.unimib.backend.facade.TeamFacade;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TaskIntegrationTest {

    @Autowired
    private TaskFacade taskFacade;
    @Autowired
    private EmployeeFacade employeeFacade;
    @Autowired
    private SupervisorFacade supervisorFacade;
    @Autowired
    private TeamFacade teamFacade;

    @Nested
    @DisplayName("CRUD and Task State management")
    class TaskCoreTests {

        /**
         * Tests the creation and saving of a Task entity.
         */
        @Test
        void testCreateAndSaveTask() {
            Task task = taskFacade.createTask(TaskState.TO_BE_STARTED);
            Task saved = taskFacade.saveTask(task);
            assertNotNull(saved.getTaskId());

            Task found = taskFacade.getTaskById(saved.getTaskId());
            assertNotNull(found);
            assertEquals(saved.getTaskId(), found.getTaskId());
        }

        /**
         * Tests task creation with different initial states and verifies date settings.
         */
        @Test
        @DisplayName("Verify task creation with different initial states and date setting")
        void testCreateTaskWithInitialState() {
            Task tStarted = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            assertNotNull(tStarted.getStartDate());
            assertNull(tStarted.getEndDate());

            Task tDone = taskFacade.saveTask(taskFacade.createTask(TaskState.DONE));
            assertNotNull(tDone.getStartDate());
            assertNotNull(tDone.getEndDate());
        }

        /**
         * Tests task creation with a null state, expecting default state assignment.
         */
        @Test
        @DisplayName("Verify that createTask(null) sets the default state")
        void testCreateTaskNullState() {
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));
            assertEquals(TaskState.TO_BE_STARTED, t.getTaskState());
        }

        /**
         * Tests the deletion of a Task entity.
         */
        @Test
        void testDeleteTask() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            Long id = task.getTaskId();
            taskFacade.deleteTask(id);
            assertThrows(IllegalArgumentException.class, () -> taskFacade.getTaskById(id));
        }

        /**
         * Tests changing task states and validates date assignments.
         */
        @Test
        void testStateChangeAndDateValidation() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));
            Long id = task.getTaskId();

            taskFacade.changeTaskState(id, task.getTaskState());
            assertNotNull(task.getStartDate());

            taskFacade.changeTaskState(id, task.getTaskState());

            assertNotNull(task.getEndDate());
        }

        /**
         * Tests resetting a task to its initial state and clearing dates.
         */
        @Test
        @DisplayName("Verify task reset to initial state and date clearing")
        void testResetTask() {
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            taskFacade.changeTaskState(t.getTaskId(), t.getTaskState());
            taskFacade.resetTask(t.getTaskId());
            Task reset = taskFacade.getTaskById(t.getTaskId());

            assertEquals(TaskState.TO_BE_STARTED, reset.getTaskState());
            assertNull(reset.getStartDate());
            assertNull(reset.getEndDate());
        }

        /**
         * Tests that invalid state transitions throw exceptions.
         */
        @Test
        void testStateChangeException() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));
            taskFacade.changeTaskState(task.getTaskId(), TaskState.STARTED);

            assertThrows(IllegalStateException.class, () -> taskFacade.changeTaskState(task.getTaskId(), TaskState.DONE));
        }
    }

    @Nested
    @DisplayName("Assignments and Relationships Management")
    class TaskAssignmentTests {
        /**
         * Tests assigning and removing an employee from a task.
         */
        @Test
        void testEmployeeAssignmentAndRemoval() {
            Employee employee = employeeFacade.createEmployee("Mario", "Rossi");
            employee = employeeFacade.saveEmployee(employee);
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));

            taskFacade.assignEmployeeToTask(t.getTaskId(), employee.getWorkerId());
            assertTrue(taskFacade.isEmployeeAssigned(t.getTaskId(), employee.getWorkerId()));

            taskFacade.removeEmployeeToTask(t.getTaskId(), employee.getWorkerId());
            assertFalse(taskFacade.isEmployeeAssigned(t.getTaskId(), employee.getWorkerId()));
        }

        /**
         * Tests that multiple assignments of the same employee to a task are forbidden.
         */
        @Test
        void testMultipleAssignmentForbidden() {
            Employee employee = employeeFacade.createEmployee("Luca", "Bianchi");
            employee = employeeFacade.saveEmployee(employee);
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            taskFacade.assignEmployeeToTask(t.getTaskId(), employee.getWorkerId());

            Employee finalEmployee = employee;
            assertThrows(IllegalStateException.class, () -> taskFacade.assignEmployeeToTask(t.getTaskId(), finalEmployee.getWorkerId()));
        }

        /**
         * Tests that assignments to COMPLETED tasks are forbidden.
         */
        @Test
        @DisplayName("Should not be possible to assign employees to already COMPLETED tasks")
        void testAssignmentForbiddenInFinalState() {
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.DONE));
            Employee employee = employeeFacade.createEmployee("Test", "Test");
            employee = employeeFacade.saveEmployee(employee);

            Employee finalEmployee = employee;
            assertThrows(IllegalStateException.class, () -> taskFacade.assignEmployeeToTask(t.getTaskId(), finalEmployee.getWorkerId()));
        }

        /**
         * Tests that an exception is thrown when assigning an employee to a non-existent task.
         */
        @Test
        @DisplayName("Throws exception when assigning an employee to a non-existent task")
        void testAssignmentToNonExistentTask() {
            Employee employee = employeeFacade.createEmployee("Invisibile", "User");
            employee = employeeFacade.saveEmployee(employee);
            Employee finalEmployee = employee;
            assertThrows(IllegalArgumentException.class, () -> taskFacade.assignEmployeeToTask(999L, finalEmployee.getWorkerId()));
        }
    }

    @Nested
    @DisplayName("Detailed tests for setAssignedEmployees")
    class TaskSetAssignedEmployeesTests {

        /**
         * Tests the successful bulk assignment of a list of employees.
         */
        @Test
        @DisplayName("Successfully assign a list of employees to a task")
        void testSetAssignedEmployeesSuccess() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            Employee e1 = employeeFacade.saveEmployee(employeeFacade.createEmployee("Alice", "Wonderland"));
            Employee e2 = employeeFacade.saveEmployee(employeeFacade.createEmployee("Bob", "Builder"));
            List<Employee> employeeList = new ArrayList<>(List.of(e1, e2));
            taskFacade.setAssignedEmployees(task.getTaskId(), employeeList);

            taskFacade.setAssignedEmployees(task.getTaskId(), employeeList);

            Task resultTask = taskFacade.getTaskById(task.getTaskId());
            List<Employee> assigned = resultTask.getAssignedEmployees();

            assertEquals(2, assigned.size());
            assertTrue(assigned.stream().anyMatch(e -> e.getWorkerId().equals(e1.getWorkerId())));
            assertTrue(assigned.stream().anyMatch(e -> e.getWorkerId().equals(e2.getWorkerId())));
        }

        /**
         * Ensures that setAssignedEmployees replaces any previous assignments.
         */
        @Test
        @DisplayName("Verify that setting a new list overwrites existing assignments")
        void testSetAssignedEmployeesOverwrite() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            Employee oldEmp = employeeFacade.saveEmployee(employeeFacade.createEmployee("Old", "Employee"));
            taskFacade.assignEmployeeToTask(task.getTaskId(), oldEmp.getWorkerId());

            Employee newEmp = employeeFacade.saveEmployee(employeeFacade.createEmployee("New", "Employee"));
            List<Employee> newList = new ArrayList<>(List.of(newEmp));

            taskFacade.setAssignedEmployees(task.getTaskId(), newList);

            Task resultTask = taskFacade.getTaskById(task.getTaskId());
            assertEquals(1, resultTask.getAssignedEmployees().size());

            assertFalse(resultTask.getAssignedEmployees().stream()
                    .anyMatch(e -> e.getWorkerId().equals(oldEmp.getWorkerId())));
        }

        /**
         * Validates that an IllegalArgumentException is thrown if the list is null.
         */
        @Test
        @DisplayName("Should throw exception when the employee list is null")
        @SuppressWarnings("ConstantConditions")
        void testSetAssignedEmployeesValidationNullList() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));

            List<Employee> nullList = null;

            assertThrows(IllegalArgumentException.class, () ->
                    taskFacade.setAssignedEmployees(task.getTaskId(), nullList)
            );
        }

        /**
         * Validates that an IllegalArgumentException is thrown if the list contains null elements.
         */
        @Test
        @DisplayName("Should throw exception when the list contains null elements")
        void testSetAssignedEmployeesValidationNullElements() {
            Task task = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));

            List<Employee> listWithNull = new java.util.ArrayList<>();
            listWithNull.add(null);

            assertThrows(IllegalArgumentException.class, () ->
                    taskFacade.setAssignedEmployees(task.getTaskId(), listWithNull)
            );
        }
    }

    @Nested
    @DisplayName("Bidirectional Mapping Coverage")
    class TaskBidirectionalTests {
        /**
         * Tests bidirectional consistency between Task and Employee entities.
         */
        @Test
        @DisplayName("Verify bidirectional consistency between Task and Employee")
        void testBidirectionalConsistency() {
            Employee employee = employeeFacade.createEmployee("Mario", "Rossi");
            employee = employeeFacade.saveEmployee(employee);
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));

            taskFacade.assignEmployeeToTask(t.getTaskId(), employee.getWorkerId());

            assertTrue(taskFacade.getTaskById(t.getTaskId()).getAssignedEmployees().contains(employee));
            assertTrue(employee.getTasks().stream().anyMatch(task -> task.getTaskId().equals(t.getTaskId())));

            taskFacade.removeEmployeeToTask(t.getTaskId(), employee.getWorkerId());
            assertFalse(taskFacade.getTaskById(t.getTaskId()).getAssignedEmployees().contains(employee));
        }
    }

    @Nested
    @DisplayName("Queries, Filters, and Statistics")
    class TaskQueryTests {
        /**
         * Tests filtering tasks by state and counting them.
         */
        @Test
        void testFiltersAndCounts() {
            taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));

            assertEquals(2, taskFacade.countTasksByState(TaskState.STARTED));
            assertEquals(3, taskFacade.getAllTasks().size());

            List<Task> startedTasks = taskFacade.getTasksByState(TaskState.TO_BE_STARTED);
            assertEquals(1, startedTasks.size());
        }

        /**
         * Tests searching for tasks by employee.
         */
        @Test
        void testSearchByEmployee() {
            Employee employee = employeeFacade.createEmployee("Anna", "Verdi");
            employee = employeeFacade.saveEmployee(employee);
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            taskFacade.assignEmployeeToTask(t.getTaskId(), employee.getWorkerId());

            List<Task> tasksAnna = taskFacade.getTasksByEmployee(employee);
            assertEquals(1, tasksAnna.size());
            assertEquals(t.getTaskId(), tasksAnna.getFirst().getTaskId());
        }

        /**
         * Tests retrieving complex tasks and unassigned tasks.
         */
        @Test
        void testComplexAndUnassignedTasks() {
            Task t1 = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            Employee employee1 = employeeFacade.createEmployee("D1", "C1");
            employee1 = employeeFacade.saveEmployee(employee1);
            Employee employee2 = employeeFacade.createEmployee("D2", "C2");
            employee2 = employeeFacade.saveEmployee(employee2);

            taskFacade.assignEmployeeToTask(t1.getTaskId(), employee1.getWorkerId());
            taskFacade.assignEmployeeToTask(t1.getTaskId(), employee2.getWorkerId());

            List<Task> complexTasks = taskFacade.getComplexTasks(1);
            assertTrue(complexTasks.contains(t1));

            List<Task> unassignedTasks = taskFacade.getUnsignedTasks();
            assertFalse(unassignedTasks.contains(t1));
        }
    }

    @Nested
    @DisplayName("Advanced Query Extensions")
    class TaskAdvancedQueryTests {
        /**
         * Tests querying tasks by state that have at least one assigned employee.
         */
        @Test
        @DisplayName("Test query for state with at least one employee")
        void testFindTasksByStateWithEMVNmployees() {
            Task t1 = taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));
            Employee employee1 = employeeFacade.createEmployee("Test", "User");
            employee1 = employeeFacade.saveEmployee(employee1);
            taskFacade.assignEmployeeToTask(t1.getTaskId(), employee1.getWorkerId());

            Task t2 = taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));
            taskFacade.assignEmployeeToTask(t2.getTaskId(), employee1.getWorkerId());

            List<Task> result = taskFacade.findTasksByStateWithEmployee(TaskState.TO_BE_STARTED);

            assertEquals(2, result.size());
            assertEquals(t1.getTaskId(), result.getFirst().getTaskId());
        }

        /**
         * Tests counting employees assigned to a specific Task ID.
         */
        @Test
        @DisplayName("Test employee count for a specific Task ID")
        void testCountEmployeesByTaskId() {
            Task t1 = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            Employee employee1 = employeeFacade.createEmployee("D1", "C1");
            employee1 = employeeFacade.saveEmployee(employee1);
            Employee employee2 = employeeFacade.createEmployee("D2", "C2");
            employee2 = employeeFacade.saveEmployee(employee2);

            taskFacade.assignEmployeeToTask(t1.getTaskId(), employee1.getWorkerId());
            taskFacade.assignEmployeeToTask(t1.getTaskId(), employee2.getWorkerId());

            Integer count = taskFacade.countEmployeeByTaskId(t1.getTaskId());
            assertEquals(2, count);
        }

        /**
         * Tests searching for tasks by state and exact number of employees.
         */
        @Test
        @DisplayName("Test search by state and exact number of employees")
        void testFindTasksByStateAndEmployeesCount() {
            Task t1 = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            Employee employee = employeeFacade.createEmployee("Solo", "User");
            employee = employeeFacade.saveEmployee(employee);
            taskFacade.assignEmployeeToTask(t1.getTaskId(), employee.getWorkerId());

            List<Task> result = taskFacade.findTasksByStateAndCountEmployee(TaskState.STARTED, 1);
            assertEquals(1, result.size());
            assertTrue(result.contains(t1));
        }

        /**
         * Tests searching for tasks by Team ID.
         */
        @Test
        @DisplayName("Test task search by Team ID")
        void testFindTasksByTeamId() {
            Supervisor s = supervisorFacade.saveSupervisor(supervisorFacade.createSupervisor("Boss", "Generale"));

            Team team = teamFacade.saveTeam(teamFacade.createTeam(s));
            Task t1 = taskFacade.saveTask(taskFacade.createTask(TaskState.STARTED));
            teamFacade.addTaskToTeam(team, t1);

            List<Task> tasksDelTeam = taskFacade.findTasksByTeamId(team.getTeamId());
            assertFalse(tasksDelTeam.isEmpty());
            assertEquals(t1.getTaskId(), tasksDelTeam.getFirst().getTaskId());
        }
    }

    @Nested
    @DisplayName("Internal Logic Validations (POJO)")
    class TaskPojoTests {
        /**
         * Tests that inconsistent date settings throw exceptions.
         */
        @Test
        @DisplayName("Verify that POJO prevents inconsistent dates")
        void testInconsistentDateValidation() {
            Task t = taskFacade.saveTask(taskFacade.createTask(TaskState.TO_BE_STARTED));
            LocalDateTime today = LocalDateTime.now();

            assertEquals(today, taskFacade.setTaskEndDate(t.getTaskId(), today).getEndDate());

            assertThrows(IllegalArgumentException.class, () ->
                    taskFacade.setTaskStartDate(t.getTaskId(), today.plusDays(1))
            );

            assertEquals(today, taskFacade.setTaskStartDate(t.getTaskId(), today).getStartDate());

            assertThrows(IllegalArgumentException.class, () ->
                    taskFacade.setTaskEndDate(t.getTaskId(), today.minusDays(1))
            );
        }
    }


}