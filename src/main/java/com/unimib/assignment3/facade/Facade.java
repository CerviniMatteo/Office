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

    // <---- Team ---->
    // Function for team service
    public Team createTeam(Supervisor supervisor) {
        return teamService.createTeam(supervisor);
    }
    public Team createTeam(List<Employee> dipendenti, Supervisor supervisor) {
        return teamService.createTeam(dipendenti, supervisor);
    }
    public Team createTeam(List<Employee> dipendenti, Supervisor supervisor, List<Task> tasks) {
        return teamService.createTeam(dipendenti, supervisor, tasks);
    }
    public Team saveTeam(Team team) {
        return teamService.saveTeam(team);
    }
    public void deleteTeam(Team team) {
        teamService.deleteTeam(team);
    }
    public List<Employee> getDipendentiInTeam(Team team) {
        return teamService.getDipendentiInTeam(team);
    }
    public void addDipendenteToTeam(Team team, Employee employee) {
        teamService.addDipendenteToTeam(team, employee);
    }
    public void removeAllDipendentiFromTeam(Team team) {
        teamService.removeAllDipendentiFromTeam(team);
    }
    public void removeDipendenteFromTeam(Team team, Employee employee) {
        teamService.removeDipendenteFromTeam(team, employee);
    }
    public Supervisor getTeamSupervisore(Team team) {
        return teamService.getTeamSupervisore(team);
    }
    public void setTeamSupervisore(Team team, Supervisor supervisor) {
        teamService.setTeamSupervisore(team, supervisor);
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
    public List<Team> getTeamsBySupervisore_Id(Long supervisoreId) {
        return teamService.getTeamsBySupervisore_Id(supervisoreId);
    }
    public Team getTeamByDipendente_Id(Long dipendenteId) {
        return teamService.getTeamByDipendente_Id(dipendenteId);
    }
    public Team getTeamByTask_Id(Long taskId) {
        return teamService.getTeamByTask_Id(taskId);
    }
    public List<Task> getTasksByTeamId(Long teamId) {
        return teamService.getTasksByTeamId(teamId);
    }
    public Supervisor getSupervisoreByTeamId(Long teamId) {
        return teamService.getSupervisoreByTeamId(teamId);
    }
    public List<Employee> getDipendentiByTeamId(Long teamId) {
        return teamService.getDipendentiByTeamId(teamId);
    }
    public List<Task> getTasksInTeamIdByTaskState(Long teamId, TaskState taskState) {
        return teamService.getTasksInTeamIdByTaskState(teamId, taskState);
    }
    public List<Employee> getDipendentiInTeamIdWithSalaryGreaterThan(Long teamId, Double salary) {
        return teamService.getDipendentiInTeamIdWithSalaryGreaterThan(teamId, salary);
    }
    public List<Employee> getDipendentiInTeamIdWithSalaryLessThan(Long teamId, Double salary) {
        return teamService.getDipendentiInTeamIdWithSalaryLessThan(teamId, salary);
    }
    public List<Employee> getDipendentiInTeamIdWithGrado(Long teamId, EmployeeRole employeeRole) {
        return teamService.getDipendentiInTeamIdWithGrado(teamId, employeeRole);
    }
}
