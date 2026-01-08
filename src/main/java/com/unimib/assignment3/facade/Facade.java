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

    // <---- Employee ---->

    public Dipendente saveEmployee(Dipendente employee) {
        return dipendenteService.saveEmployee(employee);
    }

    public List<Dipendente> saveAllEmployees(List<Dipendente> employees) {
        return dipendenteService.saveAllEmployees(employees);
    }

    public Optional<Dipendente> findEmployeeById(Long id) {
        return dipendenteService.findEmployeeById(id);
    }

    public List<Dipendente> findAllEmployees(){
        return dipendenteService.findAllEmployees();
    }

    public void fireEmployee(Long managerId, Long employeeId) {
        dipendenteService.fireEmployee(managerId, employeeId);
    }

    public void fireEmployees(Long managerId, List<Dipendente> employees) {
        dipendenteService.fireEmployees(managerId, employees);
    }

    public List<Dipendente> findEmployeesByMonthlySalary(Long employeeId, Double monthlySalary) {
        return dipendenteService.findEmployeesByMonthlySalary(employeeId, monthlySalary);
    }

    public List<Dipendente> findEmployeesByEmployeeRole(Long employeeId, EmployeeRole employeeRole) {
        return dipendenteService.findEmployeesByEmployeeRole(employeeId, employeeRole);
    }

    public List<Task> findTasksByEmployeeAndTaskState(Long dipendenteId, TaskState stato) {
        return dipendenteService.findTasksByEmployeeAndTaskState(dipendenteId, stato);
    }

    public void updateMonthlySalaryById(Long managerId, Long employeeId, Double monthlySalary){
        dipendenteService.updateMonthlySalaryById(managerId, employeeId, monthlySalary);
    }

    public void updateEmployeeRoleById(Long managerId, Long employeeId, EmployeeRole employeeRole){
        dipendenteService.updateEmployeeRoleById(managerId, employeeId, employeeRole);
    }

    // <---- Supervisor ---->

    public Supervisore saveSupervisor(Supervisore supervisore) {
        return supervisorService.saveSupervisor(supervisore);
    }

    public Optional<Supervisore> findSupervisorById(Long supervisorId) {
        return supervisorService.findById(supervisorId);
    }

    public List<Supervisore> findAllSupervisors() {
        return supervisorService.findAll();
    }

    public void deleteSupervisorById(Long supervisorId) {
        supervisorService.deleteById(supervisorId);
    }

    public List<Supervisore> trovaRoot() {
        return supervisorService.findSupervisorsWithoutSubordinates();
    }

    public void assegnaSubordinato(Long capoId, Long subordinatoId) {
        supervisorService.assignSubordinates(capoId, subordinatoId);
    }

    public void rimuoviSubordinato(Long capoId, Long subordinatoId) {
        supervisorService.removeSubordinate(capoId, subordinatoId);
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
