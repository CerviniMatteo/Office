package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.repository.TeamRepository;
import com.unimib.assignment3.service.DipendenteService;
import com.unimib.assignment3.service.SupervisoreService;
import com.unimib.assignment3.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class Facade {

    @Autowired
    DipendenteService dipendenteService;
    @Autowired
    SupervisoreService supervisorService;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TaskService taskService;

    // <---- Employee ----
    public Dipendente createEmployee(@NonNull String name, @NonNull String surname) {
        return dipendenteService.createEmployee(name, surname);
    }

    public Dipendente createEmployee(@NonNull String name, @NonNull String surname, @NonNull EmployeeRole employeeRole) {
        return  dipendenteService.createEmployee(name, surname, employeeRole);
    }

    public Dipendente createEmployee(@NonNull String name, @NonNull String surname, double monthlySalary, @NonNull EmployeeRole employeeRole) {
        return  dipendenteService.createEmployee(name, surname, monthlySalary, employeeRole);
    }

    public Dipendente saveEmployee(@NonNull Dipendente employee) {
        return dipendenteService.saveEmployee(employee);
    }

    public List<Dipendente> saveAllEmployees(@NonNull List<Dipendente> employees) {
        return dipendenteService.saveAllEmployees(employees);
    }

    public Optional<Dipendente> findEmployeeById(@NonNull Long employeeId) {
        return dipendenteService.findEmployeeById(employeeId);
    }

    public List<Dipendente> findAllEmployees(){
        return dipendenteService.findAllEmployees();
    }

    public void fireEmployee(@NonNull Long managerId,@NonNull Long employeeId) {
        dipendenteService.fireEmployee(managerId, employeeId);
    }

    public void fireEmployees(@NonNull Long managerId,@NonNull List<Dipendente> employees) {
        dipendenteService.fireEmployees(managerId, employees);
    }

    public List<Dipendente> findEmployeesByMonthlySalary(@NonNull Long employeeId,double monthlySalary) {
        return dipendenteService.findEmployeesByMonthlySalary(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByMonthlySalaryAscByEmployeeRole(@NonNull Long employeeId,double monthlySalary) {
        return dipendenteService.findEmployeesByMonthlySalaryOrderByEmployeeRoleAsc(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByMonthlySalaryDescByEmployeeRole(@NonNull Long employeeId,double monthlySalary) {
        return dipendenteService.findEmployeesByMonthlySalaryOrderByEmployeeRoleDesc(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByEmployeeRole(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        return dipendenteService.findEmployeesByEmployeeRole(employeeId, employeeRole);
    }

    public List<Dipendente> findEmployeesByEmployeeRoleAscByMonthlySalary(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        return dipendenteService.findEmployeesByEmployeeRoleOrderByMonthlySalaryAsc(employeeId, employeeRole);
    }

    public List<Dipendente> findEmployeesByEmployeeRoleDescByMonthlySalary(@NonNull Long employeeId,@NonNull EmployeeRole employeeRole) {
        return dipendenteService.findEmployeesByEmployeeRoleOrderByMonthlySalaryDesc(employeeId, employeeRole);
    }

    public void updateMonthlySalaryById(@NonNull Long managerId,@NonNull Long employeeId,double monthlySalary){
        dipendenteService.updateMonthlySalaryById(managerId, employeeId, monthlySalary);
    }

    public void updateEmployeeRoleById(@NonNull Long managerId,@NonNull Long employeeId,@NonNull EmployeeRole employeeRole){
        dipendenteService.updateEmployeeRoleById(managerId, employeeId, employeeRole);
    }

    public List<Task> findTasksByEmployeeAndTaskState(@NonNull Long employeeId,@NonNull TaskState taskState) {
        return dipendenteService.findTasksByEmployeeAndTaskState(employeeId, taskState);
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
    public Task saveTask(Task task){
        return taskRepository.saveAndFlush(task);
    }

    public Team saveTeam(Team team){
        return teamRepository.saveAndFlush(team);
    }

    public Task createTask(TaskState initialState) {
        return taskService.createTask(initialState);
    }

    public Task assegnaDipendenteATask(Long taskId, Long dipendenteId) {
        return taskService.assegnaDipendenteATask(taskId, dipendenteId);
    }

    public Task rimuoviDipendenteDaTask(Long taskId, Long dipendenteId) {
        return taskService.rimuoviDipendenteDaTask(taskId, dipendenteId);
    }

    public Task cambiaStatoTask(Long taskId, TaskState nuovoStato) {
        return taskService.cambiaStatoTask(taskId, nuovoStato);
    }

    public List<Task> getTasksByStato(TaskState stato) {
        return taskService.getTasksByStato(stato);
    }

    public List<Task> getTasksByDipendente(Dipendente dipendente) {
        return taskService.getTasksByDipendente(dipendente);
    }

    public List<Task> getTasksNonAssegnati() {
        return taskService.getTasksNonAssegnati();
    }

    public long countTasksByStato(TaskState stato) {
        return taskService.countTasksByStato(stato);
    }

    public List<Task> getTasksComplessi(int sogliaDipendenti) {
        return taskService.getTasksComplessi(sogliaDipendenti);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskService.getTaskById(id);
    }

    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    public void deleteTask(Long id) {
        taskService.deleteTask(id);
    }
    public void deleteAllTasks() {taskRepository.deleteAll();}

    public boolean isDipendenteAssegnato(Long taskId, Long dipendenteId) {
        return taskService.isDipendenteAssegnato(taskId, dipendenteId);
    }

    // <---- Team ---->
}
