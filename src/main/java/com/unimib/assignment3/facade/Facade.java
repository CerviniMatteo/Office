package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.SupervisoreRepository;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.repository.TeamRepository;
import com.unimib.assignment3.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Facade {

    @Autowired
    DipendenteRepository dipendenteRepository;
    @Autowired
    SupervisoreRepository supervisoreRepository;
    @Autowired
    TaskRepository taskRepository;
    @Autowired
    TeamRepository teamRepository;
    @Autowired
    TaskService taskService;

    public Dipendente saveDipendente(Dipendente dipendente) {
        return dipendenteRepository.saveAndFlush(dipendente);
    }

    public Supervisore saveSupervisore(Supervisore supervisore) {
        return supervisoreRepository.saveAndFlush(supervisore);
    }

    public Team saveTeam(Team team) {
        return teamRepository.saveAndFlush(team);
    }


    public Task saveTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }

    public Task createTask(TaskState initialState) {
        return taskService.createTask(initialState);
    }

    public Task assegnaDipendenteATask(Long taskId, Long dipendenteId) {
        return taskService.assignEmployeeToTask(taskId, dipendenteId);}

    public Task rimuoviDipendenteDaTask(Long taskId, Long dipendenteId) {
        return taskService.removeEmployeeFromTask(taskId, dipendenteId);
    }

    public Task cambiaStatoTask(Long taskId, TaskState nuovoStato) {
        return taskService.changeTaskState(taskId, nuovoStato);
    }

    public List<Task> getTasksByStato(TaskState stato) {
        return taskService.getTasksByState(stato);
    }

    public List<Task> getTasksByDipendente(Dipendente dipendente) {
        return taskService.getTasksByEmployee(dipendente);
    }

    public List<Task> getTasksNonAssegnati() {
        return taskService.getUnassignedTasks();
    }

    public long countTasksByStato(TaskState stato) {
        return taskService.countTasksByState(stato);
    }

    public List<Task> getTasksComplessi(int sogliaDipendenti) {
        return taskService.getComplexTasks(sogliaDipendenti);
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

    public boolean isDipendenteAssegnato(Long taskId, Long dipendenteId) {
        return taskService.isEmployeeAssigned(taskId, dipendenteId);
    }

    public Task resetTask(Long taskId) {
        return taskService.resetTask(taskId);
    }


    public List<Task> findTasksByStateWithDipendenti(TaskState stato) {
        return taskService.getTasksByStateWithEmployees(stato);
    }

    public Integer countDipendentiByTaskId(Long taskId) {
        return taskService.getEmployeeCountPerTask(taskId);
    }

    public List<Task> findTasksByStateAndDipendentiCount(TaskState stato, int numDipendenti) {
        return taskService.getTasksByStateAndEmployeeCount(stato, numDipendenti);
    }

    public List<Task> findTasksByTeamId(Long idTeam) {
        return taskService.getTasksByTeam(idTeam);
    }
}
