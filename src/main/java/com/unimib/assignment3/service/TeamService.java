package com.unimib.assignment3.service;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.constants.teamConstants;
import com.unimib.assignment3.constants.CommonConstants;
import com.unimib.assignment3.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Service class for managing Team entities.
 * <p>
 * This class provides methods to create, save, delete, and manage teams,
 * including adding/removing employees and tasks, and setting/getting supervisors.
 * It also includes repository functions to retrieve teams based on various criteria.
 */
@Service
public class TeamService {
    @Autowired
    private TaskService taskService;
    @Autowired
    private SupervisorService supervisorService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private TeamRepository teamRepository;

    /**
     * Create a new Team with the given Supervisor.
     *
     * @param supervisor the supervisor of the new team
     * @return the created Team entity
     * @throws IllegalArgumentException if the supervisor is null
     */
    @Transactional
    public Team createTeam(Supervisor supervisor){
        if(supervisor == null) {
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_CANNOT_BE_NULL);
        }
        return new Team(supervisor);
    }

    /**
     * Create a new Team with the given employees and Supervisor.
     *
     * @param employees employees of the new team
     * @param supervisor the supervisor of the new team
     * @return the created Team entity
     * @throws IllegalArgumentException if the list of employees is empty or the supervisor is null
     */
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

    /**
     * Create a new Team with the given employees, Supervisor, tasks.
     *
     * @param employees employees of the new team
     * @param supervisor the supervisor of the new team
     * @param tasks tasks of the new team
     * @return the created Team entity
     * @throws IllegalArgumentException if the list of employees or tasks is empty, or the supervisor is null
     */
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

    /**
     * Save the given Team entity.
     *
     * @param team the Team entity to be saved
     * @return the saved Team entity
     * @throws IllegalArgumentException if the Team entity is null
     */
    public Team saveTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return teamRepository.saveAndFlush(team);
    }

    /**
     * Delete the given Team entity and all its associations with employees, supervisor and tasks.
     *
     * @param team the Team entity to be deleted
     * @throws IllegalArgumentException if the Team entity is null
     */
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
        supervisor.removeTeamSupervisionato(team);

        // Finally, delete the team
        teamRepository.delete(team);
    }
    // for now dont need getIdTeam

    // other methods as needed

    /**
     * Get the list of employees in the given Team.
     *
     * @param team the Team
     * @return the list of employees in the Team
     * @throws IllegalArgumentException if the Team is null
     */
    public List<Employee> getEmployeesInTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return team.getEmployees();
    }

    /**
     * Add the given employee to the given Team removing him from any other Team he was in.
     *
     * @param newTeam the Team to which the employee will be added
     * @param employee the employee to be added
     * @throws IllegalArgumentException if the Team or employee is null
     */
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

    /**
     * Remove all employees from the given Team.
     *
     * @param team the Team from which employees will be removed
     * @throws IllegalArgumentException if the Team is null
     */
    @Transactional
    public void removeAllEmployeesFromTeam(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        team.removeAllEmployee();
    }

    /**
     * Remove the given employee from the given Team.
     *
     * @param team the Team from which the employee will be removed
     * @param employee the employee to be removed
     * @throws IllegalArgumentException if the Team or employee is null, or if the employee is not found in the Team
     */
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

    /**
     * Get the supervisor of the given Team.
     *
     * @param team the Team from which the supervisor will be retrieved
     * @return the supervisor of the Team
     * @throws IllegalArgumentException if the Team is null
     */
    public Supervisor getTeamSupervisor(Team team){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return team.getSupervisor();
    }

    /**
     * Set the supervisor of the given Team.
     *
     * @param team the Team whose supervisor will be set
     * @param supervisor the supervisor to be set
     * @throws IllegalArgumentException if the Team or supervisor is null
     */
    @Transactional
    public void setTeamSupervisor(Team team, Supervisor supervisor){
        if(team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        } else if(supervisor == null) {
            throw new IllegalArgumentException(teamConstants.SUPERVISOR_CANNOT_BE_NULL);
        }
        team.setSupervisor(supervisor);
    }

    /**
     * Get the list of tasks assigned to the given Team.
     *
     * @param team the Team from which the tasks will be retrieved
     * @return the list of tasks assigned to the Team
     * @throws IllegalArgumentException if the Team is null
     */
    public List<Task> getTeamTasks(Team team) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        return team.getTasks();
    }

    /**
     * Add the given task to the given Team removing it from any other Team it was in.
     *
     * @param newTeam the Team to which the task will be added
     * @param task the task to be added
     * @throws IllegalArgumentException if the Team or task is null
     */
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

    /**
     * Remove all tasks from the given Team.
     *
     * @param team the Team from which tasks will be removed
     * @throws IllegalArgumentException if the Team is null
     */
    @Transactional
    public void removeAllTasksFromTeam(Team team) {
        if (team == null) {
            throw new IllegalArgumentException(teamConstants.TEAM_CANNOT_BE_NULL);
        }
        team.removeAllTasks();
    }

    /**
     * Remove the given task from the given Team.
     *
     * @param team the Team from which the task will be removed
     * @param task the task to be removed
     * @throws IllegalArgumentException if the Team or task is null, or if the task is not found in the Team
     */
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

    /**
     * Get a Team by its id.
     *
     * @param id the id of the Team to retrieve
     * @return the Team with the given id, or empty if not found
     */
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findByTeamId(id);
    }

    /**
     * Delete a Team by its id.
     *
     * @param id the id of the Team to delete
     * @throws IllegalArgumentException if the Team with the given id is not found
     */
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
        supervisor.removeTeamSupervisionato(team);

        teamRepository.deleteById(id);
    }

    /**
     * Get all Teams.
     *
     * @return a list of all Teams
     */
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    /**
     * Get all Teams by supervisor id.
     * @param supervisorId the id of the supervisor to retrieve teams from
     * @return a list of all Teams by supervisor id
     * @throws IllegalArgumentException if the supervisor with the given id is not found
     */
    public List<Team> getTeamsBySupervisorPersonId(Long supervisorId) {
        supervisorService.findSupervisorById(supervisorId);
        return teamRepository.findBySupervisorPersonId(supervisorId);
    }

    /**
     * Get a Team by employee id.
     *
     * @param employeeId the id of the employee to retrieve team from
     * @return the Team the employee belongs to, or empty if not found
     * @throws IllegalArgumentException if the employee with the given id is not found
     */
    public Team getTeamByEmployeesPersonId(Long employeeId) {
        employeeService.findEmployeeById(employeeId);
        return teamRepository.findByEmployeesPersonId(employeeId);
    }

    /**
     * Get a Team by task id.
     *
     * @param taskId the id of the task to retrieve team from
     * @return the Team the task belongs to, or empty if not found
     * @throws IllegalArgumentException if the task with the given id is not found
     */
    public Team getTeamByTaskId(Long taskId) {
        //TODO aspettare che aiva aggiunge le eccezioni in taskService.getTaskById
        if(taskService.getTaskById(taskId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TASK_NOT_FOUND);
        }
        return teamRepository.findByTasksTaskId(taskId);
    }

    /**
     * Get all tasks by team id.
     *
     * @param teamId the id of the team to retrieve tasks from
     * @return a list of all tasks by team id
     * @throws IllegalArgumentException if the team with the given id is not found
     */
    public List<Task> getTasksByTeamId(Long teamId) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        return teamRepository.findTasksByTeamId(teamId);
    }

    /**
     * Get the supervisor of a team by its id.
     *
     * @param teamId the id of the team to retrieve supervisor from
     * @return the supervisor of the team, or empty if not found
     * @throws IllegalArgumentException if the team with the given id is not found
     */
    public Supervisor getSupervisorByTeamId(Long teamId) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        return teamRepository.findSupervisorByTeamId(teamId);
    }

    /**
     * Get all employees by team id.
     *
     * @param teamId the id of the team to retrieve employees from
     * @return a list of all employees by team id
     * @throws IllegalArgumentException if the team with the given id is not found
     */
    public List<Employee> getEmployeesByTeamId(Long teamId) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        }
        return teamRepository.findEmployeesByTeamId(teamId);
    }

    /**
     * Get all tasks in a team by task state.
     *
     * @param teamId the id of the team to retrieve tasks from
     * @param taskState the state of the tasks to retrieve
     * @return a list of all tasks in the team with the given state
     * @throws IllegalArgumentException if the team with the given id is not found, or if the task state is null
     */
    public List<Task> getTasksInTeamIdByTaskState(Long teamId, TaskState taskState) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if(taskState == null) {
            throw new IllegalArgumentException(teamConstants.TASK_STATE_CANNOT_BE_NULL);
        }
        return teamRepository.findTasksInTeamIdByTaskState(teamId, taskState);
    }

    /**
     * Get employees in a team with the salary greater than a given value.
     *
     * @param teamId the id of the team to retrieve employees from
     * @param salary the minimum salary of the employees to retrieve
     * @return a list of employees in the team with a salary greater than the given value
     * @throws IllegalArgumentException if the team with the given id is not found, or if the salary is negative
     */
    public List<Employee> getEmployeesInTeamIdWithSalaryGreaterThan(Long teamId, Double salary) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(CommonConstants.SALARY_MUST_BE_POSITIVE);
        }
        return teamRepository.findEmployeesInTeamIdWithSalaryGreaterThan(teamId, salary);
    }

    /**
     * Get employees in a team with the salary less than a given value.
     *
     * @param teamId the id of the team to retrieve employees from
     * @param salary the maximum salary of the employees to retrieve
     * @return a list of employees in the team with a salary less than the given value
     * @throws IllegalArgumentException if the team with the given id is not found, or if the salary is negative
     */
    public List<Employee> getEmployeesInTeamIdWithSalaryLessThan(Long teamId, Double salary) {
        if (getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if (salary <= 0) {
            throw new IllegalArgumentException(CommonConstants.SALARY_MUST_BE_POSITIVE);
        }
        return teamRepository.findEmployeesInTeamIdWithSalaryLessThan(teamId, salary);
    }

    /**
     * Get employees in a team with a given employee role.
     *
     * @param teamId the id of the team to retrieve employees from
     * @param employeeRole the role of the employees to retrieve
     * @return a list of employees in the team with the given role
     * @throws IllegalArgumentException if the team with the given id is not found, or if the employee role is null
     */
    public List<Employee> getEmployeesInTeamIdWithEmployeeRole(Long teamId, EmployeeRole employeeRole) {
        if(getTeamById(teamId).isEmpty()) {
            throw new IllegalArgumentException(teamConstants.TEAM_NOT_FOUND);
        } else if(employeeRole == null) {
            throw new IllegalArgumentException(teamConstants.EMPLOYEE_ROLE_CANNOT_BE_NULL);
        }
        return teamRepository.findEmployeesInTeamIdWithEmployeeRole(teamId, employeeRole);
    }
}
