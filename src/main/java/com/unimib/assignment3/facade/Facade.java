package com.unimib.assignment3.facade;

import com.unimib.assignment3.POJO.Dipendente;
import com.unimib.assignment3.POJO.Supervisore;
import com.unimib.assignment3.POJO.Task;
import com.unimib.assignment3.POJO.Team;
import com.unimib.assignment3.enums.EmployeeRole;
import com.unimib.assignment3.enums.TaskState;
import com.unimib.assignment3.repository.DipendenteRepository;
import com.unimib.assignment3.repository.SupervisoreRepository;
import com.unimib.assignment3.repository.TaskRepository;
import com.unimib.assignment3.service.TaskService;
import com.unimib.assignment3.service.TeamService;
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
    TaskService taskService;
    @Autowired
    private TeamService teamService;

    public  Dipendente saveDipendente(Dipendente dipendente){
        return  dipendenteRepository.saveAndFlush(dipendente);
    }

    public Supervisore saveSupervisore(Supervisore supervisore){
        return  supervisoreRepository.saveAndFlush(supervisore);
    }

    public Task saveTask(Task task){
        return taskRepository.saveAndFlush(task);
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

    public boolean isDipendenteAssegnato(Long taskId, Long dipendenteId) {
        return taskService.isDipendenteAssegnato(taskId, dipendenteId);
    }

    // Function for team service
    public Team createTeam(Supervisore supervisore) {
        return teamService.createTeam(supervisore);
    }
    public Team createTeam(List<Dipendente> dipendenti, Supervisore supervisore) {
        return teamService.createTeam(dipendenti, supervisore);
    }
    public Team createTeam(List<Dipendente> dipendenti, Supervisore supervisore, List<Task> tasks) {
        return teamService.createTeam(dipendenti, supervisore, tasks);
    }
    public Team saveTeam(Team team) {
        return teamService.saveTeam(team);
    }
    public void deleteTeam(Team team) {
        teamService.deleteTeam(team);
    }
    public List<Dipendente> getDipendentiInTeam(Team team) {
        return teamService.getDipendentiInTeam(team);
    }
    public void addDipendenteToTeam(Team team, Dipendente dipendente) {
        teamService.addDipendenteToTeam(team, dipendente);
    }
    public void removeAllDipendentiFromTeam(Team team) {
        teamService.removeAllDipendentiFromTeam(team);
    }
    public void removeDipendenteFromTeam(Team team, Dipendente dipendente) {
        teamService.removeDipendenteFromTeam(team, dipendente);
    }
    public Supervisore getTeamSupervisore(Team team) {
        return teamService.getTeamSupervisore(team);
    }
    public void setTeamSupervisore(Team team, Supervisore supervisore) {
        teamService.setTeamSupervisore(team, supervisore);
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
    public Supervisore getSupervisoreByTeamId(Long teamId) {
        return teamService.getSupervisoreByTeamId(teamId);
    }
    public List<Dipendente> getDipendentiByTeamId(Long teamId) {
        return teamService.getDipendentiByTeamId(teamId);
    }
    public List<Task> getTasksInTeamIdByTaskState(Long teamId, TaskState taskState) {
        return teamService.getTasksInTeamIdByTaskState(teamId, taskState);
    }
    public List<Dipendente> getDipendentiInTeamIdWithSalaryGreaterThan(Long teamId, Double salary) {
        return teamService.getDipendentiInTeamIdWithSalaryGreaterThan(teamId, salary);
    }
    public List<Dipendente> getDipendentiInTeamIdWithSalaryLessThan(Long teamId, Double salary) {
        return teamService.getDipendentiInTeamIdWithSalaryLessThan(teamId, salary);
    }
    public List<Dipendente> getDipendentiInTeamIdWithGrado(Long teamId, EmployeeRole employeeRole) {
        return teamService.getDipendentiInTeamIdWithGrado(teamId, employeeRole);
    }
}
