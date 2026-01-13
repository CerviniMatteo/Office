package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Employee;
import com.unimib.assignment3.POJO.Supervisor;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.service.EmployeeService;
import com.unimib.assignment3.service.SupervisorService;
import com.unimib.assignment3.service.TaskService;
import com.unimib.assignment3.service.TeamService;
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

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SupervisorService supervisorService;

    @Autowired
    private TaskService taskService;

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
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole, @NonNull Supervisor supervisor, List<Supervisor> subordinates, List<Team> supervisedTeams) {
        return supervisorService.createSupervisor(name, surname, monthlySalary, employeeRole, supervisor, subordinates, supervisedTeams);
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

    public Task createTask(TaskState initialState) {
        return taskService.createTask(initialState);
    }


    public Task saveTask(Task task) {
        return taskService.saveTask(task);
    }

    public void assignEmployeeToTask(Long taskId, Long employeeId) {
        taskService.assignEmployeeToTask(taskId, employeeId);
    }

    public void removeEmployeeToTask(Long taskId, Long employeeId) {
        taskService.removeEmployeeFromTask(taskId, employeeId);
    }

    public Task changeTaskState(Long taskId, TaskState newTaskState) {
        return taskService.changeTaskState(taskId, newTaskState);
    }

    public List<Task> getTasksByState(TaskState taskState) {
        return taskService.getTasksByState(taskState);
    }

    public List<Task> getTasksByEmployee(Employee employee) {
        return taskService.getTasksByEmployee(employee);
    }

    public List<Task> getUnsignedTasks() {
        return taskService.getUnassignedTasks();
    }

    public long countTasksByState(TaskState taskState) {
        return taskService.countTasksByState(taskState);
    }

    public List<Task> getComplexTasks(int employeeThreshold) {
        return taskService.getComplexTasks(employeeThreshold);
    }

    public Optional<Task> getTaskById(Long taskId) {
        return taskService.getTaskById(taskId);
    }

    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    public void deleteTask(Long taskId) {
        taskService.deleteTask(taskId);
    }

    public boolean isEmployeeAssigned(Long taskId, Long employeeId) {
        return taskService.isEmployeeAssigned(taskId, employeeId);
    }

    public void resetTask(Long taskId) {
        taskService.resetTask(taskId);
    }


    public List<Task> findTasksByStateWithEmployee(TaskState taskState) {
        return taskService.getTasksByStateWithEmployees(taskState);
    }

    public Integer countEmployeeByTaskId(Long taskId) {
        return taskService.getEmployeeCountPerTask(taskId);
    }

    public List<Task> findTasksByStateAndCountEmployee(TaskState taskState, int employeeNumber) {
        return taskService.getTasksByStateAndEmployeeCount(taskState, employeeNumber);
    }

    public List<Task> findTasksByTeamId(Long idTeam) {
        return taskService.getTasksByTeam(idTeam);
    }

    public Task setTaskStartDate(Long taskId, LocalDate startDate) {
        return taskService.setTaskStartDate(taskId, startDate);
    }

    public Task setTaskEndDate(Long taskId, LocalDate endDate) {
        return taskService.setTaskEndDate(taskId, endDate);
    }

    public void setTeamTask(Task task, Team team) {
        taskService.setTeamTask(task, team);
    }

    public Team getTeamTask(Task task) {
        return taskService.getTeamTask(task);
    }

    // <---- Team ---->

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
    public void setTeamSupervisor(Team team, Supervisor supervisor) {
        teamService.setTeamSupervisor(team, supervisor);
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
    public List<Employee> getEmployeesInTeamIdWithSalaryGreaterThan(Long teamId, Double salary) {
        return teamService.getEmployeesInTeamIdWithSalaryGreaterThan(teamId, salary);
    }
    public List<Employee> getEmployeesInTeamIdWithSalaryLessThan(Long teamId, Double salary) {
        return teamService.getEmployeesInTeamIdWithSalaryLessThan(teamId, salary);
    }
    public List<Employee> getEmployeesInTeamIdWithEmployeeRole(Long teamId, EmployeeRole employeeRole) {
        return teamService.getEmployeesInTeamIdWithEmployeeRole(teamId, employeeRole);
    }
}
