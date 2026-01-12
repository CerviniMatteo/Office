package com.unimib.assignment3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;

public interface TeamRepository extends JpaRepository<Team, Long> {
    // Find a team by its ID
    Optional<Team> findByIdTeam(Long idTeam);
    // Delete team by its ID
    @Override
    void deleteById(Long idTeam);
    // Retrieve all teams
    @Override
    List<Team> findAll();
    // Find teams by supervisor's ID
    List<Team> findBySupervisore_Id(Long idSupervisore);
    // Find team by a specific employee's ID
    Team findByDipendenti_Id(Long idDipendente);
    // Find team by a specific task's ID
    Team findByTasks_TaskId(Long idTask);
    // Find tasks associated with a specific team ID
    @Query("SELECT t.tasks FROM team t WHERE t.idTeam = :idTeam")
    List<Task> findTasksByIdTeam(@Param("idTeam") Long idTeam);
    // Find a supervisor by team ID
    @Query("SELECT t.supervisore FROM team t WHERE t.idTeam = :idTeam")
    Supervisore findSupervisoreByIdTeam(@Param("idTeam") Long idTeam);
    // Find employees by team ID
    @Query("SELECT t.dipendenti FROM team t WHERE t.idTeam = :idTeam")
    List<Dipendente> findDipendentiByIdTeam(@Param("idTeam") Long idTeam);

    // Find tasks by their state
    @Query("SELECT t_task FROM team t JOIN t.tasks t_task WHERE t.idTeam = :idTeam AND t_task.taskState = :taskState")
    List<Task> findTasksInTeamIdByTaskState(@Param("idTeam") Long idTeam, @Param("taskState") TaskState taskState);
    // Find employees with salary greater than a specified amount in a specific team
    @Query("SELECT d FROM team t JOIN t.dipendenti d WHERE t.idTeam = :idTeam AND d.monthlySalary > :monthlySalary")
    List<Dipendente> findDipendentiInTeamIdWithSalaryGreaterThan(@Param("idTeam") Long idTeam, @Param("monthlySalary") double monthlySalary);
    // Find employees with salary less than a specified amount in a specific team
    @Query("SELECT d FROM team t JOIN t.dipendenti d WHERE t.idTeam = :idTeam AND d.monthlySalary < :monthlySalary")
    List<Dipendente> findDipendentiInTeamIdWithSalaryLessThan(@Param("idTeam") Long idTeam, @Param("monthlySalary") double monthlySalary);
    // Find employees with a specific grade in a specific team
    @Query("SELECT d FROM team t JOIN t.dipendenti d WHERE t.idTeam = :idTeam AND d.employeeRole = :employeeRole")
    List<Dipendente> findDipendentiInTeamIdWithGrado(@Param("idTeam") Long idTeam, @Param("employeeRole") EmployeeRole employeeRole);
}



