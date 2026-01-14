package com.unimib.assignment3.repository;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for {@link Task} entities.
 * <p>
 * Provides standard CRUD operations and custom JPQL queries for task state,
 */
public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Retrieves a list of tasks that are in a specific state.
     *
     * @param taskState the state to filter tasks by
     * @return a list of tasks in the given state
     */
    List<Task> findByTaskState(TaskState taskState);

    /**
     * Counts the total number of tasks in a specific state.
     *
     * @param taskState the state to count tasks for
     * @return the total count of tasks in the given state
     */
    long countByTaskState(TaskState taskState);

    /**
     * Retrieves all tasks assigned to a specific employee.
     *
     * @param employee the employee entity to search for
     * @return a list of tasks assigned to the employee
     */
    @Query("SELECT t FROM task t JOIN t.assignedEmployees d WHERE d = :employee")
    List<Task> findTasksByEmployee(@Param("employee") Employee employee);

    /**
     * Retrieves all tasks that do not have any employees assigned.
     *
     * @return a list of unassigned tasks
     */
    @Query("SELECT t FROM task t WHERE SIZE(t.assignedEmployees) = 0")
    List<Task> findTasksWithoutEmployee();

    /**
     * Retrieves tasks that have more than a specific number of assigned employees.
     * Used to identify "complex" tasks.
     *
     * @param minEmployees the threshold of employees
     * @return a list of tasks with more than minEmployees assigned
     */
    @Query("SELECT t FROM task t WHERE SIZE(t.assignedEmployees) > :minEmployees")
    List<Task> findTasksWithMoreThanNEmployees(@Param("minEmployees") int minEmployees);

    /**
     * Retrieves tasks in a specific state that have at least one employee assigned.
     *
     * @param taskState the state of the tasks
     * @return a list of tasks in the given state with assignments
     */
    @Query("SELECT t FROM task t WHERE t.taskState = :taskState AND SIZE(t.assignedEmployees) > 0")
    List<Task> findTasksByStateWithEmployees(@Param("taskState") TaskState taskState);

    /**
     * Retrieves tasks in a specific state with an exact number of assigned employees.
     *
     * @param taskState    the state of the tasks
     * @param numEmployees the exact number of employees assigned
     * @return a list of matching tasks
     */
    @Query("SELECT t FROM task t WHERE t.taskState = :taskState AND SIZE(t.assignedEmployees) = :numEmployees")
    List<Task> findTasksByStateAndEmployeesCount(@Param("taskState") TaskState taskState, @Param("numEmployees") int numEmployees);

    /**
     * Retrieves all tasks associated with a specific team.
     *
     * @param teamId the ID of the team
     * @return a list of tasks belonging to the team
     */
    @Query("SELECT t FROM team tm JOIN tm.tasks t WHERE tm.teamId = :teamId")
    List<Task> findTasksByTeamId(@Param("teamId") Long teamId);
}