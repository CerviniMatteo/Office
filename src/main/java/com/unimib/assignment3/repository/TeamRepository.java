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

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Find a team by its ID
    Optional<Team> findByTeamId(Long teamId);
    // Delete team by its ID
    @Override
    void deleteById(Long teamId);
    // Retrieve all teams
    @Override
    List<Team> findAll();
    // Find teams by supervisor's ID
    List<Team> findBySupervisorPersonId(Long supervisorId);
    // Find team by a specific employee's ID
    Team findByEmployeesPersonId(Long employeeId);
    // Find team by a specific task's ID
    Team findByTasksTaskId(Long taskId);
    // Find tasks associated with a specific team ID
    @Query("SELECT t.tasks FROM team t WHERE t.teamId = :teamId")
    List<Task> findTasksByTeamId(@Param("teamId") Long teamId);
    // Find a supervisor by team ID
    @Query("SELECT t.supervisor FROM team t WHERE t.teamId = :teamId")
    Supervisor findSupervisorByTeamId(@Param("teamId") Long teamId);
    // Find employees by team ID
    @Query("SELECT t.employees FROM team t WHERE t.teamId = :teamId")
    List<Employee> findEmployeesByTeamId(@Param("teamId") Long teamId);

    // Find tasks by their state
    @Query("SELECT t_task FROM team t JOIN t.tasks t_task WHERE t.teamId = :teamId AND t_task.taskState = :taskState")
    List<Task> findTasksInTeamIdByTaskState(@Param("teamId") Long teamId, @Param("taskState") TaskState taskState);
    // Find employees with salary greater than a specified amount in a specific team
    @Query("SELECT d FROM team t JOIN t.employees d WHERE t.teamId = :teamId AND d.monthlySalary > :monthlySalary")
    List<Employee> findEmployeesInTeamIdWithSalaryGreaterThan(@Param("teamId") Long teamId, @Param("monthlySalary") double monthlySalary);
    // Find employees with salary less than a specified amount in a specific team
    @Query("SELECT d FROM team t JOIN t.employees d WHERE t.teamId = :teamId AND d.monthlySalary < :monthlySalary")
    List<Employee> findEmployeesInTeamIdWithSalaryLessThan(@Param("teamId") Long teamId, @Param("monthlySalary") double monthlySalary);
    // Find employees with a specific grade in a specific team
    @Query("SELECT d FROM team t JOIN t.employees d WHERE t.teamId = :teamId AND d.employeeRole = :employeeRole")
    List<Employee> findEmployeesInTeamIdWithGrado(@Param("teamId") Long teamId, @Param("employeeRole") EmployeeRole employeeRole);
}



