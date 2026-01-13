package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.constants.teamConstants;
import com.unimib.assignment3.constants.CommonConstants;
import com.unimib.assignment3.repository.EmployeeRepository;
import com.unimib.assignment3.repository.SupervisorRepository;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class TeamService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SupervisorRepository supervisorRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private TeamRepository teamRepository;

    @Transactional
    public Team createTeam(Supervisor supervisor){
        if(supervisor == null) {
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_CANNOT_BE_NULL);
        }
        return new Team(supervisor);
    }
    @Transactional
    public Team createTeam(List<Employee> employees, Supervisor supervisor){
        if(employees.isEmpty()){
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_LIST_IS_EMPTY);
        } else if(supervisor == null){
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_CANNOT_BE_NULL);
        }
        List<Employee> employeeList = new ArrayList<>(employees);
        return new Team(employeeList, supervisor);
    }
    @Transactional
    public Team createTeam(List<Employee> employees, Supervisor supervisor, List<Task> tasks){
        if(employees.isEmpty()){
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_LIST_IS_EMPTY);
        } else if(tasks.isEmpty()){
            throw new IllegalArgumentException(teamConstants.TASK_LIST_IS_EMPTY);
        } else if(supervisor == null){
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_CANNOT_BE_NULL);
        }
        List<Employee> employeeList = new ArrayList<>(employees);
        List<Task> taskList = new ArrayList<>(tasks);
        return new Team(employeeList, supervisor, taskList);
    }
    public Team saveTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return teamRepository.saveAndFlush(team);
    }
    @Transactional
    public void deleteTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        // Remove the association of the team from its employees
        team.removeAllEmployee();

        // Remove all tasks from the team
        team.removeAllTasks();

        // Remove the team from its supervisor
        Supervisor supervisor = team.getSupervisor();
        supervisor.removeSupervisedTeam(team);

        // Finally, delete the team
        teamRepository.delete(team);
    }
    // for now dont need getIdTeam

    // other methods as needed
    public List<Employee> getEmployeesInTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return team.getEmployees();
    }
    @Transactional
    public void addEmployeeToTeam(Team newTeam, Employee employee){
        if(newTeam == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        } else if(employee == null) {
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_CANNOT_BE_NULL);
        }
        Team oldTeam = employee.getEmployeeTeam();
        // if the employee is already in a team, remove him from the old team
        if(oldTeam != null) {
            oldTeam.removeEmployee(employee);
        }
        newTeam.addEmployee(employee);
    }
    @Transactional
    public void removeAllEmployeesFromTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        team.removeAllEmployee();
    }
    @Transactional
    public void removeEmployeeFromTeam(Team team, Employee employee){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        } else if(employee == null) {
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_CANNOT_BE_NULL);
        } else if(!team.getEmployees().contains(employee)){
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_NOT_FOUND);
        }
        team.removeEmployee(employee);
    }

    public Supervisor getTeamSupervisor(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return team.getSupervisor();
    }
    @Transactional
    public void setTeamSupervisor(Team team, Supervisor supervisor){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        } else if(supervisor == null) {
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_CANNOT_BE_NULL);
        }
        team.setSupervisor(supervisor);
    }
    public List<Task> getTeamTasks(Team team) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return team.getTasks();
    }
    @Transactional
    public void addTaskToTeam(Team newTeam, Task task) {
        if (newTeam == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        } else if (task == null) {
            throw new IllegalArgumentException(teamConstants.TASK_CANNOT_BE_NULL);
        }
        Team oldTeam = task.getTeamTask();
        if (oldTeam != null) {
            oldTeam.removeTask(task);
        }
        newTeam.addTask(task);
    }
    @Transactional
    public void removeAllTasksFromTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        team.removeAllTasks();
    }
    @Transactional
    public void removeTaskFromTeam(Team team, Task task) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        } else if(task == null) {
            throw new IllegalArgumentException(teamConstants.TASK_CANNOT_BE_NULL);
        } else if (!team.getTasks().contains(task)) {
            throw new IllegalArgumentException(teamConstants.TASK_NOT_FOUND);
        }
        team.removeTask(task);
    }

    // Repository functions
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findByTeamId(id);
    }
    @Transactional
    public void deleteTeamById(Long id) {
        if(getTeamById(id).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        Team team = getTeamById(id).get();

        // Remove the association of the team from its employees
        team.removeAllEmployee();

        // Remove all tasks from the team
        team.removeAllTasks();

        // Remove the team from its supervisor
        Supervisor supervisor = team.getSupervisor();
        supervisor.removeSupervisedTeam(team);

        teamRepository.deleteById(id);
    }



    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    public List<Team> getTeamsBySupervisorPersonId(Long supervisorId) {
        if(supervisorRepository.findById(supervisorId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_NOT_FOUND);
        }
        return teamRepository.findBySupervisorPersonId(supervisorId);
    }
    public Team getTeamByEmployeesPersonId(Long employeeId) {
        if(employeeRepository.findById(employeeId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_NOT_FOUND);
        }
        return teamRepository.findByEmployeesPersonId(employeeId);
    }
    public Team getTeamByTaskId(Long taskId) {
        if(taskRepository.findById(taskId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TASK_NOT_FOUND);
        }
        return teamRepository.findByTasksTaskId(taskId);
    }
    public List<Task> getTasksByTeamId(Long teamId) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        return teamRepository.findTasksByTeamId(teamId);
    }
    public Supervisor getSupervisorByTeamId(Long teamId) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        return teamRepository.findSupervisorByTeamId(teamId);
    }
    public List<Employee> getEmployeesByTeamId(Long teamId) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        return teamRepository.findEmployeesByTeamId(teamId);
    }

    public List<Task> getTasksInTeamIdByTaskState(Long teamId, TaskState taskState) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if(taskState == null) {
            throw new IllegalArgumentException(teamConstants.TASK_STATE_CANNOT_BE_NULL);
        }
        return teamRepository.findTasksInTeamIdByTaskState(teamId, taskState);
    }
    public List<Employee> getEmployeesInTeamIdWithSalaryGreaterThan(Long teamId, Double salary) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(CommonConstants.SALARY_MUST_BE_POSITIVE);
        }
        return teamRepository.findEmployeesInTeamIdWithSalaryGreaterThan(teamId, salary);
    }
    public List<Employee> getEmployeesInTeamIdWithSalaryLessThan(Long teamId, Double salary) {
        if (getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(CommonConstants.SALARY_MUST_BE_POSITIVE);
        }
        return teamRepository.findEmployeesInTeamIdWithSalaryLessThan(teamId, salary);
    }
    public List<Employee> getEmployeesInTeamIdWithEmployeeRole(Long teamId, EmployeeRole employeeRole) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if(employeeRole == null) {
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_ROLE_CANNOT_BE_NULL);
        }
        return teamRepository.findEmployeesInTeamIdWithEmployeeRole(teamId, employeeRole);
    }
}
