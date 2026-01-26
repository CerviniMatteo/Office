package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.*;
import com.unimib.assignment3.enums.*;
import com.unimib.assignment3.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Facade class providing a unified interface to manage Employees, Supervisors, Tasks, and Teams.
 * <p>
 * This class delegates calls to the underlying services:
 * {@link EmployeeService}, {@link SupervisorService}, {@link TaskService}, and {@link TeamService}.
 * It simplifies the interaction between controllers and services by exposing high-level methods.
 */
@Service
public class Facade {

    /** Employee service for managing employee-related operations. */
    @Autowired
    private EmployeeService employeeService;

    /** Supervisor service for managing supervisor-related operations. */
    @Autowired
    private SupervisorService supervisorService;

    /** Task service for managing task-related operations. */
    @Autowired
    private TaskService taskService;

    /** Team service for managing team-related operations. */
    @Autowired
    private TeamService teamService;

    // <---- Employee Methods ---->

    /**
     * Creates a new employee with only name and surname.
     *
     * @param name    the employee's first name (must not be null)
     * @param surname the employee's surname (must not be null)
     * @return the newly created Employee object
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname) {
        return employeeService.createEmployee(name, surname);
    }

    /**
     * Creates a new employee with name, surname, role, and monthly salary.
     *
     * @param name          the employee's first name (must not be null)
     * @param surname       the employee's surname (must not be null)
     * @param monthlySalary the employee's monthly salary
     * @param employeeRole  the employee's role (must not be null)
     * @return the newly created Employee object
     */
    public Employee createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return employeeService.createEmployee(name, surname, monthlySalary, employeeRole);
    }

    /**
     * Saves an employee to the database.
     *
     * @param employee the employee to save (must not be null)
     * @return the saved Employee object
     */
    public Employee saveEmployee(@NonNull Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    /**
     * Saves a list of employees to the database.
     *
     * @param employees list of employees to save (must not be null)
     * @return list of saved Employee objects
     */
    public List<Employee> saveAllEmployees(@NonNull List<Employee> employees) {
        return employeeService.saveAllEmployees(employees);
    }

    /**
     * Finds an employee by their ID.
     *
     * @param employeeId the employee's ID (must not be null)
     * @return an Optional containing the Employee if found
     */
    public Optional<Employee> findEmployeeById(@NonNull Long employeeId) {
        return employeeService.findEmployeeById(employeeId);
    }

    /**
     * Retrieves all employees.
     *
     * @return list of all Employee objects
     */
    public List<Employee> findAllEmployees() {
        return employeeService.findAllEmployees();
    }

    /**
     * Fires/removes a single employee under a specific manager.
     *
     * @param managerId  the manager's ID (must not be null)
     * @param employeeId the employee's ID (must not be null)
     */
    public void fireEmployee(@NonNull Long managerId, @NonNull Long employeeId) {
        employeeService.fireEmployee(managerId, employeeId);
    }

    /**
     * Fires/removes multiple employees under a specific manager.
     *
     * @param managerId the manager's ID (must not be null)
     * @param employees list of employees to fire (must not be null)
     */
    public void fireEmployees(@NonNull Long managerId, @NonNull List<Employee> employees) {
        employeeService.fireEmployees(managerId, employees);
    }

    /**
     * Finds employees under a manager filtered by monthly salary.
     *
     * @param employeeId    the manager's employee ID (must not be null)
     * @param monthlySalary the salary to filter
     * @return list of employees matching the salary criteria
     */
    public List<Employee> findEmployeesByMonthlySalary(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalary(employeeId, monthlySalary);
    }

    /**
     * Finds employees under a manager filtered by salary, ordered ascending by role.
     *
     * @param employeeId    the manager's employee ID (must not be null)
     * @param monthlySalary the salary to filter
     * @return list of employees sorted by role ascending
     */
    public List<Employee> findEmployeesByMonthlySalaryAscByEmployeeRole(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(employeeId, monthlySalary);
    }

    /**
     * Finds employees under a manager filtered by salary, ordered descending by role.
     *
     * @param employeeId    the manager's employee ID (must not be null)
     * @param monthlySalary the salary to filter
     * @return list of employees sorted by role descending
     */
    public List<Employee> findEmployeesByMonthlySalaryDescByEmployeeRole(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(employeeId, monthlySalary);
    }

    /**
     * Finds tasks assigned to a specific employee filtered by task state and start date.
     *
     * @param employeeId the employee's ID (must not be null)
     * @param taskState  the state of tasks to filter (must not be null)
     * @param startDate  the start date to filter (must not be null)
     * @return list of tasks assigned to the employee with the given state and start date
     */
    public List<Task>  findTasksByEmployeeByTaskStateByStartDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate startDate) {
        return employeeService.findTasksByEmployeeByTaskStateByStartDate(employeeId, taskState, startDate);
    }

    /**
     * Finds tasks assigned to a specific employee filtered by task state and end date.
     *
     * @param employeeId the employee's ID (must not be null)
     * @param taskState  the state of tasks to filter (must not be null)
     * @param endDate    the end date to filter (must not be null)
     * @return list of tasks assigned to the employee with the given state and end date
     */
    public List<Task>  findTasksByEmployeeByTaskStateByEndDate(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate endDate) {
        return employeeService.findTasksByEmployeeByTaskStateByEndDate(employeeId, taskState, endDate);
    }

    /**
     * Finds tasks assigned to a specific employee filtered by task state, start date range, and end date range.
     *
     * @param employeeId   the employee's ID (must not be null)
     * @param taskState    the state of tasks to filter (must not be null)
     * @param startDate   the start date to filter (must not be null)
     * @param endDate     the end date to filter (must not be null)
     * @return list of tasks assigned to the employee with the given state and within the specified date ranges
     */
    public List<Task>  findTasksByEmployeeByTaskStateByStartDateBetweenAndEndDateBetween(@NonNull Long employeeId, @NonNull TaskState taskState, @NonNull LocalDate startDate, @NonNull LocalDate endDate) {
        return employeeService.findTasksByEmployeeByTaskStateBetweenStartDateAndEndDate(employeeId, taskState, startDate, endDate);
    }

    /**
     * Finds tasks assigned to a specific employee filtered by task state, ordered by starting date descendant
     *
     * @param employeeId   the employee's ID (must not be null)
     * @param taskState    the state of tasks to filter (must not be null)
     * @return list of tasks assigned to the employee with the given state and within the specified date conditions
     */
    public List<Task> findTasksByEmployeeByTaskStateOrderByStartDateDesc(@NonNull Long employeeId, @NonNull TaskState taskState){
        return employeeService.findTasksByEmployeeByTaskStateOrderByStartDateDesc(employeeId, taskState);
    }

    /**
     * Finds tasks assigned to a specific employee filtered by task state, ordered by ending date descendant
     *
     * @param employeeId   the employee's ID (must not be null)
     * @param taskState    the state of tasks to filter (must not be null)
     * @return list of tasks assigned to the employee with the given state and within the specified date conditions
     */
    public List<Task> findTasksByEmployeeByTaskStateOrderByEndDateDesc(@NonNull Long employeeId, @NonNull TaskState taskState) {
        return employeeService.findTasksByEmployeeByTaskStateOrderByEndDateDesc(employeeId, taskState);
    }
    /**
     * Finds employees under a manager filtered by role.
     *
     * @param employeeId   the manager's employee ID (must not be null)
     * @param employeeRole the role to filter (must not be null)
     * @return list of employees matching the role
     */
    public List<Employee> findEmployeesByEmployeeRole(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRole(employeeId, employeeRole);
    }

    /**
     * Finds employees under a manager filtered by role, ordered ascending by salary.
     *
     * @param employeeId   the manager's employee ID (must not be null)
     * @param employeeRole the role to filter (must not be null)
     * @return list of employees sorted by salary ascending
     */
    public List<Employee> findEmployeesByEmployeeRoleAscByMonthlySalary(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(employeeId, employeeRole);
    }

    /**
     * Finds employees under a manager filtered by role, ordered descending by salary.
     *
     * @param employeeId   the manager's employee ID (must not be null)
     * @param employeeRole the role to filter (must not be null)
     * @return list of employees sorted by salary descending
     */
    public List<Employee> findEmployeesByEmployeeRoleDescByMonthlySalary(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRoleOrderByMonthlySalaryDesc(employeeId, employeeRole);
    }

    /**
     * Updates an employee's monthly salary.
     *
     * @param managerId     the manager's ID (must not be null)
     * @param employeeId    the employee's ID (must not be null)
     * @param monthlySalary the new monthly salary
     */
    public void updateMonthlySalaryById(@NonNull Long managerId, @NonNull Long employeeId, double monthlySalary) {
        employeeService.updateMonthlySalaryById(managerId, employeeId, monthlySalary);
    }

    /**
     * Updates an employee's role.
     *
     * @param managerId    the manager's ID (must not be null)
     * @param employeeId   the employee's ID (must not be null)
     * @param employeeRole the new role (must not be null)
     */
    public void updateEmployeeRoleById(@NonNull Long managerId, @NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        employeeService.updateEmployeeRoleById(managerId, employeeId, employeeRole);
    }

    /**
     * Finds tasks assigned to a specific employee filtered by task state.
     *
     * @param employeeId the employee's ID (must not be null)
     * @param taskState  the state of tasks to filter (must not be null)
     * @return list of tasks assigned to the employee with the given state
     */
    public List<Task> findTasksByEmployeeAndTaskState(@NonNull Long employeeId, @NonNull TaskState taskState) {
        return employeeService.findTasksByEmployeeAndTaskState(employeeId, taskState);
    }

    // <---- Supervisor Methods ---->

    /**
     * Creates a new supervisor with only name and surname.
     *
     * @param name    the supervisor's first name (must not be null)
     * @param surname the supervisor's surname (must not be null)
     * @return the newly created Supervisor object
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname) {
        return supervisorService.createSupervisor(name, surname);
    }

    /**
     * Creates a new supervisor with name, surname, and role.
     *
     * @param name         the supervisor's first name (must not be null)
     * @param surname      the supervisor's surname (must not be null)
     * @param employeeRole the supervisor's role (must not be null)
     * @return the newly created Supervisor object
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        return supervisorService.createSupervisor(name, surname, employeeRole);
    }

    /**
     * Creates a new supervisor with name, surname, role, and monthly salary.
     *
     * @param name          the supervisor's first name (must not be null)
     * @param surname       the supervisor's surname (must not be null)
     * @param monthlySalary the supervisor's monthly salary
     * @param employeeRole  the supervisor's role (must not be null)
     * @return the newly created Supervisor object
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return supervisorService.createSupervisor(name, surname, monthlySalary, employeeRole);
    }

    /**
     * Creates a new supervisor with name, surname, role, and monthly salary.
     *
     * @param name          the supervisor's first name (must not be null)
     * @param surname       the supervisor's surname (must not be null)
     * @param monthlySalary the supervisor's monthly salary
     * @param employeeRole  the supervisor's role (must not be null)
     * @return the newly created Supervisor object
     */
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole, @NonNull Supervisor supervisor, List<Supervisor> subordinates) {
        return supervisorService.createSupervisor(name, surname, monthlySalary, employeeRole, supervisor, subordinates);
    }

    /**
     * Saves a supervisor to the database.
     *
     * @param supervisor the supervisor to save (must not be null)
     * @return the saved Supervisor object
     */
    public Supervisor saveSupervisor(@NonNull Supervisor supervisor) {
        return supervisorService.saveSupervisor(supervisor);
    }

    /**
     * Retrieves a supervisor by ID.
     *
     * @param supervisorId the supervisor's ID (must not be null)
     * @return an Optional containing the Supervisor if found
     */
    public Optional<Supervisor> findSupervisorById(@NonNull Long supervisorId) {
        return supervisorService.findSupervisorById(supervisorId);
    }

    /**
     * Retrieves all supervisors.
     *
     * @return list of all Supervisor objects
     */
    public List<Supervisor> findAllSupervisors() {
        return supervisorService.findAllSupervisors();
    }

    /**
     * Deletes a supervisor by ID.
     *
     * @param supervisorId the supervisor's ID (must not be null)
     */
    public void deleteSupervisorById(@NonNull Long supervisorId) {
        supervisorService.deleteSupervisorById(supervisorId);
    }

    /**
     * Assigns a subordinate to a supervisor.
     *
     * @param supervisorId  the supervisor's ID (must not be null)
     * @param subordinateId the subordinate's ID (must not be null)
     */
    public void assignSubordinate(@NonNull Long supervisorId, @NonNull Long subordinateId) {
        supervisorService.assignSubordinate(supervisorId, subordinateId);
    }

    /**
     * Removes a subordinate from a supervisor.
     *
     * @param supervisorId  the supervisor's ID (must not be null)
     * @param subordinateId the subordinate's ID (must not be null)
     */
    public void removeSubordinate(@NonNull Long supervisorId, @NonNull Long subordinateId) {
        supervisorService.removeSubordinate(supervisorId, subordinateId);
    }

    /**
     * Finds supervisors without a supervisor (top-level supervisors).
     *
     * @return list of top-level supervisors
     */
    public List<Supervisor> findSupervisorsWithoutSupervisor() {
        return supervisorService.findSupervisorsWithoutSupervisor();
    }

    /**
     * Finds supervisors without any subordinates.
     *
     * @return list of supervisors with no subordinates
     */
    public List<Supervisor> findSupervisorsWithoutSubordinates() {
        return supervisorService.findSupervisorsWithoutSubordinates();
    }

    /**
     * Finds supervisors without any supervised teams.
     *
     * @return list of supervisors who are not supervising any team
     */
    public List<Supervisor> findSupervisorsWithoutSupervisedTeam() {
        return supervisorService.findSupervisorsWithoutSupervisedTeam();
    }
    // <---- Task ---->

    /**
     * Creates a new task with an initial state.
     *
     * @param initialState the starting state of the task
     * @return the newly created task entity
     */
    public Task createTask(TaskState initialState) {
        return taskService.createTask(initialState);
    }

    /**
     * Saves or updates a task in the database.
     *
     * @param task the task entity to save
     * @return the persisted task entity
     */
    public Task saveTask(Task task) {
        return taskService.saveTask(task);
    }

    /**
     * Assigns an employee to a specific task.
     *
     * @param taskId     the ID of the task
     * @param employeeId the ID of the employee to assign
     */
    public void assignEmployeeToTask(Long taskId, Long employeeId) {
        taskService.assignEmployeeToTask(taskId, employeeId);
    }

    /**
     * Removes an employee from a specific task.
     *
     * @param taskId     the ID of the task
     * @param employeeId the ID of the employee to remove
     */
    public void removeEmployeeToTask(Long taskId, Long employeeId) {
        taskService.removeEmployeeFromTask(taskId, employeeId);
    }

    /**
     * Transitions a task to a new state and updates relevant dates.
     *
     * @param taskId       the ID of the task to update
     * @param newTaskState the target state
     * @return the updated task entity
     */
    public Task changeTaskState(Long taskId, TaskState newTaskState) {
        return taskService.changeTaskState(taskId, newTaskState);
    }

    /**
     * Retrieves tasks filtered by their current state.
     *
     * @param taskState the task state to filter by
     * @return a list of matching tasks
     */
    public List<Task> getTasksByState(TaskState taskState) {
        return taskService.getTasksByState(taskState);
    }

    /**
     * Retrieves all tasks assigned to a specific employee.
     *
     * @param employee the employee entity
     * @return a list of tasks assigned to the employee
     */
    public List<Task> getTasksByEmployee(Employee employee) {
        return taskService.getTasksByEmployee(employee);
    }

    /**
     * Retrieves all tasks that have no employees assigned.
     *
     * @return a list of unassigned tasks
     */
    public List<Task> getUnsignedTasks() {
        return taskService.getUnassignedTasks();
    }

    /**
     * Counts the total number of tasks in a specific state.
     *
     * @param taskState the task state to count
     * @return the count of tasks
     */
    public long countTasksByState(TaskState taskState) {
        return taskService.countTasksByState(taskState);
    }

    /**
     * Retrieves tasks that have more than a specified number of employees assigned.
     *
     * @param employeeThreshold the employee threshold
     * @return a list of complex tasks
     */
    public List<Task> getComplexTasks(int employeeThreshold) {
        return taskService.getComplexTasks(employeeThreshold);
    }

    /**
     * Finds a task by its unique ID.
     *
     * @param taskId the task ID
     * @return the task entity if found
     */
    public Task getTaskById(Long taskId) {
        return taskService.getTaskById(taskId);
    }

    /**
     * Retrieves all tasks in the system.
     *
     * @return a list of all tasks
     */
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    /**
     * Deletes a task from the system by ID.
     *
     * @param taskId the ID of the task to delete
     */
    public void deleteTask(Long taskId) {
        taskService.deleteTask(taskId);
    }

    /**
     * Checks if a specific employee is assigned to a specific task.
     *
     * @param taskId     the task ID
     * @param employeeId the employee ID
     * @return true if the employee is assigned, false otherwise
     */
    public boolean isEmployeeAssigned(Long taskId, Long employeeId) {
        return taskService.isEmployeeAssigned(taskId, employeeId);
    }

    /**
     * Resets a task to the default state and clears associated dates.
     *
     * @param taskId the ID of the task to reset
     */
    public void resetTask(Long taskId) {
        taskService.resetTask(taskId);
    }

    /**
     * Retrieves tasks in a specific state that have at least one employee assigned.
     *
     * @param taskState the task state
     * @return a list of matching tasks
     */
    public List<Task> findTasksByStateWithEmployee(TaskState taskState) {
        return taskService.getTasksByStateWithEmployees(taskState);
    }

    /**
     * Gets the total number of employees assigned to a specific task.
     *
     * @param taskId the task ID
     * @return the count of assigned employees
     */
    public Integer countEmployeeByTaskId(Long taskId) {
        return taskService.getEmployeeCountPerTask(taskId);
    }

    /**
     * Retrieves tasks that match a specific state and an exact employee count.
     *
     * @param taskState      the task state
     * @param employeeNumber the exact number of employees required
     * @return a list of matching tasks
     */
    public List<Task> findTasksByStateAndCountEmployee(TaskState taskState, int employeeNumber) {
        return taskService.getTasksByStateAndEmployeeCount(taskState, employeeNumber);
    }

    /**
     * Retrieves all tasks belonging to a specific team.
     *
     * @param idTeam the team ID
     * @return a list of tasks for that team
     */
    public List<Task> findTasksByTeamId(Long idTeam) {
        return taskService.getTasksByTeam(idTeam);
    }

    /**
     * Sets the list of employees assigned to a task.
     *
     * @param taskId    the ID of the task (must not be null)
     * @param employees the list of employees to assign (must not be null)
     */
    public void setAssignedEmployees(@NonNull Long taskId, @NonNull List<Employee> employees) {
        taskService.setAssignedEmployees(taskId, employees);
    }

    /**
     * Sets the start date for a specific task.
     *
     * @param taskId    the task ID
     * @param startDate the new start date
     * @return the updated task entity
     */
    public Task setTaskStartDate(Long taskId, LocalDate startDate) {
        return taskService.setTaskStartDate(taskId, startDate);
    }

    /**
     * Sets the end date for a specific task.
     *
     * @param taskId  the task ID
     * @param endDate the new end date
     * @return the updated task entity
     */
    public Task setTaskEndDate(Long taskId, LocalDate endDate) {
        return taskService.setTaskEndDate(taskId, endDate);
    }


    // <---- Team ---->
    /**
     * Create a new team with the given supervisor.
     *
     * @param supervisor the supervisor of the team
     * @return the created team
     */
    public Team createTeam(Supervisor supervisor) {
        return teamService.createTeam(supervisor);
    }

    /**
     * Create a new team with the given employees and supervisor.
     *
     * @param employees the list of employees in the team
     * @param supervisor the supervisor of the team
     * @return the created team
     */
    public Team createTeam(List<Employee> employees, Supervisor supervisor) {
        return teamService.createTeam(employees, supervisor);
    }

    /**
     * Create a new team with the given employees, supervisor and tasks.
     *
     * @param employees the list of employees in the team
     * @param supervisor the supervisor of the team
     * @param tasks the list of tasks assigned to the team
     * @return the created team
     */
    public Team createTeam(List<Employee> employees, Supervisor supervisor, List<Task> tasks) {
        return teamService.createTeam(employees, supervisor, tasks);
    }

    /**
     * Save the given team.
     *
     * @param team the team to be saved
     * @return the saved team
     */
    public Team saveTeam(Team team) {
        return teamService.saveTeam(team);
    }

    /**
     * Delete the given team.
     *
     * @param team the team to be deleted
     */
    public void deleteTeam(Team team) {
        teamService.deleteTeam(team);
    }

    /**
     * Get the list of employees in the given team.
     *
     * @param team the team whose employees are to be retrieved
     * @return the list of employees in the team
     */
    public List<Employee> getEmployeesInTeam(Team team) {
        return teamService.getEmployeesInTeam(team);
    }

    /**
     * Add an employee to the given team.
     *
     * @param team the team to which the employee is to be added
     * @param employee the employee to be added to the team
     */
    public void addEmployeeToTeam(Team team, Employee employee) {
        teamService.addEmployeeToTeam(team, employee);
    }

    /**
     * Remove all employees from the given team.
     *
     * @param team the team from which all employees are to be removed
     */
    public void removeAllEmployeesFromTeam(Team team) {
        teamService.removeAllEmployeesFromTeam(team);
    }

    /**
     * Remove an employee from the given team.
     *
     * @param team the team from which the employee is to be removed
     * @param employee the employee to be removed from the team
     */
    public void removeEmployeeFromTeam(Team team, Employee employee) {
        teamService.removeEmployeeFromTeam(team, employee);
    }

    /**
     * Get the supervisor of the given team.
     *
     * @param team the team whose supervisor is to be retrieved
     * @return the supervisor of the team
     */
    public Supervisor getTeamSupervisor(Team team) {
        return teamService.getTeamSupervisor(team);
    }

    /**
     * Get the list of tasks assigned to the given team.
     *
     * @param team the team whose tasks are to be retrieved
     * @return the list of tasks assigned to the team
     */
    public List<Task> getTeamTasks(Team team) {
        return teamService.getTeamTasks(team);
    }

    /**
     * Add a task to the given team.
     *
     * @param team the team to which the task is to be added
     * @param task the task to be added to the team
     */
    public void addTaskToTeam(Team team, Task task) {
        teamService.addTaskToTeam(team, task);
    }

    /**
     * Remove all tasks from the given team.
     *
     * @param team the team from which all tasks are to be removed
     */
    public void removeAllTasksFromTeam(Team team) {
        teamService.removeAllTasksFromTeam(team);
    }

    /**
     * Remove a task from the given team.
     *
     * @param team the team from which the task is to be removed
     * @param task the task to be removed from the team
     */
    public void removeTaskFromTeam(Team team, Task task) {
        teamService.removeTaskFromTeam(team, task);
    }

    /**
     * Get a team by its ID.
     *
     * @param id the ID of the team
     * @return an Optional containing the team if found, or empty if not found
     */
    public Optional<Team> getTeamById(Long id) {
        return teamService.getTeamById(id);
    }

    /**
     * Get all teams.
     *
     * @return the list of all teams
     */
    public List<Team> getAllTeams() {
        return teamService.getAllTeams();
    }

    /**
     * Delete a team by its ID.
     *
     * @param id the ID of the team to be deleted
     */
    public void deleteTeamById(Long id) {
        teamService.deleteTeamById(id);
    }

    /**
     * Get teams by supervisor's person ID.
     *
     * @param supervisorId the supervisor's ID
     * @return the list of teams supervised by the given supervisor
     */
    public List<Team> getTeamsBySupervisorPersonId(Long supervisorId) {
        return teamService.getTeamsBySupervisorPersonId(supervisorId);
    }

    /**
     * Get team by employee's person ID.
     *
     * @param employeeId the employee's ID
     * @return the team associated with the given employee
     */
    public Team getTeamByEmployeePersonId(Long employeeId) {
        return teamService.getTeamByEmployeesPersonId(employeeId);
    }

    /**
     * Get team by task ID.
     *
     * @param taskId the task's ID
     * @return the team associated with the given task
     */
    public Team getTeamByTask_Id(Long taskId) {
        return teamService.getTeamByTaskId(taskId);
    }

    /**
     * Get tasks by team ID.
     *
     * @param teamId the team's ID
     * @return the list of tasks associated with the given team
     */
    public List<Task> getTasksByTeamId(Long teamId) {
        return teamService.getTasksByTeamId(teamId);
    }

    /**
     * Get supervisor by team ID.
     *
     * @param teamId the team's ID
     * @return the supervisor associated with the given team
     */
    public Supervisor getSupervisorByTeamId(Long teamId) {
        return teamService.getSupervisorByTeamId(teamId);
    }

    /**
     * Get employees by team ID.
     *
     * @param teamId the team's ID
     * @return the list of employees associated with the given team
     */
    public List<Employee> getEmployeesByTeamId(Long teamId) {
        return teamService.getEmployeesByTeamId(teamId);
    }

    /**
     * Get tasks in a team by task state.
     *
     * @param teamId the team's ID
     * @param taskState the state of the tasks to filter by
     * @return the list of tasks in the given team with the specified state
     */
    public List<Task> getTasksInTeamIdByTaskState(Long teamId, TaskState taskState) {
        return teamService.getTasksInTeamIdByTaskState(teamId, taskState);
    }

    /**
     * Get employees in a team with the salary greater than the specified amount.
     *
     * @param teamId the team's ID
     * @param salary the salary threshold
     * @return the list of employees in the given team with salary greater than the specified amount
     */
    public List<Employee> getEmployeesInTeamIdWithSalaryGreaterThan(Long teamId, double salary) {
        return teamService.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, salary);
    }

    /**
     * Get employees in a team with salary less than the specified amount.
     *
     * @param teamId the team's ID
     * @param salary the salary threshold
     * @return the list of employees in the given team with salary less than the specified amount
     */
    public List<Employee> getEmployeesInTeamIdWithSalaryLessThan(Long teamId, double salary) {
        return teamService.getEmployeesInTeamIdWithSalaryLessThan(teamId, salary);
    }
    /**
     * Get employees in a team with a specific employee role.
     *
     * @param teamId the team's ID
     * @param employeeRole the role of the employees to filter by
     * @return the list of employees in the given team with the specified role
     */
    public List<Employee> getEmployeesInTeamIdWithEmployeeRole(Long teamId, EmployeeRole employeeRole) {
        return teamService.getEmployeesInTeamIdWithEmployeeRole(teamId, employeeRole);
    }
}
