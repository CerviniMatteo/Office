package com.unimib.backend.facade;

import com.unimib.backend.POJO.Employee;
import com.unimib.backend.POJO.Supervisor;
import com.unimib.backend.POJO.Task;
import com.unimib.backend.POJO.Team;
import com.unimib.backend.enums.TaskState;
import com.unimib.backend.enums.WorkerRole;
import com.unimib.backend.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeamFacade {

    @Autowired
    private TeamService teamService;

    public Team createTeam(Supervisor supervisor) {
        return teamService.createTeam(supervisor);
    }

    public Team createTeam(List<Employee> employees, Supervisor supervisor) {
        return teamService.createTeam(employees, supervisor);
    }

    public Team createTeam(List<Employee> employees, Supervisor supervisor, List<Task> tasks) {
        return teamService.createTeam(employees, supervisor, tasks);
    }

    public Team saveTeam(Team team) {
        return teamService.saveTeam(team);
    }

    public void deleteTeam(Team team) {
        teamService.deleteTeam(team);
    }

    public List<Employee> getEmployeesInTeam(Team team) {
        return teamService.getEmployeesInTeam(team);
    }

    public void addEmployeeToTeam(Team team, Employee employee) {
        teamService.addEmployeeToTeam(team, employee);
    }

    public void removeAllEmployeesFromTeam(Team team) {
        teamService.removeAllEmployeesFromTeam(team);
    }

    public void removeEmployeeFromTeam(Team team, Employee employee) {
        teamService.removeEmployeeFromTeam(team, employee);
    }

    public Supervisor getTeamSupervisor(Team team) {
        return teamService.getTeamSupervisor(team);
    }

    public List<Task> getTeamTasks(Team team) {
        return teamService.getTeamTasks(team);
    }

    public void addTaskToTeam(Team team, Task task) {
        teamService.addTaskToTeam(team, task);
    }

    public void removeAllTasksFromTeam(Team team) {
        teamService.removeAllTasksFromTeam(team);
    }

    public void removeTaskFromTeam(Team team, Task task) {
        teamService.removeTaskFromTeam(team, task);
    }

    public Optional<Team> getTeamById(Long id) {
        return teamService.getTeamById(id);
    }

    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    public void deleteTeamById(Long id) {
        teamService.deleteTeamById(id);
    }

    public List<Team> getTeamsBySupervisorPersonId(Long supervisorId) {
        return teamService.getTeamsBySupervisorPersonId(supervisorId);
    }

    public Team getTeamByEmployeePersonId(Long employeeId) {
        return teamService.getTeamByEmployeesPersonId(employeeId);
    }

    public Team getTeamByTask_Id(Long taskId) {
        return teamService.getTeamByTaskId(taskId);
    }

    public List<Task> getTasksByTeamId(Long teamId) {
        return teamService.getTasksByTeamId(teamId);
    }

    public Supervisor getSupervisorByTeamId(Long teamId) {
        return teamService.getSupervisorByTeamId(teamId);
    }

    public List<Employee> getEmployeesByTeamId(Long teamId) {
        return teamService.getEmployeesByTeamId(teamId);
    }

    public List<Task> getTasksInTeamIdByTaskState(Long teamId, TaskState taskState) {
        return teamService.getTasksInTeamIdByTaskState(teamId, taskState);
    }

    public List<Employee> getEmployeesInTeamIdWithSalaryGreaterThan(Long teamId, double salary) {
        return teamService.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, salary);
    }

    public List<Employee> getEmployeesInTeamIdWithSalaryLessThan(Long teamId, double salary) {
        return teamService.getEmployeesInTeamIdWithSalaryLessThan(teamId, salary);
    }

    public List<Employee> getEmployeesInTeamIdWithEmployeeRole(Long teamId, WorkerRole workerRole) {
        return teamService.getEmployeesInTeamIdWithEmployeeRole(teamId, workerRole);
    }
}