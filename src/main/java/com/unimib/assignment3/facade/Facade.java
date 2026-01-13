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

@Service
public class Facade {

    @Autowired
    EmployeeService employeeService;
    @Autowired
    SupervisorService supervisorService;
    @Autowired
    TaskService taskService;
    @Autowired
    private TeamService teamService;

    // <---- Employee ----
    public Employee createEmployee(@NonNull String name, @NonNull String surname) {
        return employeeService.createEmployee(name, surname);
    }

    public Employee createEmployee(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        return  employeeService.createEmployee(name, surname, employeeRole);
    }

    public Employee createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return  employeeService.createEmployee(name, surname, monthlySalary, employeeRole);
    }

    public Employee saveEmployee(@NonNull Employee employee) {
        return employeeService.saveEmployee(employee);
    }

    public List<Employee> saveAllEmployees(@NonNull List<Employee> employees) {
        return employeeService.saveAllEmployees(employees);
    }

    public Optional<Employee> findEmployeeById(@NonNull Long employeeId) {
        return employeeService.findEmployeeById(employeeId);
    }

    public List<Employee> findAllEmployees(){
        return employeeService.findAllEmployees();
    }

    public void fireEmployee(@NonNull Long managerId,@NonNull Long employeeId) {
        employeeService.fireEmployee(managerId, employeeId);
    }

    public void fireEmployees(@NonNull Long managerId,@NonNull List<Employee> employees) {
        employeeService.fireEmployees(managerId, employees);
    }

    public List<Employee> findEmployeesByMonthlySalary(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalary(employeeId, monthlySalary);
    }

    public List<Employee> findEmployeesByMonthlySalaryAscByEmployeeRole(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(employeeId, monthlySalary);
    }

    public List<Employee> findEmployeesByMonthlySalaryDescByEmployeeRole(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(employeeId, monthlySalary);
    }

    public List<Employee> findEmployeesByEmployeeRole(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRole(employeeId, employeeRole);
    }

    public List<Employee> findEmployeesByEmployeeRoleAscByMonthlySalary(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(employeeId, employeeRole);
    }

    public List<Employee> findEmployeesByEmployeeRoleDescByMonthlySalary(@NonNull Long employeeId, @NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRoleOrderByMonthlySalaryDesc(employeeId, employeeRole);
    }

    public void updateMonthlySalaryById(@NonNull Long managerId,@NonNull Long employeeId,double monthlySalary){
        employeeService.updateMonthlySalaryById(managerId, employeeId, monthlySalary);
    }

    public void updateEmployeeRoleById(@NonNull Long managerId,@NonNull Long employeeId,@NonNull EmployeeRole employeeRole){
        employeeService.updateEmployeeRoleById(managerId, employeeId, employeeRole);
    }

    public List<Task> findTasksByEmployeeAndTaskState(@NonNull Long employeeId,@NonNull TaskState taskState) {
        return employeeService.findTasksByEmployeeAndTaskState(employeeId, taskState);
    }

    // <---- Supervisor ---->
    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname) {
        return supervisorService.createSupervisor(name, surname);
    }

    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        return  supervisorService.createSupervisor(name, surname, employeeRole);
    }

    public Supervisor createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return  supervisorService.createSupervisor(name, surname, monthlySalary, employeeRole);
    }

    public Supervisor saveSupervisor(@NonNull Supervisor supervisor) {
        return supervisorService.saveSupervisor(supervisor);
    }

    public Optional<Supervisor> findSupervisorById(@NonNull Long supervisorId) {
        return supervisorService.findSupervisorById(supervisorId);
    }

    public List<Supervisor> findAllSupervisors() {
        return supervisorService.findAllSupervisors();
    }

    public void deleteSupervisorById(@NonNull Long supervisorId) {
        supervisorService.deleteSupervisorById(supervisorId);
    }

    public void assignSubordinate(@NonNull Long supervisorId,@NonNull Long subordinateId) {
        supervisorService.assignSubordinate(supervisorId, subordinateId);
    }

    public void removeSubordinate(@NonNull Long supervisorId,@NonNull Long subordinateId) {
        supervisorService.removeSubordinate(supervisorId, subordinateId);
    }

    public List<Supervisor> findSupervisorsWithoutSupervisor() {
        return supervisorService.findSupervisorsWithoutSupervisor();
    }

    public List<Supervisor> findSupervisorsWithoutSubordinates() {
        return supervisorService.findSupervisorsWithoutSubordinates();
    }

    public List<Supervisor> findSupervisorsWithoutSupervisionedTeam() {
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
     * Set the supervisor of the given team.
     *
     * @param team the team whose supervisor is to be set
     * @param supervisor the supervisor to be set for the team
     */
    public void setTeamSupervisor(Team team, Supervisor supervisor) {
        teamService.setTeamSupervisor(team, supervisor);
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
     * Get employees in a team with salary greater than the specified amount.
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
     * Get employees in team with specific employee role.
     *
     * @param teamId the team's ID
     * @param employeeRole the role of the employees to filter by
     * @return the list of employees in the given team with the specified role
     */
    public List<Employee> getEmployeesInTeamIdWithEmployeeRole(Long teamId, EmployeeRole employeeRole) {
        return teamService.getEmployeesInTeamIdWithEmployeeRole(teamId, employeeRole);
    }
}
