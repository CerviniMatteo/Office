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
    SupervisoreService supervisoreService;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TaskService taskService;

    // <---- Dipendente ---->

    public Dipendente saveDipendente(Dipendente dipendente) {
        return dipendenteService.saveEmployee(dipendente);
    }

    public List<Dipendente> saveAllDipendenti(List<Dipendente> dipendenti) {
        return dipendenteService.saveAllEmployees(dipendenti);
    }

    public Optional<Dipendente> trovaPerIdDipendente(Long id) {
        return dipendenteService.findById(id);
    }

    public Dipendente getReferenceDipendente(Long id) {
        return dipendenteService.getReferenceById(id);
    }

    public List<Dipendente> trovaTuttiDipendenti() {
        return dipendenteService.findAll();
    }

    public boolean esisteDipendentePerId(Long id) {
        return dipendenteService.existById(id);
    }

    public long contaTuttiDipendenti() {
        return dipendenteService.countAll();
    }

    public void eliminaPerIdDipendente(Long id) {
        dipendenteService.deleteById(id);
    }

    public void eliminaDipendente(Dipendente dipendente) {
        dipendenteService.deleteEmployee(dipendente);
    }

    public void eliminaTuttiDipendenti(List<Dipendente> dipendenti) {
        dipendenteService.deleteAllByList(dipendenti);
    }

    public void eliminaTuttiDipendenti() {
        dipendenteService.deleteAll();
    }

    public void flushDipendenti() {
        dipendenteService.flush();
    }

    public List<Dipendente> findDipendentiByStipendio(Long employeeId, Double monthlySalary) {
        return dipendenteService.findEmployeesByMonthlySalary(employeeId, monthlySalary);
    }

    public List<Dipendente> findDipendentiByGrado(EmployeeRole employeeRole) {
        return dipendenteService.findEmployeesByEmployeeRole(employeeRole);
    }

    public List<Task> findTasksByDipendenteAndState(Long dipendenteId, TaskState stato) {
        return dipendenteService.findTasksByDipendenteAndState(dipendenteId, stato);
    }

    // <---- Supervisore ---->

    public Supervisore saveSupervisore(Supervisore supervisore) {
        return supervisoreService.saveSupervisore(supervisore);
    }

    public Optional<Supervisore> trovaPerId(Long id) {
        return supervisoreService.trovaPerId(id);
    }

    public List<Supervisore> trovaTutti() {
        return supervisoreService.trovaTutti();
    }

    public boolean esistePerId(Long id) {
        return supervisoreService.esistePerId(id);
    }

    public void eliminaPerId(Long id) {
        supervisoreService.eliminaPerId(id);
    }

    public void elimina(Supervisore supervisore) {
        supervisoreService.elimina(supervisore);
    }

    public long conta() {
        return supervisoreService.contaSupervisori();
    }

    public List<Supervisore> trovaSupervisionati(Long supervisoreId) {
        return supervisoreService.trovaSupervisoriSupervisionatiPerId(supervisoreId);
    }

    public List<Supervisore> trovaRoot() {
        return supervisoreService.trovaSupervisoriSenzaSupervisore();
    }

    public long contaSupervisionati(Long supervisoreId) {
        return supervisoreService.contaSupervisoriSupervisionati(supervisoreId);
    }

    public boolean haSubordinati(Long supervisoreId) {
        return supervisoreService.esisteSupervisoreConSubordinati(supervisoreId);
    }

    public List<Supervisore> trovaConTeam() {
        return supervisoreService.trovaSupervisoriConTeam();
    }

    public List<Supervisore> trovaConSubordinati() {
        return supervisoreService.trovaSupervisoriConSubordinati();
    }

    public Supervisore trovaPerTeam(Long teamId) {
        return supervisoreService.trovaSupervisorePerTeam(teamId);
    }

    public void assegnaSubordinato(Long capoId, Long subordinatoId) {
        supervisoreService.assegnaSubordinato(capoId, subordinatoId);
    }

    public void rimuoviSubordinato(Long capoId, Long subordinatoId) {
        supervisoreService.rimuoviSubordinato(capoId, subordinatoId);
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
