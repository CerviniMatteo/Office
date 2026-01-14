package com.unimib.assignment3.repository;

import java.util.List;
import java.util.Optional;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import org.springframework.lang.NonNull;

/**
 * TeamRepository interface for managing Team entities.
 * <p>
 * Provides methods for CRUD operations and custom queries related to teams, employees, supervisors, and tasks.
 * </p>
 */
public interface TeamRepository extends JpaRepository<Team, Long> {
    /**
     * Find a team by its ID
     *
     * @param teamId the ID of the team to retrieve
     * @return the team with the given ID, or empty if not found
     */
    Optional<Team> findByTeamId(Long teamId);

    /**
     * Delete a team by its ID.
     *
     * @param teamId must not be {@literal null}. The ID of the team to delete.
     */
    @Override
    void deleteById(@NonNull Long teamId);

    /**
     * Retrieve all teams.
     *
     * @return all teams
     */
    @Override
    @NonNull
    List<Team> findAll();

    /**
     * Find teams by supervisor's ID.
     *
     * @param supervisorId supervisor's ID
     * @return list of teams
     */
    List<Team> findBySupervisorPersonId(Long supervisorId);

    /**
     * Find the team by a specific employee's ID.
     *
     * @param employeeId the ID of the employee
     * @return the team associated with the employee
     */
    Team findByEmployeesPersonId(Long employeeId);

    /**
     * Find the team by a specific task's ID.
     *
     * @param taskId the ID of the task
     * @return the team associated with the task
     */
    Team findByTasksTaskId(Long taskId);

    /**
     * Find tasks associated with a specific team ID.
     * @param teamId the ID of the team
     * @return list of tasks
     */
    @Query("SELECT t.tasks FROM team t WHERE t.teamId = :teamId")
    List<Task> findTasksByTeamId(@Param("teamId") Long teamId);

    /**
     * Find a supervisor by team ID.
     *
     * @param teamId the ID of the team
     * @return the supervisor associated with the team
     */
    @Query("SELECT t.supervisor FROM team t WHERE t.teamId = :teamId")
    Supervisor findSupervisorByTeamId(@Param("teamId") Long teamId);

    /**
     * Find employees by team ID.
     *
     * @param teamId the ID of the team
     * @return list of employees associated with the team
     */
    @Query("SELECT t.employees FROM team t WHERE t.teamId = :teamId")
    List<Employee> findEmployeesByTeamId(@Param("teamId") Long teamId);

    /**
     * Find tasks in a specific team by their state.
     *
     * @param teamId the ID of the team
     * @param taskState the state of the tasks to retrieve
     * @return list of tasks in the team with the given state
     */
    @Query("SELECT t_task FROM team t JOIN t.tasks t_task WHERE t.teamId = :teamId AND t_task.taskState = :taskState")
    List<Task> findTasksInTeamIdByTaskState(@Param("teamId") Long teamId, @Param("taskState") TaskState taskState);

    /**
     * Find employees with salary greater than a specified amount in a specific team.
     *
     * @param teamId the ID of the team
     * @param monthlySalary the salary threshold
     * @return list of employees in the team with a salary greater than the threshold
     */
    @Query("SELECT d FROM team t JOIN t.employees d WHERE t.teamId = :teamId AND d.monthlySalary > :monthlySalary")
    List<Employee> findEmployeesInTeamIdWithSalaryGreaterThan(@Param("teamId") Long teamId, @Param("monthlySalary") double monthlySalary);

    /**
     * Find employees with salary less than a specified amount in a specific team.
     *
     * @param teamId the ID of the team
     * @param monthlySalary the salary threshold
     * @return list of employees in the team with a salary less than the threshold
     */
    @Query("SELECT d FROM team t JOIN t.employees d WHERE t.teamId = :teamId AND d.monthlySalary < :monthlySalary")
    List<Employee> findEmployeesInTeamIdWithSalaryLessThan(@Param("teamId") Long teamId, @Param("monthlySalary") double monthlySalary);

    /**
     * Find employees with a specific employee role in a specific team.
     *
     * @param teamId the ID of the team
     * @param employeeRole the employee role to filter by
     * @return list of employees in the team with the given role
     */
    @Query("SELECT d FROM team t JOIN t.employees d WHERE t.teamId = :teamId AND d.employeeRole = :employeeRole")
    List<Employee> findEmployeesInTeamIdWithEmployeeRole(@Param("teamId") Long teamId, @Param("employeeRole") EmployeeRole employeeRole);
}



