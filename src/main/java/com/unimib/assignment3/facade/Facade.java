package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.TeamRepository;
import com.unimib.assignment3.service.EmployeeService;
import com.unimib.assignment3.service.SupervisorService;
import com.unimib.assignment3.service.TaskService;
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
    TeamRepository teamRepository;
    @Autowired
    TaskService taskService;

    // <---- Employee ----
    public Dipendente createEmployee(@NonNull String name, @NonNull String surname) {
        return employeeService.createEmployee(name, surname);
    }

    public Dipendente createEmployee(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        return  employeeService.createEmployee(name, surname, employeeRole);
    }

    public Dipendente createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return  employeeService.createEmployee(name, surname, monthlySalary, employeeRole);
    }

    public Dipendente saveEmployee(@NonNull Dipendente employee) {
        return employeeService.saveEmployee(employee);
    }

    public List<Dipendente> saveAllEmployees(@NonNull List<Dipendente> employees) {
        return employeeService.saveAllEmployees(employees);
    }

    public Optional<Dipendente> findEmployeeById(@NonNull Long employeeId) {
        return employeeService.findEmployeeById(employeeId);
    }

    public List<Dipendente> findAllEmployees(){
        return employeeService.findAllEmployees();
    }

    public void fireEmployee(@NonNull Long managerId,@NonNull Long employeeId) {
        employeeService.fireEmployee(managerId, employeeId);
    }

    public void fireEmployees(@NonNull Long managerId,@NonNull List<Dipendente> employees) {
        employeeService.fireEmployees(managerId, employees);
    }

    public List<Dipendente> findEmployeesByMonthlySalary(@NonNull Long employeeId, double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalary(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByMonthlySalaryAscByEmployeeRole(@NonNull Long employeeId,double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByMonthlySalaryDescByEmployeeRole(@NonNull Long employeeId,double monthlySalary) {
        return employeeService.findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByEmployeeRole(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRole(employeeId, employeeRole);
    }

    public List<Dipendente> findEmployeesByEmployeeRoleAscByMonthlySalary(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        return employeeService.findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(employeeId, employeeRole);
    }

    public List<Dipendente> findEmployeesByEmployeeRoleDescByMonthlySalary(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
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
    public Supervisore createSupervisor(@NonNull String name, @NonNull String surname) {
        return supervisorService.createSupervisor(name, surname);
    }

    public Supervisore createSupervisor(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        return  supervisorService.createSupervisor(name, surname, employeeRole);
    }

    public Supervisore createSupervisor(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return  supervisorService.createSupervisor(name, surname, monthlySalary, employeeRole);
    }

    public Supervisore saveSupervisor(@NonNull Supervisore supervisor) {
        return supervisorService.saveSupervisor(supervisor);
    }

    public Optional<Supervisore> findSupervisorById(@NonNull Long supervisorId) {
        return supervisorService.findSupervisorById(supervisorId);
    }

    public List<Supervisore> findAllSupervisors() {
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

    public List<Supervisore> findSupervisorsWithoutSupervisor() {
        return supervisorService.findSupervisorsWithoutSupervisor();
    }

    public List<Supervisore> findSupervisorsWithoutSubordinates() {
        return supervisorService.findSupervisorsWithoutSubordinates();
    }

    public List<Supervisore> findSupervisorsWithoutSupervisionedTeam() {
        return supervisorService.findSupervisorsWithoutSupervisionedTeam();
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

    public List<Task> getTasksByEmployee(Dipendente employee) {
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
    public Team saveTeam(Team team){
        return teamRepository.saveAndFlush(team);
    }
}
